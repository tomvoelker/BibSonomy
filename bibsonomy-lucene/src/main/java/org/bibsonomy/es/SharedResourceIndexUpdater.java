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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.util.GroupUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

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

	private final String indexName = ESConstants.INDEX_NAME;

	private String resourceType;

	private final SystemInformation systemInfo = new SystemInformation();
	/** The Url of the project home */
	private final String systemHome;

	/** list posts to insert into index */
	private ArrayList<Map<String, Object>> esPostsToInsert;
	/** the node client */
	// private final ESNodeClient esClient = new ESNodeClient();
	/** the transport client */
	private ESClient esClient;

	/** list containing content ids of cached delete operations */
	protected List<Integer> contentIdsToDelete;
	
	/** converts post model objects to documents of the index structure */
	private final LuceneResourceConverter<R> resourceConverter;
	
	/**
	 * 
	 */
	protected Set<String> usersToFlag;
	
	/** the database manager */
	protected LuceneDBInterface<R> dbLogic;

	/**
	 * @param systemHome
	 */
	public SharedResourceIndexUpdater(final String systemHome, final LuceneResourceConverter<R> resourceConverter) {
		this.systemHome = systemHome;
		this.resourceConverter = resourceConverter;
		this.contentIdsToDelete = new LinkedList<Integer>();
		this.esPostsToInsert = new ArrayList<Map<String, Object>>();
		this.usersToFlag = new TreeSet<String>();

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
			final Map<String, Object> result = this.getSystemInfos();
			if (result != null) {
				final Object val = result.get(fieldToRetrieve);
				if (val == null) {
					return null;
				}
				return val.toString();
			}
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e.getDetailedMessage() + " -> returning null", e);
		}
		return null;
	}

	public List<Map<String, Object>> getAllSystemInfos() {
		return this.getAllSystemInfosInternal(QueryBuilders.matchQuery("postType", this.resourceType), 200);
	}

	private List<Map<String, Object>> getAllSystemInfosInternal(final QueryBuilder query, final int size) {
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.esClient.getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();

		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(this.indexName);
		searchRequestBuilder.setTypes(ESConstants.SYSTEM_INFO_INDEX_TYPE);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(query);
		searchRequestBuilder.setFrom(0).setSize(size).setExplain(true);

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		final List<Map<String, Object>> rVal = new ArrayList<>();

		for (final SearchHit hit : response.getHits()) {
			rVal.add(hit.getSource());
		}

		return rVal;
	}

	public Map<String, Object> getSystemInfos() {
		final List<Map<String, Object>> l = this.getAllSystemInfosInternal(QueryBuilders.idsQuery().ids(this.systemHome + this.resourceType), 1);
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

		}
	}

	/**
	 * @param contentId
	 * @return
	 */
	private long calculateIndexId(final Number contentId) {
		return calculateIndexId(contentId, this.systemHome);
	}

	protected static long calculateIndexId(final Number contentId, final String systemHome) {
		return (((long) systemHome.hashCode()) << 32l) + contentId.longValue();
	}

	/**
	 * updates the system information for lastLogDate, lastTasId
	 */
	public void flushSystemInformation() {
		final ObjectMapper mapper = new ObjectMapper();
		String jsonDocumentForSystemInfo;
		try {
			jsonDocumentForSystemInfo = mapper.writeValueAsString(this.systemInfo);
			this.esClient.getClient().prepareIndex(this.indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, this.systemHome + this.resourceType).setSource(jsonDocumentForSystemInfo).execute().actionGet();
		} catch (final JsonProcessingException e) {
			log.error("Failed to convert SystemInformation into a JSON", e);
		}
	}

	/**
	 * @param esPostsToInsert2
	 */
	private void insertNewPosts(final ArrayList<Map<String, Object>> esPostsToInsert2) {
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.esClient.getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();

		for (final Map<String, Object> jsonDocument : esPostsToInsert2) {
			jsonDocument.put(ESConstants.SYSTEM_URL_FIELD_NAME, this.systemHome);
			final long indexId = this.calculateIndexId(Long.parseLong(jsonDocument.get(LuceneFieldNames.CONTENT_ID).toString()));
			insertPostDocument(jsonDocument, String.valueOf(indexId));
		}

		log.info("post has been indexed.");
	}

	private void insertPostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		this.esClient.getClient().prepareIndex(this.indexName, this.resourceType, indexIdStr).setSource(jsonDocument).setRefresh(true).execute().actionGet();
	}
	
	private void updatePostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		this.esClient.getClient().prepareUpdate(this.indexName, this.resourceType, indexIdStr).setDoc(jsonDocument).setRefresh(true).execute().actionGet();
	}

	/**
	 * @param userName
	 */
	@Override
	public void deleteIndexForUser(final String userName) {

		this.esClient.getClient().prepareDeleteByQuery(this.indexName).setTypes(this.resourceType).setQuery(QueryBuilders.termQuery(LuceneFieldNames.USER_NAME, userName)).execute().actionGet();
	}

	/**
	 * @param indexId
	 */

	@Override
	public void deleteIndexForIndexId(final long indexId) {
		this.esClient.getClient().prepareDelete(this.indexName, this.resourceType, String.valueOf(indexId)).setRefresh(true).execute().actionGet();
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
	public void setResourceType(final String iNDEX_TYPE) {
		this.resourceType = iNDEX_TYPE;
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
	public void setSystemInformation(final IndexUpdaterState state) {
		this.systemInfo.setUpdaterState(state);
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#updateIndexWithPersonRelation(java.lang.String, java.util.List)
	 */
	@Override
	public void updateIndexWithPersonRelation(String interhash, List<ResourcePersonRelation> newRels) {
			
		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(ESConstants.INDEX_NAME);
		searchRequestBuilder.setTypes(this.resourceType);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(QueryBuilders.matchQuery("interhash", interhash));
		searchRequestBuilder.setExplain(true);

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				this.resourceConverter.setPersonFields(doc, newRels);
				this.updatePostDocument(doc, hit.getId());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#updateIndexWithPersonNameInfo(org.bibsonomy.model.PersonName, org.apache.commons.collections.LRUMap)
	 */
	@Override
	public void updateIndexWithPersonNameInfo(PersonName name, LRUMap updatedInterhashes) {
		final String personId = name.getPersonId();
		updateIndexForPersonWithId(updatedInterhashes, personId);
	}

	private void updateIndexForPersonWithId(LRUMap updatedInterhashes, final String personId) {
		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(ESConstants.INDEX_NAME);
		searchRequestBuilder.setTypes(this.resourceType);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(QueryBuilders.matchQuery(ESConstants.PERSON_ENTITY_IDS_FIELD_NAME, personId));
		searchRequestBuilder.setExplain(true);

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				final String interhash = (String) doc.get("interhash");
				if (updatedInterhashes.put(interhash, interhash) == null) {
					List<ResourcePersonRelation> newRels = this.dbLogic.getResourcePersonRelationsByPublication(interhash);
					this.updateIndexWithPersonRelation(interhash, newRels);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#updateIndexWithPersonInfo(org.bibsonomy.model.Person, org.apache.commons.collections.LRUMap)
	 */
	@Override
	public void updateIndexWithPersonInfo(Person per, LRUMap updatedInterhashes) {
		updateIndexForPersonWithId(updatedInterhashes, per.getPersonId());
	}

	public LuceneDBInterface<R> getDbLogic() {
		return this.dbLogic;
	}

	public void setDbLogic(LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

}
