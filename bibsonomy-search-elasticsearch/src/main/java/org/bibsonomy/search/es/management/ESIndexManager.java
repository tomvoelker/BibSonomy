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
package org.bibsonomy.search.es.management;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.util.LockAutoCloseable.LockFailedException;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class for every basic functionality on top of elasticsearch e.g. resolving
 * index names by alias names and locking indices
 *
 * @author lutful
 * @author jil
 */
@Deprecated // remove TODODZO
public class ESIndexManager {
	private static final Log log = LogFactory.getLog(ESIndexManager.class);
	
	// FIXME: synchronized map!
	private final Map<String, ReadWriteLock> locksByIndexName = new HashMap<String, ReadWriteLock>();
	private final ESClient esClient;
	private final URI systemHome;

	
	/**
	 * @param esClient
	 * @param systemHome
	 */
	public ESIndexManager(ESClient esClient, URI systemHome) {
		this.esClient = esClient;
		this.systemHome = systemHome;
	}
	
	private ReadWriteLock getRwLock(String indexName) {
		ReadWriteLock rwLock = this.locksByIndexName.get(indexName);
		if (rwLock == null) {
			rwLock = new ReentrantReadWriteLock();
			this.locksByIndexName.put(indexName, rwLock);
		}
		return rwLock;
	}

	/**
	 * @return returns the error message or null if everything seems ok
	 */
	public String getGlobalIndexNonExistanceError() {
		final ClusterStateResponse response = this.esClient.getClient().admin().cluster().prepareState().execute().actionGet();
		final ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliases = response.getState().metaData().getAliases();
		if (aliases.isEmpty()) {
			return "No Index found!! Please generate Index";
		}
		
		final String bibtexNonExistanceError = this.getResourceIndexNonExistanceError(BibTex.class);
		final String bookmarkNonExistanceError = this.getResourceIndexNonExistanceError(Bookmark.class);
		final String goldStandardNonExistanceError = this.getResourceIndexNonExistanceError(GoldStandardPublication.class);
		if (bibtexNonExistanceError != null && bookmarkNonExistanceError != null && goldStandardNonExistanceError != null){
			return "No Index found for this system!! Please generate Index";
		}
		return null;
	}

	/**
	 * @param resourceType
	 * @return checks if documents for a particular resource exists
	 */
	private String getResourceIndexNonExistanceError(final Class<? extends Resource> resourceType) {
		final String activeIndex = getThisSystemsIndexNameFromAlias(ElasticSearchUtils.getGlobalAliasForResource(resourceType, true));
		final String inactiveIndex =  getThisSystemsIndexNameFromAlias(ElasticSearchUtils.getGlobalAliasForResource(resourceType, false));
		if (activeIndex == null && inactiveIndex == null) {
			return "No index for \"" + resourceType	+ "\" of current system found!! Please re-generate Index";
		}
		return null;
	}

	/**
	 * only creates the index/ a temporary index based on the resource type
	 * does not set any alias
	 * 
	 * @param resourceType
	 * @return returns the index name
	 */
	private String createIndex(final Class<? extends Resource> resourceType) {
		final String indexName = ElasticSearchUtils.getIndexNameWithTime(this.systemHome, resourceType);
		log.info("creating index: " + indexName);
		final CreateIndexResponse createIndex = this.esClient.getClient().admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
		if (!createIndex.isAcknowledged()) {
			log.error("Error in creating index: " + indexName);
			return null;
		}
		return indexName;
	}
	
	/**
	 * creates a temporary index and sets an alias for it
	 * deletes any previously existing temporary index
	 * 
	 * @param resourceType
	 * @return returns the index name of the temporary index
	 */
	public String createTempIndex(final Class<? extends Resource> resourceType){
		final String tempAlias =  ElasticSearchUtils.getTempAliasForResource(resourceType);
		final List<String> prevTempIndexes = this.getThisSystemsIndexesFromAlias(tempAlias);
		for (String indexName : prevTempIndexes) {
			
			log.info("removing alias and index: " + tempAlias + "->" + indexName);
			final IndexLock lock = aquireLockForIndexName(indexName, true, 10000l);
			if (lock != null) {
				try {
					if (removeAlias(indexName, tempAlias) == false) {
						log.error("Error deleting the existing temp index alias: " + tempAlias + "->" + indexName);
						return null;
					}
					final DeleteIndexResponse deleteIndex = this.esClient.getClient().admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
					if (!deleteIndex.isAcknowledged()) {
						log.error("Error deleting the existing temp index: " + indexName);
						return null;
					}
				} finally {
					lock.close();
				}
			} else {
				log.warn("timeout waiting for lock to delete index " + indexName);
			}
		}
		final String indexName = this.createIndex(resourceType);
		if (indexName != null) {
			this.setAliasForIndex(tempAlias, indexName);
			return indexName;
		}
		return null;
	}
	
	public List<String> getTempIndicesOfThisSystem(Class<? extends Resource> resourceType) {
		final String tempAlias = ElasticSearchUtils.getTempAliasForResource(resourceType);
		return this.getThisSystemsIndexesFromAlias(tempAlias);
	}
	
	/**
	 * sets the alias to the index
	 * 
	 * @param alias
	 * @param indexName
	 */
	private void setAliasForIndex(String alias, String indexName) {
		final IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases() //
				.addAlias(indexName, alias) //
				.execute() //
				.actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("Error in setting alias for Index: " + indexName);
			return;
		}
	}
	
	/**
	 * @param indexName
	 * @param resourceType
	 * @return true if switch successful
	 */
	public boolean activateIndex(String indexName, Class<? extends Resource> resourceType) {
		// with the following lock we make sure that nobody currently writes to the index which is to be activated
		try (final IndexLock indexLock = aquireLockForIndexName(indexName, false, null)) {
			// with the following lock we make sure that nobody depends on the currently active index anymore
			try (final IndexLock indexActivityLock = aquireLockForTheActiveIndexAlias(resourceType, true)) {
				setActiveIndexAlias(resourceType, indexName);
			}
		}
		return true;
	}

	private void setActiveIndexAlias(Class<? extends Resource> resourceType, String nameOfIntexToBeActivated) {
		final String activeIndexAlias = ElasticSearchUtils.getGlobalAliasForResource(resourceType, true);
		final String backupIndexAlias = ElasticSearchUtils.getGlobalAliasForResource(resourceType, false);
		
		final String oldActiveIndexName = getThisSystemsIndexNameFromAlias(activeIndexAlias);
		removeAlias(nameOfIntexToBeActivated, backupIndexAlias);
		if (oldActiveIndexName != null) {
			removeAlias(oldActiveIndexName, activeIndexAlias);
			setAliasForIndex(backupIndexAlias, oldActiveIndexName);
		}
		setAliasForIndex(activeIndexAlias, nameOfIntexToBeActivated);
	}

	
	private String getThisSystemsIndexNameFromAlias(String aliasName) {
		List<String> indexList = getThisSystemsIndexesFromAlias(aliasName);
		if (indexList.size() > 1) {
			log.warn("local system has more than one index for " + aliasName + ": " + indexList);
		}
		if (indexList.size() < 1) {
			log.warn("local system has no index for " + aliasName);
			return null;
		}
		return indexList.get(0);
	}
	
	
	
	/**
	 * gets the active indexName for the resource of the current system
	 * 
	 * @param resourceType
	 * @return returns the active indexName
	 */
	public String getActiveIndexnameForResource(Class<? extends Resource> resourceType) {
		return this.getThisSystemsIndexNameFromAlias(ElasticSearchUtils.getGlobalAliasForResource(resourceType, true));
	}
	
	/**
	 * removes the temporary index's alias and adds the index to the set of backup indices that may later be taken by the updater for updating and activation
	 * 
	 * @param indexName
	 * @param resourceType 
	 */
	public void changeUnderConstructionStatus(final String indexName, final Class<? extends Resource> resourceType){
		final String underConstructionAlias = ElasticSearchUtils.getTempAliasForResource(resourceType);
		final String backupIndexAlias = ElasticSearchUtils.getGlobalAliasForResource(resourceType, false);
		final IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
				.removeAlias(indexName, underConstructionAlias)
				.addAlias(indexName, backupIndexAlias)
				.execute()
				.actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("Error in removing alias of index: "+ indexName);
		}
	}
	
	/**
	 * removes one particular alias linking one aliasName to one indexName
	 * @param indexName
	 * @param alias
	 * @return
	 */
	public boolean removeAlias(final String indexName, final String alias){
		final IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
				.removeAlias(indexName, alias)
				.execute()
				.actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("Error in removing alias of index: "+ indexName);
			return false;
		}
		return true;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return esClient.getClient();
	}

	/**
	 * @param resourceType
	 * @return
	 */
	public IndexLock aquireReadLockForTheActiveIndexAlias(Class<? extends Resource> resourceType) {
		return aquireLockForTheActiveIndexAlias(resourceType, false);
	}
	
	private IndexLock aquireLockForTheActiveIndexAlias(Class<? extends Resource> resourceType, boolean writeAccess) {
		// here we lock the alias name instead of the name of a real index because: a) active indices are never modified and b) an active index can only become inactive when a write lock on the active index alias is acquired 
		final String activeAliasName = ElasticSearchUtils.getGlobalAliasForResource(resourceType, true);
		return aquireLockForIndexName(activeAliasName, writeAccess, null);
	}

	/**
	 * @param indexName
	 * @param writeAccess
	 * @param maxWaitMillis 
	 * @return 
	 */
	public IndexLock aquireLockForIndexName(String indexName, boolean writeAccess, Long maxWaitMillis) {
		final ReadWriteLock rwlock = getRwLock(indexName);
		final Lock lock = writeAccess ? rwlock.writeLock() : rwlock.readLock();
		if (maxWaitMillis == null) {
			return new IndexLock(indexName, lock);
		}
		try {
			return new IndexLock(indexName, lock, maxWaitMillis.longValue(), TimeUnit.MILLISECONDS);
		} catch (final LockFailedException e) {
			return null;
		}
	}

	/**
	 * @param resourceType
	 * @return
	 */
	public IndexLock aquireWriteLockForAnInactiveIndex(Class<? extends Resource> resourceType) {
		final String inactiveAlias = ElasticSearchUtils.getGlobalAliasForResource(resourceType, false);
		
		for (int attempt = 0; attempt < 10; ++attempt) {
			// aquire read-lock on the alias name 
			try (final IndexLock indexActivityLock = aquireLockForTheActiveIndexAlias(resourceType, false)) {
				final String realIndexName = getThisSystemsIndexNameFromAlias(inactiveAlias);
				if (realIndexName == null) {
					return null;
				}
				return new IndexLock(realIndexName, getRwLock(realIndexName).writeLock(), 5, TimeUnit.MILLISECONDS);
			} catch (final LockFailedException e) {
				log.error("timeout waiting for inactive index - attempt " + attempt + "/10", e);
			}
			try {
				Thread.sleep(200);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("got interrupted while waiting for inactive index");
			}
		}
		throw new RuntimeException("too many timeouts waiting for inactive index");
	}

	public Map<String,SystemInformation> getAllSystemInfosAsObjects(final QueryBuilder query, final int size, String indexName) {
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.esClient.waitForReadyState();

		final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(indexName);
		searchRequestBuilder.setTypes(ESConstants.SYSTEM_INFO_INDEX_TYPE);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setQuery(query);
		searchRequestBuilder.setFrom(0).setSize(size).setExplain(true); // FIXME: remove explain

		final SearchResponse response = searchRequestBuilder.execute().actionGet();

		final Map<String, SystemInformation> rVal = new TreeMap<String, SystemInformation>();
		if (response != null) {
			for (final SearchHit hit : response.getHits()) {
				rVal.put(hit.getIndex(), parseSystemInformation(hit.getSourceAsString(), indexName));
			}
		}
		
		return rVal;
	}

	private static SystemInformation parseSystemInformation(String json, String indexName) {
		try {
			return new ObjectMapper().readValue(json, SystemInformation.class);
		} catch (final Exception e) {
			log.error("cannot parse systeminformation for index " + indexName + ": " + json);
			return null;
		}
	}

	private Map<String, SystemInformation> getAllIndexSystemInformations(Class<? extends Resource> resourceType, final String alias) {
		final Map<String, SystemInformation> rVal = new TreeMap<>();
		for (String indexName : getIndexesFromAlias(alias)) {
			rVal.putAll(getAllSystemInfosByAlias(resourceType, indexName));
		}
		return rVal;
	}

	/**
	 * @param resourceType
	 * @return
	 */
	public Map<String, SystemInformation> getAllActiveIndexSystemInformations(Class<? extends Resource> resourceType) {
		final String alias = ElasticSearchUtils.getGlobalAliasForResource(resourceType, true);
		return getAllIndexSystemInformations(resourceType, alias);
	}
	
	/**
	 * @param resourceType
	 * @return
	 */
	public Map<String, SystemInformation> getAllInactiveIndexSystemInformations(Class<? extends Resource> resourceType) {
		final String alias = ElasticSearchUtils.getGlobalAliasForResource(resourceType, false);
		return getAllIndexSystemInformations(resourceType, alias);
	}
	
	/**
	 * @param resourceType
	 * @return
	 */
	public Map<String, SystemInformation> getAllGeneratingIndexSystemInformations(Class<? extends Resource> resourceType) {
		final String alias = ElasticSearchUtils.getTempAliasForResource(resourceType);
		return getAllIndexSystemInformations(resourceType, alias);
	}

	private Map<String, SystemInformation> getAllSystemInfosByAlias(Class<? extends Resource> resourceType, final String alias) {
		return this.getAllSystemInfosAsObjects(QueryBuilders.matchQuery("postType", ResourceFactory.getResourceName(resourceType)), 10000, alias); // TODO: constant: postType! 
	}
}