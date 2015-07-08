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
package org.bibsonomy.es;

import static org.bibsonomy.es.ESConstants.SYSTEMURL_FIELD;
import static org.bibsonomy.es.ESConstants.SYSTEM_INFO_INDEX_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.GroupUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: add documentation to this class
 * 
 * @author lutful
 * @param <R>
 *        the resource of the index
 */
public class SharedResourceIndexUpdater<R extends Resource> implements IndexUpdater<R> {
	private static final Log log = LogFactory.getLog(SharedResourceIndexUpdater.class);

	private final String activeIndexID;

	private final String resourceType;

	private final ESIndexManager esIndexManager;

	private final SystemInformation systemInfo = new SystemInformation();
	/** The Url of the project home */
	private final String systemHome;

	private final String systemUrlFieldName = SYSTEMURL_FIELD;

	/** list posts to insert into index */
	private ArrayList<Map<String, Object>> esPostsToInsert;

	/** the Elasticsearch client */
	private ESClient esClient;

	/** list containing content ids of cached delete operations */
	protected List<Integer> contentIdsToDelete;
	
	/** converts post model objects to documents of the index structure */
	private final LuceneResourceConverter<R> resourceConverter;
	
	/**
	 * 
	 */
	protected Set<String> usersToFlag;

	/**
	 * @param systemHome
	 * @param resourceType
	 */
	public SharedResourceIndexUpdater(final String systemHome, String resourceType, final LuceneResourceConverter<R> resourceConverter) {
		this.systemHome = systemHome;
		this.resourceConverter = resourceConverter;
		this.contentIdsToDelete = new LinkedList<Integer>();
		this.esPostsToInsert = new ArrayList<Map<String, Object>>();
		this.usersToFlag = new TreeSet<String>();
		this.resourceType =  resourceType;
		this.activeIndexID = ESConstants.getGlobalAliasForResource(this.resourceType, true);
		esIndexManager = new ESIndexManager(esClient, systemHome);		
	}

	/**
	 * @return lastLogDate
	 */
	@SuppressWarnings("boxing")
	public Date getLastLogDate() {
		synchronized (this) {
			final String lastLogDateString = this.fetchSystemInfoField(LuceneFieldNames.LAST_LOG_DATE);
			if (lastLogDateString == null) {
				return null;
			}
			return new Date(Long.parseLong(lastLogDateString));
		}
	}

	private String fetchSystemInfoField(final String fieldToRetrieve) {
		try {
			final List<Map<String, Object>> resultList = this.getSystemInfos();
			if (resultList != null) {
				List<String> valList = new ArrayList<String>();
				for(Map<String, Object> result : resultList){
					final Object val = result.get(fieldToRetrieve);
					if (val != null) {
						valList.add(val.toString());
					}
				}
				if(!valList.isEmpty()){
					Collections.sort(valList);
					return valList.get(0);
				}
				return null;
			}
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e.getDetailedMessage() + " -> returning null", e);
		}
		return null;
	}

	/**
	 * @return returns the index informations on all the systems
	 */
	public List<Map<String, Object>> getAllSystemInfos() {
		return this.getAllSystemInfosInternal(QueryBuilders.matchQuery("postType", this.resourceType), 200);
	}

	private List<Map<String, Object>> getAllSystemInfosInternal(final QueryBuilder query, final int size) {
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		SearchResponse response = null;
		this.esClient.getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();

		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(this.activeIndexID);
		searchRequestBuilder.setTypes(SYSTEM_INFO_INDEX_TYPE);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(query);
		searchRequestBuilder.setFrom(0).setSize(size).setExplain(true);

		response = searchRequestBuilder.execute().actionGet();
		
		final List<Map<String, Object>> rVal = new ArrayList<>();
		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				rVal.add(hit.getSource());
			}
		}

		return rVal;
	}

	/**
	 * @return returns system info of this system
	 */
	public List<Map<String, Object>> getSystemInfos() {
		final List<Map<String, Object>> l = this.getAllSystemInfosInternal(QueryBuilders.idsQuery().ids(this.systemHome + this.resourceType), 1);
		if (l.size() > 0) {
			return l;
		}
		return null;
	}

	/**
	 * @return lastTasId
	 */
	@Override
	@SuppressWarnings("boxing")
	public Integer getLastTasId() {
		synchronized (this) {
			final String lastTasIdString = this.fetchSystemInfoField(LuceneFieldNames.LAST_TAS_ID);
			if (lastTasIdString == null) {
				log.error("no lastTasId  -> starting from the very beginning");
				return Integer.MIN_VALUE;
			}
			return Integer.parseInt(lastTasIdString);
		}
	}

	/**
	 * @param contentIdsToDelete
	 *        the contentIdsToDelete to set
	 */
	public void deleteDocumentsForContentIds(final List<Integer> contentIdsToDelete) {
		synchronized (this) {
			this.contentIdsToDelete = contentIdsToDelete;
		}
	}

	/**
	 * @return the esPostsToInsert
	 */
	public ArrayList<Map<String, Object>> getEsPostsToInsert() {
		return this.esPostsToInsert;
	}

	/**
	 * @param postsToInsert
	 *        the esPostsToInsert to set
	 */
	public void setEsPostsToInsert(final ArrayList<Map<String, Object>> postsToInsert) {
		this.esPostsToInsert = postsToInsert;
	}

	/**
	 * perform all cached operations to index
	 */
	@Override
	public void flush() {
		synchronized (this) {
			// ----------------------------------------------------------------
			// remove cached posts from index
			// ----------------------------------------------------------------
			log.debug("Performing " + this.contentIdsToDelete.size() + " delete operations");
			try{
				if ((this.contentIdsToDelete.size() > 0) || (this.usersToFlag.size() > 0)) {
					// remove each cached post from index
					for (final Integer contentId : this.contentIdsToDelete) {
						final long indexID = this.calculateIndexId(contentId);
						this.deleteIndexForIndexId(indexID);
						log.debug("deleted post " + contentId);
					}
					// remove spam posts from index
					for (final String userName : this.usersToFlag) {
						// final int cnt = purgeDocumentsForUser(userName);
						// log.debug("Purged " + cnt + " posts for user " +
						// userName);
						this.deleteIndexForUser(userName);
						log.debug("Purged posts for user " + userName);
					}
				}
	
				// ----------------------------------------------------------------
				// add cached posts to index
				// ----------------------------------------------------------------
				log.debug("Performing " + this.esPostsToInsert.size() + " insert operations");
				if (this.esPostsToInsert.size() > 0) {
					this.insertNewPosts(this.esPostsToInsert);
				}
	
				// ----------------------------------------------------------------
				// Update system informations
				// ----------------------------------------------------------------
				this.flushSystemInformation();
	
				// ----------------------------------------------------------------
				// clear all cached data
				// ----------------------------------------------------------------
				this.esPostsToInsert.clear();
				this.contentIdsToDelete.clear();
				this.usersToFlag.clear();
			}catch (JsonProcessingException e){
				log.error("unable to convert the post into JSON document", e);
			}
		}
	}

	/**
	 * checks if there are any new indexes waiting to be activated after regeneration
	 * if true then activate the most recent as the active index and the next one as
	 * backup index
	 */
	public void checkNewIndexInPipeline() {
		String tempAlias = ESConstants.getTempAliasForResource(this.resourceType, false);
		List<String> indexesList=esIndexManager.getIndexesFfromAlias(tempAlias);
		String activeIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, true);
		String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
		indexesList.addAll(esIndexManager.getIndexesFfromAlias(activeIndexAlias));
		indexesList.addAll(esIndexManager.getIndexesFfromAlias(backupIndexAlias));
		Collections.sort(indexesList);
		/* first remove all aliases for avoiding confusion
		 * then set alias for last as active and 2nd last as backup
		 */
		esIndexManager.removeAliases(indexesList);
		esIndexManager.setAliasForIndex(activeIndexAlias, indexesList.get(indexesList.size()-1));
		esIndexManager.setAliasForIndex(backupIndexAlias, indexesList.get(indexesList.size()-2));
		for(int i=0;i<indexesList.size()-2;i++){
			final DeleteIndexResponse deleteIndex = this.esClient.getClient().admin().indices().delete(new DeleteIndexRequest(indexesList.get(i))).actionGet();
			if (!deleteIndex.isAcknowledged()) {
				log.error("Error in deleting the old index: " + indexesList.get(i)+ " after re-generate");
				return;
			}
		}
		
	}

	/**
	 * @param contentId
	 * @return
	 */
	private long calculateIndexId(final Number contentId) {
		return calculateIndexId(contentId, this.systemHome);
	}

	/**
	 * @param contentId
	 * @param systemHome
	 * @return returns the indexId for a document
	 */
	protected static long calculateIndexId(final Number contentId, final String systemHome) {
		return (((long) systemHome.hashCode()) << 32l) + contentId.longValue();
	}
	
	
	/**
	 * updates the system information for lastLogDate, lastTasId
	 * @throws JsonProcessingException 
	 */
	public void flushSystemInformation() throws JsonProcessingException {
		//first update the active index at the same time make the backup index active 
		String indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.flushSystemInformation(indexName);
		//then update the backup index
		indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.flushSystemInformation(indexName);
	}
	/**
	 * updates the system information for lastLogDate, lastTasId
	 * @param indexName 
	 * @throws JsonProcessingException 
	 */
	public void flushSystemInformation(String indexName) throws JsonProcessingException {
		if (indexName == null) {
			indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		}
		final ObjectMapper mapper = new ObjectMapper();
		String jsonDocumentForSystemInfo;
		jsonDocumentForSystemInfo = mapper.writeValueAsString(this.systemInfo);
		this.esClient.getClient().prepareIndex(indexName, SYSTEM_INFO_INDEX_TYPE, this.systemHome + this.resourceType).setSource(jsonDocumentForSystemInfo).execute().actionGet();
		
	}
	
	private void insertNewPosts(final ArrayList<Map<String, Object>> esPostsToInsert2) {
		//first update the active index at the same time make the backup index active 
		String indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.insertNewPosts(esPostsToInsert2, indexName);
		//then update the backup index
		indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.insertNewPosts(esPostsToInsert2, indexName);
	}
	/**
	 * @param esPostsToInsert2
	 * @param indexName 
	 */
	@SuppressWarnings("boxing")
	private void insertNewPosts(final ArrayList<Map<String, Object>> esPostsToInsert2, String indexName) {
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.esClient.getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();

		for (final Map<String, Object> jsonDocument : esPostsToInsert2) {
			jsonDocument.put(this.systemUrlFieldName, this.systemHome);
			final long indexId = this.calculateIndexId(Long.parseLong(jsonDocument.get(LuceneFieldNames.CONTENT_ID).toString()));
			this.esClient.getClient().prepareIndex(indexName, this.resourceType, String.valueOf(indexId)).setSource(jsonDocument).setRefresh(true).execute().actionGet();
		}

		log.info("post has been indexed.");
	}

	/**
	 * @param userName
	 */
	@Override
	public void deleteIndexForUser(final String userName) {
		//first update the active index at the same time make the backup index active 
		String indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.deleteIndexForForUser(userName, indexName);
		//then update the backup index
		indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.deleteIndexForForUser(userName, indexName);
	}
	/**
	 * @param userName
	 * @param indexName
	 */
	public void deleteIndexForForUser(final String userName, String indexName){
		this.esClient.getClient().prepareDeleteByQuery(indexName).setTypes(this.resourceType).setQuery(QueryBuilders.termQuery(LuceneFieldNames.USER_NAME, userName)).execute().actionGet();
	}
	/**
	 * @param indexId
	 */
	@Override
	public void deleteIndexForIndexId(final long indexId) {
		//first update the active index at the same time make the backup index active 
		String indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.deleteIndexForIndexId(indexId, indexName);
		//then update the backup index
		indexName = esIndexManager.switchToBackupIndexByAlias(activeIndexID, resourceType);
		this.deleteIndexForIndexId(indexId, indexName);
	}
	/**
	 * @param indexId
	 * @param indexName 
	 */
	public void deleteIndexForIndexId(final long indexId, String indexName) {
		this.esClient.getClient().prepareDelete(indexName, this.resourceType, String.valueOf(indexId)).setRefresh(true).execute().actionGet();
		}

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param postDoc
	 */
	public void insertDocument(final Map<String, Object> postDoc) {
		this.esPostsToInsert.add(postDoc);
	}

	/**
	 * @param contentId
	 */
	@Override
	public void deleteDocumentForContentId(final Integer contentId) {
		synchronized (this) {
			this.contentIdsToDelete.add(contentId);
		}
	}

	/**
	 * flag given user as spammer - preventing further posts to be inserted and
	 * mark user's posts for deletion from index
	 * 
	 * @param username
	 */
	public void flagUser(final String username) {
		synchronized (this) {
			this.usersToFlag.add(username);
		}
	}

	/**
	 * unflag given user as spammer - enabling further posts to be inserted
	 * 
	 * @param userName
	 */
	public void unFlagUser(final String userName) {
		synchronized (this) {
			this.usersToFlag.remove(userName);
		}
	}

	/**
	 * sets the system informations to update
	 * 
	 * @param lastTasId
	 * @param lastLogDate
	 */
	public void setSystemInformation(final Integer lastTasId, final Date lastLogDate) {
		this.systemInfo.setLast_log_date(lastLogDate);
		this.systemInfo.setLast_tas_id(lastTasId);
		this.systemInfo.setPostType(this.resourceType);
		this.systemInfo.setSystemUrl(this.systemHome);
	}

	/**
	 * @return the esClient
	 */
	public ESClient getEsClient() {
		return this.esClient;
	}

	/**
	 * @param esClient the esClient to set
	 */
	public void setEsClient(final ESClient esClient) {
		this.esClient = esClient;
	}
	
	@Override
	public void insertDocument(LucenePost<R> post, final Date currentLogDate) {
		if (post.getGroups().contains(GroupUtils.buildPublicGroup())) {
			if (currentLogDate != null) {
				post.setLastLogDate(currentLogDate);
			}
			final Map<String, Object> postDoc = (Map<String, Object>)this.resourceConverter.readPost(post, IndexType.ELASTICSEARCH);
			this.insertDocument(postDoc);
		}
	}

}
