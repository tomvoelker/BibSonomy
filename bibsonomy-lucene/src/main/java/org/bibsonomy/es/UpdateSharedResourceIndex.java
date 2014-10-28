package org.bibsonomy.es;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
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
public class UpdateSharedResourceIndex<R extends Resource> {
	private static final Log log = LogFactory.getLog(UpdateSharedResourceIndex.class);


	private final String INDEX_NAME = "posts";

	private String TYPE_NAME;

	/** list posts to insert into index */
	private Set<Map<String, Object>> esPostsToInsert = new TreeSet<Map<String, Object>>();

	private final ESNodeClient esClient = new ESNodeClient();

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
	 * @return lastLogDate
	 */
	@SuppressWarnings("boxing")
	public long getLastLogDate() {
		synchronized (this) {

			if (this.lastLogDate != null) {
				return this.lastLogDate;
			}
			try {
				SearchRequestBuilder searchRequestBuilder = esClient
						.getClient().prepareSearch(INDEX_NAME);
				searchRequestBuilder.setTypes(TYPE_NAME);
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
	@SuppressWarnings("boxing")
	public Integer getLastTasId() {
		synchronized (this) {

			if (this.lastTasId != null) {
				return this.lastTasId;
			}
			try {
				SearchRequestBuilder searchRequestBuilder = esClient
						.getClient().prepareSearch(INDEX_NAME);
				searchRequestBuilder.setTypes(TYPE_NAME);
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
	public ESNodeClient getNodeClient() {
		return this.esClient;
	}

	/**
	 * @return the iNDEX_NAME
	 */
	public String getINDEX_NAME() {
		return this.INDEX_NAME;
	}

	/**
	 * @return the tYPE_NAME
	 */
	public String getTYPE_NAME() {
		return this.TYPE_NAME;
	}

	/**
	 * @return the esPostsToInsert
	 */
	public Set<Map<String, Object>> getEsPostsToInsert() {
		return this.esPostsToInsert;
	}

	/**
	 * @param postsToInsert
	 *            the esPostsToInsert to set
	 */
	public void setEsPostsToInsert(Set<Map<String, Object>> postsToInsert) {
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
					this.DeleteIndexForContentId(contentId);
					log.debug("deleted post " + contentId);
				}

				// remove spam posts from index
				for (final String userName : this.usersToFlag) {
					// final int cnt = purgeDocumentsForUser(userName);
					// log.debug("Purged " + cnt + " posts for user " +
					// userName);
					this.DeleteIndexForForUser(userName);
					log.debug("Purged posts for user " + userName);
				}
			}

			// ----------------------------------------------------------------
			// add cached posts to index
			// ----------------------------------------------------------------
			log.debug("Performing " + esPostsToInsert.size()
					+ " insert operations");
			if (this.esPostsToInsert.size() > 0) {
				this.InsertNewPosts(esPostsToInsert);
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
	 * @param postsToInsert
	 */
	private void InsertNewPosts(Set<Map<String, Object>> postsToInsert) {
		for (Map<String, Object> jsonDocument : postsToInsert) {
			esClient.getClient()
					.prepareIndex(
							INDEX_NAME,
							TYPE_NAME,
							jsonDocument.get(LuceneFieldNames.CONTENT_ID)
									.toString()).setSource(jsonDocument)
					.setRefresh(true).execute().actionGet();
		}

		log.info("post has been indexed.");
	}

	/**
	 * @param userName
	 */
	private void DeleteIndexForForUser(String userName) {
		// TODO Dont forget to refresh

	}

	/**
	 * @param contentId
	 */
	@SuppressWarnings("unused")
	private void DeleteIndexForContentId(Integer contentId) {
		DeleteResponse response = esClient
				.getClient()
				.prepareDelete(INDEX_NAME, TYPE_NAME, String.valueOf(contentId))
				.setRefresh(true).execute().actionGet();
	}

	/**
	 * @param postDoc
	 */
	public void insertDocument(Map<String, Object> postDoc) {
		esPostsToInsert.add(postDoc);
	}

	/**
	 * @param tYPE_NAME
	 *            the tYPE_NAME to set
	 */
	public void setTYPE_NAME(String tYPE_NAME) {
		TYPE_NAME = tYPE_NAME;
	}

	/**
	 * @param contentId
	 */
	public void deleteDocumentForContentId(Integer contentId) {
		synchronized(this) {
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
		synchronized(this) {
			this.usersToFlag.add(username);
		}
	}
	
	/**
	 * unflag given user as spammer - enabling further posts to be inserted 
	 * 
	 * @param userName
	 */
	public void unFlagUser(final String userName) {
		synchronized(this) {
			this.usersToFlag.remove(userName);
		}
	}
	

}
