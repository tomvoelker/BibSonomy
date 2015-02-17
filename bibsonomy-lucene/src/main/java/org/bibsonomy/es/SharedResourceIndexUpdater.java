package org.bibsonomy.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
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
 *            the resource of the index
 */
public class SharedResourceIndexUpdater<R extends Resource> implements IndexUpdater{
	private static final Log log = LogFactory
			.getLog(SharedResourceIndexUpdater.class);

	private final String indexName = ESConstants.INDEX_NAME;

	private String resourceType;

	private SystemInformation systemInfo =  new SystemInformation();
	/**The Url of the project home */
	private final String systemHome;
	
	private final String systemUrlFieldName = "systemUrl";
	
	/** list posts to insert into index */
	private ArrayList<Map<String, Object>> esPostsToInsert;
	/** the node client */
//	private final ESNodeClient esClient = new ESNodeClient();
	/** the transport client */
	private ESClient esClient;

	/** list containing content ids of cached delete operations */
	protected List<Integer> contentIdsToDelete;
	/**
	 * 
	 */
	protected Set<String> usersToFlag;

	/**
	 * @param systemHome
	 */
	public SharedResourceIndexUpdater(final String systemHome) {
		this.systemHome = systemHome;
		this.contentIdsToDelete = new LinkedList<Integer>();
		this.esPostsToInsert = new ArrayList<Map<String, Object>>();
		this.usersToFlag = new TreeSet<String>();
		
	}

	/**
	 * @return lastLogDate
	 */
	@Override
	@SuppressWarnings("boxing")
	public long getLastLogDate() {
		synchronized (this) {			
			final String lastLogDateString = fetchSystemInfoField(LuceneFieldNames.LAST_LOG_DATE);
			if (lastLogDateString == null) {
				return Long.MIN_VALUE;
			}
			return Long.parseLong(lastLogDateString);
		}
	}

	private String fetchSystemInfoField(String fieldToRetrieve) {
		try {
			Map<String, Object> result = getSystemInfos();
			if (result != null) {
				Object val = result.get(fieldToRetrieve);
				if (val == null) {
					return null;
				}
				return val.toString();
			}
		} catch (IndexMissingException e) {
			log.error("IndexMissingException: " + e.getDetailedMessage() + " -> returning null", e);
		}
		return null;
	}
	
	
	

	public List<Map<String, Object>> getAllSystemInfos() {
		return getAllSystemInfosInternal(QueryBuilders.matchQuery("postType", this.resourceType), 200);
	}
	
	private List<Map<String, Object>> getAllSystemInfosInternal(QueryBuilder query, int size) {
		Map<String, Object> result = null;
		
		SearchRequestBuilder searchRequestBuilder = esClient
				.getClient().prepareSearch(indexName);
		searchRequestBuilder.setTypes(ESConstants.SYSTEM_INFO_INDEX_TYPE);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(query);
		searchRequestBuilder.setFrom(0).setSize(1).setExplain(true);

		SearchResponse response = searchRequestBuilder.execute()
				.actionGet();

		List<Map<String, Object>> rVal = new ArrayList<>();
		
		for (SearchHit hit : response.getHits()) {
			rVal.add(hit.getSource());
		}
		
		return rVal;
	}
	
	public Map<String, Object> getSystemInfos() {
		List<Map<String, Object>> l = getAllSystemInfosInternal(QueryBuilders.idsQuery().ids(systemHome+resourceType), 1);
		if (l.size() > 0) {
			return l.get(0);
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
			String lastTasIdString = fetchSystemInfoField(LuceneFieldNames.LAST_TAS_ID);
			if (lastTasIdString == null) {
				log.error("no lastTasId  -> starting from the very beginning");
				return Integer.MIN_VALUE;
			}
			return Integer.parseInt(lastTasIdString);
		}
	}

	/**
	 * @param contentIdsToDelete
	 *            the contentIdsToDelete to set
	 */
	public void setContentIdsToDelete(List<Integer> contentIdsToDelete) {
		synchronized (this) {
			this.contentIdsToDelete = contentIdsToDelete;
		}
	}

	/**
	 * @return the nodeClient
	 */
	public Client getClient() {
		return this.getClient();
	}

	/**
	 * @return the iNDEX_NAME
	 */
	public String getIndexName() {
		return this.indexName;
	}

	/**
	 * @return the esPostsToInsert
	 */
	public ArrayList<Map<String, Object>> getEsPostsToInsert() {
		return this.esPostsToInsert;
	}

	/**
	 * @param postsToInsert
	 *            the esPostsToInsert to set
	 */
	public void setEsPostsToInsert(ArrayList<Map<String, Object>> postsToInsert) {
		this.esPostsToInsert = postsToInsert;
	}

	/**
	 * perform all cached operations to index
	 */

	public void flush() {
		synchronized (this) {
			// ----------------------------------------------------------------
			// remove cached posts from index
			// ----------------------------------------------------------------
			log.debug("Performing " + contentIdsToDelete.size()
					+ " delete operations");
			if ((contentIdsToDelete.size() > 0) || (usersToFlag.size() > 0)) {
				// remove each cached post from index
				for (final Integer contentId : this.contentIdsToDelete) {
					long indexID = calculateIndexId(contentId);
					this.deleteIndexForIndexId(indexID);
					log.debug("deleted post " + contentId);
				}

				// remove spam posts from index
				for (final String userName : this.usersToFlag) {
					// final int cnt = purgeDocumentsForUser(userName);
					// log.debug("Purged " + cnt + " posts for user " +
					// userName);
					this.deleteIndexForForUser(userName);
					log.debug("Purged posts for user " + userName);
				}
			}

			// ----------------------------------------------------------------
			// add cached posts to index
			// ----------------------------------------------------------------
			log.debug("Performing " + esPostsToInsert.size()
					+ " insert operations");
			if (this.esPostsToInsert.size() > 0) {
				this.insertNewPosts(esPostsToInsert);
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

		}
	}

	/**
	 * @param contentId
	 * @return
	 */
	private long calculateIndexId(Number contentId) {
		return calculateIndexId(contentId, this.systemHome);
	}

	protected static long calculateIndexId(Number contentId, String systemHome) {
		return (((long) systemHome.hashCode()) << 32l) + contentId.longValue();
	}

	/**
	 * updates the system information for lastLogDate, lastTasId
	 */
	public void flushSystemInformation() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonDocumentForSystemInfo;
		try {
			jsonDocumentForSystemInfo = mapper.writeValueAsString(systemInfo);
			esClient.getClient()
			.prepareIndex(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, systemHome+resourceType)
			.setSource(jsonDocumentForSystemInfo).execute().actionGet();
		} catch (JsonProcessingException e) {
			log.error("Failed to convert SystemInformation into a JSON", e);
		}		
	}

	/**
	 * @param esPostsToInsert2
	 */
	@Override
	public void insertNewPosts(ArrayList<Map<String, Object>> esPostsToInsert2) {
		//TODO add systemUrl
		for (Map<String, Object> jsonDocument : esPostsToInsert2) {
			jsonDocument.put(this.systemUrlFieldName, systemHome);
			long indexId = calculateIndexId(Long.parseLong(jsonDocument.get(LuceneFieldNames.CONTENT_ID).toString()));
			this.esClient
					.getClient()
					.prepareIndex(
							indexName,
							resourceType,
							String.valueOf(indexId)).setSource(jsonDocument)
					.setRefresh(true).execute().actionGet();
		}

		log.info("post has been indexed.");
	}

	/**
	 * @param userName
	 */
	@Override
	public void deleteIndexForForUser(String userName) {

		@SuppressWarnings("unused")
		DeleteByQueryResponse response = this.esClient.getClient().prepareDeleteByQuery(indexName)
				.setTypes(resourceType).setQuery(QueryBuilders.termQuery(LuceneFieldNames.USER_NAME, userName))
				.execute()
				.actionGet();
	}

	/**
	 * @param indexId
	 */

	@Override
	public void deleteIndexForIndexId(long indexId) {
		@SuppressWarnings("unused")
		DeleteResponse response = esClient
				.getClient()
				.prepareDelete(indexName, resourceType, String.valueOf(indexId))
				.setRefresh(true).execute().actionGet();
	}

	/**
	 * @return the iNDEX_TYPE
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param iNDEX_TYPE the iNDEX_TYPE to set
	 */
	public void setResourceType(String iNDEX_TYPE) {
		resourceType = iNDEX_TYPE;
	}
	
	/**
	 * @param postDoc
	 */
	public void insertDocument(Map<String, Object> postDoc) {
		esPostsToInsert.add(postDoc);
	}

	/**
	 * @param contentId
	 */
	@Override
	public void deleteDocumentForContentId(Integer contentId) {
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
	 * @param lastTasId
	 * @param lastLogDate
	 */
	public void setSystemInformation(Integer lastTasId, Date lastLogDate){
		this.systemInfo.setLast_log_date(lastLogDate);
		this.systemInfo.setLast_tas_id(lastTasId);
		this.systemInfo.setPostType(resourceType);
		this.systemInfo.setSystemUrl(systemHome);
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
	public void setEsClient(ESClient esClient) {
		this.esClient = esClient;
	}

}
