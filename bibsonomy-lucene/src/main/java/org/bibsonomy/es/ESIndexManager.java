package org.bibsonomy.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class for every basic functionality on top of elasticsearch ee.g. resolving index names by alias names and locking indices
 *
 * @author lutful
 * @author jil
 */
public class ESIndexManager {
	private final Map<String, ReadWriteLock> locksByIndexName = new HashMap<String, ReadWriteLock>();
	private final ESClient esClient;
	private final String systemHome;
	private static final Log log = LogFactory.getLog(ESIndexManager.class);

	
	/**
	 * @param esClient
	 * @param systemHome
	 */
	public ESIndexManager(ESClient esClient, String systemHome) {
		this.esClient = esClient;
		this.systemHome = systemHome;
	}
	
	private ReadWriteLock getRwLock(String indexName) {
		ReadWriteLock rwLock = locksByIndexName.get(indexName);
		if (rwLock == null) {
			rwLock = new ReentrantReadWriteLock();
			locksByIndexName.put(indexName, rwLock);
		}
		return rwLock;
	}


	/**
	 * @return returns true if an active index exists
	 */
	public String getGlobalIndexNonExistanceError() {
		final ClusterStateResponse response = this.esClient.getClient().admin().cluster().prepareState().execute().actionGet(); 
		final ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliases = response.getState().metaData().getAliases(); 
		if(aliases.isEmpty()){
			return "No Index found!! Please generate Index";
		}
		final String bibtexNonExistanceError= this.getResourceIndexNonExistanceError(BibTex.class.getSimpleName());
		final String bookmarkNonExistanceError= this.getResourceIndexNonExistanceError(Bookmark.class.getSimpleName());
		final String goldStandardNonExistanceError= this.getResourceIndexNonExistanceError(GoldStandardPublication.class.getSimpleName());
		if(bibtexNonExistanceError!=null && bookmarkNonExistanceError!=null && goldStandardNonExistanceError!=null){
			return "No Index found for this system!! Please generate Index";
		}
		return null;
	}

	/**
	 * @param resourceType
	 * @return checks if documents for a particular resource exists
	 */
	private String getResourceIndexNonExistanceError(final String resourceType) {
		final String activeIndex = getThisSystemsIndexNameFromAlias(ESConstants.getGlobalAliasForResource(resourceType, true));
		final String backupIndex =  getThisSystemsIndexNameFromAlias(ESConstants.getGlobalAliasForResource(resourceType, false));
		if (activeIndex == null && backupIndex == null) {
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
	private String createIndex(final String resourceType) {
		final String indexName = ESConstants.getIndexNameWithTime(this.systemHome, resourceType);
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
	public String createTempIndex(final String resourceType){
		final String tempAlias =  ESConstants.getTempAliasForResource(resourceType);
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
	
	public List<String> getTempIndicesOfThisSystem(String resourceType) {
		final String tempAlias =  ESConstants.getTempAliasForResource(resourceType);
		return this.getThisSystemsIndexesFromAlias(tempAlias);
	}
	
	/**
	 * sets the alias to the index
	 * 
	 * @param alias
	 * @param indexName
	 */
	private void setAliasForIndex(String alias, String indexName){
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
	public boolean activateIndex(String indexName, String resourceType) {
		// with the following lock we make sure that nobody currently writes to the index which is to be activated
		try (final IndexLock indexLock = aquireLockForIndexName(indexName, false, null)) {
			// with the following lock we make sure that nobody depends on the currently active index anymore
			try (final IndexLock indexActivityLock = aquireLockForTheActiveIndexAlias(resourceType, true)) {
				setActiveIndexAlias(resourceType, indexName);
			}
		}
		return true;
	}

	private void setActiveIndexAlias(String resourceType, String nameOfIntexToBeActivated) {
		final String activeIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, true);
		final String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
		
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
	 * gets all the indexes set under the alias for the current system
	 *  
	 * @param alias
	 * @return return a list of indexes
	 */
	public List<String> getThisSystemsIndexesFromAlias(String alias) {
		final String thisSystemPrefix = this.systemHome.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		final List<String> rVal = getIndexesFromAlias(alias);
		for (Iterator<String> it = rVal.iterator(); it.hasNext();) {
			String indexName = it.next();
			if (!indexName.contains(thisSystemPrefix)) {
				it.remove();
			}
		}
		return rVal;
	}
	
	
	private List<String> getIndexesFromAlias(String alias){
		final List<String> indexes = new ArrayList<String>();
		final ImmutableOpenMap<String, AliasMetaData> indexToAliasesMap = this.esClient.getClient().admin().cluster() //
				.state(Requests.clusterStateRequest()) //
				.actionGet() //
				.getState() //
				.getMetaData() //
				.aliases().get(alias);
		if (indexToAliasesMap != null && !indexToAliasesMap.isEmpty()) {
			for (ObjectCursor<String> cursor : indexToAliasesMap.keys()) {
				indexes.add(cursor.value);
			}
		}
		return indexes;
	}
	
	/**
	 * gets the active indexName for the resource of the current system
	 * 
	 * @param resourceType
	 * @return returns the active indexName
	 */
	public String getActiveIndexnameForResource(String resourceType){
		return getThisSystemsIndexNameFromAlias(ESConstants.getGlobalAliasForResource(resourceType, true));
	}
	
	/**
	 * removes the temporary index's alias and adds the index to the set of backup indices that may later be taken by the updater for updating and activation
	 * 
	 * @param indexName
	 * @param resourceType 
	 */
	public void changeUnderConstructionStatus(final String indexName, final String resourceType){
		final String underConstructionAlias = ESConstants.getTempAliasForResource(resourceType);
		final String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
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
		IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
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
	 * @return
	 */
	public Client getClient() {
		return esClient.getClient();
	}

	

	/**
	 * @param resourceType
	 * @return
	 */
	public IndexLock aquireReadLockForTheActiveIndexAlias(String resourceType) {
		return aquireLockForTheActiveIndexAlias(resourceType, false);
	}
	
	private IndexLock aquireLockForTheActiveIndexAlias(String resourceType, boolean writeAccess) {
		// here we lock the alias name instead of the name of a real index because: a) active indices are never modified and b) an active index can only become inactive when a write lock on the active index alias is acquired 
		final String activeAliasName = ESConstants.getGlobalAliasForResource(resourceType, true);
		return aquireLockForIndexName(activeAliasName, writeAccess, null);
	}

	/**
	 * @param indexName
	 * @param writeAccess
	 * @return 
	 */
	protected IndexLock aquireLockForIndexName(String indexName, boolean writeAccess, Long maxWaitMillis) {
		final ReadWriteLock rwlock = getRwLock(indexName);
		final Lock lock = writeAccess ? rwlock.writeLock() : rwlock.readLock();
		if (maxWaitMillis == null) {
			return new IndexLock(indexName, lock);
		}
		try {
			return new IndexLock(indexName, lock, maxWaitMillis, TimeUnit.MILLISECONDS);
		} catch (LockFailedException e) {
			return null;
		}
	}

	/**
	 * @param resourceType
	 * @return
	 */
	public IndexLock aquireWriteLockForAnInactiveIndex(String resourceType) {
		final String inactiveAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
		
		for (int attempt = 0; attempt < 10; ++attempt) {
			// aquire read-lock on the alias name 
			try (final IndexLock indexActivityLock = aquireLockForTheActiveIndexAlias(resourceType, false)) {
				final String realIndexName = getThisSystemsIndexNameFromAlias(inactiveAlias);
				if (realIndexName == null) {
					return null;
				}
				return new IndexLock(realIndexName, getRwLock(realIndexName).writeLock(), 5, TimeUnit.MILLISECONDS);
			} catch (LockFailedException e) {
				log.error("timeout waiting for inactive index - attempt " + attempt + "/10", e);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
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
		searchRequestBuilder.setFrom(0).setSize(size).setExplain(true);

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
		} catch (Exception e) {
			log.error("cannot parse systeminformation for index " + indexName + ": " + json);
			return null;
		}
	}

	private Map<String, SystemInformation> getAllIndexSystemInformations(String resourceType, final String alias) {
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
	public Map<String, SystemInformation> getAllActiveIndexSystemInformations(String resourceType) {
		final String alias = ESConstants.getGlobalAliasForResource(resourceType, true);
		return getAllIndexSystemInformations(resourceType, alias);
	}
	
	/**
	 * @param resourceType
	 * @return
	 */
	public Map<String, SystemInformation> getAllInactiveIndexSystemInformations(String resourceType) {
		final String alias = ESConstants.getGlobalAliasForResource(resourceType, false);
		return getAllIndexSystemInformations(resourceType, alias);
	}
	
	/**
	 * @param resourceType
	 * @return
	 */
	public Map<String, SystemInformation> getAllGeneratingIndexSystemInformations(String resourceType) {
		final String alias = ESConstants.getTempAliasForResource(resourceType);
		return getAllIndexSystemInformations(resourceType, alias);
	}

	private Map<String, SystemInformation> getAllSystemInfosByAlias(String resourceType, final String alias) {
		return this.getAllSystemInfosAsObjects(QueryBuilders.matchQuery("postType", resourceType), 10000, alias);
	}

}
