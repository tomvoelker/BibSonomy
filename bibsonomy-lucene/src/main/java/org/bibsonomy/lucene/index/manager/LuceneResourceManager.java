package org.bibsonomy.lucene.index.manager;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LuceneResourceIndexInfo;
import org.bibsonomy.lucene.param.LuceneResourceIndices;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.lucene.util.generator.LuceneGenerateResourceIndex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * class for maintaining the lucene index
 * 
 * - regularly update the index by looking for new posts - asynchronously handle
 * requests for flagging/unflagging of spam users
 * 
 * @author fei
 * @version $Id: LuceneResourceManager.java,v 1.19 2012-06-06 18:17:51 nosebrain
 *          Exp $
 * @param <R>
 *            the resource to manage
 */
public class LuceneResourceManager<R extends Resource> implements GenerateIndexCallback<R> {
	private static final Log log = LogFactory.getLog(LuceneResourceManager.class);

	/**
	 * constant for querying for all posts which have been deleted since the
	 * last index update
	 */
	protected static final long QUERY_TIME_OFFSET_MS = 30 * 1000;

	/** flag indicating whether to update the index or not */
	private boolean luceneUpdaterEnabled = true;

	/** flag indicating that an index-generation is currently running */
	private boolean generatingIndex = false;

	protected int alreadyRunning = 0; // das geht bestimmt irgendwie besser
	private final int maxAlreadyRunningTrys = 20;

	/** all known redundant resource indeces */
	private List<LuceneResourceIndex<R>> resourceIndices;

	/** the index current used by the searcher */
	private LuceneResourceIndex<R> activeIndex;

	/** the index currently updated by this manager */
	protected LuceneResourceIndex<R> updatingIndex;

	/** the queue containing the next indices to be updated */
	private final Queue<LuceneResourceIndex<R>> updateQueue = new LinkedList<LuceneResourceIndex<R>>();

	/** the lucene index searcher */
	private LuceneResourceSearch<R> searcher;

	/** the database manager */
	protected LuceneDBInterface<R> dbLogic;

	/** converts post model objects to lucene documents */
	protected LuceneResourceConverter<R> resourceConverter;

	private LuceneGenerateResourceIndex<R> generator;

	/**
	 * triggers index optimization during next update
	 */
	public void optimizeIndex() {
		final LuceneResourceIndex<R> nextIndex = this.updateQueue.peek();
		if (nextIndex != null) {
			nextIndex.optimizeIndex();
		}
	}

	/**
	 * Get statistics for the active index
	 * 
	 * @return LuceneIndexStatistics for the active index
	 */
	public LuceneIndexStatistics getStatistics() {
		return this.activeIndex.isIndexEnabled() ? this.activeIndex.getStatistics() : null;
	}

	/**
	 * Get statistics for the inactive index
	 * 
	 * @return LuceneIndexStatistics for the inactive index
	 */
	public List<LuceneIndexStatistics> getInactiveIndecesStatistics() {
		final List<LuceneIndexStatistics> inactiveIndecesStatistics = new LinkedList<LuceneIndexStatistics>();

		if (this.updatingIndex != null) {
			inactiveIndecesStatistics.add(this.updatingIndex.getStatistics());
		}

		for (final LuceneResourceIndex<R> index : this.updateQueue) {
			inactiveIndecesStatistics.add(index.getStatistics());
		}

		return inactiveIndecesStatistics;
	}

	/**
	 * updates the index, that is - adds new posts - updates posts, where tag
	 * assignments have changed - removes deleted posts
	 * 
	 * For that, we keep track of the newest tas_id seen during index update.
	 * 
	 * On each update, we query for all posts with greater tas_ids. These Posts
	 * are either new, or belong to posts, where the tag assignments have
	 * changed. We delete all those posts from the index (for implementing the
	 * tag update). Afterwards, all these posts are (re-)inserted.
	 * 
	 * To keep track of deleted posts, we further hold the last log_date t and
	 * query for all content_ids from the log_table with a change_date >=
	 * t-epsilon. These posts are removed from the index together with the
	 * updated posts.
	 */
	protected void updateIndexes() {
		synchronized (this) {
			/*
			 * get next index to update
			 */
			this.updatingIndex = this.updateQueue.poll();
			if (this.updatingIndex == null) {
				// TODO: log no index in update queue
				return;
			}

			// current time stamp for storing as 'lastLogDate' in the index
			// FIXME: get this date from the log_table via
			// 'getContentIdsToDelete'
			final long currentLogDate = System.currentTimeMillis();

			// FIXME: this should be done in the constructor
			// keeps track of the newest tas_id during last index update
			Integer lastTasId = this.updatingIndex.getLastTasId();
			log.debug("lastTasId: " + lastTasId);

			// keeps track of the newest log_date during last index update
			final long lastLogDate = this.updatingIndex.getLastLogDate();

			lastTasId = updateIndex(currentLogDate, lastTasId, lastLogDate);

			/*
			 * commit changes
			 */
			this.updatingIndex.flush();

			/*
			 * update variables
			 */
			this.updatingIndex.setLastLogDate(currentLogDate);
			this.updatingIndex.setLastTasId(lastTasId);
		}

		this.alreadyRunning = 0;
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
		 * 4) remove posts from 1) & 2) from the index and update field
		 * 'lastTasId'
		 */
		for (final LucenePost<R> post : newPosts) {
			contentIdsToDelete.add(post.getContentId());
			lastTasId = Math.max(post.getLastTasId(), lastTasId);
		}

		this.updatingIndex.deleteDocumentsInIndex(contentIdsToDelete);

		/*
		 * 5) add all posts from 1) to the index
		 */
		for (final LucenePost<R> post : newPosts) {
			post.setLastLogDate(new Date(currentLogDate));
			final Document postDoc = this.resourceConverter.readPost(post);
			this.updatingIndex.insertDocument(postDoc);
		}

		return lastTasId;
	}

	/**
	 * reload each registered searcher's index
	 */
	public void reloadIndex() {
		// if lucene updater is disabled or index-generation running, return
		// without doing something
		if (!luceneUpdaterEnabled || generatingIndex) {
			log.debug("lucene updater is disabled by user");
			return;
		}

		// don't run twice at the same time - if something went wrong, delete
		// alreadyRunning
		if ((alreadyRunning > 0) && (alreadyRunning < maxAlreadyRunningTrys)) {
			alreadyRunning++;
			log.warn("reloadIndex - alreadyRunning (" + alreadyRunning + "/" + maxAlreadyRunningTrys + ")");
			return;
		}
		alreadyRunning = 1;
		log.debug("reloadIndex - run and reset alreadyRunning (" + alreadyRunning + "/" + maxAlreadyRunningTrys + ")");

		// do the actual work
		if (this.updatingIndex != null) {
			log.debug("switching from index " + this.activeIndex + " to index " + this.updatingIndex);

			this.setActiveIndex(this.updatingIndex);
			this.updatingIndex = null;

			log.debug("reload search index done");
		} else {
			log.debug("no index to switch");
		}

		this.alreadyRunning = 0;
	}

	/**
	 * update each registered index
	 */
	protected void updateIndex() {
		// if lucene updater is disabled, return without doing something
		if (!luceneUpdaterEnabled) {
			log.debug("updateIndex - lucene updater is disabled");
			alreadyRunning = 0;
			return;
		}

		// don't run twice at the same time - if something went wrong, delete
		// alreadyRunning
		if ((alreadyRunning > 0) && (alreadyRunning < maxAlreadyRunningTrys)) {
			alreadyRunning++;
			log.warn("updateIndex - alreadyRunning (" + alreadyRunning + "/" + maxAlreadyRunningTrys + ")");
			return;
		}
		alreadyRunning = 1;
		log.debug("updateIndex - run and reset alreadyRunning (" + alreadyRunning + "/" + maxAlreadyRunningTrys + ")");

		// do the actual work
		log.debug("update indexes");
		updateIndexes();
		log.debug("update indexes done");
	}

	/**
	 * switches the active index and updates and reloads the index
	 */
	public void updateAndReloadIndex() {
		// do not update index during index-generation
		if (this.generatingIndex) {
			return;
		}

		// update passive index
		updateIndex();

		// make tell searcher to use the updated index
		reloadIndex();
	}

	/**
	 * 
	 */
	public void generateIndex() {
		this.generateIndex(true);
	}

	public void regenerateIndex(int id) {
		regenerateIndex(id, true);
	}

	/**
	 * (re)-generate only one index. While generating the searcher remains
	 * active on a redundant index.
	 * 
	 * @param id
	 * 
	 */
	public void regenerateIndex(int id, boolean async) {
		// allow only one index-generation at a time
		if (this.generatingIndex) {
			return;
		}

		synchronized (this) {
			this.generatingIndex = true;
			// Stop the updating process
			this.setLuceneUpdaterEnabled(false);
			LuceneResourceIndex<R> indexToGenerate = null;
			for (LuceneResourceIndex<R> index : this.getResourceIndeces()) {
				if (index.getStatistics().getIndexId() == id) {
					indexToGenerate = index;
				}
			}
			if (activeIndex.getStatistics().getIndexId() == id) {
				this.setActiveIndex(this.updateQueue.poll());
			} 
			if (indexToGenerate != null) {
				/* the method 'setActiveIndex' will add the old 
				 * activeIndex to the updateQueue. This will
				 * cause that we have a third index after regenerating
				 * a new active one, since the old active one is
				 * added to the queue too */
				updateQueue.remove(indexToGenerate);
				final LuceneGenerateResourceIndex<R> generator = new LuceneGenerateResourceIndex<R>();
				generator.setResourceIndex(indexToGenerate);
				generator.setLogic(this.dbLogic);
				generator.setResourceConverter(this.resourceConverter);
				generator.setCallback(this);

				this.generator = generator;

				if (async) {
					// run in another thread (non blocking)
					new Thread(generator).start();
				} else {
					generator.run();
				}
			} else {
				log.error("There was no index with id " + id + " found.");
				this.generatingIndex = false;
				/* Should it be created? */
			}
		}
	}

	/**
	 * Perform an index-generation with the searcher still active on a redundant
	 * index.
	 * 
	 * @param assync
	 */
	public void generateIndex(final boolean assync) {
		// allow only one index-generation at a time
		if (this.generatingIndex) {
			return;
		}

		// prepare index generation
		synchronized (this) {
			this.generatingIndex = true;

			this.setLuceneUpdaterEnabled(false);

			// get the next index to update for generating new index
			final LuceneResourceIndex<R> resourceIndex = this.updateQueue.poll();

			if (resourceIndex == null) {
				log.error("no index for re-generation found");
				this.generatingIndex = false;
				return;
			}

			final LuceneGenerateResourceIndex<R> generator = new LuceneGenerateResourceIndex<R>();
			generator.setResourceIndex(resourceIndex);
			generator.setLogic(this.dbLogic);
			generator.setResourceConverter(this.resourceConverter);
			generator.setCallback(this);

			this.generator = generator;

			if (assync) {
				// run in another thread (non blocking)
				new Thread(generator).start();
			} else {
				generator.run();
			}
		}
	}

	/**
	 * @return <code>true</code> iff the index manager currently generating a
	 *         index
	 */
	public boolean isGeneratingIndex() {
		return generatingIndex;
	}

	/**
	 * reopen index reader - e.g. after the index has changed on the disc
	 */
	public void resetIndexReader() {
		if (!this.generatingIndex) {
			for (final LuceneResourceIndex<R> index : this.resourceIndices) {
				index.reset();
			}
		}
	}

	/**
	 * spam handling get spam prediction which were missed since last index
	 * update
	 * 
	 * FIXME: this code is due to the old spam-flagging-mechanism it is probably
	 * more efficient to get all un-flagged-posts directly via a join with the
	 * user table
	 */
	private void updatePredictions() {
		// keeps track of the newest log_date during last index update
		final Long lastLogDate = this.updatingIndex.getLastLogDate() - QUERY_TIME_OFFSET_MS;

		// get date of last index update
		final Date fromDate = new Date(lastLogDate);

		final List<User> predictedUsers = this.dbLogic.getPredictionForTimeRange(fromDate);

		// the prediction table holds up to two entries per user
		// - the first entry is the one to consider (ordered descending by date)
		// we keep track of users which appear twice via this set
		final Set<String> alreadyUpdated = new HashSet<String>();
		for (final User user : predictedUsers) {
			if (!alreadyUpdated.contains(user.getName())) {
				alreadyUpdated.add(user.getName());
				/*
				 * flag/unflag spammer, depending on user.getPrediction()
				 */
				log.debug("updating spammer status for user " + user.getName());
				switch (user.getPrediction()) {
				case 0:
					log.debug("unflag non-spammer");
					final List<LucenePost<R>> userPosts = this.getDbLogic().getPostsForUser(user.getName(), Integer.MAX_VALUE, 0);
					// insert new records into index
					if (present(userPosts)) {
						for (final Post<R> post : userPosts) {
							// cache possible pre existing duplicate for
							// deletion
							this.updatingIndex.deleteDocumentForContentId(post.getContentId());
							// cache document for writing
							this.updatingIndex.insertDocument(this.resourceConverter.readPost(post));
						}
					}
					this.updatingIndex.unFlagUser(user.getName());
					break;
				case 1:
					log.debug("flag spammer");
					// remove all docs of the user from the index!
					this.updatingIndex.flagUser(user.getName());
					break;
				}
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
	 * @param dbLogic
	 *            the dbLogic to set
	 */
	public void setDbLogic(final LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	/**
	 * checks, whether the index is readily initialized
	 * 
	 * @return <code>true</code>, if index is ready <code>false</code>,
	 *         otherwise (e.g. if no lucene-index has been generated yet)
	 */
	public boolean isIndexEnabled() {
		if (this.activeIndex.isIndexEnabled())
			return true;
		for (LuceneResourceIndex<R> indices: this.resourceIndices) {
			if (indices.isIndexEnabled())
				return true;
		}
		return false;
	}

	/**
	 * @return the searcher
	 */
	public LuceneResourceSearch<R> getSearcher() {
		return searcher;
	}

	/**
	 * @param searcher
	 *            the searcher to set
	 */
	public void setSearcher(final LuceneResourceSearch<R> searcher) {
		this.searcher = searcher;
	}

	/**
	 * @return the generator
	 */
	public LuceneGenerateResourceIndex<R> getGenerator() {
		return generator;
	}

	/**
	 * @param generator
	 *            the generator to set
	 */
	public void setGenerator(final LuceneGenerateResourceIndex<R> generator) {
		this.generator = generator;
	}

	/**
	 * @return the resourceConverter
	 */
	public LuceneResourceConverter<R> getResourceConverter() {
		return resourceConverter;
	}

	/**
	 * @param resourceConverter
	 *            the resourceConverter to set
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
	 * @param resourceIndeces
	 *            the resourceIndeces to set
	 */
	public void setResourceIndices(final List<LuceneResourceIndex<R>> resourceIndeces) {
		this.resourceIndices = resourceIndeces;
	}

	/**
	 * @param luceneUpdaterEnabled
	 *            the luceneUpdaterEnabled to set
	 */
	public void setLuceneUpdaterEnabled(final boolean luceneUpdaterEnabled) {
		this.luceneUpdaterEnabled = luceneUpdaterEnabled;
		// TODO: remove TODODZ
		if (!this.luceneUpdaterEnabled) {
			log.info("updater disabled by project settings");
		}
	}

	/**
	 * Sets the given index as the new active one. If there was an other active
	 * index before it will be moved to the updateQueue
	 * 
	 * @param activeIndex
	 *            the activeIndex to set
	 */
	public void setActiveIndex(final LuceneResourceIndex<R> activeIndex) {
		final LuceneResourceIndex<R> oldIndex = this.activeIndex;

		this.activeIndex = activeIndex;

		this.searcher.setIndex(this.activeIndex);

		if (oldIndex != null) {
			// add old active index to the update queue
			this.updateQueue.add(oldIndex);
		}
	}

	/**
	 * @return the name of the managed resource
	 */
	public String getResourceName() {
		// all resource indices have the same resource name (they should!)
		return resourceIndices.get(0).getResourceClass().getSimpleName();
	}

	/**
	 * must be called after all properties are set
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		/*
		 * set the first index to the be the active one
		 */
		this.setActiveIndex(this.resourceIndices.get(0));

		/*
		 * all others must be inserted into the update queue
		 */
		for (int i = 1; i < this.resourceIndices.size(); i++) {
			this.updateQueue.add(this.resourceIndices.get(i));
		}
	}

	@Override
	public void generatedIndex(final LuceneResourceIndex<R> index) {
		/*
		 * finished index generation use the new index for the searcher
		 */
		this.setActiveIndex(index);
		this.setLuceneUpdaterEnabled(true);

		/*
		 * enable generating and updating
		 */
		this.generatingIndex = false;
		this.generator = null;
	}

	public List<LuceneResourceIndexInfo> getIndicesInfos() {
		List<LuceneResourceIndexInfo> lrii = new LinkedList<LuceneResourceIndexInfo>();
		
		// First put the active index in the list if it exists
		LuceneResourceIndexInfo indexInfo = getActiveIndexInfo();
		lrii.add(indexInfo);
		// put the inactive indices to the list
		for (LuceneResourceIndex<R> resourceIndex: this.resourceIndices) {
			if (resourceIndex.equals(activeIndex))
				continue;
			boolean isIndexEnabled = resourceIndex.isIndexEnabled();
			indexInfo = new LuceneResourceIndexInfo();
			
			indexInfo.setEnabled(isIndexEnabled);
			indexInfo.setId(resourceIndex.getIndexId());
			
			if (this.isGeneratingIndex() && 
					this.generator != null &&
					(resourceIndex.getIndexId() == this.generator.getGeneratingIndexId())) {
				indexInfo.setGeneratingIndex(true);
				indexInfo.setIndexGenerationProgress(this.getGenerator().getProgressPercentage());
			}
			
			if (resourceIndex.isIndexEnabled()) {
				indexInfo.setIndexStatistics(resourceIndex.getStatistics());
			}
			lrii.add(indexInfo);
		}
		return lrii;
	}
	
	

	private LuceneResourceIndexInfo getActiveIndexInfo() {
		final boolean isIndexEnabled = this.isIndexEnabled();
		final LuceneResourceIndexInfo indexInfo = new LuceneResourceIndexInfo();

		indexInfo.setEnabled(isIndexEnabled);
		indexInfo.setId(this.activeIndex.getIndexId());
		indexInfo.setActive(true);

		if (this.isGeneratingIndex() && 
				this.generator != null &&
				(activeIndex.getIndexId() == this.generator.getGeneratingIndexId())) {
			indexInfo.setGeneratingIndex(true);
			indexInfo.setIndexGenerationProgress(this.getGenerator().getProgressPercentage());
		}

		if (isIndexEnabled) {
			indexInfo.setIndexStatistics(this.getStatistics());
		}
		return indexInfo;
	}

}
