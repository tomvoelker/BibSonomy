package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LuceneData;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.search.delegate.LuceneDelegateResourceSearch;
import org.bibsonomy.lucene.util.Utils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
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
			LuceneResourceIndex<? extends Resource> luceneIndex = this.resourceIndex;
			LuceneDBInterface<? extends Resource> luceneLogic   = this.dbLogic;

			//  get date of newest record in index := retrieveFromDate
			Date retrieveFromDate = luceneIndex.getNewestRecordDateFromIndex();
			//  get date of newest record from tas := retrieveToDate
			Date retrieveToDate   = luceneLogic.getNewestRecordDateFromTas();

			// DEBUG
			log.debug("retrieveFromDate: >  " +retrieveFromDate);
			log.debug("retrieveToDate:   <= " +retrieveToDate);

			//----------------------------------------------------------------
			//  1) delete records in lucene index which were altered 
			//----------------------------------------------------------------

			//  get content_ids from log-table within retrieveTimePeriod		
			List<Integer> contentIdsToDelete = dbLogic.getContentIdsToDelete(retrieveFromDate, retrieveToDate);

			//  delete records in lucene index matching this content_ids
			status = false;
			try {
				status = luceneIndex.deleteDocumentsInIndex(contentIdsToDelete);
			} catch (CorruptIndexException e) {
				log.error("CorruptIndexException while deleteDocumentsInIndex ("+e.getMessage()+")");
			} catch (IOException e) {
				log.error("IOException while deleteDocumentsInIndex1 ("+e.getMessage()+")");
			}

			// TODO: when does this happen? shouldn't such an error be catched above?
			if (!status) 
				log.error("Error on deleting documents in index");

			//----------------------------------------------------------------
			//  2) get new records to insert into index with single new sql command 
			//----------------------------------------------------------------
			List<HashMap<String, Object>> posts = new ArrayList<HashMap<String, Object>>();
			posts = luceneLogic.getPostsForTimeRange(retrieveFromDate, retrieveToDate);

			ArrayList<Integer> contentIdsToInsert = new ArrayList<Integer>(); 
			for (HashMap<String, Object> content : posts) {
				contentIdsToInsert.add(((Long)content.get("content_id")).intValue()); 
			}

			status = false;
			//  delete records in lucene index which whom are going to be inserted
			try {
				status = luceneIndex.deleteDocumentsInIndex(contentIdsToInsert);
			} catch (IOException e) {
				log.error("IOException while deleteDocumentsInIndex2 ("+e.getMessage()+")");
			}

			//----------------------------------------------------------------
			//  3) write new records into the index 
			//----------------------------------------------------------------
			try {
				luceneIndex.insertRecordsIntoIndex2(posts, optimizeindex);
			} catch (CorruptIndexException e) {
				log.error("Index corrupted while writing new posts to index ", e);
			} catch (IOException e) {
				log.error("IO error while writing new posts to index ", e);
			}
		}
		
		// all done.
		alreadyRunning = 0;
		return;
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
				// convert ResultList<Post<?>> into LuceneData
				List<LuceneData> recordContentArrayList = new LinkedList<LuceneData>();
				for (Post<?> post : userPosts ) {
					LuceneData data = new LuceneData(null);
					data.setPost(post);
					recordContentArrayList.add(data);
				}

				try {
					resourceIndex.insertRecordsIntoIndex(recordContentArrayList, false);
				} catch (Exception e) {
					log.error("Error unflagging spam posts.", e);
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
