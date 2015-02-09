package org.bibsonomy.es;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;

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
		
		//check if the index already exists if so it deletes and creates empty index again otherwise mapping fails with existing resource types
		boolean isIndexExist = esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ESConstants.INDEX_NAME)).actionGet().isExists();
		
		if(isIndexExist){
			DeleteIndexResponse delete = esClient.getClient().admin().indices().delete(new DeleteIndexRequest(ESConstants.INDEX_NAME)).actionGet();
			if (!delete.isAcknowledged()) {
				log.error("Index wasn't deleted");
			    return;
			}
		}
		
		CreateIndexResponse createIndex =  esClient.getClient().admin().indices().create(new CreateIndexRequest(ESConstants.INDEX_NAME)).actionGet();
	    if(!createIndex.isAcknowledged()){
	    	log.error("Index wasn't recreated");
		    return;	
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
