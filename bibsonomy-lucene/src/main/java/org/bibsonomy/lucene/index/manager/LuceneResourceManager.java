/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.lucene.index.manager;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.bibsonomy.es.IndexType;
import org.bibsonomy.es.SharedResourceIndexUpdater;
import org.bibsonomy.es.UpdatePlugin;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LuceneIndexInfo;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.lucene.util.generator.LuceneGenerateResourceIndex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;

/**
 * class for maintaining the lucene index
 * 
 * - regularly update the index by looking for new posts - asynchronously handle
 * requests for flagging/unflagging of spam users
 * 
 * @author fei
 * @param <R>
 *            the resource to manage
 */
public class LuceneResourceManager<R extends Resource> implements GenerateIndexCallback<R> {
	
	/**
	 * this constant determines the difference of docs between the lucene index and the DB that will be tolerated
	 * until the index is handled as incorrect.
	 */
	private static final int DOC_TOLERANCE = 1000;

	private static final Log log = LogFactory.getLog(LuceneResourceManager.class);

	/**
	 * constant for querying for all posts which have been deleted since the
	 * last index update
	 */
	protected static final long QUERY_TIME_OFFSET_MS = 30 * 1000;

	/** flag indicating whether to update the index or not */
	private boolean luceneUpdaterEnabled = true;

	/** flag indicating that an index-generation is currently running */
	protected boolean generatingIndex = false;

	private int alreadyRunning = 0; // das geht bestimmt irgendwie besser
	private final int maxAlreadyRunningTrys = 20;

	/** all known redundant resource indeces */
	private List<LuceneResourceIndex<R>> resourceIndices;

	/** the index current used by the searcher */
	private LuceneResourceIndex<R> activeIndex;

	/** the index currently updated by this manager */
	protected LuceneResourceIndex<R> updatingIndex;
	
	/**
	 * for shared resources index
	 */
	// FIXME: object lifecycle is unclear
	protected SharedResourceIndexUpdater<R> sharedIndexUpdater;

	
	/**
	 * The plugin for indexUpdater
	 */
	protected UpdatePlugin plugin;

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
	@SuppressWarnings({ "boxing", "unchecked" })
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
			Integer lastTasId = this.updatingIndex.getLastTasId(); // FIXME
			log.debug("lastTasId: " + lastTasId);

			// keeps track of the newest log_date during last index update
			final long lastLogDate = this.updatingIndex.getLastLogDate();
			
			if (plugin != null) {
				//Shared index updater
				this.sharedIndexUpdater =  (SharedResourceIndexUpdater<R>) plugin.createUpdater(this.getResourceName());
				//if there is no shared resource index it can be null
				if(this.sharedIndexUpdater!=null){
					Integer lastTasIdSharedIndex = this.sharedIndexUpdater.getLastTasId();
					final long lastLogDateSharedIndex =  this.sharedIndexUpdater.getLastLogDate();
					Integer newLastTasId;
					Integer newLastTasIdSharedIndex;
					if ((lastLogDate == lastLogDateSharedIndex) && (lastTasId == lastTasIdSharedIndex)){
						newLastTasId = this.updateIndex(currentLogDate, lastTasId, lastLogDateSharedIndex, IndexType.BOTH);
						newLastTasIdSharedIndex = newLastTasId;
					} else {
						newLastTasId = this.updateIndex(currentLogDate, lastTasId, lastLogDate, IndexType.LUCENE);
						newLastTasIdSharedIndex =  this.updateIndex(currentLogDate, lastTasIdSharedIndex, lastLogDateSharedIndex, IndexType.ELASTICSEARCH);
					}
					
					if (newLastTasIdSharedIndex != lastTasIdSharedIndex) {
						this.sharedIndexUpdater.setSystemInformation(lastTasIdSharedIndex, new Date(currentLogDate));
						this.sharedIndexUpdater.setLastLogDate(currentLogDate);
						this.sharedIndexUpdater.setLastTasId(newLastTasIdSharedIndex);
						this.sharedIndexUpdater.flush();
					}
					if (newLastTasId != lastTasId) {
						this.updatingIndex.setLastLogDate(currentLogDate);
						this.updatingIndex.setLastTasId(newLastTasId);
						this.updatingIndex.flush();
					}
				} else {
					this.runLuceneIndexUpdate(currentLogDate, lastTasId, lastLogDate, IndexType.LUCENE);
				}
				
			} else {
				this.runLuceneIndexUpdate(currentLogDate, lastTasId, lastLogDate, IndexType.LUCENE);
			}
		}
		this.alreadyRunning = 0;
	}
		
	@SuppressWarnings("boxing")
	private void runLuceneIndexUpdate(final long currentLogDate, Integer lastTasId,final long lastLogDate, IndexType indexType){
		lastTasId = this.updateIndex(currentLogDate, lastTasId, lastLogDate, indexType);
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

	/**
	 * udpates the index for the current log data and last tas id and last log date
	 * @param currentLogDate
	 * @param lastTasId
	 * @param lastLogDate
	 * @param indexType 
	 * @return the lastTasId found by generating the new index
	 */
	@SuppressWarnings({ "boxing", "unchecked" })
	protected int updateIndex(final long currentLogDate, int lastTasId, final long lastLogDate, final IndexType indexType) {
		/*
		 * 1) flag/unflag spammer
		 */
		this.updatePredictions(indexType, lastLogDate);

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
		
		/*lastTasIdlastTasId
		 * 5) add all posts from 1) to the index
		 */
		if (IndexType.LUCENE == indexType) {
			this.updatingIndex.deleteDocumentsInIndex(contentIdsToDelete);

			for (final LucenePost<R> post : newPosts) {
				post.setLastLogDate(new Date(currentLogDate));
				final Document postDoc = (Document)this.resourceConverter.readPost(post, indexType);
				this.updatingIndex.insertDocument(postDoc);					
			}
		} else if (IndexType.ELASTICSEARCH == indexType) {
			this.sharedIndexUpdater.setContentIdsToDelete(contentIdsToDelete);

			for (final LucenePost<R> post : newPosts) {
				if (post.getGroups().contains(GroupUtils.getPublicGroup())) {
					post.setLastLogDate(new Date(currentLogDate));
					final Map<String, Object> postDoc = (Map<String, Object>)this.resourceConverter.readPost(post, indexType);
					this.sharedIndexUpdater.insertDocument(postDoc);
				}
			}
		} else if (IndexType.BOTH == indexType) {
			this.updatingIndex.deleteDocumentsInIndex(contentIdsToDelete);
			this.sharedIndexUpdater.setContentIdsToDelete(contentIdsToDelete);

			for (final LucenePost<R> post : newPosts) {
				post.setLastLogDate(new Date(currentLogDate));
				//sets the system informations for update
				final Document postDoc = (Document)this.resourceConverter.readPost(post, IndexType.LUCENE);
				if (post.getGroups().contains(GroupUtils.getPublicGroup())) {
					final Map<String, Object> postJsonDoc = (Map<String, Object>)this.resourceConverter.readPost(post, IndexType.ELASTICSEARCH);
					this.sharedIndexUpdater.insertDocument(postJsonDoc);
				}
				this.updatingIndex.insertDocument(postDoc);					
			}
		}

		return lastTasId;
	}

	/**
	 * reload each registered searcher's index
	 */
	public void reloadIndex() {
		// if lucene updater is disabled or index-generation running, return
		// without doing something
		if (!this.luceneUpdaterEnabled || this.generatingIndex) {
			log.debug("lucene updater is disabled by user");
			return;
		}

		// don't run twice at the same time - if something went wrong, delete
		// alreadyRunning
		if ((this.alreadyRunning > 0) && (this.alreadyRunning < this.maxAlreadyRunningTrys)) {
			this.alreadyRunning++;
			log.warn("reloadIndex - alreadyRunning (" + this.alreadyRunning + "/" + this.maxAlreadyRunningTrys + ")");
			return;
		}
		this.alreadyRunning = 1;
		log.debug("reloadIndex - run and reset alreadyRunning (" + this.alreadyRunning + "/" + this.maxAlreadyRunningTrys + ")");

		// do the actual work, check if there IS a index to switch and if it is correct
		if (this.updatingIndex != null && isIndexCorrect(this.updatingIndex)) {
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
		if (!this.luceneUpdaterEnabled) {
			log.debug("updateIndex - lucene updater is disabled");
			this.alreadyRunning = 0;
			return;
		}

		// don't run twice at the same time - if something went wrong, delete
		// alreadyRunning
		if ((this.alreadyRunning > 0) && (this.alreadyRunning < this.maxAlreadyRunningTrys)) {
			this.alreadyRunning++;
			log.warn("updateIndex - alreadyRunning (" + this.alreadyRunning + "/" + this.maxAlreadyRunningTrys + ")");
			return;
		}
		this.alreadyRunning = 1;
		log.debug("updateIndex - run and reset alreadyRunning (" + this.alreadyRunning + "/" + this.maxAlreadyRunningTrys + ")");

		// do the actual work
		log.debug("update indexes");
		this.updateIndexes();
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
		this.updateIndex();

		// make tell searcher to use the updated index
		this.reloadIndex();
	}

	/**
	 * Generates all Indices for this resource, single threaded
	 */
	public void generateIndex() {
//		this.generateIndex(true, 1);
		for (int i = 0; i < this.resourceIndices.size(); ++i) {
			this.regenerateIndex(i, false);
		}
	}

	/**
	 * regenerates the resource index with the specified id
	 * @param id
	 */
	public void regenerateIndex(final int id) {
		this.regenerateIndex(id, true);
	}
	
	/**
	 * This method determines if a given index isn't broken, i.e. that its documents are up to date
	 * and that no documents are missing.
	 * The main purpose of this method is in the updating process to prevent switching to a incomplete
	 * or corrupted index
	 * @param index
	 * @return
	 */
	private boolean isIndexCorrect(LuceneResourceIndex<R> index) {
		int noDocsInLucene = index.getStatistics().getNumDocs();
		/*
		 * first check if there ARE documents and don't do a count on the database
		 */
		if (noDocsInLucene < 1) {
			return false;
		}
		int noPostInDB = dbLogic.getNumberOfPosts();
		if (Math.abs(noDocsInLucene - noPostInDB) > DOC_TOLERANCE) {
			return false;
		}
		return true;
	}

	/**
	 * (re)-generate only one index. While generating the searcher remains
	 * active on a redundant index.
	 * 
	 * @param id
	 * @param async 
	 */
	public void regenerateIndex(final int id, final boolean async) {
		// allow only one index-generation at a time
		if (this.generatingIndex) {
			return;
		}

		synchronized (this) {
			this.generatingIndex = true;
			// Stop the updating process
			this.setLuceneUpdaterEnabled(false);
			LuceneResourceIndex<R> indexToGenerate = null;
			for (final LuceneResourceIndex<R> index : this.getResourceIndeces()) {
				if (index.getIndexId() == id) {
					indexToGenerate = index;
					break;
				}
			}
			if (this.activeIndex.getStatistics().getIndexId() == id) {
				this.setActiveIndex(this.updateQueue.poll());
			} 
			if (indexToGenerate != null) {
				/* the method 'setActiveIndex' will add the old 
				 * activeIndex to the updateQueue. This will
				 * cause that we have a third index after regenerating
				 * a new active one, since the old active one is
				 * added to the queue too */
				this.updateQueue.remove(indexToGenerate);
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
				log.warn("There was no index with id " + id + " found.");
			
				this.generatingIndex = false;
			}
		}
	}

	/**
	 * Perform an index-generation with the searcher still active on a redundant
	 * index.
	 * 
	 * @param assync
	 * @param numberOfThreads 
	 */
	public void generateIndex(final boolean assync, final int numberOfThreads) {
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
		return this.generatingIndex;
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
	 * @param searchType 
	 */
	@SuppressWarnings({ "unchecked", "boxing" })
	private void updatePredictions(IndexType searchType, final long lastLogDate) {
		// keeps track of the newest log_date during last index update
		// final long lastLogDate = lastLogDate - QUERY_TIME_OFFSET_MS;
		// get date of last index update
		final Date fromDate = new Date(lastLogDate - QUERY_TIME_OFFSET_MS);

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
					if (searchType == IndexType.LUCENE) {
						if (present(userPosts)) {
							for (final Post<R> post : userPosts) {
								// cache possible pre existing duplicate for
								// deletion
								this.updatingIndex.deleteDocumentForContentId(post.getContentId());
								// cache document for writing
								this.updatingIndex.insertDocument((Document) this.resourceConverter.readPost(post, searchType));
							}
						}
						this.updatingIndex.unFlagUser(user.getName());
					} else if (searchType == IndexType.ELASTICSEARCH) {
						if (present(userPosts)) {
							for (final Post<R> post : userPosts) {
								// cache possible pre existing duplicate for
								// deletion
								this.sharedIndexUpdater.deleteDocumentForContentId(post.getContentId());
								// cache document for writing
								if (post.getGroups().contains(GroupUtils.getPublicGroup())) {
									this.sharedIndexUpdater.insertDocument((Map<String, Object>) this.resourceConverter.readPost(post, searchType));
								}
							}
						}
						this.sharedIndexUpdater.unFlagUser(user.getName());
					} else if (searchType == IndexType.BOTH) {
						for (final Post<R> post : userPosts) {
							this.updatingIndex.deleteDocumentForContentId(post.getContentId());
							this.updatingIndex.insertDocument((Document) this.resourceConverter.readPost(post, IndexType.LUCENE));
							this.sharedIndexUpdater.deleteDocumentForContentId(post.getContentId());
							if (post.getGroups().contains(GroupUtils.getPublicGroup())) {
								this.sharedIndexUpdater.insertDocument((Map<String, Object>) this.resourceConverter.readPost(post, IndexType.ELASTICSEARCH));
							}
						}
					}
					break;
				case 1:
					log.debug("flag spammer");
					
					// remove all docs of the user from the index!
					if (searchType == IndexType.LUCENE) {
						this.updatingIndex.flagUser(user.getName());	
					} else if (searchType == IndexType.ELASTICSEARCH) {
						this.sharedIndexUpdater.flagUser(user.getName());	

					} else if(searchType == IndexType.BOTH) {
						this.updatingIndex.flagUser(user.getName());	
						this.sharedIndexUpdater.flagUser(user.getName());	
					}
						
					break;
				}
			}
		}
	}

	/**
	 * @return the dbLogic
	 */
	public LuceneDBInterface<R> getDbLogic() {
		return this.dbLogic;
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
		if (this.activeIndex.isIndexEnabled()) {
			return true;
		}
		for (final LuceneResourceIndex<R> indices: this.resourceIndices) {
			if (indices.isIndexEnabled()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the searcher
	 */
	public LuceneResourceSearch<R> getSearcher() {
		return this.searcher;
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
		return this.generator;
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
		return this.resourceConverter;
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
	 * @return the plugin
	 */
	public UpdatePlugin getPlugin() {
		return this.plugin;
	}

	/**
	 * @param plugin the plugin to set
	 */
	public void setPlugin(UpdatePlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return the name of the managed resource
	 */
	public String getResourceName() {
		// all resource indices have the same resource name (they should!)
		return this.resourceIndices.get(0).getResourceClass().getSimpleName();
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
		
		/*
		 * Check if there old is an tmp folder from an previous
		 * index generation left
		 */
		File tmpIndexPath = new File(activeIndex.getIndexPath()+LuceneGenerateResourceIndex.TMP_INDEX_SUFFIX);
		if (tmpIndexPath.exists()) {
			for (File file: tmpIndexPath.listFiles()) {
				file.delete();
			}
			tmpIndexPath.delete();
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
	
	/**
	 * @return	a list of {@link LuceneIndexInfo} for each managed resource index
	 * 			of this manager
	 */
	public List<LuceneIndexInfo> getIndicesInfos() {
		final List<LuceneIndexInfo> lrii = new LinkedList<LuceneIndexInfo>();
		
		// First put the active index in the list if it exists
		LuceneIndexInfo indexInfo;
		
		// put the inactive indices to the list
		for (final LuceneResourceIndex<R> resourceIndex: this.resourceIndices) {
			indexInfo = new LuceneIndexInfo();
			final boolean isIndexEnabled = resourceIndex.isIndexEnabled();
			indexInfo.setCorrect(isIndexCorrect(resourceIndex));
			indexInfo.setBasePath(resourceIndex.getIndexPath());
			indexInfo.setEnabled(isIndexEnabled);
			indexInfo.setId(resourceIndex.getIndexId());
			if (resourceIndex.equals(this.activeIndex)) {
				indexInfo.setActive(true);
				if (isIndexEnabled) {
					indexInfo.setIndexStatistics(this.getStatistics());
				}
			}
			else {
				if (resourceIndex.isIndexEnabled()) {
					indexInfo.setIndexStatistics(resourceIndex.getStatistics());
				}
			}
			
			if (this.isGeneratingIndex() && 
					(this.generator != null) &&
					(resourceIndex.getIndexId() == this.generator.getGeneratingIndexId())) {
				indexInfo.setGeneratingIndex(true);
				indexInfo.setIndexGenerationProgress(this.getGenerator().getProgressPercentage());
			}
			lrii.add(indexInfo);
		}
		return lrii;
	}

}
