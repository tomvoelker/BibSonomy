package org.bibsonomy.lucene.index;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LucenePostConverter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * class for maintaining the lucene index
 * 
 *  - regularly update the index by looking for new posts
 *  - asynchronously handle requests for flagging/unflagging of spam users
 * 
 * @author fei
 */
public class LuceneResourceManager<R extends Resource> extends LuceneBase {
	private static final Log log = LogFactory.getLog(LuceneResourceManager.class);

	/** flag indicating whether to update the index or not */
	private Boolean luceneUpdaterEnabled = true;
	
	private boolean useUpdater = false;
	
	private int alreadyRunning        = 0; // das geht bestimmt irgendwie besser
	private int maxAlreadyRunningTrys = 20;

	/** the resource index */ 
	private LuceneResourceIndex<R> resourceIndex;
	
	/** the database manager */
	private LuceneDBInterface<R> dbLogic;
	
	/** the lucene index searcher */
	private LuceneResourceSearch<R> searcher;

	/** keeps track of the newest tas_id during last index update */
	private Integer lastTasId = null;

	/** keeps track of the newest log_date during last index update */
	private Long lastLogDate  = null;

	/** constant for querying for all posts which have been deleted since the last index update */
	private static final long QUERY_TIME_OFFSET_MS = 30*1000;

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
		this.luceneUpdaterEnabled = getEnableUpdater();
		this.useUpdater           = true;
	}
	
	/**
	 * triggers index optimization during next update
	 */
	public void optimizeIndex() {
		if( this.resourceIndex!=null )
			this.resourceIndex.optimizeIndex();
	}
	
	/**
	 * updates the index, that is
	 *  - adds new posts 
	 *  - updates posts, where tag assignments have changed
	 *  - removes deleted posts
	 * 
	 * For that, we keep track of the newest tas_id seen during index update. 
	 * 
	 * On each update, we query for all posts with greater tas_ids. These Posts are either new,
	 * or belong to posts, where the tag assignments have changed. We delete all those posts 
	 * from the index (for implementing the tag update). Afterwards, all these posts are
	 * (re-)inserted.
	 * 
	 * To keep track of deleted posts, we further hold the last log_date t and query for
	 * all content_ids from the log_table with a change_date >= t-epsilon. These posts are 
	 * removed from the index together with the updated posts. 
	 * 
	 * @param optimizeindex indicate whether the index should be optimized
	 */
	private void updateIndexes()  {
		synchronized(this) {
			//----------------------------------------------------------------
			//  0) initialize variables  
			//----------------------------------------------------------------
			// current time stamp for storing as 'lastLogDate' in the index
			// FIXME: get this date from the log_table via 'getContentIdsToDelete'
			long currentLogDate = System.currentTimeMillis();
			
			// FIXME: this should be done in the constructor
			if( lastTasId==null )
				lastTasId = this.resourceIndex.getLastTasId();
			if( lastLogDate==null )
				lastLogDate = this.resourceIndex.getLastLogDate()-QUERY_TIME_OFFSET_MS;
			
			//----------------------------------------------------------------
			//  0) flag/unflag spammer  
			//----------------------------------------------------------------
			this.updatePredictions();
			
			//----------------------------------------------------------------
			//  1) get new posts  
			//----------------------------------------------------------------
			List<LucenePost<R>> newPosts = this.dbLogic.getNewPosts(lastTasId);

			//----------------------------------------------------------------
			//  2) get posts to delete  
			//----------------------------------------------------------------
			List<Integer> contentIdsToDelete = this.dbLogic.getContentIdsToDelete(new Date(lastLogDate));


			//----------------------------------------------------------------
			//  3) remove posts from 1) & 2) from the index
			//     and update field 'lastTasId'
			//----------------------------------------------------------------
			for( LucenePost<R> post : newPosts ) {
				contentIdsToDelete.add(post.getContentId());
				if( post.getLastTasId()>this.lastTasId )
					this.lastTasId = post.getLastTasId();
			}
			this.resourceIndex.deleteDocumentsInIndex(contentIdsToDelete);

			//----------------------------------------------------------------
			//  4) add all posts from 1) to the index  
			//----------------------------------------------------------------
			for( LucenePost<R> post : newPosts ) {
				post.setLastLogDate(new Date(currentLogDate));
				Document postDoc = LucenePostConverter.readPost(post);
				this.resourceIndex.insertDocument(postDoc);
			}

			//----------------------------------------------------------------
			//  6) commit changes 
			//----------------------------------------------------------------
			this.resourceIndex.flush();

			//----------------------------------------------------------------
			//  7) update variables 
			//----------------------------------------------------------------
			this.lastLogDate = currentLogDate-QUERY_TIME_OFFSET_MS;
		}
		
		// all done.
		alreadyRunning = 0;
		return;
	}
	
	/**
	 * reload each registered searcher's index 
	 */
	public void reloadIndex() {
		// if lucene updater is disabled, return without doing something
		if (!luceneUpdaterEnabled) {
			log.debug("lucene updater is disabled by user");
			return;
		}
		
		// don't run twice at the same time  - if something went wrong, delete alreadyRunning
		if ((alreadyRunning > 0) && (alreadyRunning<maxAlreadyRunningTrys) ) {
			alreadyRunning++;
			log.warn("reloadIndex - alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");
			return;	
		}
		alreadyRunning = 1;
		log.debug("reloadIndex - run and reset alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");

		init();

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
	public void updateIndex() {
		// if lucene updater is disabled, return without doing something
		if (!luceneUpdaterEnabled) {
			log.debug("reloadIndex - lucene updater is disabled");
			alreadyRunning = 0;
			return;
		}

		// don't run twice at the same time  - if something went wrong, delete alreadyRunning
		if ((alreadyRunning > 0) && (alreadyRunning<maxAlreadyRunningTrys) ) {
			alreadyRunning++;
			log.warn("reloadIndex - alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");
			return;	
		}
		alreadyRunning = 1;
		log.debug("reloadIndex - run and reset alreadyRunning ("+alreadyRunning+"/"+maxAlreadyRunningTrys+")");

		// initialize data structures
		init();

		// check if the updater successfully initialized
		if (!useUpdater) {
			log.warn("updateIndex - LuceneUpdater deactivated!");
			alreadyRunning = 0;
			return;	
		}

		// do the actual work
		log.debug("update indexes");
		updateIndexes();
		log.debug("update indexes done");
	}

	public void updateAndReloadIndex() {
		updateIndex();
		reloadIndex();
	}

	
	//------------------------------------------------------------------------
	// private helper methods
	//------------------------------------------------------------------------
	/**
	 * incorporate all database changes before startup which would otherwise
	 * get lost
	 */
	public void recovery() {
	}
	
	//------------------------------------------------------------------------
	// spam handling
	//------------------------------------------------------------------------
	/**
	 * get spam prediction which were missed since last index update
	 * 
	 * FIXME: this code is due to the old spam-flagging-mechanism
	 *        it is probably more efficient to get all un-flagged-posts directly via 
	 *        a join with the user table
	 */
	private void updatePredictions() {
		if( lastLogDate==null )
			lastLogDate = this.resourceIndex.getLastLogDate()-QUERY_TIME_OFFSET_MS;
		
		// get date of last index update
		Date fromDate = new Date(lastLogDate);
		
		// get users which where flagged as spammers since then 
		List<String> lostSpammer    = this.dbLogic.getSpamPredictionForTimeRange(fromDate);
		
		// get users which where unflagged since then 
		List<String> lostNonSpammer = this.dbLogic.getNonSpamPredictionForTimeRange(fromDate);
		
		// flag lost spammers in index
		User transientUser = new User();
		for( String spammer : lostSpammer ) {
			transientUser.setName(spammer);
			transientUser.setPrediction(1);
			transientUser.setSpammer(true);
			flagSpammer(transientUser);
		}

		// unflag lost nonspammers in index
		for( String spammer : lostNonSpammer ) {
			transientUser.setName(spammer);
			transientUser.setPrediction(0);
			transientUser.setSpammer(false);
			flagSpammer(transientUser);
		}
	}
	
	
	/**
	 * flag/unflag spammer, depending on user.getPrediction()
	 */
	private void  flagSpammer(User user) {
		log.debug("flagSpammer called for user " + user.getName());
		switch( user.getPrediction() ) {
		case 0:
			log.debug("unflag non-spammer");
			List<LucenePost<R>> userPosts = this.getDbLogic().getPostsForUser(
					user.getName(), user.getName(), 
					HashID.INTER_HASH, 
					GroupID.PUBLIC.getId(), new LinkedList<Integer>(), 
					Integer.MAX_VALUE, 0);
			unflagEntryAsSpam(userPosts);
			this.resourceIndex.unFlagUser(user.getName());
			break;
		case 1:
			log.debug("flag spammer");
			this.resourceIndex.flagUser(user.getName());
			break;
		}
	}
	
	/** 
	 * flags an entry as non-spammer
	 *  
	 * @param userPosts all of the user's posts - these will be inserted into the index
	 */
	private void unflagEntryAsSpam(List<LucenePost<R>> userPosts) {
		//  insert new records into index
		if( (userPosts!=null) && (userPosts.size()>0) ) {
			for (Post<?> post : userPosts ) {
				// cache possible pre existing duplicate for deletion 
				resourceIndex.deleteDocumentForContentId(post.getContentId());
				// cache document for writing 
				resourceIndex.insertDocument(LucenePostConverter.readPost(post));
			}
		}
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
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
