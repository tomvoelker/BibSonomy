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

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.client.DeleteData;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexSyncState;
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
	
	/** access to the main database */
	protected final SearchDBInterface<R> inputLogic;

	/**
	 * default constructor
	 *
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param entityInformationProvider
	 */
	public ElasticsearchPostManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Post<R>> generator, EntityInformationProvider<Post<R>> entityInformationProvider, final SearchDBInterface<R> inputLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, entityInformationProvider);
		this.inputLogic = inputLogic;
	}

	/**
	 * @param indexName
	 */
	@Override
	protected void updateIndex(final String indexName) {
		final String systemSyncStateIndexName = ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId);

		final SearchIndexSyncState oldState = this.client.getSearchIndexStateForIndex(systemSyncStateIndexName, indexName);
		final SearchIndexSyncState targetState = this.inputLogic.getDbState();
		
		final int oldLastTasId = oldState.getLast_tas_id().intValue();
		
		/*
		 * 1) flag/unflag spammer
		 */
		this.updatePredictions(indexName, oldState.getLastPredictionChangeDate(), targetState.getLastPredictionChangeDate());
		
		/*
		 * 2) remove old deleted or updated posts
		 */
		if (oldState.getLast_log_date() != null) {
			final List<Integer> contentIdsToDelete = this.inputLogic.getContentIdsToDelete(new Date(oldState.getLast_log_date().getTime() - QUERY_TIME_OFFSET_MS));
			
			
			final List<DeleteData> idsToDelete = new LinkedList<>();
			for (final Integer contentId : contentIdsToDelete) {
				final String indexID = ElasticsearchUtils.createElasticSearchId(contentId.intValue());
				final DeleteData deleteData = new DeleteData();
				deleteData.setType(this.entityInformationProvider.getType());
				deleteData.setId(indexID);
				idsToDelete.add(deleteData);
			}
			
			this.client.deleteDocuments(indexName, idsToDelete);
		}

		/*
		 * 3) add new and updated posts to the index
		 */
		log.debug("inserting new/updated posts into " + indexName);
		final Map<String, IndexData> convertedPosts = new HashMap<>();
		List<Post<R>> newPosts;
		int offset = 0;
		int totalCountNewPosts = 0;
		do {
			newPosts = this.inputLogic.getNewPosts(oldLastTasId, SearchDBInterface.SQL_BLOCKSIZE, offset);
			for (final Post<R> post : newPosts) {
				final Map<String, Object> convertedPost = this.entityInformationProvider.getConverter().convert(post);
				
				final Integer contentId = post.getContentId();
				final String id = ElasticsearchUtils.createElasticSearchId(contentId.intValue());
				final IndexData indexData = new IndexData();
				indexData.setType(this.entityInformationProvider.getType());
				indexData.setSource(convertedPost);
				convertedPosts.put(id, indexData);
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
			newState.setLast_tas_id(targetState.getLast_tas_id());
			newState.setLastPersonChangeId(targetState.getLastPersonChangeId());
			newState.setLastDocumentDate(targetState.getLastDocumentDate());
			this.updateIndexState(indexName, newState);
		} catch (final RuntimeException e) {
			this.updateIndexState(indexName, oldState);
			throw e;
		} catch (final Exception e) {
			this.updateIndexState(indexName, oldState);
			throw new RuntimeException(e);
		}

		if (log.isDebugEnabled()) {
			log.debug("posts updated for " + indexName);
		}
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
		final Set<String> alreadyUpdated = new HashSet<>();
		final Map<String, IndexData> convertedPosts = new HashMap<>();
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
					List<Post<R>> userPosts;
					do {
						userPosts = this.inputLogic.getPostsForUser(userName, SearchDBInterface.SQL_BLOCKSIZE, offset);
						// insert new records into index
						if (present(userPosts)) {
							for (final Post<R> post : userPosts) {
								final Map<String, Object> convertedPost = this.entityInformationProvider.getConverter().convert(post);
								final String id = ElasticsearchUtils.createElasticSearchId(post.getContentId().intValue());
								final IndexData indexData = new IndexData();
								indexData.setType(this.entityInformationProvider.getType());
								indexData.setSource(convertedPost);
								convertedPosts.put(id, indexData);
								
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
					this.client.deleteDocuments(indexName, this.entityInformationProvider.getType(), QueryBuilders.termQuery(Fields.USER_NAME, userName));
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
		return this.client.search(this.getActiveLocalAlias(), this.entityInformationProvider.getType(), query, null, order, offset, limit, minScore, fieldsToRetrieve);
	}

	public long getDocumentCount(QueryBuilder query) {
		return this.client.getDocumentCount(this.getActiveLocalAlias(), this.entityInformationProvider.getType(), query);
	}
}
