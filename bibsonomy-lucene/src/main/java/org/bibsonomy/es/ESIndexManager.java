package org.bibsonomy.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;

/**
 * TODO: add documentation to this class
 *
 * @author lutful
 */
public class ESIndexManager {
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
		final String activeIndex = getIndexNameFromAliasName(ESConstants.getGlobalAliasForResource(resourceType, true));
		final String backupIndex =  getIndexNameFromAliasName(ESConstants.getGlobalAliasForResource(resourceType, false));
		if (activeIndex == null || backupIndex == null) {
			return "No index for \"" + resourceType	+ "\" of current system found!! Please re-generate Index";
		}
		return null;
	}
	
	/**
	 * check if the index already exists if yes, it deletes the index and finally it creates empty index and assigns Aliases
	 * Index Name: systemurl + ResourceType + Unix time stamp
	 * @param resourceType
	 * @param indexNumber
	 * @param isActiveIndex
	 * @param oldIndexname 
	 * @return returns the indexName
	 */
	public String checkNcreateIndex(final String resourceType, final String oldIndexname, final boolean isActiveIndex){
		if (oldIndexname != null && oldIndexname != "") {
			final DeleteIndexResponse deleteIndex = this.esClient.getClient().admin().indices().delete(new DeleteIndexRequest(oldIndexname)).actionGet();
			if (!deleteIndex.isAcknowledged()) {
				log.error("Error in deleting the existing index: " + oldIndexname);
				return null;
			}
			//refresh the change
			this.esClient.getClient().admin().indices().prepareRefresh().execute().actionGet();
		}
		String newIndexName = this.createIndex(resourceType);
		if(newIndexName != null){
			this.setAliasForIndex(ESConstants.getGlobalAliasForResource(resourceType, isActiveIndex), newIndexName);
		}
		return newIndexName;
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
		String alias =  ESConstants.getTempAliasForResource(resourceType);
		List<String> prevTempIndexes = this.getIndexesFfromAlias(alias);
		if(!prevTempIndexes.isEmpty()){
			for(String indexName: prevTempIndexes){
				final DeleteIndexResponse deleteIndex = this.esClient.getClient().admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
				if (!deleteIndex.isAcknowledged()) {
					log.error("Error in deleting the existing temp index: " + indexName);
					return null;
				}
			}
		}
		String indexName = this.createIndex(resourceType);
		if(indexName!=null){
			this.setAliasForIndex(alias, indexName);
			return indexName;
		}
		return null;		
	}
	
	/**
	 * checks if there are any active and backup index for the pair for the resource type 
	 * 
	 * @param resourceType
	 * @param isActiveIndex 
	 * @return returns the index name if exists
	 */
	public String indexExist(final String resourceType, final boolean isActiveIndex){
		return this.getIndexNameFromAliasName(ESConstants.getGlobalAliasForResource(resourceType, isActiveIndex));
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
		activeIndexName = this.getIndexNameFromAliasName(activeIndexAlias);
		String backupIndexName  = this.getIndexNameFromAliasName(backupIndexAlias);
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
	public boolean switchToBackupIndex(String indexName, String resourceType){
		/*
		 * get the index for this system from the alias and compare
		 */
		String activeIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, true);
		String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);

		if(indexName.equalsIgnoreCase(this.getIndexNameFromAliasName(activeIndexAlias))){
			String backupIndexName  = this.getIndexNameFromAliasName(backupIndexAlias);
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
	
	private String getIndexNameFromAliasName(String aliasName) {
	    ImmutableOpenMap<String, AliasMetaData> indexToAliasesMap = this.esClient.getClient().admin().cluster()
	            .state(Requests.clusterStateRequest())
	            .actionGet()
	            .getState()
	            .getMetaData()
	            .aliases().get(aliasName);
	    if(indexToAliasesMap != null && !indexToAliasesMap.isEmpty()){
	    	Iterator<ObjectCursor<String>> it = indexToAliasesMap.keys().iterator();
	        while( it.hasNext()){
	        	String indexName = it.next().value;
	        	if(indexName.contains(this.systemHome.replaceAll("[^a-zA-Z0-9]", "").toLowerCase())){
	        		return indexName;
	        	}
	        }
	    }
	    return null;
	}
	
	/**
	 * gets all the indexes set under the alias for the current system
	 *  
	 * @param alias
	 * @return return a list of indexes
	 */
	public List<String> getIndexesFfromAlias(String alias){
		List<String> indexes = new ArrayList<String>();
		  ImmutableOpenMap<String, AliasMetaData> indexToAliasesMap = this.esClient.getClient().admin().cluster()
		            .state(Requests.clusterStateRequest())
		            .actionGet()
		            .getState()
		            .getMetaData()
		            .aliases().get(alias);
		    if(indexToAliasesMap != null && !indexToAliasesMap.isEmpty()){
		    	Iterator<ObjectCursor<String>> it = indexToAliasesMap.keys().iterator();
		        while( it.hasNext()){
		        	String indexName = it.next().value;
		        	if(indexName.contains(this.systemHome.replaceAll("[^a-zA-Z0-9]", "").toLowerCase())){
		        		indexes.add(indexName);
		        	}
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
		return getIndexNameFromAliasName(ESConstants.getGlobalAliasForResource(resourceType, true));
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
	
	private boolean removeAlias(final String indexName, final String alias){
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
}
