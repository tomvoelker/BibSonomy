package org.bibsonomy.lucene.index.manager;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneResourceConverter;
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
 * @version $Id$
 * @param <R> the resource to manage
 */
public class LuceneResourceManager<R extends Resource> {
	private static final Log log = LogFactory.getLog(LuceneResourceManager.class);

	/** constant for querying for all posts which have been deleted since the last index update */
	protected static final long QUERY_TIME_OFFSET_MS = 30 * 1000;
	
	/** flag indicating whether to update the index or not */
	private boolean luceneUpdaterEnabled = true;
	
	private boolean useUpdater = false;
	
	protected int alreadyRunning = 0; // das geht bestimmt irgendwie besser
	private final int maxAlreadyRunningTrys = 20;

	/** the resource index */ 
	protected LuceneResourceIndex<R> resourceIndex;

	/** redundant resource indeces */ 
	protected List<LuceneResourceIndex<R>> resourceIndices;
	
	/** the database manager */
	protected LuceneDBInterface<R> dbLogic;
	
	/** the lucene index searcher */
	private LuceneResourceSearch<R> searcher;
	
	/** converts post model objects to lucene documents */
	protected LuceneResourceConverter<R> resourceConverter;
	
	/** selects current (redundant) index */
	protected int idxSelect; // TODO use an object representation instead

	/**
	 * constructor
	 */
	public LuceneResourceManager() {
		init();
		
		this.idxSelect = 0;
		this.resourceIndex = null;
	}
	
	/**
	 * initialize internal data structures
	 */
	private final void init() {
		this.luceneUpdaterEnabled = LuceneBase.getEnableUpdater();
		this.useUpdater = true;
	}
	
	/**
	 * triggers index optimization during next update
	 */
	public void optimizeIndex() {
		if (this.resourceIndex != null) {
			this.resourceIndex.optimizeIndex();
		}
	}
	
	/**
	 * Get statistics for the active index
	 * @return LuceneIndexStatistics for the active index 
	 */
	public LuceneIndexStatistics getStatistics() {
		final LuceneResourceIndex<R> index = this.resourceIndices.get(idxSelect);
		return index == null ? null : index.getStatistics();
	}

	/**
	 * Get statistics for the inactive index
	 * @return LuceneIndexStatistics for the inactive index 
	 */
	public LuceneIndexStatistics getInactiveIndexStatistics() {
		final LuceneResourceIndex<R> index = this.resourceIndices.get((idxSelect + 1) % this.resourceIndices.size());
		return index == null ? null : index.getStatistics();
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
	protected void updateIndexes()  {
		synchronized(this) {
			/*
			 *  initialize variables  
			 */
			// set the active resource index
			this.resourceIndex = this.resourceIndices.get(idxSelect);
			
			// current time stamp for storing as 'lastLogDate' in the index
			// FIXME: get this date from the log_table via 'getContentIdsToDelete'
			final long currentLogDate = System.currentTimeMillis();
			
			// FIXME: this should be done in the constructor
			// keeps track of the newest tas_id during last index update 
			Integer lastTasId = this.resourceIndex.getLastTasId();
			log.debug("lastTasId: " + lastTasId);

			// keeps track of the newest log_date during last index update
			final long lastLogDate = this.resourceIndex.getLastLogDate();
			
			lastTasId = updateIndex(currentLogDate, lastTasId, lastLogDate);

			/*
			 * commit changes 
			 */
			this.resourceIndex.flush();
			
			/*
			 * update variables
			 */
			this.resourceIndex.setLastLogDate(currentLogDate);
			this.resourceIndex.setLastTasId(lastTasId);
		}
		alreadyRunning = 0;
	}

	protected int updateIndex(final long currentLogDate, int lastTasId, final long lastLogDate) {
	    /*
	     * 1) flag/unflag spammer 
	     */
	    this.updatePredictions();
	    
	    /*
	     * 2) get new posts 
	     */
	    final List<LucenePost<R>> newPosts = this.dbLogic.getNewPosts(lastTasId);

	    /*
	     * 3) get posts to delete
	     */
	    final List<Integer> contentIdsToDelete = this.dbLogic.getContentIdsToDelete(new Date(lastLogDate - QUERY_TIME_OFFSET_MS));


	    /*
	     * 4) remove posts from 1) & 2) from the index
	     *     and update field 'lastTasId'
	     */
	    for (final LucenePost<R> post : newPosts) {
	    	contentIdsToDelete.add(post.getContentId());
	    	lastTasId = Math.max(post.getLastTasId(), lastTasId);
	    }
	    
	    this.resourceIndex.deleteDocumentsInIndex(contentIdsToDelete);

	    /*
	     * 5) add all posts from 1) to the index 
	     */
	    for (final LucenePost<R> post : newPosts) {
	    	post.setLastLogDate(new Date(currentLogDate));
	    	final Document postDoc = this.resourceConverter.readPost(post);
	    	this.resourceIndex.insertDocument(postDoc);
	    }
	    
	    return lastTasId;
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
		final int oldIdxId = this.searcher.getIndexId();
		final int newIdxId = this.resourceIndices.get(idxSelect).getIndexId();
		log.debug("switching from index "+oldIdxId+" to index "+newIdxId);
		searcher.reloadIndex(newIdxId);
		log.debug("reload search index done");

		alreadyRunning = 0;
	}

	/**
	 * update each registered index
	 * 
	 * @param optimizeIndex flag indicating whether the indices should be optimized after commiting changes
	 */
	protected void updateIndex() {
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

	/**
	 * switches the active index and updates and reloads the index
	 */
	public void updateAndReloadIndex() {
		    // switch active index
		    this.idxSelect = (idxSelect + 1) % this.resourceIndices.size();
		    
		    // update passive index
		    updateIndex();
		    
		    // make tell searcher to use the updated index
		    reloadIndex();
	}

	
	/**
	 * reopen index reader - e.g. after the index has changed on the disc
	 */
	public void resetIndexReader() {
		for (final LuceneResourceIndex<R> index : this.resourceIndices) {
			index.reset();
		}
	}

	/**
	 * reopen index searcher - e.g. after the index has changed on the disc
	 */
	public void resetIndexSearcher() {
		this.searcher.reloadIndex(this.idxSelect);
	}
	
	/**
	 * spam handling
	 * get spam prediction which were missed since last index update
	 * 
	 * FIXME: this code is due to the old spam-flagging-mechanism
	 *        it is probably more efficient to get all un-flagged-posts directly via 
	 *        a join with the user table
	 */
	private void updatePredictions() {
		// keeps track of the newest log_date during last index update
		final Long lastLogDate  = this.resourceIndex.getLastLogDate()-QUERY_TIME_OFFSET_MS;
		
		// get date of last index update
		final Date fromDate = new Date(lastLogDate);
		
		// get users which where flagged as spammers since then 
		final List<String> lostSpammer    = this.dbLogic.getSpamPredictionForTimeRange(fromDate);
		
		// get users which where unflagged since then 
		final List<String> lostNonSpammer = this.dbLogic.getNonSpamPredictionForTimeRange(fromDate);
		
		// flag lost spammers in index
		final User transientUser = new User();
		for (final String spammer : lostSpammer) {
			transientUser.setName(spammer);
			transientUser.setPrediction(1);
			transientUser.setSpammer(true);
			flagSpammer(transientUser);
		}

		// unflag lost nonspammers in index
		for (final String spammer : lostNonSpammer) {
			transientUser.setName(spammer);
			transientUser.setPrediction(0);
			transientUser.setSpammer(false);
			flagSpammer(transientUser);
		}
	}
	
	/**
	 * flag/unflag spammer, depending on user.getPrediction()
	 */
	private void flagSpammer(final User user) {
		log.debug("flagSpammer called for user " + user.getName());
		switch (user.getPrediction()) {
		case 0:
			log.debug("unflag non-spammer");
			final List<LucenePost<R>> userPosts = this.getDbLogic().getPostsForUser(user.getName(), user.getName(), HashID.INTER_HASH, GroupID.PUBLIC.getId(), new LinkedList<Integer>(),  Integer.MAX_VALUE, 0);
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
	private void unflagEntryAsSpam(final List<LucenePost<R>> userPosts) {
		//  insert new records into index
		if ((userPosts != null) && (userPosts.size() > 0)) {
			for (final Post<R> post : userPosts) {
				// cache possible pre existing duplicate for deletion 
				this.resourceIndex.deleteDocumentForContentId(post.getContentId());
				// cache document for writing 
				this.resourceIndex.insertDocument(this.resourceConverter.readPost(post));
			}
		}
	}

	/**
	 * @return the dbLogic
	 */
	public LuceneDBInterface<R> getDbLogic() {
		return dbLogic;
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(final LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}
	
	/**
	 * checks, whether the index is readily initialized
	 * @return true, if index is ready - false, otherwise
	 * (e.g. if no lucene-index has been generated yet) 
	 */
	
	public boolean isIndexEnabled() {
	    return this.resourceIndices.get(idxSelect).isIndexEnabled();
	}

	/**
	 * @return the searcher
	 */
	public LuceneResourceSearch<R> getSearcher() {
		return searcher;
	}

	/**
	 * @param searcher the searcher to set
	 */
	public void setSearcher(final LuceneResourceSearch<R> searcher) {
		this.searcher = searcher;
	}

	/**
	 * @return the resourceConverter
	 */
	public LuceneResourceConverter<R> getResourceConverter() {
		return resourceConverter;
	}

	/**
	 * @param resourceConverter the resourceConverter to set
	 */
	public void setResourceConverter(final LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @return the resourceIndeces
	 */
	public List<LuceneResourceIndex<R>> getResourceIndeces() {
		return this.resourceIndices;
	}

	/**
	 * @param resourceIndeces the resourceIndeces to set
	 */
	public void setResourceIndeces(final List<LuceneResourceIndex<R>> resourceIndeces) {
		this.resourceIndices = resourceIndeces;
	}
	
	/**
	 * @return the name of the managed resource
	 */
	public String getResourceName() {
	    return resourceIndices.get(idxSelect).getResourceName();
	}
}
