package org.bibsonomy.es;

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
import org.bibsonomy.model.es.ESClient;
import org.bibsonomy.model.es.IndexUpdater;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

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

	private String indexType;

	private SystemInformation systemInfo =  new SystemInformation();
	/**The Url of the project home */
	private static String systemtHome;
	
	private final String systemUrlFieldName = "systemUrl";
	
	/** list posts to insert into index */
	private ArrayList<Map<String, Object>> esPostsToInsert;
	/** the node client */
//	private final ESNodeClient esClient = new ESNodeClient();
	/** the transport client */
	private static ESClient esClient;

	/** keeps track of the newest log_date during last index update */
	private Long lastLogDate;

	/** keeps track of the newest tas_id during last index update */
	private Integer lastTasId;
	/** list containing content ids of cached delete operations */
	protected List<Integer> contentIdsToDelete;
	/**
	 * 
	 */
	protected Set<String> usersToFlag;

	/**
	 * 
	 */
	public SharedResourceIndexUpdater() {
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
			if (this.lastLogDate != null) {
				return this.lastLogDate;
			}
			
			final String lastLogDateString = fetchSystemInfoField(LuceneFieldNames.LAST_LOG_DATE);
			if (lastLogDateString == null) {
				return Long.MIN_VALUE;
			}
			return Long.parseLong(lastLogDateString);
		}
	}

	private String fetchSystemInfoField(String fieldToRetrieve) {
		try {
			IdsQueryBuilder query = QueryBuilders.idsQuery().ids(systemtHome+indexType);
			SearchRequestBuilder searchRequestBuilder = esClient
					.getClient().prepareSearch(indexName);
			searchRequestBuilder.setTypes(ESConstants.SYSTEM_INFO_INDEX_TYPE);
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(query);
			searchRequestBuilder.setFrom(0).setSize(1).setExplain(true);

			SearchResponse response = searchRequestBuilder.execute()
					.actionGet();

			if (response != null) {
				SearchHits hits = response.getHits();
				if (hits.getTotalHits() < 1) {
					return null;
				}
				SearchHit hit = hits.getAt(0);
				Map<String, Object> result = hit.getSource();
				return result.get(
						fieldToRetrieve).toString();
			}
		} catch (IndexMissingException e) {
			log.error("IndexMissingException: " + e.getDetailedMessage() + " -> returning null", e);
		}
		return null;
	}

	/**
	 * @param lastLogDate
	 *            the lastLogDate to set
	 */
	public void setLastLogDate(Long lastLogDate) {
		this.lastLogDate = lastLogDate;
	}

	/**
	 * @return lastTasId
	 */
	@Override
	@SuppressWarnings("boxing")
	public Integer getLastTasId() {
		synchronized (this) {
			if (this.lastTasId != null) {
				return this.lastTasId;
			}
			String lastTasIdString = fetchSystemInfoField(LuceneFieldNames.LAST_TAS_ID);
			if (lastTasIdString == null) {
				log.error("no lastTasId  -> starting from the very beginning");
				return Integer.MIN_VALUE;
			}
			return Integer.parseInt(lastTasIdString);
		}
	}

	/**
	 * @param lastTasId
	 *            the lastTasId to set
	 */

	public void setLastTasId(Integer lastTasId) {
		this.lastTasId = lastTasId;
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
		return SharedResourceIndexUpdater.esClient.getClient();
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
					long indexID = (systemtHome.hashCode() << 32) + Long.parseLong(contentId.toString());
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
			this.updateSystemInformation();
			
			// ----------------------------------------------------------------
			// clear all cached data
			// ----------------------------------------------------------------
			this.esPostsToInsert.clear();
			this.contentIdsToDelete.clear();
			this.usersToFlag.clear();

		}
	}

	/**
	 * updates the system information for lastLogDate, lastTasId
	 */
	private void updateSystemInformation() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonDocumentForSystemInfo;
		try {
			jsonDocumentForSystemInfo = mapper.writeValueAsString(systemInfo);
			esClient.getClient()
			.prepareIndex(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, systemtHome+indexType)
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
			jsonDocument.put(this.systemUrlFieldName, systemtHome);
			long indexId = (systemtHome.hashCode()<<32)+Long.parseLong(jsonDocument.get(LuceneFieldNames.CONTENT_ID).toString());
			SharedResourceIndexUpdater.esClient
					.getClient()
					.prepareIndex(
							indexName,
							indexType,
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
		DeleteByQueryResponse response = SharedResourceIndexUpdater.esClient.getClient().prepareDeleteByQuery(indexName)
				.setTypes(indexType).setQuery(QueryBuilders.termQuery(LuceneFieldNames.USER_NAME, userName))
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
				.prepareDelete(indexName, indexType, String.valueOf(indexId))
				.setRefresh(true).execute().actionGet();
	}

	/**
	 * @return the iNDEX_TYPE
	 */
	public String getIndexType() {
		return this.indexType;
	}

	/**
	 * @param iNDEX_TYPE the iNDEX_TYPE to set
	 */
	public void setIndexType(String iNDEX_TYPE) {
		indexType = iNDEX_TYPE;
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
	 * @param esClient the esClient to set
	 */
	public void setEsClient(ESClient esClient) {
		SharedResourceIndexUpdater.esClient = esClient;
	}

	/**
	 * @return the esClient
	 */
	public ESClient getEsClient() {
		return SharedResourceIndexUpdater.esClient;
	}

	/**
	 * @return the systemtHome
	 */
	public static String getSystemtHome() {
		return systemtHome;
	}

	/**
	 * @param systemtHome the systemtHome to set
	 */
	public static void setSystemtHome(String systemtHome) {
		SharedResourceIndexUpdater.systemtHome = systemtHome;
	}
	
	/**
	 * sets the system informations to update
	 * @param lastTasId
	 * @param lastLogDate
	 */
	public void setSystemInformation(Integer lastTasId, Date lastLogDate){
		this.systemInfo.setLast_log_date(lastLogDate);
		this.systemInfo.setLast_tas_id(lastTasId);
		this.systemInfo.setPostType(indexType);
		this.systemInfo.setSystemUrl(systemtHome);
	}

}
