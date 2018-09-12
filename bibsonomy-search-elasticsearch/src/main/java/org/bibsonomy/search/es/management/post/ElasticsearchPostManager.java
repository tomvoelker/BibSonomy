/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.management.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.management.ElasticsearchIndex;
import org.bibsonomy.search.es.management.ElasticsearchIndexTools;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.model.SearchIndexStatistics;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * manager for Elasticsearch
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchPostManager<R extends Resource> extends ElasticsearchManager<Post<R>> {
	private static final Log log = LogFactory.getLog(ElasticsearchPostManager.class);
	
	/** how many posts should be retrieved from the database */
	public static final int SQL_BLOCKSIZE = 5000;
	private static final long QUERY_TIME_OFFSET_MS = 1000;
	
	/** iff <code>true</code> the update of the index is disabled */
	private boolean updateEnabled;
	
	/** access to the main database */
	protected final SearchDBInterface<R> inputLogic;
	
	/** mappers, converters, */
	protected final ElasticsearchIndexTools<R> tools;
	
	/**
	 * @param updateEnabled 
	 * @param disabledIndexing 
	 * @param client
	 * @param inputLogic
	 * @param tools
	 */
	public ElasticsearchPostManager(final boolean updateEnabled, final boolean disabledIndexing, final ESClient client, SearchDBInterface<R> inputLogic, ElasticsearchIndexTools<R> tools) {
		super(disabledIndexing);
		this.updateEnabled = updateEnabled;
		this.client = client;
		this.inputLogic = inputLogic;
		this.tools = tools;
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
		
		final List<String> indices = this.getAllStandByIndices();
		for (final String indexName : indices) {
			final SearchIndexInfo searchIndexInfoStandBy = getIndexInfoForIndex(indexName, SearchIndexState.STANDBY, true);
			searchIndexInfoStandBy.setId(indexName);
			infos.add(searchIndexInfoStandBy);
		}
		
		if (this.currentGenerator != null) {
			try {
				final String indexName = this.currentGenerator.getIndexName();
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
	 * @return all standby index names
	 */
	private List<String> getAllStandByIndices() {
		return this.client.getIndexNamesForAlias(this.getAliasNameForState(SearchIndexState.STANDBY));
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
			searchIndexInfo.setSyncState(this.client.getSearchIndexStateForIndex(ElasticsearchUtils.getSearchIndexStateIndexName(this.tools.getSystemURI()), indexName));
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
			this.updateIndex(localInactiveAlias);
			
			this.switchActiveAndInactiveIndex();
		} catch (final IndexNotFoundException e) {
			log.error("Can't update " + this.tools.getResourceTypeAsString() + " index. No inactive index available.");
		} finally {
			this.updateLock.release();
		}
	}

	/**
	 * @param indexName
	 */
	protected void updateIndex(final String indexName) {
		final String systemSyncStateIndexName = ElasticsearchUtils.getSearchIndexStateIndexName(this.tools.getSystemURI());

		final String realIndexName = this.client.getIndexNameForAlias(indexName);
		final SearchIndexSyncState oldState = this.client.getSearchIndexStateForIndex(systemSyncStateIndexName, realIndexName);
		final SearchIndexSyncState targetState = this.inputLogic.getDbState();
		
		final int oldLastTasId = oldState.getLast_tas_id().intValue();
		int newLastTasId = oldLastTasId;
		
		/*
		 * 1) flag/unflag spammer
		 */
		this.updatePredictions(indexName, oldState.getLastPredictionChangeDate(), targetState.getLastPredictionChangeDate());
		
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
			
			this.client.deleteDocuments(indexName, this.tools.getResourceTypeAsString(), idsToDelete);
		}

		/*
		 * 3) add new and updated posts to the index
		 */
		log.debug("inserting new/updated posts into " + indexName);
		final Map<String, Map<String, Object>> convertedPosts = new HashMap<>();
		List<SearchPost<R>> newPosts;
		int offset = 0;
		int totalCountNewPosts = 0;
		do {
			newPosts = this.inputLogic.getNewPosts(oldLastTasId, SearchDBInterface.SQL_BLOCKSIZE, offset);
			for (final SearchPost<R> post : newPosts) {
				final Map<String, Object> convertedPost = this.tools.getConverter().convert(post);
				
				final Integer contentId = post.getContentId();
				final String id = ElasticsearchUtils.createElasticSearchId(contentId.intValue());
				convertedPosts.put(id, convertedPost);
				newLastTasId = Math.max(post.getLastTasId().intValue(), newLastTasId);
			}
			
			if (convertedPosts.size() >= ESConstants.BULK_INSERT_SIZE) {
				this.clearQueue(indexName, convertedPosts);
			}
			
			totalCountNewPosts += newPosts.size();
			offset += SearchDBInterface.SQL_BLOCKSIZE;
		} while (newPosts.size() == SearchDBInterface.SQL_BLOCKSIZE);
		
		if (present(convertedPosts)) {
			this.clearQueue(indexName, convertedPosts);
		}
		
		log.debug("inserted " + totalCountNewPosts + " new/updated posts into " + indexName);
		
		this.updateResourceSpecificProperties(indexName, oldState, targetState);
		
		// 4) update the index state
		try {
			final SearchIndexSyncState newState = new SearchIndexSyncState(oldState);
			newState.setLast_log_date(targetState.getLast_log_date());
			newState.setLast_tas_id(Integer.valueOf(newLastTasId));
			newState.setLastPersonChangeId(targetState.getLastPersonChangeId());
			newState.setLastDocumentDate(targetState.getLastDocumentDate());
			this.updateIndexState(realIndexName, newState);
		} catch (final RuntimeException e) {
			this.updateIndexState(realIndexName, oldState);
			throw e;
		} catch (final Exception e) {
			this.updateIndexState(realIndexName, oldState);
			throw new RuntimeException(e);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("posts updated for " + indexName);
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
	 * @param indexName
	 * @param state
	 */
	private void updateIndexState(final String indexName, final SearchIndexSyncState state) {
		final Map<String, Object> values = ElasticsearchUtils.serializeSearchIndexState(state);
		
		this.client.insertNewDocument(ElasticsearchUtils.getSearchIndexStateIndexName(this.tools.getSystemURI()), ESConstants.SYSTEM_INFO_INDEX_TYPE, indexName, values);
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
	 * @param lastPredictionChangeDate 
	 * @param currentLastPreditionChangeDate 
	 */
	protected void updatePredictions(final String indexName, final Date lastPredictionChangeDate, final Date currentLastPreditionChangeDate) {
		// keeps track of the newest log_date during last index update
		// get date of last index update
		final Date fromDate = new Date(lastPredictionChangeDate.getTime());

		final List<User> predictedUsers = this.inputLogic.getPredictionForTimeRange(fromDate, currentLastPreditionChangeDate);

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
					log.debug("user " + userName + " flaged as non-spammer");
					
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
					log.debug("user " + userName + " flagged as spammer");
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
	 * execute a search
	 * @param query the query to use
	 * @param order the order
	 * @param offset the offset
	 * @param limit the limit
	 * @param minScore the min score
	 * @param fieldsToRetrieve the fields to retrieve
	 * @return
	 */
	public SearchHits search(final QueryBuilder query, final Pair<String, SortOrder> order, int offset, int limit, Float minScore, final Set<String> fieldsToRetrieve) {
		final String resourceType = ResourceFactory.getResourceName(this.tools.getResourceType());
		return this.client.search(this.getActiveLocalAlias(), resourceType, query, null, order, offset, limit, minScore, fieldsToRetrieve);
	}

	public long getDocumentCount(QueryBuilder query) {
		final String resourceType = ResourceFactory.getResourceName(this.tools.getResourceType());
		return this.client.getDocumentCount(this.getActiveLocalAlias(), resourceType, query);
	}
}
