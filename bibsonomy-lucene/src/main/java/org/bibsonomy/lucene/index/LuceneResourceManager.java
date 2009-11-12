package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LuceneData;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.search.delegate.LuceneDelegateResourceSearch;
import org.bibsonomy.lucene.util.LucenePostConverter;
import org.bibsonomy.lucene.util.Utils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.tex.TexEncode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

/**
 * class for maintaining the lucene index
 * 
 *  - regularly update the index by looking for new posts
 *  - asynchronously handle requests for flagging/unflagging of spam users
 * 
 * @author fei
 */
public class LuceneResourceManager<R extends Resource> {
	private static final Log log = LogFactory.getLog(LuceneResourceManager.class);

	/** flag indicating whether to update the index or not */
	private Boolean luceneUpdaterEnabled = true;
	
	private boolean useUpdater = false;
	
	private int alreadyRunning = 0; // das geht bestimmt irgendwie besser
	private int maxAlreadyRunningTrys = 20;

	/** the resource index */ 
	private LuceneResourceIndex<R> resourceIndex;
	
	/** the database manager */
	private LuceneDBInterface<R> dbLogic;
	
	/** the lucene index searcher */
	private LuceneResourceSearch<R> searcher;
	
	/** MAGIC KEY identifying the context environment for this class */
	private static final String CONTEXT_ENV_NAME = "java:/comp/env";
	
	/** MAGIC KEY identifying context variables for this class */
	private static final String CONTEXT_ENABLE_FLAG= "enableLuceneUpdater";

	/** FIXME: to handle the special case, that a new post with an older date
	           as 'retrieveFromIndex' was inserted after last index
			   update, we enlarge the time window for all queries
			   retrieveFromdate - QUERY_TIME_OFFSET_MS */
	private static final long QUERY_TIME_OFFSET_MS = 30*1000;

	private static final String FLD_DATE        = "date";
	private static final String FLD_MERGEDFIELD = "mergedfields";
	private static final String FLD_TAS         = "tas";
	
	/**
	 * constructor
	 */
	public LuceneResourceManager() {
		init();
	}
	
	/**
	 * initialize internal data structures
	 */
	private void init() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup(CONTEXT_ENV_NAME);
			
			this.luceneUpdaterEnabled = (Boolean) envContext.lookup(CONTEXT_ENABLE_FLAG);
			this.useUpdater = true;
		} catch (NamingException e) {
			this.useUpdater = false;
			log.error("NamingException requesting JNDI environment variables 'luceneIndexPathBoomarks' and 'luceneIndexPathPublications' ("+e.getMessage()+")", e);
		}

	}
	
	/**
	 * 
	 * update procedure
	 *  get date of newest record in index := retrieveFromDate
	 *  get date of newest record from tas := retrieveToDate
	 *  retrieveTimePeriod = "> retrieveFromDate AND <= retrieveToDate"
	 *  get content_ids from log-table within retrieveTimePeriod
	 *  delete records in lucene index matching this content_id
	 *  get new records to insert into index with single new sql command 
	 *  insert new records into index

	 * sollten sicherheitshalber alle einzufügenden einträge aus dem index vorher gelöscht werden
	 * sofern die content_id gleich ist oder nicht eingefügt werden, wenn die content_id 
	 * bereits existiert? -  wird zur zeit gemacht

	 * neues feld in tabellen in log_bookmark und log_bibtex: log_date
	 * log_date wird auf den aktuellen timestamp gesetzt, wenn neuer eintrag eingefügt wird
	 * dieses einfügedatum kann hinter dem datum des selben eintrags in der tas liegen.
	 * das hat zur folge, dass der alte Eintrag nicht im gleichen Durchlauf aus dem Index gelöscht wird,
	 * wir der neue eingefügt wird. es können also beide im Index enthalten sein. Beim nächsten Durchlauf wird dann
	 * der Eintrag entfernt.
	 * Lösungsvorschlag: Das Änderungsdatum, das eigentlich an den Bookmark-Eintrag angehangen werden soll, dort auch anhängen und
	 * danach den Eintrag (mit diesem Datum!) in die Logtabelle kopieren. Dan kann das vorhandene Feld change_date ausgewertet werden.
	 * 
	 * @param optimizeindex
	 */
	private void updateIndexes(boolean optimizeindex)  {
		// FIXME: this is not needed
		Boolean status = false;

		// don't run twice at the same time  - if something went wrong, delete alreadyRunning
		if ((alreadyRunning > 0) && (alreadyRunning<maxAlreadyRunningTrys) ) {
			alreadyRunning++;
			log.warn("reloadIndex - alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");
			return;	
		}
		alreadyRunning = 1;
		log.debug("reloadIndex - run and reset alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");

		// assure that flagging and unflagging of spammers as well as index updating is mutual exclusive
		synchronized(this) {
			// 
			// update the index
			//
			LuceneResourceIndex<R> luceneIndex = this.resourceIndex;
			LuceneDBInterface<R> luceneLogic   = this.dbLogic;

			//  get date of newest record in index := retrieveFromDate
			Date retrieveFromDate = luceneIndex.getNewestRecordDateFromIndex();
			// FIXME: to handle the special case, that a new post with an older date
			//        as 'retrieveFromIndex' was inserted after last index
			//        update, we enlarge the time window for all queries
			//        retrieveFromdate - QUERY_TIME_OFFSET_MS
			retrieveFromDate = new Date(retrieveFromDate.getTime()-QUERY_TIME_OFFSET_MS);
			//  get date of newest record from tas := retrieveToDate
			Date retrieveToDate   = luceneLogic.getNewestRecordDateFromTas();

			// DEBUG
			log.debug("retrieveFromDate: >  " +retrieveFromDate);
			log.debug("retrieveToDate:   <= " +retrieveToDate);

			//----------------------------------------------------------------
			//  1) get posts which were altered and should be deleted from index  
			//----------------------------------------------------------------
			//  get content_ids from log-table within retrieveTimePeriod		
			List<Integer> contentIdsToDelete = dbLogic.getContentIdsToDelete(retrieveFromDate, retrieveToDate);

			//----------------------------------------------------------------
			//  2) get new records to insert into index 
			//----------------------------------------------------------------
			List<Post<R>> posts = luceneLogic.getPostsForTimeRange2(retrieveFromDate, retrieveToDate);
			
			// FIXME: due to sloppy time constraints in SQL-Queries, we have to skip
			//        posts which already were indexed to avoid duplicates
			Iterator<Post<R>> it = posts.iterator();
			while (it.hasNext()) {
				Post<R> post = it.next();
				if( this.resourceIndex.getRecordForContentId(post.getContentId())!=null )
					it.remove();
			}
			
			//----------------------------------------------------------------
			//  3) delete posts from lucene index which will be re-inserted 
			//     afterwards 
			//----------------------------------------------------------------
			status = false;
			try {
				status = deleteDocumentsInIndex(contentIdsToDelete);
			} catch (IOException e) {
				log.error("IOException while deleteDocumentsInIndex2 ("+e.getMessage()+")");
			}

			//----------------------------------------------------------------
			//  4) update tag assignments of altered posts  
			//----------------------------------------------------------------
			List<Post<R>> updatedPosts = luceneLogic.getUpdatedPostsForTimeRange(retrieveFromDate, retrieveToDate);
			try {
				this.updateTagAssignments(updatedPosts);
			} catch (IOException e) {
				log.error("Error updating posts where only tag assignments have changed", e);
			}

			//----------------------------------------------------------------
			//  5) write new records into the index 
			//----------------------------------------------------------------
			for( Post<R> post : posts ) {
				Document postDoc = LucenePostConverter.readPost(post);
				luceneIndex.insertDocument(postDoc);
			}
			
			//----------------------------------------------------------------
			//  6) commit changes 
			//----------------------------------------------------------------
			luceneIndex.flush();
		}
		
		// all done.
		alreadyRunning = 0;
		return;
	}
	
	/**
	 * update tag column in index for given posts
	 * 
	 * FIXME: this is quickly hacked due to zeitnot and probably inefficient
	 * FIXME: this logic probably should move to the index manager
	 * 
	 * @param postsToUpdate list of posts whose tag
	 *    assignments have changed
	 * @throws IOException 
	 * @throws LockObtainFailedException 
	 * @throws CorruptIndexException 
	 * @throws StaleReaderException 
	 */
	@SuppressWarnings("unchecked")
	public void updateTagAssignments(List<Post<R>> postsToUpdate) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
		// FIXME: use global type handling via spring!
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		TexEncode tex = new TexEncode();

		// cache documents to update
		List<Document> updatedDocuments = new LinkedList<Document>();
		
		// process each post
		for( Post<R> post : postsToUpdate ) {
			log.debug("Updating post " + post.getResource().getTitle() + " ("+post.getContentId()+")");
			// get old post from index
			Document doc = this.resourceIndex.getRecordForContentId(post.getContentId());
			
			// skip post, if it is already updated in the index
			if( doc.getField(FLD_DATE)!=null ) {
				String dateString = doc.getField(FLD_DATE).stringValue();
				Date entryDate = null;
				try {
					entryDate = dateFormatter.parse(dateString);
				} catch (java.text.ParseException e) {
					log.error("Error parsing index date "+entryDate);
				}
				if( entryDate.equals(post.getDate()) ) {
					log.debug("Skipping unmodified update.");
					continue;
				}
			}
			// update field 'tas'
			// FIXME: apply generic data extraction framework
			doc.removeField(FLD_TAS);
			doc.removeField(FLD_MERGEDFIELD);
			String tags = "";
			for( Tag tag : post.getTags() ) {
				tags += " " + tag.getName();
			};
			doc.add(new Field(FLD_TAS, tex.encode(tags), Field.Store.YES, Field.Index.ANALYZED));
			// update field  'mergedfield'
			// FIXME: configure mergedfields via spring
			String mergedFields = "";
			for( Field field : (List<Field>)doc.getFields() ) {
				mergedFields += (field.stringValue()==null)?"":field.stringValue();
				mergedFields += " ";
			}
			doc.add(new Field(FLD_MERGEDFIELD, tex.encode(mergedFields), Field.Store.YES, Field.Index.ANALYZED));
			// update date 
			// FIXME: this is for setting the index' last change date - overriding the post's real date
			doc.removeField(FLD_DATE);
			doc.add(new Field(FLD_DATE, dateFormatter.format(post.getDate()), Field.Store.YES,Field.Index.NOT_ANALYZED));			
			// cache document for update
			updatedDocuments.add(doc);
			/*
			if( this.purgeDocumentForContentId(post.getContentId())!=1 ) {
				log.error("Error updating tag assignment");
			}*/
			this.resourceIndex.deleteDocumentForContentId(post.getContentId());
		}
	
		// finally write updated posts to index
		this.resourceIndex.insertDocuments(updatedDocuments);
	}

	/**
	 * reload each registered searcher's index 
	 */
	public void reloadIndex() {
		// don't run twice at the same time  - if something went wrong, delete alreadyRunning
		if ((alreadyRunning > 0) && (alreadyRunning<maxAlreadyRunningTrys) ) {
			alreadyRunning++;
			log.warn("reloadIndex - alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");
			return;	
		}
		alreadyRunning = 1;
		log.debug("reloadIndex - run and reset alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");

		init();

		// if lucene updater is disabled, return without doing something
		if (!luceneUpdaterEnabled) {
			log.debug("reloadIndex - lucene updater is disabled by user");
			alreadyRunning = 0;
			return;
		}

		if (!useUpdater) {
			log.error("reloadIndex - LuceneUpdater deactivated!");
			alreadyRunning = 0;
			return;	
		}

		// do the actual work
		log.debug("reload search index");
		searcher.reloadIndex();
		log.debug("reload search index done");

		alreadyRunning = 0;
	}

	/**
	 * update each registered index
	 * 
	 * @param optimizeIndex flag indicating whether the indices should be optimized after commiting changes
	 */
	public void updateIndex(boolean optimizeIndex) {
		init();

		// if lucene updater is disabled, return without doing something
		if (!luceneUpdaterEnabled) {
			log.debug("reloadIndex - lucene updater is disabled");
			alreadyRunning = 0;
			return;
		}

		if (!useUpdater) {
			log.warn("updateIndex - LuceneUpdater deactivated!");
			alreadyRunning = 0;
			return;	
		}

		// do the actual work
		log.debug("update indexes");
		updateIndexes(optimizeIndex);
		log.debug("update indexes done");
	}

	public void updateAndReloadIndex() {
		updateIndex(false);
		reloadIndex();
	}

	public void updateAndReloadIndex(String optimizeIndexString) {
		if ("true".equals(optimizeIndexString)) {
			// optimize index
			updateIndex(true);
		} else {
			// do not optimize index
			updateIndex(false);
		}
		reloadIndex();
	}
	
	//------------------------------------------------------------------------
	// private helper methods
	//------------------------------------------------------------------------
	/**
	 * delete resources from index whose content was updated
	 * 
	 * FIXME: this class mixes logic from index updater and resource index
	 *  
	 * @param indexReader index reader
	 * @param contentIdsToDelete list of content ids which should be updated 
	 * @param contentToInsert list of content ids which should be inserted and thus deleted, if they
	 *                        already exist in the index
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected boolean deleteDocumentsInIndex(List<Integer> contentIdsToUpdate, Map<Integer, Map<String, Object>> contentToInsert) throws CorruptIndexException, IOException {
		boolean allDocsDeleted = true;
		
		Iterator<Integer> i = contentIdsToUpdate.iterator();
		while (i.hasNext()) {
			Integer contentId = i.next();
			if( contentToInsert.containsKey(contentId) ) {
				// only delete entries from index which will be inserted afterwards
				// FIXME: why should content_ids be parsed to integer beforehead???
				
				/*
				int cnt;
				if( (cnt = this.resourceIndex.purgeDocumentForContentId(contentId))==0 ) {
					log.debug("Document " +contentId+ " NOT deleted ("+cnt+")!");
					allDocsDeleted = false;
				}
				else {
					log.debug("Document " +contentId + " deleted ("+cnt+" occurences)!");
				}*/
				this.resourceIndex.deleteDocumentForContentId(contentId);
				
				// remove content_id from list so that contentIdsToUpdate only contains
				// ids of documents where the tag assignments have changed
				i.remove();
			}
			
		}
		
		// FIXME: this isn't set correctly - do we need it anyway???
		return allDocsDeleted;
	}	
	
	/**
	 * delete resources from index 
	 * 
	 * @param indexReader index reader
	 * @param contentIdsToDelete list of content ids which should be updated 
	 * @param contentToInsert list of content ids which should be inserted and thus deleted, if they
	 *                        already exist in the index
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected boolean deleteDocumentsInIndex(List<Integer> contentIdsToDelete) throws CorruptIndexException, IOException {
		boolean allDocsDeleted = true;
		
		Iterator<Integer> i = contentIdsToDelete.iterator();
		while (i.hasNext()) {
			Integer contentId = i.next();
			this.resourceIndex.deleteDocumentForContentId(contentId);
		}
		
		// FIXME: this isn't set correctly - do we need it anyway???
		return allDocsDeleted;
	}	
	//------------------------------------------------------------------------
	// spam handling
	//------------------------------------------------------------------------
	/**
	 * flag/unflag spammer, depending on user.getPrediction()
	 */
	public void  flagSpammer(User user) {
		log.debug("flagSpammer called for user " + user.getName());
		switch( user.getPrediction() ) {
		case 0:
			log.debug("unflag non-spammer");
			List<Post<R>> userPosts = this.getDbLogic().getPostsForUser(
					user.getName(), user.getName(), 
					HashID.INTER_HASH, 
					GroupID.PUBLIC.getId(), new LinkedList<Integer>(), 
					Integer.MAX_VALUE, 0);
			unflagEntryAsSpam(userPosts);
			// flush changes to the index
			this.resourceIndex.flush();
			break;
		case 1:
			log.debug("flag spammer");
			flagEntryAsSpam(user.getName());
			break;
		}
	}
	
	/**
	 * flags an entry as spammer. This is the same like deleting one entry from index 
	 * - no it IS deleting one entry from index
	 * FIXME: check whether this is thread safe!!!
	 *  
	 */
	protected void flagEntryAsSpam(String username) {
		// assure that flagging and unflagging of spammers as well as index updating is mutual exclusive
		synchronized(this) {
			try {
				resourceIndex.deleteDocumentsInIndex(username);
			} catch (Exception e) {
				log.error("Error removing spam posts for user " +username+ " from index", e);
			}
		}
	}

	/** 
	 * flags an entry as non-spammer. This is the same like adding one entry to the index - no it IS adding one entry to the index 
	 * FIXME: check whether this is thread safe!!!
	 * 
	 * @param recordContent
	 * @param recordType
	 */
	protected void unflagEntryAsSpam(List<Post<R>> userPosts) {
		// assure that flagging and unflagging of spammers as well as index updating is mutual exclusive
		synchronized(this) {
			//  insert new records into index
			if( (userPosts!=null) && (userPosts.size()>0) ) {
				for (Post<?> post : userPosts ) {
					// cache document for writing 
					resourceIndex.insertDocument(LucenePostConverter.readPost(post));
				}
			}
		}
	}



	public void setResourceIndex(LuceneResourceIndex<R> resourceIndex) {
		this.resourceIndex = resourceIndex;
	}

	public LuceneResourceIndex<R> getResourceIndex() {
		return resourceIndex;
	}

	public void setDbLogic(LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	public LuceneDBInterface<R> getDbLogic() {
		return dbLogic;
	}

	public void setSearcher(LuceneResourceSearch<R> searcher) {
		this.searcher = searcher;
	}

	public LuceneResourceSearch<R> getSearcher() {
		return searcher;
	}
}
