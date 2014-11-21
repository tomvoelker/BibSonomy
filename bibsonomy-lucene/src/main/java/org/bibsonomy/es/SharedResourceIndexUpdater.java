package org.bibsonomy.es;

import java.util.ArrayList;
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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

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

	private final String INDEX_NAME = ESConstants.INDEX_NAME;

	private String INDEX_TYPE;

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
			try {
				SearchRequestBuilder searchRequestBuilder = esClient
						.getClient().prepareSearch(INDEX_NAME);
				searchRequestBuilder.setTypes(INDEX_TYPE);
				searchRequestBuilder.setSearchType(SearchType.DEFAULT);
				searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
				searchRequestBuilder.setSize(1).setExplain(true);
				searchRequestBuilder.addSort(LuceneFieldNames.LAST_LOG_DATE,
						SortOrder.DESC);

				SearchResponse response = searchRequestBuilder.execute()
						.actionGet();

				if (response != null) {
					SearchHit hit = response.getHits().getAt(0);
					Map<String, Object> result = hit.getSource();
					String lastLogDateString = result.get(
							LuceneFieldNames.LAST_LOG_DATE).toString();
					return Long.parseLong(lastLogDateString);

				}
			} catch (IndexMissingException e) {
				log.error("IndexMissingException: " + e);
			}
			return Long.MAX_VALUE;
		}
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
			try {
				SearchRequestBuilder searchRequestBuilder = esClient
						.getClient().prepareSearch(INDEX_NAME);
				searchRequestBuilder.setTypes(INDEX_TYPE);
				searchRequestBuilder.setSearchType(SearchType.DEFAULT);
				searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
				searchRequestBuilder.setSize(1).setExplain(true);
				searchRequestBuilder.addSort(LuceneFieldNames.LAST_TAS_ID,
						SortOrder.DESC);

				SearchResponse response = searchRequestBuilder.execute()
						.actionGet();

				if (response != null) {
					SearchHit hit = response.getHits().getAt(0);
					Map<String, Object> result = hit.getSource();
					String lastTasIdString = result.get(
							LuceneFieldNames.LAST_TAS_ID).toString();
					return Integer.parseInt(lastTasIdString);

				}
			} catch (IndexMissingException e) {
				log.error("IndexMissingException: " + e);
			}
			return Integer.MAX_VALUE;
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
	 * @return the contentIdsToDelete
	 */
	public List<Integer> getContentIdsToDelete() {
		return this.contentIdsToDelete;
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
	public String getINDEX_NAME() {
		return this.INDEX_NAME;
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
					this.deleteIndexForContentId(contentId);
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
			// clear all cached data
			// ----------------------------------------------------------------
			this.esPostsToInsert.clear();
			this.contentIdsToDelete.clear();
			this.usersToFlag.clear();

		}
	}

	/**
	 * @param esPostsToInsert2
	 */
	@Override
	public void insertNewPosts(ArrayList<Map<String, Object>> esPostsToInsert2) {
		for (Map<String, Object> jsonDocument : esPostsToInsert2) {
			SharedResourceIndexUpdater.esClient
					.getClient()
					.prepareIndex(
							INDEX_NAME,
							INDEX_TYPE,
							jsonDocument.get(LuceneFieldNames.CONTENT_ID)
									.toString()).setSource(jsonDocument)
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
		DeleteByQueryResponse response = SharedResourceIndexUpdater.esClient.getClient().prepareDeleteByQuery(INDEX_NAME)
				.setTypes(INDEX_TYPE).setQuery(QueryBuilders.termQuery(LuceneFieldNames.USER_NAME, userName))
				.execute()
				.actionGet();
//		log.warn(response);
	}

	/**
	 * @param contentId
	 */

	@Override
	public void deleteIndexForContentId(Integer contentId) {
		@SuppressWarnings("unused")
		DeleteResponse response = esClient
				.getClient()
				.prepareDelete(INDEX_NAME, INDEX_TYPE, String.valueOf(contentId))
				.setRefresh(true).execute().actionGet();
	}

	/**
	 * @return the iNDEX_TYPE
	 */
	public String getINDEX_TYPE() {
		return this.INDEX_TYPE;
	}

	/**
	 * @param iNDEX_TYPE the iNDEX_TYPE to set
	 */
	public void setINDEX_TYPE(String iNDEX_TYPE) {
		INDEX_TYPE = iNDEX_TYPE;
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

}
