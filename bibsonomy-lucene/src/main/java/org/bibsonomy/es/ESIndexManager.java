package org.bibsonomy.es;

import java.util.Iterator;

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
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
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
	public String createIndex(final String resourceType){
		String indexName = ESConstants.getIndexNameWithTime(this.systemHome, resourceType);
		final CreateIndexResponse createIndex = this.esClient.getClient().admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
		if (!createIndex.isAcknowledged()) {
			log.error("Error in creating Index: " + indexName);
			return null;
		}
		return indexName;
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
	
	private void setAliasForIndex(String alias, String indexName){
		IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
                .addAlias(indexName, alias)
                .execute()
                .actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("Error in creating Index");
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
	 * gets the active indexName for the resource of the current system
	 * 
	 * @param resourceType
	 * @return returns the active indexName
	 */
	public String getActiveIndexnameForResource(String resourceType){
		return getIndexNameFromAliasName(ESConstants.getGlobalAliasForResource(resourceType, true));
	}
		
}
