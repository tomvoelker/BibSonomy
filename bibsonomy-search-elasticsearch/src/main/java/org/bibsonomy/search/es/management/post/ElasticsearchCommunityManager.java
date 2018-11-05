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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.DeleteData;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.post.CommunityPostIndexCommunityUpdateLogic;
import org.bibsonomy.search.index.update.post.CommunityPostIndexUpdateLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchCommunityIndexSyncState;
import org.bibsonomy.search.util.Converter;

/**
 * special class that manages community posts
 *
 * @author dzo
 * @param <G> the community resource class
 */
public class ElasticsearchCommunityManager<G extends Resource> extends ElasticsearchManager<Post<G>, SearchCommunityIndexSyncState> {

	private final CommunityPostIndexCommunityUpdateLogic<G> communityPostUpdateLogic;
	private final CommunityPostIndexUpdateLogic<G> postUpdateLogic;
	private final SearchDBInterface<G> inputLogic;
	private final DatabaseInformationLogic<SearchCommunityIndexSyncState> databaseInformationLogic;

	/**
	 * default constructor
	 *  @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param inputLogic
	 * @param communityPostUpdateLogic
	 * @param postUpdateLogic
	 */
	public ElasticsearchCommunityManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Post<G>, SearchCommunityIndexSyncState> generator, Converter syncStateConverter, EntityInformationProvider entityInformationProvider, SearchDBInterface<G> inputLogic, CommunityPostIndexCommunityUpdateLogic<G> communityPostUpdateLogic, CommunityPostIndexUpdateLogic<G> postUpdateLogic, final DatabaseInformationLogic<SearchCommunityIndexSyncState> databaseInformationLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, entityInformationProvider);
		this.communityPostUpdateLogic = communityPostUpdateLogic;
		this.postUpdateLogic = postUpdateLogic;
		this.inputLogic = inputLogic;
		this.databaseInformationLogic = databaseInformationLogic;
	}

	@Override
	protected void updateIndex(final String indexName, SearchCommunityIndexSyncState oldState) {
		final DefaultSearchIndexSyncState oldNormalSearchIndexState = oldState.getNormalSearchIndexState();
		final DefaultSearchIndexSyncState oldCommunitySearchIndexState = oldState.getCommunitySearchIndexState();

		final Integer communityPostLastContentId = oldCommunitySearchIndexState.getLast_tas_id();
		final Integer postLastContentId = oldNormalSearchIndexState.getLast_tas_id();
		final Date communityPostLastLogDate = oldCommunitySearchIndexState.getLast_log_date();
		final Date postLastLogDate = oldNormalSearchIndexState.getLast_log_date();

		final SearchCommunityIndexSyncState targetState = this.databaseInformationLogic.getDbState();

		/*
		 * 1. step: get only deleted entries, not updated
		 *
		 * a) get all community deletes
		 */
		final List<Post<G>> deletedEntities = this.communityPostUpdateLogic.getDeletedEntities(communityPostLastLogDate);
		this.deletePostsFromIndexAndInsertOtherPostInDB(indexName, deletedEntities);

		/*
		 * now all normal posts that were deleted without a community post
		 */
 		final List<Post<G>> deletedNormalPosts = this.postUpdateLogic.getDeletedEntities(postLastLogDate);
		this.deletePostsFromIndexAndInsertOtherPostInDB(indexName, deletedNormalPosts);

		/*
		 * 2. step: insert updated or new posts
		 * a) for the "normal" posts
		 * here posts with a community post are excluded by the logic
		 */
		this.insertNewPosts(indexName, postLastContentId, postLastLogDate, this.postUpdateLogic);

		/*
		 * b) new posts for gold standard posts
		 */
		this.insertNewPosts(indexName, communityPostLastContentId, communityPostLastLogDate, this.communityPostUpdateLogic);

		/*
		 * 3. handle flagging of users
		 * user flagged as spammer: the community posts that are created were created by the user must be removed and
		 * replaced with the most current post in the database (when there is another post with the hash in the database)
		 *
		 * user unflagged as spammer: the post in the index must be updated iff there is no community post in the database
		 * and the post is newer than the post in the index
		 */
		final List<User> users = this.inputLogic.getPredictionForTimeRange(oldNormalSearchIndexState.getLastPredictionChangeDate(), targetState.getNormalSearchIndexState().getLastPredictionChangeDate());
		final Map<String, IndexData> postsToInsert = new LinkedHashMap<>();
		for (final User user : users) {
			final String userName = user.getName();
			final int prediction = user.getPrediction();
			switch (prediction) {
				case 0:
					// user unflagged as spammer
					int offset = 0;
					List<Post<G>> userPosts;
					do {
						// get new posts to insert
						userPosts = this.communityPostUpdateLogic.getPostsOfUser(userName, ElasticsearchPostManager.SQL_BLOCKSIZE, offset);
						// insert new records into index
						if (present(userPosts)) {
							for (final Post<G> post : userPosts) {
								final IndexData indexData = this.buildIndexDataForPost(post);
								final String id = this.entityInformationProvider.getEntityId(post);
								postsToInsert.put(id, indexData);
							}
						}

						offset += SearchDBInterface.SQL_BLOCKSIZE;
					} while (userPosts.size() == SearchDBInterface.SQL_BLOCKSIZE);
					break;
				case 1:
					/*
					 * user flagged as spammer
					 */
					final List<Post<G>> allPostsOfUser = this.communityPostUpdateLogic.getAllPostsOfUser(userName);
					this.deletePostsFromIndexAndInsertOtherPostInDB(indexName, allPostsOfUser);
					break;
			}
		}

		/*
		 * TODO: implement
		 * update the all_users field; add users, and remove users
		 */

		/*
		 * n step: update the target state
		 */
		this.updateIndexState(indexName, targetState);
	}

	private void insertNewPosts(String indexName, Integer communityPostLastContentId, Date communityPostLastLogDate, CommunityPostIndexUpdateLogic<G> indexUpdateLogic) {
		int offset = 0;
		int postSize;
		final Map<String, IndexData> postUpdateMap = new LinkedHashMap<>();
		do {
			final List<Post<G>> newerEntities = indexUpdateLogic.getNewerEntities(communityPostLastContentId, communityPostLastLogDate, ElasticsearchPostManager.SQL_BLOCKSIZE, offset);

			for (final Post<G> newEntity : newerEntities) {
				final IndexData indexData = this.buildIndexDataForPost(newEntity);
				final String entityId = this.entityInformationProvider.getEntityId(newEntity);

				postUpdateMap.put(entityId, indexData);
			}

			if (postUpdateMap.size() >= ESConstants.BULK_INSERT_SIZE) {
				this.clearQueue(indexName, postUpdateMap);
			}

			postSize = newerEntities.size();
			offset += ElasticsearchPostManager.SQL_BLOCKSIZE;
		} while (postSize == ElasticsearchPostManager.SQL_BLOCKSIZE);

		this.clearQueue(indexName, postUpdateMap);
	}

	/**
	 * this methods deletes the given posts from the index and if possible it replaces a deleted post with the newest post
	 * that remains in the database
	 *
	 * @param indexName
	 * @param deletedEntities
	 */
	private <RR extends Resource> void deletePostsFromIndexAndInsertOtherPostInDB(final String indexName, List<Post<RR>> deletedEntities) {
		final Stream<String> interHashesToDelete = deletedEntities.stream().map(Post::getResource).map(Resource::getInterHash);
		final List<DeleteData> postsToDelete = new LinkedList<>();
		final Map<String, IndexData> postsToUpdate = new LinkedHashMap<>();

		interHashesToDelete.forEach(interHash -> {
			final DeleteData deleteData = new DeleteData();
			deleteData.setId(interHash);
			deleteData.setType(this.entityInformationProvider.getType());
			postsToDelete.add(deleteData);

			// check it there is another post in the database
			final Post<G> newestPostByInterHash = this.communityPostUpdateLogic.getNewestPostByInterHash(interHash);
			if (present(newestPostByInterHash)) {
				// prepare the post for indexing
				final IndexData indexData = this.buildIndexDataForPost(newestPostByInterHash);

				final String entityId = this.entityInformationProvider.getEntityId(newestPostByInterHash);
				postsToUpdate.put(entityId, indexData);
			}
		});

		// first delete the posts, then insert or update existing posts
		this.client.deleteDocuments(indexName, postsToDelete);
		this.client.updateOrCreateDocuments(indexName, postsToUpdate);
	}

	private IndexData buildIndexDataForPost(Post<G> newestPostByInterHash) {
		final Map<String, Object> source = this.entityInformationProvider.getConverter().convert(newestPostByInterHash);
		final IndexData indexData = new IndexData();
		indexData.setSource(source);
		indexData.setType(this.entityInformationProvider.getType());
		indexData.setRouting(this.entityInformationProvider.getRouting(newestPostByInterHash));
		return indexData;
	}
}
