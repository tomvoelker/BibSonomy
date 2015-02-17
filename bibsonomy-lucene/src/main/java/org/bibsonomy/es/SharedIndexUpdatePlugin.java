package org.bibsonomy.es;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.param.LuceneIndexInfo;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.util.generator.AbstractIndexGenerator;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
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
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin, GenerateIndexCallback<R> {
	private final ESClient esClient;
	private final String systemHome;
	private static final Log log = LogFactory.getLog(SharedIndexUpdatePlugin.class);
	private static final ThreadPoolExecutor generatorThreadExecutor = new ThreadPoolExecutor(0, 1, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private List<SharedResourceIndexGenerator<R>> queuedOrRunningGenerators = Collections.synchronizedList(new ArrayList<SharedResourceIndexGenerator<R>>());

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
	public SharedResourceIndexUpdater<R> createUpdater(String resourceType) {
		String errorMsg = getGlobalIndexNonExistanceError();
		if (errorMsg == null) {
			errorMsg = getResourceIndexNonExistanceError(resourceType);
		}
		if (errorMsg != null) {
			log.error(errorMsg);
			return null;
		}
		return createUpdaterInternal(resourceType);
	}

	private SharedResourceIndexUpdater<R> createUpdaterInternal(String resourceType) {
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>(this.systemHome);
		sharedIndexUpdater.setEsClient(esClient);
		sharedIndexUpdater.setResourceType(resourceType);
		return sharedIndexUpdater;
	}
	
	public String getGlobalIndexNonExistanceError() {
		boolean indexExist = esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ESConstants.INDEX_NAME)).actionGet().isExists();
		if(!indexExist){
			return "No Index named \""+ESConstants.INDEX_NAME  +"\" found!! Please generate Index";
		}
		return null;
	}
	
	
	public String getResourceIndexNonExistanceError(String indexType) {
		// Check if a document exists
		GetResponse response = esClient.getClient().prepareGet(ESConstants.INDEX_NAME, ESConstants.SYSTEM_INFO_INDEX_TYPE, systemHome+indexType).setRefresh(true).execute().actionGet();
		if(!response.isExists()){
			return "No documents for \""+ indexType +"\" in \""+ESConstants.INDEX_NAME  +"\" for current system found!! Please re-generate Index";
		}
		return null;
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
		
		for (LuceneResourceManager<? extends Resource> manager: luceneResourceManagers) {
			generate(manager);
		}
	}

	private void generate(LuceneResourceManager<? extends Resource> manager) {
		SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator(this.systemHome, this.createUpdaterInternal(manager.getResourceName()));
		generator.setLogic((LuceneDBInterface<Resource>) manager.getDbLogic());
		generator.setEsClient(esClient);
		generator.setResourceType(manager.getResourceName());
		generator.setResourceConverter((LuceneResourceConverter<Resource>) manager.getResourceConverter());
		generator.setCallback(this);
		
		queuedOrRunningGenerators.add(generator);
		generatorThreadExecutor.execute(generator);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		generatorThreadExecutor.shutdown();
	}

	/**
	 * @param mng
	 */
	public void generateIndex(LuceneResourceManager<? extends Resource> mng) {
		// allow only one index-generation at a time
		if (generatorThreadExecutor.getQueue().isEmpty() == false) {
			return;
		}

		//check if the index already exists if not, it creates empty index
		boolean indexExist = esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ESConstants.INDEX_NAME)).actionGet().isExists();
		if (!indexExist) {
			return;
		}
		
		generate(mng);
	}

	/**
	 * @param manager
	 * @return
	 */
	public Collection<? extends LuceneIndexInfo> getIndicesInfos(LuceneResourceManager<? extends Resource> manager) {
		final Collection<LuceneIndexInfo> rVal = new ArrayList<>();
		final String resourceType = manager.getResourceName();
		
		SharedResourceIndexUpdater<R> updater = createUpdater(resourceType);
		if (updater == null) {
			return rVal;
		}
		List<Map<String, Object>> allSystemInfos = updater.getAllSystemInfos();
		if (allSystemInfos == null) {
			return rVal;
		}
		for (Map<String, Object> infos : allSystemInfos) {
			final LuceneIndexInfo indexInfo = new LuceneIndexInfo();
			indexInfo.setActive(true);
			indexInfo.setBasePath(String.valueOf(infos.get("systemUrl")));
			final LuceneIndexStatistics statistics = new LuceneIndexStatistics();
			statistics.setNewestRecordDate(new Date(getLong(infos, "last_log_date")));
			statistics.setLastTasId(getLong(infos, "last_tas_id"));
			indexInfo.setIndexStatistics(statistics);

			for (SharedResourceIndexGenerator<R> gen : queuedOrRunningGenerators) {
				if (resourceType.equals(gen.getResourceType())) {
					if (gen.isRunning() == true) {
						indexInfo.setGeneratingIndex(true);
						indexInfo.setIndexGenerationProgress(gen.getProgressPercentage());
					}
				}
			}
			rVal.add(indexInfo);
		}
		return rVal;
	}

	private long getLong(Map<String, Object> infos, String key) {
		String strVal = String.valueOf(infos.get(key));
		if (strVal == null) {
			return 0;
		}
		return Long.parseLong(strVal);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.GenerateIndexCallback#generatedIndex(org.bibsonomy.lucene.util.generator.AbstractIndexGenerator)
	 */
	@Override
	public void generatedIndex(AbstractIndexGenerator<R> index) {
		this.queuedOrRunningGenerators.remove(index);
	}
	
}
