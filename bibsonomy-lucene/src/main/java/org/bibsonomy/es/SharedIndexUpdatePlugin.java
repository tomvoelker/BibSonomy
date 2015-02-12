package org.bibsonomy.es;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.get.GetResponse;

/**
 * Initiates the IndexUpdater for the kronjobs to update indexes
 * 
 * @author lutful
 * @param <R> 
 */
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin {
	private final ESClient esClient;
	private final String systemHome;
	private static final Log log = LogFactory.getLog(SharedIndexUpdatePlugin.class);
	private static final ThreadPoolExecutor generatorThreadExecutor = new ThreadPoolExecutor(0, 1, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

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
	public synchronized void generateIndex(List<LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		// allow only one index-generation at a time
		if (generatorThreadExecutor.getQueue().isEmpty() == false) {
			return;
		}

		//check if the index already exists if not, it creates empty index
		boolean indexExist = esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ESConstants.INDEX_NAME)).actionGet().isExists();
		if (!indexExist) {
			CreateIndexResponse createIndex =  esClient.getClient().admin().indices().create(new CreateIndexRequest(ESConstants.INDEX_NAME)).actionGet();
			if (!createIndex.isAcknowledged()) {
				log.error("Error in creating Index");
				return;	
			}
		}
	
		SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator(this.systemHome);
		generator.setIndexType(IndexType.ELASTICSEARCH);
		// TODO: currently generators for all executors are queued at once - in the future (after the PUMA2 final release) it should also be possible to generate only one index generation process
		for (LuceneResourceManager<? extends Resource> manager: luceneResourceManagers) {
			generator.setLogic((LuceneDBInterface<Resource>) manager.getDbLogic());
			generator.setEsClient(esClient);
			generator.setResourceType(manager.getResourceName());
			generator.setResourceConverter((LuceneResourceConverter<Resource>) manager.getResourceConverter());
			
			generatorThreadExecutor.execute(generator);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		generatorThreadExecutor.shutdown();
	}
}
