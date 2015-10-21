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
package org.bibsonomy.search.es.update;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.index.ResourceConverter;
import org.bibsonomy.search.es.management.ESIndexManager;
import org.bibsonomy.search.es.management.IndexLock;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.IndexUpdater;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.util.ValidationUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
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
@Deprecated // TODO: replace with ElasticSearchIndexUpdater
public class SharedResourceIndexUpdater<R extends Resource> implements IndexUpdater<R>, AutoCloseable {
	private static final Log log = LogFactory.getLog(SharedResourceIndexUpdater.class);

	private final Class<R> resourceType;

	private final ESIndexManager esIndexManager;
	
	private final SystemInformation systemInfo = new SystemInformation();
	
	/** The Url of the project home */
	private final URI systemHome;

	/** list posts to insert into index */
	private List<Map<String, Object>> esPostsToInsert;

	/** the Elasticsearch client */
	@Deprecated
	private ESClient esClient;

	/** list containing content ids of cached delete operations */
	protected List<Integer> contentIdsToDelete;
	
	/** converts post model objects to documents of the index structure */
	private final ResourceConverter<R> resourceConverter;
	
	/** the users to flag */
	protected Set<String> usersToFlag;
	
	/** the database manager */
	protected SearchDBInterface<R> dbLogic;

	private final SharedIndexUpdatePlugin<R> plugin;

	private final IndexLock lockOfIndexBeingUpdated;

	/**
	 * @param esClient 
	 * @param systemHome
	 * @param resourceType
	 * @param resourceConverter 
	 * @param lockOfIndexBeingUpdated 
	 * @param plugin 
	 * @param nameOfIndexBeingUpdated 
	 */
	public SharedResourceIndexUpdater(final ESClient esClient, final URI systemHome, Class<R> resourceType, final ResourceConverter<R> resourceConverter, final IndexLock lockOfIndexBeingUpdated, final SharedIndexUpdatePlugin<R> plugin) {
		this.systemHome = systemHome;
		this.resourceConverter = resourceConverter;
		this.lockOfIndexBeingUpdated = lockOfIndexBeingUpdated;
		this.plugin = plugin;
		this.contentIdsToDelete = new LinkedList<Integer>();
		this.esPostsToInsert = new ArrayList<Map<String, Object>>();
		this.usersToFlag = new TreeSet<String>();
		this.resourceType =  resourceType;
		this.esClient = esClient;
		this.esIndexManager = new ESIndexManager(esClient, systemHome);
	}

	/**
	 * @return lastLogDate
	 */
	@Override
	public Date getLastLogDate() {
		final SystemInformation sysinfos = getSingleSystemInfos();
		if (sysinfos == null) {
			log.error("no lastLogDate for index " + this.lockOfIndexBeingUpdated.getIndexName());
			return null;
		}
		return sysinfos.getUpdaterState().getLast_log_date();
	}
	
	private SystemInformation getSingleSystemInfos() {
		final Collection<SystemInformation> list = this.esIndexManager.getAllSystemInfosAsObjects(QueryBuilders.matchQuery("postType", getResorceTypeAsString()), 2, this.lockOfIndexBeingUpdated.getIndexName()).values();
		if (!ValidationUtils.present(list)) {
			throw new NoSuchElementException("no systeminfos for index " + this.lockOfIndexBeingUpdated.getIndexName());
		}
		if (list.size() > 1) {
			throw new IllegalStateException("" + list.size() + " systeminfos for index " + this.lockOfIndexBeingUpdated.getIndexName());
		}
		return list.iterator().next();
	}
	
	/**
	 * @return lastTasId
	 */
	@Override
	public Integer getLastTasId() {
		SystemInformation sysinfos = getSingleSystemInfos();
		if (sysinfos == null) {
			log.error("no lastTasId  -> starting from the very beginning");
			return Integer.valueOf(Integer.MIN_VALUE);
		}
		return sysinfos.getUpdaterState().getLast_tas_id();
	}

	/**
	 * @param contentIdsToDelete
	 *        the contentIdsToDelete to set
	 */
	@Override
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
	 * @return the esPostsToInsert
	 */
	public List<Map<String, Object>> getEsPostsToInsert() {
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
			try {
				if ((this.contentIdsToDelete.size() > 0) || (this.usersToFlag.size() > 0)) {
					// remove each cached post from index
					for (final Integer contentId : this.contentIdsToDelete) {
						final long indexID = calculateIndexId(contentId, this.systemHome);
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
			} catch (JsonProcessingException e) {
				log.error("unable to convert the post into JSON document", e);
			}
		}
	}

	/**
	 * @param contentId
	 * @param systemHome
	 * @return returns the indexId for a document
	 */
	public static long calculateIndexId(final Number contentId, final URI systemHome) {
		return (((long) systemHome.hashCode()) << 32l) + contentId.longValue();
	}

	/**
	 * updates the system information for lastLogDate, lastTasId
	 * @param indexName 
	 * @throws JsonProcessingException 
	 */
	public void flushSystemInformation() throws JsonProcessingException {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonDocumentForSystemInfo = mapper.writeValueAsString(this.systemInfo);
			final IndexResponse res = this.esClient.getClient().prepareIndex(this.lockOfIndexBeingUpdated.getIndexName(), ESConstants.SYSTEM_INFO_INDEX_TYPE, ElasticSearchUtils.getIndexName(this.systemHome, this.resourceType)).setSource(jsonDocumentForSystemInfo).execute().actionGet();
			if ((res == null) || !ValidationUtils.present(res.getId())) {
				throw new RuntimeException("failed to save systeminformation for index " + this.lockOfIndexBeingUpdated.getIndexName());
			}
			
			log.info("updated systeminformation of index " + this.lockOfIndexBeingUpdated.getIndexName() + " to " + jsonDocumentForSystemInfo);
		} catch (final JsonProcessingException e) {
			log.error("Failed to convert SystemInformation into JSON", e);
		}
	}

	/**
	 * @param esPostsToInsert2
	 * @param indexName 
	 */
	private void insertNewPosts(final List<Map<String, Object>> esPostsToInsert2) {
		this.esClient.waitForReadyState();

		for (final Map<String, Object> jsonDocument : esPostsToInsert2) {
			// TODO: move to converter
			jsonDocument.put(Fields.SYSTEM_URL, this.systemHome);
			final long indexId = calculateIndexId(Long.parseLong(jsonDocument.get(Fields.CONTENT_ID).toString()), this.systemHome);
			// TODO: this method needs to support the additional parameter?
			insertPostDocument(jsonDocument, String.valueOf(indexId));
		}
	}

	private void insertPostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		this.esClient.getClient().prepareIndex(this.lockOfIndexBeingUpdated.getIndexName(), getResorceTypeAsString(), indexIdStr).setSource(jsonDocument).setRefresh(true).execute().actionGet();
	}
	
	private void updatePostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		this.esClient.getClient().prepareUpdate(this.lockOfIndexBeingUpdated.getIndexName(), getResorceTypeAsString(), indexIdStr).setDoc(jsonDocument).setRefresh(true).execute().actionGet();
	}

	/**
	 * @return
	 */
	protected String getResorceTypeAsString() {
		return ResourceFactory.getResourceName(this.resourceType);
	}

	/**
	 * @param userName
	 */
	@Override
	public void deleteIndexForUser(final String userName){
		this.esClient.getClient().prepareDeleteByQuery(this.lockOfIndexBeingUpdated.getIndexName()).setTypes(getResorceTypeAsString()).setQuery(QueryBuilders.filteredQuery( QueryBuilders.termQuery(Fields.USER_NAME, userName), FilterBuilders.termFilter(Fields.SYSTEM_URL, systemHome))).execute().actionGet();
	}

	/**
	 * @param indexId
	 */
	@Override
	public void deleteIndexForIndexId(final long indexId) {
		this.esClient.getClient().prepareDelete(this.lockOfIndexBeingUpdated.getIndexName(), getResorceTypeAsString(), String.valueOf(indexId)).setRefresh(true).execute().actionGet();
	}

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return getResorceTypeAsString();
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
	@Override
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
	@Override
	public void unFlagUser(final String userName) {
		synchronized (this) {
			this.usersToFlag.remove(userName);
		}
	}

	/**
	 * sets the system informations to update
	 * 
	 * @param state
	 */
	@Override
	public void setSystemInformation(final SearchIndexState state) {
		this.systemInfo.setUpdaterState(state);
		this.systemInfo.setPostType(getResorceTypeAsString());
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
	public void insertDocument(SearchPost<R> post, final Date currentLogDate) {
		// TODO: move this call to a filter that can be configured for every index
		if (post.getGroups().contains(GroupUtils.buildPublicGroup())) {
			if (currentLogDate != null) {
				post.setLastLogDate(currentLogDate);
			}
			final Map<String, Object> postDoc = this.resourceConverter.convert(post);
			this.insertDocument(postDoc);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#updateIndexWithPersonRelation(java.lang.String, java.util.List)
	 */
	@Override
	public void updateIndexWithPersonRelation(String interhash, List<ResourcePersonRelation> newRels) {
		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(this.lockOfIndexBeingUpdated.getIndexName());
		searchRequestBuilder.setTypes(ResourceFactory.getResourceName(this.resourceType));
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(QueryBuilders.constantScoreQuery(FilterBuilders.andFilter(FilterBuilders.termFilter(Fields.SYSTEM_URL, systemHome), FilterBuilders.termFilter("interhash", interhash))));
		searchRequestBuilder.setExplain(true); // TODO: remove

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		int numUpdatedPosts = 0;
		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				// FIXME: readd this.resourceConverter.setPersonFields(doc, newRels); TODODZO
				this.updatePostDocument(doc, hit.getId());
				numUpdatedPosts++;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("updating " + this.toString() + " with " + numUpdatedPosts + " posts having interhash=" + interhash);
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
		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(this.lockOfIndexBeingUpdated.getIndexName());
		searchRequestBuilder.setTypes(ResourceFactory.getResourceName(this.resourceType));
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(QueryBuilders.constantScoreQuery(FilterBuilders.andFilter(FilterBuilders.termFilter(Fields.SYSTEM_URL, systemHome), FilterBuilders.termFilter(Fields.PERSON_ENTITY_IDS_FIELD_NAME, personId))));

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				final Map<String, Object> doc = hit.getSource();
				final String interhash = (String) doc.get("interhash");
				if (updatedInterhashes.put(interhash, interhash) == null) {
					final List<ResourcePersonRelation> newRels = this.dbLogic.getResourcePersonRelationsByPublication(interhash);
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

	public void setDbLogic(SearchDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#onUpdateComplete()
	 */
	@Override
	public void onUpdateComplete() {
		this.lockOfIndexBeingUpdated.close();
		this.plugin.activateIndex(this.lockOfIndexBeingUpdated.getIndexName());
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#getUpdaterState()
	 */
	@Override
	public SearchIndexState getUpdaterState() {
		// SystemInformation sysi = getSingleSystemInfos();
		// if (sysi != null) {
		//	return sysi.getUpdaterState();
		// }
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + ": " + this.lockOfIndexBeingUpdated + "]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() {
		this.lockOfIndexBeingUpdated.close();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#closeUpdateProcess()
	 */
	@Override
	public void closeUpdateProcess() {
		close();
	}
}
