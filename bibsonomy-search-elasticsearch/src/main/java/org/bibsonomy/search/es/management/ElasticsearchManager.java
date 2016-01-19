/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.management;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.management.SearchIndexManager;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.model.SearchIndexStatistics;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.util.Sets;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * manager for Elasticsearch
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchManager<R extends Resource> implements SearchIndexManager<R> {
	private static final Log log = LogFactory.getLog(ElasticsearchManager.class);
	
	/** how many posts should be retrieved from the database */
	public static final int SQL_BLOCKSIZE = 5000;
	private static final long QUERY_TIME_OFFSET_MS = 1000;
	
	private abstract class AbstractSearchIndexGenerationTask implements Callable<Void> {
		private final ElasticsearchIndexGenerator<R> generator;
		private final ElasticsearchIndex<R> newIndex;

		/**
		 * @param generator
		 * @param newIndex
		 */
		private AbstractSearchIndexGenerationTask(ElasticsearchIndexGenerator<R> generator, ElasticsearchIndex<R> newIndex) {
			this.generator = generator;
			this.newIndex = newIndex;
		}
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public final Void call() throws Exception {
			try {
				ElasticsearchManager.this.currentGenerator = this.generator;
				this.generator.generateIndex();
				this.indexGenerated(this.newIndex);
			} catch (final Exception e) {
				log.error("error while generating index", e);
			} finally {
				ElasticsearchManager.this.currentGenerator = null;
				ElasticsearchManager.this.generatorLock.release();
			}
			
			return null;
		}

		/**
		 * @param generatedIndex
		 */
		protected abstract void indexGenerated(ElasticsearchIndex<R> generatedIndex);
	}
	
	/**
	 * task for index regeneration
	 * @author dzo
	 */
	private final class ElasticSearchIndexRegenerationTask extends AbstractSearchIndexGenerationTask {
		private String toRegenerateIndexName;

		/**
		 * @param generator
		 * @param newIndex
		 * @param toRegenerateIndexName
		 */
		private ElasticSearchIndexRegenerationTask(ElasticsearchIndexGenerator<R> generator, ElasticsearchIndex<R> newIndex, final String toRegenerateIndexName) {
			super(generator, newIndex);
			this.toRegenerateIndexName = toRegenerateIndexName;
		}
		
		/* (non-Javadoc)
		 * @see org.bibsonomy.search.es.management.ElasticsearchManager.ElasticSearchIndexGenerationTask#indexGenerated(org.bibsonomy.search.es.management.ElasticsearchIndex)
		 */
		@Override
		protected void indexGenerated(ElasticsearchIndex<R> generatedIndex) {
			ElasticsearchManager.this.activateNewIndex(generatedIndex, this.toRegenerateIndexName);
		}
	}
	
	/**
	 * task for index generation
	 * @author dzo
	 */
	private class ElasticSearchIndexGenerationTask extends AbstractSearchIndexGenerationTask {
		/**
		 * @param generator
		 * @param newIndex
		 */
		private ElasticSearchIndexGenerationTask(ElasticsearchIndexGenerator<R> generator, ElasticsearchIndex<R> newIndex) {
			super(generator, newIndex);
		}

		/* (non-Javadoc)
		 * @see org.bibsonomy.search.es.management.ElasticsearchManager.AbstractSearchIndexGenerationTask#indexGenerated(org.bibsonomy.search.es.management.ElasticsearchIndex)
		 */
		@Override
		protected void indexGenerated(final ElasticsearchIndex<R> generatedIndex) {
			ElasticsearchManager.this.activateNewIndex(generatedIndex, null);
		}
	}
	
	/** iff <code>true</code> the update of the index is disabled */
	private boolean updateEnabled;
	
	/** the client to use for all interaction with elasticsearch */
	protected final ESClient client;
	
	private final URI systemURI;
	
	private final Semaphore updateLock = new Semaphore(1);
	
	private final Semaphore generatorLock = new Semaphore(1);
	private ElasticsearchIndexGenerator<R> currentGenerator;
	private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	
	/** access to the main database */
	protected final SearchDBInterface<R> inputLogic;
	
	/** mappers, converters, */
	protected final ElasticsearchIndexTools<R> tools;
	
	/**
	 * @param updateEnabled 
	 * @param client
	 * @param systemURI
	 * @param inputLogic
	 * @param tools
	 */
	public ElasticsearchManager(final boolean updateEnabled, ESClient client, URI systemURI, SearchDBInterface<R> inputLogic, ElasticsearchIndexTools<R> tools) {
		super();
		this.updateEnabled = updateEnabled;
		this.client = client;
		this.systemURI = systemURI;
		this.inputLogic = inputLogic;
		this.tools = tools;
	}

	/**
	 * generates a new index for the resource
	 * @throws IndexAlreadyGeneratingException
	 */
	@Override
	public void generateIndex() throws IndexAlreadyGeneratingException {
		generateIndex(true);
	}

	/**
	 * @param async 
	 * @throws IndexAlreadyGeneratingException
	 */
	protected void generateIndex(final boolean async) throws IndexAlreadyGeneratingException {
		if (!this.generatorLock.tryAcquire()) {
			throw new IndexAlreadyGeneratingException();
		}
		final String newIndexName = ElasticsearchUtils.getIndexNameWithTime(this.systemURI, this.tools.getResourceType());
		final ElasticsearchIndex<R> newIndex = new ElasticsearchIndex<>(newIndexName);
		
		final ElasticsearchIndexGenerator<R> generator = new ElasticsearchIndexGenerator<>(newIndex, this.inputLogic, this.client, this.tools);
		
		final ElasticSearchIndexGenerationTask task = new ElasticSearchIndexGenerationTask(generator, newIndex);
		this.executeTask(async, task);
	}

	/**
	 * @param async
	 * @param task
	 */
	private void executeTask(final boolean async, final AbstractSearchIndexGenerationTask task) {
		if (async) {
			this.executorService.submit(task);
		} else {
			try {
				task.call();
			} catch (Exception e) {
				log.error("error while running synchronous generation task.", e);
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * @param indexNameToReplace
	 * @throws IndexAlreadyGeneratingException 
	 */
	@Override
	public void regenerateIndex(final String indexNameToReplace) throws IndexAlreadyGeneratingException {
		regenerateIndex(indexNameToReplace, true);
	}
	
	/**
	 * @param indexNameToReplace
	 * @param async
	 * @throws IndexAlreadyGeneratingException
	 */
	protected void regenerateIndex(final String indexNameToReplace, final boolean async) throws IndexAlreadyGeneratingException {
		if (!this.generatorLock.tryAcquire()) {
			throw new IndexAlreadyGeneratingException();
		}
		
		final String newIndexName = ElasticsearchUtils.getIndexNameWithTime(this.systemURI, this.tools.getResourceType());
		final ElasticsearchIndex<R> newIndex = new ElasticsearchIndex<>(newIndexName);
		
		final ElasticsearchIndexGenerator<R> generator = new ElasticsearchIndexGenerator<>(newIndex, this.inputLogic, this.client, this.tools);
		
		final ElasticSearchIndexRegenerationTask task = new ElasticSearchIndexRegenerationTask(generator, newIndex, indexNameToReplace);
		this.executeTask(async, task);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexManager#regenerateAllIndices()
	 */
	@Override
	public void regenerateAllIndices() {
		try {
			final String activeIndex = this.client.getIndexNameForAlias(this.getActiveLocalAlias());
			if (present(activeIndex)) {
				this.regenerateIndex(activeIndex, false);
			} else {
				this.generateIndex(false);
			}
			final String currentActiveIndex = this.client.getIndexNameForAlias(this.getActiveLocalAlias());
			final String currentInactiveIndex = this.client.getIndexNameForAlias(this.getInactiveLocalAlias());
			
			final String secondIndexToRegenerate;
			if (present(activeIndex) && !activeIndex.equals(currentActiveIndex)) {
				secondIndexToRegenerate = currentActiveIndex;
			} else {
				secondIndexToRegenerate = currentInactiveIndex;
			}
			
			if (present(secondIndexToRegenerate)) {
				this.regenerateIndex(secondIndexToRegenerate, false);
			} else {
				this.generateIndex(false);
			}
		} catch (final IndexAlreadyGeneratingException e) {
			log.error("error ", e);
		}
	}
	
	/**
	 * @param 	newIndex
	 * @param 	indexToDelete which index should be deleted;
	 * 			<code>null</code> iff no preference
	 */
	protected void activateNewIndex(ElasticsearchIndex<R> newIndex, String indexToDelete) {
		try {
			this.updateLock.acquire();
			
			/*
			 * change alias:
			 * 1. new index will be the new active index
			 * 2. old active index will be the new inactive index
			 * 3. if present the inactive or indexToDelete index will be deleted
			 */
			final String localActiveAlias = this.getActiveLocalAlias();
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final String activeIndexName = this.client.getIndexNameForAlias(localActiveAlias);
			final String inactiveIndexName = this.client.getIndexNameForAlias(localInactiveAlias);
			// set the preferedIndexToDelete to the inactive one iff no index specified
			if (!present(indexToDelete)) {
				indexToDelete = inactiveIndexName;
			}
			
			final Set<Pair<String, String>> aliasesToAdd = new HashSet<>();
			aliasesToAdd.add(new Pair<>(newIndex.getIndexName(), localActiveAlias));
			
			final Set<Pair<String, String>> aliasesToRemove = new HashSet<>();
			// only set the alias if the index should not be deleted
			final boolean peferedDeletedActiveIndex = present(activeIndexName) && activeIndexName.equals(indexToDelete);
			if (present(activeIndexName)) {
				// remove active alias from the current active index
				aliasesToRemove.add(new Pair<>(activeIndexName, localActiveAlias));
				// we use the index as inactive index if we do not want to delete it
				if (!peferedDeletedActiveIndex) {
					aliasesToAdd.add(new Pair<>(activeIndexName, localInactiveAlias));
				}
			}
			
			// only remove the alias if the other index should be deleted
			if (present(inactiveIndexName) && !peferedDeletedActiveIndex) {
				aliasesToRemove.add(new Pair<>(inactiveIndexName, localInactiveAlias));
			}
			
			this.client.updateAliases(aliasesToAdd, aliasesToRemove);
			
			if (present(indexToDelete)) {
				this.client.deleteIndex(indexToDelete);
			}
		} catch (InterruptedException e) {
			log.error("can't acquire lock to update aliases", e);
		} finally {
			this.updateLock.release();
		}
	}

	/**
	 * @return informations about the indices managed by this manager
	 */
	@Override
	public List<SearchIndexInfo> getIndexInformations() {
		final List<SearchIndexInfo> infos = new LinkedList<>();
		try {
			final String localActiveAlias = this.getActiveLocalAlias();
			final SearchIndexInfo searchIndexInfo = getIndexInfoForIndex(localActiveAlias, SearchIndexState.ACTIVE, true);
			infos.add(searchIndexInfo);
		} catch (final IndexNotFoundException e) {
			// ignore
		}
		
		try {
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final SearchIndexInfo searchIndexInfoInactive = getIndexInfoForIndex(localInactiveAlias, SearchIndexState.INACTIVE, true);
			infos.add(searchIndexInfoInactive);
		} catch (final IndexNotFoundException e) {
			// ignore
		}
		
		if (this.currentGenerator != null) {
			try {
				final String indexName = this.currentGenerator.getIndex().getIndexName();
				final SearchIndexInfo searchIndexInfoGeneratingIndex = getIndexInfoForIndex(indexName, SearchIndexState.GENERATING, false);
				searchIndexInfoGeneratingIndex.setIndexGenerationProgress(this.currentGenerator.getProgress());
				searchIndexInfoGeneratingIndex.setId(indexName);
				infos.add(searchIndexInfoGeneratingIndex);
			} catch (final IndexNotFoundException e) {
				// ignore
			}
		}
		
		// TODO: include indices that could not be generated (dead bodies)
		
		return infos;
	}

	/**
	 * @param indexName
	 * @param state 
	 * @param loadSyncState 
	 * @return
	 */
	private SearchIndexInfo getIndexInfoForIndex(final String indexName, SearchIndexState state, boolean loadSyncState) {
		final SearchIndexInfo searchIndexInfo = new SearchIndexInfo();
		searchIndexInfo.setState(state);
		searchIndexInfo.setId(this.client.getIndexNameForAlias(indexName));
		
		if (loadSyncState) {
			searchIndexInfo.setSyncState(this.client.getSearchIndexStateForIndex(indexName));
		}
		
		final SearchIndexStatistics statistics = new SearchIndexStatistics();
		final long count = this.client.getDocumentCount(indexName, this.tools.getResourceTypeAsString(), null);
		statistics.setNumberOfDocuments(count);
		searchIndexInfo.setStatistics(statistics);
		return searchIndexInfo;
	}

	/**
	 * update the inactive index
	 */
	@Override
	public void updateIndex() {
		if (!this.updateEnabled) {
			log.debug("skipping updating index, update disabled");
			return;
		}
		
		if (!this.updateLock.tryAcquire()) {
			log.warn("Another update in progress. Skipping update.");
			return;
		}
		
		try {
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final SearchIndexSyncState oldState = this.client.getSearchIndexStateForIndex(localInactiveAlias);
			final SearchIndexSyncState targetState = this.inputLogic.getDbState();
			
			final int oldLastTasId = oldState.getLast_tas_id().intValue();
			int newLastTasId = oldLastTasId;
			
			/*
			 * 1) flag/unflag spammer if the index existed before
			 */
			if (oldState.getLast_log_date() != null) {
				this.updatePredictions(localInactiveAlias, oldState.getLast_log_date());
			}
			
			/*
			 * 2) remove old deleted or updated posts
			 */
			if (oldState.getLast_log_date() != null) {
				final List<Integer> contentIdsToDelete = this.inputLogic.getContentIdsToDelete(new Date(oldState.getLast_log_date().getTime() - QUERY_TIME_OFFSET_MS));
				
				
				final Set<String> idsToDelete = new HashSet<>();
				for (final Integer contentId : contentIdsToDelete) {
					final String indexID = ElasticsearchUtils.createElasticSearchId(contentId.intValue());
					idsToDelete.add(indexID);
				}
				
				this.client.deleteDocuments(localInactiveAlias, this.tools.getResourceTypeAsString(), idsToDelete);
			}
	
			/*
			 * 3) add new and updated posts to the index
			 */
			log.debug("inserting new/updated posts into " + localInactiveAlias);
			final Map<String, Map<String, Object>> convertedPosts = new HashMap<>();
			List<SearchPost<R>> newPosts;
			int offset = 0;
			int totalCountNewPosts = 0;
			do {
				newPosts = this.inputLogic.getNewPosts(oldLastTasId, SearchDBInterface.SQL_BLOCKSIZE, offset);
				for (final SearchPost<R> post : newPosts) {
					final Map<String, Object> convertedPost = this.tools.getConverter().convert(post);
					
					final String id = ElasticsearchUtils.createElasticSearchId(post.getContentId().intValue());
					
					convertedPosts.put(id, convertedPost);
					newLastTasId = Math.max(post.getLastTasId().intValue(), newLastTasId);
				}
				
				if (convertedPosts.size() >= SearchDBInterface.SQL_BLOCKSIZE / 2) {
					this.clearQueue(localInactiveAlias, convertedPosts);
				}
				
				totalCountNewPosts += newPosts.size();
				offset += SearchDBInterface.SQL_BLOCKSIZE;
			} while (newPosts.size() == SearchDBInterface.SQL_BLOCKSIZE);
			
			if (present(convertedPosts)) {
				this.clearQueue(localInactiveAlias, convertedPosts);
			}
			
			log.debug("inserted " + totalCountNewPosts + " new/updated posts into " + localInactiveAlias);
			
			this.updateResourceSpecificProperties(localInactiveAlias, oldState, targetState);
			
			// 4) update the index state
			try {
				SearchIndexSyncState newState = new SearchIndexSyncState(oldState);
				newState.setLast_log_date(targetState.getLast_log_date());
				newState.setLast_tas_id(Integer.valueOf(newLastTasId));
				newState.setLastPersonChangeId(targetState.getLastPersonChangeId());
				this.updateIndexState(localInactiveAlias, newState);
			} catch (final RuntimeException e) {
				this.updateIndexState(localInactiveAlias, oldState);
				throw e;
			} catch (final Exception e) {
				this.updateIndexState(localInactiveAlias, oldState);
				throw new RuntimeException(e);
			}
			
			if (log.isDebugEnabled()) {
				log.debug("posts updated for " + localInactiveAlias);
			}
			
			this.switchActiveAndInactiveIndex();
		} catch (final IndexNotFoundException e) {
			log.error("Can't update " + this.tools.getResourceTypeAsString() + " index. No inactive index available.");
		} finally {
			this.updateLock.release();
		}
	}

	/**
	 * @param localInactiveAlias
	 * @param convertedPosts
	 */
	private void clearQueue(final String localInactiveAlias, final Map<String, Map<String, Object>> convertedPosts) {
		/*
		 * maybe we are updating an updated post in es
		 */
		this.client.updateOrCreateDocuments(localInactiveAlias, this.tools.getResourceTypeAsString(), convertedPosts);
		convertedPosts.clear();
	}
	
	/**
	 * switch active and inactive index
	 */
	private void switchActiveAndInactiveIndex() {
		final String localActiveAlias = this.getActiveLocalAlias();
		final String localInactiveAlias = this.getInactiveLocalAlias();
		final String activeIndexName = this.client.getIndexNameForAlias(localActiveAlias);
		final String inactiveIndexName = this.client.getIndexNameForAlias(localInactiveAlias);
		
		@SuppressWarnings("unchecked")
		final Set<Pair<String, String>> aliasesToAdd = Sets.asSet(
			new Pair<>(inactiveIndexName, localActiveAlias),
			new Pair<>(activeIndexName, localInactiveAlias)
		);
		@SuppressWarnings("unchecked")
		final Set<Pair<String, String>> aliasesToRemove = Sets.asSet(
			new Pair<>(activeIndexName, localActiveAlias),
			new Pair<>(inactiveIndexName, localInactiveAlias)
		);
		
		log.debug("switching index (current state I=" + inactiveIndexName + ", A=" + activeIndexName);
		// update the aliases (atomic, see elastic search docu)
		this.client.updateAliases(aliasesToAdd, aliasesToRemove);
	}
	
	/**
	 * @param indexName
	 */
	@Override
	public void deleteIndex(final String indexName) {
		if (!this.updateLock.tryAcquire()) {
			throw new IllegalStateException("You cannot delete indices while update is in progress.");
		}
		
		try {
			// check if index is the current active one
			final String currentActiveIndexName = this.client.getIndexNameForAlias(this.getActiveLocalAlias());
			final String inactiveIndex = this.client.getIndexNameForAlias(this.getInactiveLocalAlias());
			if (currentActiveIndexName.equals(indexName) && present(inactiveIndex)) {
				this.switchActiveAndInactiveIndex();
			}
			
			this.client.deleteIndex(indexName);
		} finally {
			this.updateLock.release();
		}
	}
	
	/**
	 * @return
	 */
	private String getInactiveLocalAlias() {
		return ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), this.systemURI, false);
	}

	/**
	 * @return
	 */
	private String getActiveLocalAlias() {
		return ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), this.systemURI, true);
	}

	/**
	 * @param indexName
	 * @param oldState
	 */
	private void updateIndexState(String indexName, SearchIndexSyncState state) {
		final Map<String, Object> values = ElasticsearchUtils.serializeSearchIndexState(state);
		
		this.client.insertNewDocument(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, ESConstants.SYSTEM_INFO_INDEX_TYPE, values);
	}

	/**
	 * @param oldState
	 * @param targetState
	 * @param indexName
	 */
	protected void updateResourceSpecificProperties(final String indexName, final SearchIndexSyncState oldState, SearchIndexSyncState targetState) {
		// noop
	}
	
	/**
	 * spam handling get spam prediction which were missed since last index
	 * update
	 * 
	 * FIXME: this code is due to the old spam-flagging-mechanism it is probably
	 * more efficient to get all un-flagged-posts directly via a join with the
	 * user table
	 * @param indexName 
	 * @param lastLogDate 
	 */
	protected void updatePredictions(final String indexName, final Date lastLogDate) {
		// keeps track of the newest log_date during last index update
		// get date of last index update
		final Date fromDate = new Date(lastLogDate.getTime() - QUERY_TIME_OFFSET_MS);

		final List<User> predictedUsers = this.inputLogic.getPredictionForTimeRange(fromDate);

		// the prediction table holds up to two entries per user
		// - the first entry is the one to consider (ordered descending by date)
		// we keep track of users which appear twice via this set
		final Set<String> alreadyUpdated = new HashSet<String>();
		final Map<String, Map<String, Object>> convertedPosts = new HashMap<>();
		for (final User user : predictedUsers) {
			final String userName = user.getName();
			final boolean unknowUser = alreadyUpdated.add(userName);
			if (unknowUser) {
				/*
				 * flag/unflag spammer, depending on user.getPrediction()
				 */
				log.debug("updating spammer status for user " + userName);
				switch (user.getPrediction().intValue()) {
				case 0:
					log.debug("unflag non-spammer");
					
					int offset = 0;
					List<SearchPost<R>> userPosts;
					do {
						userPosts = this.inputLogic.getPostsForUser(userName, SearchDBInterface.SQL_BLOCKSIZE, offset);
						// insert new records into index
						if (present(userPosts)) {
							for (final SearchPost<R> post : userPosts) {
								final Map<String, Object> convertedPost = this.tools.getConverter().convert(post);
								final String id = ElasticsearchUtils.createElasticSearchId(post.getContentId().intValue());
								
								convertedPosts.put(id, convertedPost);
								
								if (convertedPosts.size() >= SearchDBInterface.SQL_BLOCKSIZE / 2) {
									this.clearQueue(indexName, convertedPosts);
								}
							}
						}
						
						offset += SearchDBInterface.SQL_BLOCKSIZE;
					} while (userPosts.size() == SearchDBInterface.SQL_BLOCKSIZE);
					break;
				case 1:
					log.debug("flag spammer");
					// remove all docs of the user from the index!
					this.client.deleteDocuments(indexName, this.tools.getResourceTypeAsString(), QueryBuilders.termQuery(Fields.USER_NAME, userName));
					break;
				}
			}
		}
		
		// clear the queue
		if (present(convertedPosts)) {
			this.clearQueue(indexName, convertedPosts);
		}
	}
	
	/**
	 * @return the count request builder
	 * 
	 */
	public CountRequestBuilder prepareCount() {
		return this.client.prepareCount(this.getActiveLocalAlias());
	}
	
	/**
	 * @return the search request builder
	 */
	public SearchRequestBuilder prepareSearch() {
		return prepareSearch(this.getActiveLocalAlias());
	}

	/**
	 * @param indexName
	 * @return the prepared search builder
	 */
	protected SearchRequestBuilder prepareSearch(final String indexName) {
		return this.client.prepareSearch(indexName);
	}
	
	/**
	 * shut downs the executor service
	 */
	public void shutdown() {
		this.executorService.shutdownNow();
	}
}
