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

import static org.bibsonomy.es.ESConstants.SYSTEMURL_FIELD;

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
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.param.LuceneIndexInfo;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.util.generator.AbstractIndexGenerator;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;

/**
 * Initiates the IndexUpdater for the cronjobs to update indexes
 * 
 * @author lutful
 * @param <R>
 */
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin, GenerateIndexCallback<R> {
	private final ESClient esClient;
	private final String systemHome;
	private final ESIndexManager esIndexManager;
	private static final Log log = LogFactory.getLog(SharedIndexUpdatePlugin.class);
	private static final ThreadPoolExecutor generatorThreadExecutor = new ThreadPoolExecutor(0, 1, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private final List<SharedResourceIndexGenerator<R>> queuedOrRunningGenerators = Collections.synchronizedList(new ArrayList<SharedResourceIndexGenerator<R>>());
	/** converts post model objects to documents of the index structure */
	private final LuceneResourceConverter<R> resourceConverter;
	private final String resourceType;

	/**
	 * @param esClient
	 * @param systemHome
	 */
	public SharedIndexUpdatePlugin(final ESClient esClient, final String systemHome, final LuceneResourceConverter<R> resourceConverter, final String resourceType) {
		this.esClient = esClient;
		this.systemHome = systemHome;
		this.resourceType = resourceType;
		esIndexManager = new ESIndexManager(this.esClient, this.systemHome);
		this.resourceConverter = resourceConverter;
	}

	/**
	 * checks if there are any new indexes waiting to be activated after regeneration
	 * if true then activate the most recent as the active index and the next one as
	 * backup index
	 */
	public void checkNewIndexInPipeline() {
		String tempAlias = ESConstants.getTempAliasForResource(this.resourceType, false);
		List<String> indexesList=esIndexManager.getIndexesFfromAlias(tempAlias);
		String activeIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, true);
		String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
		indexesList.addAll(esIndexManager.getIndexesFfromAlias(activeIndexAlias));
		indexesList.addAll(esIndexManager.getIndexesFfromAlias(backupIndexAlias));
		Collections.sort(indexesList);
		/* first remove all aliases for avoiding confusion
		 * then set alias for last as active and 2nd last as backup
		 */
		esIndexManager.removeAliases(indexesList);
		esIndexManager.setAliasForIndex(activeIndexAlias, indexesList.get(indexesList.size()-1));
		esIndexManager.setAliasForIndex(backupIndexAlias, indexesList.get(indexesList.size()-2));
		for(int i=0;i<indexesList.size()-2;i++){
			final DeleteIndexResponse deleteIndex = this.esClient.getClient().admin().indices().delete(new DeleteIndexRequest(indexesList.get(i))).actionGet();
			if (!deleteIndex.isAcknowledged()) {
				log.error("Error in deleting the old index: " + indexesList.get(i)+ " after re-generate");
				return;
			}
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.UpdatePlugin#createUpdater(java.lang.String)
	 */
	@Override
	public SharedResourceIndexUpdater<R> createUpdater(final String resourceType) {
		checkNewIndexInPipeline();
		
		//TODO adjust changes and use read lock
		String errorMsg = this.getGlobalIndexNonExistanceError();
		if (errorMsg == null) {
			errorMsg = esIndexManager.getResourceIndexNonExistanceError(resourceType);
		}
		if (errorMsg != null) {
			log.error(errorMsg);
			return null;
		}
		return this.createUpdaterInternal(resourceType);
	}

	/**
	 * @return 
	 */
	public String getGlobalIndexNonExistanceError() {
		return esIndexManager.getGlobalIndexNonExistanceError();
	}

	private SharedResourceIndexUpdater<R> createUpdaterInternal(final String resourceType) {
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>(this.systemHome, resourceType, resourceConverter);
		sharedIndexUpdater.setEsClient(this.esClient);
		return sharedIndexUpdater;
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
		for (final LuceneResourceManager<? extends Resource> manager : luceneResourceManagers) {
			this.generate(manager, false);
		}
	}

	/**
	 * @param manager
	 */
	public void generateIndex(final LuceneResourceManager<? extends Resource> manager) {
		// allow only one index-generation at a time
		if (generatorThreadExecutor.getQueue().isEmpty() == false) {
			return;
		}
		this.generate(manager, true);
	}
	

	/**
	 * indexes documents of a particular resource type for shared resource search
	 * if the re-generate command is given then it first checks if there is any existing active and backup
	 * indexes for the resource. If exists then it first only creates a temporary index which will later be updated
	 * and set as the active index with next update run by the updater
	 * the updater checks for the latest two indexes and deletes the other old indexes
	 * @param manager
	 */
	private void generate(final LuceneResourceManager<? extends Resource> manager, boolean isTempIndex) {
		/*
		 *	if index exists create a temporary index 
		 */
		final String oldBackupIndex = esIndexManager.indexExist(manager.getResourceName(), false);
		final String oldActiveIndex = esIndexManager.indexExist(manager.getResourceName(), true);
		boolean indexExist = (oldBackupIndex != null && oldBackupIndex != "") && (oldActiveIndex != null && oldActiveIndex != "");
		if(isTempIndex && indexExist){
			//TODO check if any existing temp index, then delete them and re-create
			final String tempIndexName = esIndexManager.createTempIndex(manager.getResourceName());
			this.generate(manager, tempIndexName , isTempIndex);
		}else{
		final String backupIndexName = esIndexManager.checkNcreateIndex(manager.getResourceName(), oldBackupIndex, false);
		final String activeIndexName = esIndexManager.checkNcreateIndex(manager.getResourceName(), oldActiveIndex, true);
		this.generate(manager, activeIndexName, isTempIndex);
		this.generate(manager, backupIndexName, isTempIndex);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void generate(final LuceneResourceManager<? extends Resource> manager, final String indexName, final boolean isTempIndex) {
		if(!isTempIndex && !esIndexManager.switchToBackupIndex(indexName,manager.getResourceName())){
			log.error("Index generation failed!!");
			return;
		}
		final SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator(this.systemHome, this.createUpdaterInternal(manager.getResourceName()), indexName);
		generator.setLogic(manager.getDbLogic());
		generator.setEsClient(this.esClient);
		generator.setGenerateTempIndex(isTempIndex);
		generator.setResourceType(manager.getResourceName());
		generator.setResourceConverter(this.resourceConverter);
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
	 * @param manager
	 * @return returns informations on the lucene or elasticsearch indices
	 */
	public Collection<? extends LuceneIndexInfo> getIndicesInfos(final LuceneResourceManager<? extends Resource> manager) {
		final Collection<LuceneIndexInfo> rVal = new ArrayList<>();
		final String resourceType = manager.getResourceName();

		final SharedResourceIndexUpdater<R> updater = this.createUpdater(resourceType);
		if (updater == null) {
			final LuceneIndexInfo indexInfo = new LuceneIndexInfo();
			indexInfo.setErrorMassage("No Index found! please regenerate!");
			rVal.add(indexInfo);
			return rVal;
		}
		final List<Map<String, Object>> allSystemInfos = updater.getAllSystemInfos();
		if (allSystemInfos == null) {
			final LuceneIndexInfo indexInfo = new LuceneIndexInfo();
			indexInfo.setErrorMassage("No Index found! please regenerate!");
			rVal.add(indexInfo);
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
		this.esIndexManager.changeTempIndexStaus(index.getIndexName(), index.getResourceType());
		this.queuedOrRunningGenerators.remove(index);
	}

}
