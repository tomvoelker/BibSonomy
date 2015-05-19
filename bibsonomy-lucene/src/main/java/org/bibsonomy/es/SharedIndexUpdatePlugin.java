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
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.param.LuceneIndexInfo;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.util.generator.AbstractIndexGenerator;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import static org.bibsonomy.es.ESConstants.*;

/**
 * Initiates the IndexUpdater for the cronjobs to update indexes
 * 
 * @author lutful
 * @param <R>
 */
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin, GenerateIndexCallback<R> {
	private final ESClient esClient;
	private final String systemHome;
	private static final Log log = LogFactory.getLog(SharedIndexUpdatePlugin.class);
	private static final ThreadPoolExecutor generatorThreadExecutor = new ThreadPoolExecutor(0, 1, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private final List<SharedResourceIndexGenerator<R>> queuedOrRunningGenerators = Collections.synchronizedList(new ArrayList<SharedResourceIndexGenerator<R>>());

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
	public SharedResourceIndexUpdater<R> createUpdater(final String resourceType) {
		String errorMsg = this.getGlobalIndexNonExistanceError();
		if (errorMsg == null) {
			errorMsg = this.getResourceIndexNonExistanceError(resourceType);
		}
		if (errorMsg != null) {
			log.error(errorMsg);
			return null;
		}
		return this.createUpdaterInternal(resourceType);
	}

	private SharedResourceIndexUpdater<R> createUpdaterInternal(final String resourceType) {
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>(this.systemHome);
		sharedIndexUpdater.setEsClient(this.esClient);
		sharedIndexUpdater.setResourceType(resourceType);
		return sharedIndexUpdater;
	}

	/**
	 * @return returns true if an active index exists
	 */
	public String getGlobalIndexNonExistanceError() {
		//checks if there are any active indices
		final boolean indexExist = this.esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ACTIVE_INDEX_ID)).actionGet().isExists();
		if (!indexExist) {
			return "No Index found!! Please generate Index";
		}
		return null;
	}

	/**
	 * @param indexType
	 * @return checks if documents for a particular resource exists
	 */
	public String getResourceIndexNonExistanceError(final String indexType) {
		// Check if a document exists
		final GetResponse response = this.esClient.getClient().prepareGet(ACTIVE_INDEX_ID, SYSTEM_INFO_INDEX_TYPE, this.systemHome + indexType).setRefresh(true).execute().actionGet();
		if (!response.isExists()) {
			return "No documents for \"" + indexType + "\" in the index for current system found!! Please re-generate Index";
		}
		return null;
	}

	/**
	 * generates indexes for shared resource aka ElasticSearch
	 * 
	 * @param luceneResourceManagers
	 */
	public synchronized void generateIndex(final List<LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		// allow only one index-generation at a time
		if (generatorThreadExecutor.getQueue().isEmpty() == false) {
			return;
		}

		// check if the index already exists if not, it creates empty index and assigns Aliases
		final boolean index_1_Exist = this.esClient.getClient().admin().indices().exists(new IndicesExistsRequest(INDEX_NAME_1)).actionGet().isExists();
		if (!index_1_Exist) {
			final CreateIndexResponse createIndex = this.esClient.getClient().admin().indices().create(new CreateIndexRequest(INDEX_NAME_1)).actionGet();
			if (!createIndex.isAcknowledged()) {
				log.error("Error in creating Index");
				return;
			}
			this.setAliasForIndex(ACTIVE_INDEX_ID, INDEX_NAME_1);
		}
				
		final boolean index_2_Exist = this.esClient.getClient().admin().indices().exists(new IndicesExistsRequest(INDEX_NAME_2)).actionGet().isExists();
		if (!index_2_Exist) {
			final CreateIndexResponse createIndex = this.esClient.getClient().admin().indices().create(new CreateIndexRequest(INDEX_NAME_2)).actionGet();
			if (!createIndex.isAcknowledged()) {
				log.error("Error in creating Index");
				return;
			}
			this.setAliasForIndex(INACTIVE_INDEX_ID, INDEX_NAME_2);

		}

		for (final LuceneResourceManager<? extends Resource> manager : luceneResourceManagers) {
			this.generate(manager, INDEX_NAME_1);
		}
		
		for (final LuceneResourceManager<? extends Resource> manager : luceneResourceManagers) {
			this.generate(manager, INDEX_NAME_2);
		}
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
	
	private void switchToBackupIndex(String indexName){		
		if(indexName.equalsIgnoreCase(this.getIndexNameFromAliasName(ACTIVE_INDEX_ID))){
			String backupIndexName  = this.getIndexNameFromAliasName(INACTIVE_INDEX_ID);
			IndicesAliasesResponse aliasReponse = this.esClient.getClient().admin().indices().prepareAliases()
	                .removeAlias(indexName, ACTIVE_INDEX_ID)
	                .addAlias(indexName, INACTIVE_INDEX_ID)
	                .removeAlias(backupIndexName, INACTIVE_INDEX_ID)
	                .addAlias(backupIndexName, ACTIVE_INDEX_ID)
	                .execute()
	                .actionGet();
			if (!aliasReponse.isAcknowledged()) {
				log.error("Error in switching to backup index");
				return;
			}
		}
	}
	
	private String getIndexNameFromAliasName(final String aliasName) {
	    ImmutableOpenMap<String, AliasMetaData> indexToAliasesMap = this.esClient.getClient().admin().cluster()
	            .state(Requests.clusterStateRequest())
	            .actionGet()
	            .getState()
	            .getMetaData()
	            .aliases().get(aliasName);
	    if(indexToAliasesMap != null && !indexToAliasesMap.isEmpty()){
	        return indexToAliasesMap.keys().iterator().next().value;
	    }
	    return null;
	}
	/**
	 * indexes documents of a particular resource type for shared resource search
	 * @param manager
	 */
	private void generate(final LuceneResourceManager<? extends Resource> manager) {
		String activeIndex = this.getIndexNameFromAliasName(ACTIVE_INDEX_ID);
		String inactiveIndex =  this.getIndexNameFromAliasName(INACTIVE_INDEX_ID);
		this.generate(manager, activeIndex);
		this.generate(manager, inactiveIndex);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void generate(final LuceneResourceManager<? extends Resource> manager, String indexName) {
		switchToBackupIndex(indexName);
		final SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator(this.systemHome, this.createUpdaterInternal(manager.getResourceName()), indexName);
		generator.setLogic(manager.getDbLogic());
		generator.setEsClient(this.esClient);
		generator.setResourceType(manager.getResourceName());
		generator.setResourceConverter(manager.getResourceConverter());
		generator.setCallback(this);

		this.queuedOrRunningGenerators.add(generator);
		generatorThreadExecutor.execute(generator);
	}

	/*
	 * (non-Javadoc)
	 * 
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
	public void generateIndex(final LuceneResourceManager<? extends Resource> mng) {
		// allow only one index-generation at a time
		if (generatorThreadExecutor.getQueue().isEmpty() == false) {
			return;
		}

		// check if the index already exists if not, it creates empty index
		final boolean indexExist = this.esClient.getClient().admin().indices().exists(new IndicesExistsRequest(ACTIVE_INDEX_ID)).actionGet().isExists();
		if (!indexExist) {
			return;
		}
		this.generate(mng);
	}

	/**
	 * @param manager
	 * @return returns informations on the lucene or elasticsearch indices
	 */
	public Collection<? extends LuceneIndexInfo> getIndicesInfos(final LuceneResourceManager<? extends Resource> manager) {
		final Collection<LuceneIndexInfo> rVal = new ArrayList<>();
		final String resourceType = manager.getResourceName();

		final SharedResourceIndexUpdater<R> updater = this.createUpdater(resourceType);
		if (updater == null) {
			return rVal;
		}
		final List<Map<String, Object>> allSystemInfos = updater.getAllSystemInfos();
		if (allSystemInfos == null) {
			return rVal;
		}
		for (final Map<String, Object> infos : allSystemInfos) {
			final LuceneIndexInfo indexInfo = new LuceneIndexInfo();
			indexInfo.setActive(true);
			indexInfo.setBasePath(String.valueOf(infos.get(SYSTEMURL_FIELD)));
			final LuceneIndexStatistics statistics = new LuceneIndexStatistics();
			statistics.setNewestRecordDate(new Date(this.getLong(infos, "last_log_date")));
			statistics.setLastTasId(this.getLong(infos, "last_tas_id"));
			indexInfo.setIndexStatistics(statistics);

			for (final SharedResourceIndexGenerator<R> gen : this.queuedOrRunningGenerators) {
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

	@SuppressWarnings("static-method")
	private long getLong(final Map<String, Object> infos, final String key) {
		final Object strVal = infos.get(key);
		if (strVal == null) {
			return 0;
		}
		return Long.parseLong(strVal.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.lucene.util.generator.GenerateIndexCallback#generatedIndex
	 * (org.bibsonomy.lucene.util.generator.AbstractIndexGenerator)
	 */
	@Override
	public void generatedIndex(final AbstractIndexGenerator<R> index) {
		this.queuedOrRunningGenerators.remove(index);
	}

}
