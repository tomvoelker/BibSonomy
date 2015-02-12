package org.bibsonomy.es;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.get.GetResponse;

/**
 * Initiates the IndexUpdater for the kronjobs to update indexes
 * 
 * @author lutful
 * @param <R> 
 */
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin, GenerateIndexCallback<R> {
	private final ESClient esClient;
	private final String systemHome;
	private boolean generatingIndex;
	private static final Log log = LogFactory.getLog(SharedIndexUpdatePlugin.class);

	/**
	 * @param esClient
	 * @param systemHome
	 */
	public SharedIndexUpdatePlugin(final ESClient esClient, final String systemHome) {
		this.esClient = esClient;
		this.systemHome = systemHome;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.UpdatePlugin#createUpdater(java.lang.String)
	 */
	@Override
	public IndexUpdater createUpdater(String indexType) {
		boolean indexExist = esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ESConstants.INDEX_NAME)).actionGet().isExists();
		if(!indexExist){
			log.error("No Index named \""+ESConstants.INDEX_NAME  +"\" found!! Please generate Index");
			return null;
		}
		
        // Check if a document exists
        GetResponse response = esClient.getClient().prepareGet(ESConstants.INDEX_NAME, ESConstants.SYSTEM_INFO_INDEX_TYPE, systemHome+indexType).setRefresh(true).execute().actionGet();
        boolean resourceDocumentExist = response.isExists();
        if(!resourceDocumentExist){
			log.error("No documents for \""+ indexType +"\" in \""+ESConstants.INDEX_NAME  +"\" for current system found!! Please re-generate Index");
			return null;
		}
        
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>(this.systemHome);
		sharedIndexUpdater.setEsClient(esClient);
		sharedIndexUpdater.setIndexType(indexType);
		return sharedIndexUpdater;
	}

	/**
	 * generates indexes for shared resource aka ElasticSearch 
	 * @param luceneResourceManagers 
	 */
	@SuppressWarnings("unchecked")
	public void generateIndex(List<LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		// allow only one index-generation at a time
		synchronized (this) {
			if (this.generatingIndex == true) {
				return;
			}
			this.generatingIndex = true;
		}
		
		//check if the index already exists if not, it creates empty index
		boolean indexExist = esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ESConstants.INDEX_NAME)).actionGet().isExists();			
		if(!indexExist){
			CreateIndexResponse createIndex =  esClient.getClient().admin().indices().create(new CreateIndexRequest(ESConstants.INDEX_NAME)).actionGet();
		    if(!createIndex.isAcknowledged()){
		    	log.error("Error in creating Index");
			    return;	
		    }
		}
	
		SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator(this.systemHome);
		generator.setIndexType(IndexType.ELASTICSEARCH);
		for(LuceneResourceManager<? extends Resource> manager: luceneResourceManagers){
			generator.setLogic((LuceneDBInterface<Resource>) manager.getDbLogic());
			generator.setEsClient(esClient);
			generator.setResourceType(manager.getResourceName());
			generator.setResourceConverter((LuceneResourceConverter<Resource>) manager.getResourceConverter());
			// this cast is really ugly, but safe because nothing specific is done with the resource in the generatedIndex method of this object
			generator.setCallback((GenerateIndexCallback<Resource>) this);
			generator.run();
		}		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.GenerateIndexCallback#generatedIndex(org.bibsonomy.lucene.index.LuceneResourceIndex)
	 */
	@Override
	public void generatedIndex(LuceneResourceIndex<R> index) {
		//it is better to refresh the index after creating the documents
		esClient.getClient().admin().indices().flush(new FlushRequest(ESConstants.INDEX_NAME).full(true)).actionGet();
		this.generatingIndex = false;
	}
}
