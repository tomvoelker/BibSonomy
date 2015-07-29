package org.bibsonomy.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
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
	public String getResourceIndexNonExistanceError(final String resourceType) {
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
	private String createIndex(final String resourceType){
		String indexName = ESConstants.getIndexNameWithTime(this.systemHome, resourceType);
		final CreateIndexResponse createIndex = this.esClient.getClient().admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
		if (!createIndex.isAcknowledged()) {
			log.error("Error in creating Index: " + indexName);
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
		if(!prevTempIndexes.isEmpty()){
			for(String indexName: prevTempIndexes){
				if (removeAlias(indexName, tempAlias) == false) {
					log.error("Error deleting the existing temp index alias: " + tempAlias + "->" + indexName);
					return null;
				}
				final DeleteIndexResponse deleteIndex = this.esClient.getClient().admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
				if (!deleteIndex.isAcknowledged()) {
					log.error("Error deleting the existing temp index: " + indexName);
					return null;
				}
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
	 * checks if there are any active and backup index for the pair for the resource type 
	 * 
	 * @param resourceType
	 * @param isActiveIndex 
	 * @return returns the index name if exists
	 */
	public String indexExist(final String resourceType, final boolean isActiveIndex){
		return this.getThisSystemsIndexNameFromAlias(ESConstants.getGlobalAliasForResource(resourceType, isActiveIndex));
	}
	
	/**
	 * sets the alias to the index
	 * 
	 * @param alias
	 * @param indexName
	 */
	public void setAliasForIndex(String alias, String indexName){
		IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
                .addAlias(indexName, alias)
                .execute()
                .actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("Error in setting alias for Index: " + indexName);
			return;
		}
	}
	/**
	 * switches to the backup index to active and returns 
	 * the previously active index name for update
	 * @param activeIndexAlias
	 * 			the active alias name
	 * @param resourceType
	 * 			the resource type
	 * @return returns the previously active index name
	 */
	public String switchToBackupIndexByAlias(String activeIndexAlias, String resourceType){	
		String activeIndexName = null;
		String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
		activeIndexName = this.getThisSystemsIndexNameFromAlias(activeIndexAlias);
		String backupIndexName  = this.getThisSystemsIndexNameFromAlias(backupIndexAlias);
		IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
                .removeAlias(activeIndexName, activeIndexAlias)
                .addAlias(activeIndexName, backupIndexAlias)
                .removeAlias(backupIndexName, backupIndexAlias)
                .addAlias(backupIndexName, activeIndexAlias)
                .execute()
                .actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("Error in switching to backup index");
		}
		return activeIndexName;
	}
	
	/**
	 * @param indexName
	 * @param resourceType
	 * @return true if switch successful
	 */
	public boolean activateIndex(String indexName, String resourceType) {
		
		try (final IndexLock indexLock = aquireLockForIndexName(indexName, false)) {
			// inside this lock we can be sure that nobody currently writes to the index which is to be activated
			try (final IndexLock indexActivityLock = aquireWriteLockForTheActiveIndex(resourceType)) {
				// inside this lock we can be sure that nobody changes this system's active alias
				final String activeIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, true);
				final String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
				
				final String oldActiveIndexName = getThisSystemsIndexNameFromAlias(activeIndexAlias);
				removeAlias(indexName, backupIndexAlias);
				if (oldActiveIndexName != null) {
					removeAlias(oldActiveIndexName, activeIndexAlias);
					setAliasForIndex(backupIndexAlias, oldActiveIndexName);
				}
				setAliasForIndex(activeIndexAlias, indexName);
			}
		}
		return true;
	}
	
	/**
	 * @param indexName
	 * @param resourceType
	 * @return true if switch successful
	 */
	public boolean switchToBackupIndex(String indexName, String resourceType){
		/*
		 * get the index for this system from the alias and compare
		 */
		final String activeIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, true);
		final String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);

		if(indexName.equalsIgnoreCase(this.getThisSystemsIndexNameFromAlias(activeIndexAlias))){
			String backupIndexName  = this.getThisSystemsIndexNameFromAlias(backupIndexAlias);
			IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
	                .removeAlias(indexName, activeIndexAlias)
	                .addAlias(indexName, backupIndexAlias)
	                .removeAlias(backupIndexName, backupIndexAlias)
	                .addAlias(backupIndexName, activeIndexAlias)
	                .execute()
	                .actionGet();
			if (!aliasReponse.isAcknowledged()) {
				log.error("Error in switching to backup index!");
				return false;
			}
			return true;
		}
		log.error("Wrong Index name!! Error in switching to backup index!");
		return false;
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
	public List<String> getIndexesFromAlias(String alias){
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
	 * gets the list of aliases for the index
	 * 
	 * @return returns the map of [index => alias]
	 */
	public Map<String, String> getIndexToAliasMap(){
		// Get the map of [alias => [index => metadata, ...], ...]
		ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliasesAndIndices = 
		        esClient.getClient().admin().cluster()
		        .prepareState().execute().actionGet().getState()
		        .getMetaData().getAliases();
		Map<String, String> aliasForIndex = new HashMap<>();
	
		// Convert it to a map of [index => alias, ...]
		for (String alias : aliasesAndIndices.keys().toArray(String.class)) {
		    ImmutableOpenMap<String, AliasMetaData> innerMap = aliasesAndIndices.get(alias);
		    for (String index : innerMap.keys().toArray(String.class)) {
		    	if(aliasForIndex.containsKey(index)){
		    		String aliases = aliasForIndex.get(index);
		    		aliases += ";" +alias;
			        aliasForIndex.put(index, aliases);
		    	}else{
		    		aliasForIndex.put(index, alias);
		    	}
		    }
		}
		return aliasForIndex;
	}
	
	/**
	 * removes aliases of the indexes from given list 
	 * 
	 * @param listOfIndexes
	 * @return true if all removal were successful
	 */
	public boolean removeAliases(List<String> listOfIndexes){
		Map<String, String> indexToAlias = this.getIndexToAliasMap();
		for(String indexName:listOfIndexes){
			String aliases = indexToAlias.get(indexName);
			if(!aliases.contains(";")){
				if(!removeAlias(indexName, aliases)){
					return false;
				}
			}else{
				String[] aliasArray = aliases.split(";");
				for(String alias: aliasArray){
					if(!removeAlias(indexName, alias)){
						return false;
					}
				}
			}
		}
		return true;
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
	public IndexLock aquireReadLockForTheActiveIndex(String resourceType) {
		return acquireLockForTheActiveIndex(resourceType, false);
	}
	
	/**
	 * @param resourceType
	 * @return
	 */
	public IndexLock aquireWriteLockForTheActiveIndex(String resourceType) {
		return acquireLockForTheActiveIndex(resourceType, true);
	}
	
	private IndexLock acquireLockForTheActiveIndex(String resourceType, boolean writeAccess) {
		// here we lock the alias name instead of the nme of a real index because: a) active indices are never modified and b) an active index can only become inactive when a write lock on the active index is acquired 
		final String activeAliasName = ESConstants.getGlobalAliasForResource(resourceType, true);
		// wrong: nevertheless, the name of the real index is said to be locked, because there is only one active index per resource at a time
		//final String realIndexName = getThisSystemsIndexNameFromAlias(activeAliasName);
		return aquireLockForIndexName(activeAliasName, writeAccess);
	}
	
	public IndexLock acquireReadLockForTheLocalActiveIndex(String resourceType) {
		return acquireLockForTheLocalActiveIndex(resourceType, false);
	}
	
	private IndexLock acquireLockForTheLocalActiveIndex(String resourceType, boolean writeAccess) {
		// here we lock the alias name instead of the nme of a real index because: a) active indices are never modified and b) an active index can only become inactive when a write lock on the active index is acquired 
		final String activeAliasName = ESConstants.getGlobalAliasForResource(resourceType, true);

		final ReadWriteLock rwlock = getRwLock(activeAliasName);
		final String realIndexName = getThisSystemsIndexNameFromAlias(activeAliasName);
		final Lock lock = writeAccess ? rwlock.writeLock() : rwlock.readLock();
		// we locked the active-alias to prevent the updater from changing the active alias in the meantime which in turn hinders the modification or deletion of the active index of this system.
		// Yet, we only use the active index of the local system as the indexName of the lock object (independently of the underlying lock)
		return new IndexLock(realIndexName, lock);
	}

	/**
	 * @param indexName
	 * @param writeAccess
	 * @return 
	 */
	protected IndexLock aquireLockForIndexName(String indexName, boolean writeAccess) {
		final ReadWriteLock rwlock = getRwLock(indexName);
		final Lock lock = writeAccess ? rwlock.writeLock() : rwlock.readLock();
		return new IndexLock(indexName, lock);
	}

	/**
	 * @param resourceType
	 * @return
	 */
	public IndexLock aquireWriteLockForAnInactiveIndex(String resourceType) {
		final String realIndexName = getThisSystemsIndexNameFromAlias(ESConstants.getGlobalAliasForResource(resourceType, false));
		if (realIndexName == null) {
			return null;
		}
		return new IndexLock(realIndexName, getRwLock(realIndexName).writeLock());
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
	
	/**
	 * @param map
	 * @return
	 */
	private SystemInformation parseSystemInformation(String json, String indexName) {
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
