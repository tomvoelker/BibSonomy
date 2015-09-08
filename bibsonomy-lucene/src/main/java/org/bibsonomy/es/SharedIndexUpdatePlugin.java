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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LuceneIndexInfo;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.util.generator.AbstractIndexGenerator;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.SimpleBlockingThreadPoolExecutor;

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
	private SimpleBlockingThreadPoolExecutor<SharedResourceIndexGenerator<? super R>> generatorThreadPool;
	
	/** converts post model objects to documents of the index structure */
	private LuceneResourceConverter<R> resourceConverter;
	/** the database manager */
	protected LuceneDBInterface<R> dbLogic;
	private String resourceType;

	/**
	 * @param esClient
	 * @param systemHome
	 */
	public SharedIndexUpdatePlugin(final ESClient esClient, final String systemHome) {
		this.esClient = esClient;
		this.systemHome = systemHome;
		this.esIndexManager = new ESIndexManager(this.esClient, this.systemHome);
	}
	
	/**
	 * removes the oldest indices if there are more than two indices. Does not remove active indices.
	 */
	private void removeOutdatedIndices() {
		String tempAlias = ESConstants.getTempAliasForResource(this.resourceType);
		List<String> indexesList=esIndexManager.getThisSystemsIndexesFromAlias(tempAlias);
		final String activeIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, true);
		final String backupIndexAlias = ESConstants.getGlobalAliasForResource(resourceType, false);
		final List<String> activeIndices = esIndexManager.getThisSystemsIndexesFromAlias(activeIndexAlias);
		indexesList.addAll(activeIndices);
		indexesList.addAll(esIndexManager.getThisSystemsIndexesFromAlias(backupIndexAlias));
		Collections.sort(indexesList);
		
		if (indexesList.size() < 3) {
			return;
		}
		for (int i = 2; i < indexesList.size(); ++i) {
			if (!activeIndices.contains(indexesList.get(i))) {
				esIndexManager.removeAlias(indexesList.get(i), backupIndexAlias);
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
		final IndexLock inactiveIndexLock = this.esIndexManager.aquireWriteLockForAnInactiveIndex(resourceType);
		if (inactiveIndexLock == null) {
			List<String> tempIndices = this.esIndexManager.getTempIndicesOfThisSystem(resourceType);
			if (tempIndices.size() > 5) {
				log.error("no inactive index found for resource type " + resourceType + "@" + systemHome + " and too many aborted regenerations -> giving up");
			} else {
				log.warn("no inactive index found for resource type " + resourceType + "@" + systemHome + "  -> not updating, regeneration is triggered");
				this.generateIndex(false);
			}
			return null;
		}
		return this.createUpdaterInternal(resourceType, inactiveIndexLock);
	}

	/**
	 * @return 
	 */
	public String getGlobalIndexNonExistanceError() {
		return esIndexManager.getGlobalIndexNonExistanceError();
	}
	
	protected SharedResourceIndexUpdater<R> createUpdaterForGenerator(String indexName) {
		return this.createUpdaterInternal(this.resourceType, this.esIndexManager.aquireLockForIndexName(indexName, true, null));
	}

	private SharedResourceIndexUpdater<R> createUpdaterInternal(final String resourceType, IndexLock indexLock) {
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>(this.esClient, this.systemHome, resourceType, this.resourceConverter, indexLock, this);
		sharedIndexUpdater.setDbLogic(this.dbLogic);
		return sharedIndexUpdater;
	}


	/**
	 * generates an ElasticSearch index of this plugin's resourceType
	 * 
	 * @param sync  
	 */
	public void generateIndex(boolean sync) {
		this.generate(sync);
	}
	

	/**
	 * indexes documents of a particular resource type for shared resource search
	 * if the re-generate command is given then it first checks if there is any existing active and backup
	 * indexes for the resource. If exists then it first only creates a temporary index which will later be updated
	 * and set as the active index with next update run by the updater
	 * the updater checks for the latest two indexes and deletes the other old indexes
	 * @param sync
	 */
	private void generate(boolean sync) {
		generatorThreadPool.getWaitingTasks();
		for (SharedResourceIndexGenerator<?> generator : generatorThreadPool.getUnfinishedTasks()) {
			if (this.resourceType.equals(generator.getResourceType())) {
				log.warn("The " + this.resourceType + " index '" + generator.getIndexName() + "' is already  being / waiting to be  generated -> no further generator scheduled");
				return;
			}
		}
		
		final String tempIndexName = esIndexManager.createTempIndex(this.resourceType);
		this.generate(sync, tempIndexName);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void generate(boolean sync, final String indexName) {
		final SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator(this.systemHome, this, indexName);
		generator.setLogic(this.getDbLogic());
		generator.setEsClient(this.esClient);
		generator.setResourceType(this.resourceType);
		generator.setResourceConverter(this.resourceConverter);
		generator.setCallback(this);

		generatorThreadPool.scheduleTaskForExecution(generator, sync);
	}

	/**
	 * @param resourceType something like Bibtex or Bookmark
	 * @return returns informations on the lucene or elasticsearch indices
	 */
	public Collection<? extends LuceneIndexInfo> getIndicesInfos(final String resourceType) {
		final Collection<LuceneIndexInfo> rVal = new ArrayList<>();

		Map<String, SystemInformation> activeSystemInfos = esIndexManager.getAllActiveIndexSystemInformations(resourceType);
		Map<String, SystemInformation> inactiveSystemInfos = esIndexManager.getAllInactiveIndexSystemInformations(resourceType);
		Map<String, SystemInformation> generatingSystemInfos = esIndexManager.getAllGeneratingIndexSystemInformations(resourceType);
		
		rVal.addAll(getIndexInfos(activeSystemInfos, true, false));
		rVal.addAll(getIndexInfos(inactiveSystemInfos, false, false));
		rVal.addAll(getIndexInfos(generatingSystemInfos, false, true));
		
		for (final SharedResourceIndexGenerator<?> gen : this.generatorThreadPool.getRunningTasks()) {
			if (resourceType.equals(gen.getResourceType())) {
				final String indexName = gen.getIndexName();
				for (LuceneIndexInfo lii : rVal) {
					if (indexName.equals(lii.getBasePath())) {
						lii.setProcessInfo("currently running as " + gen.toString());
					}
				}
			}
		}
		
		int i = 0;
		for (final SharedResourceIndexGenerator<?> gen : this.generatorThreadPool.getWaitingTasks()) {
			if (resourceType.equals(gen.getResourceType())) {
				final String indexName = gen.getIndexName();
				for (LuceneIndexInfo lii : rVal) {
					if (indexName.equals(lii.getBasePath())) {
						lii.setProcessInfo("waiting at index " + i + " as " + gen.toString());
					}
				}
			}
			++i;
		}
		
		return rVal;
		
//		try (SharedResourceIndexUpdater<R> updater = this.createUpdater(resourceType)) {
//		if (updater == null) {
//			final LuceneIndexInfo indexInfo = new LuceneIndexInfo();
//			indexInfo.setErrorMassage("No Index found! please regenerate!");
//			rVal.add(indexInfo);
//			return rVal;
//		}
//		final List<Map<String, Object>> allSystemInfos = updater.getAllSystemInfos();
//		if (allSystemInfos == null) {
//			final LuceneIndexInfo indexInfo = new LuceneIndexInfo();
//			indexInfo.setErrorMassage("No Index found! please regenerate!");
//			rVal.add(indexInfo);
//			return rVal;
//		}
//		for (final Map<String, Object> infos : allSystemInfos) {
//			final LuceneIndexInfo indexInfo = new LuceneIndexInfo();
//			indexInfo.setActive(true);
//			indexInfo.setBasePath(String.valueOf(infos.get(ESConstants.SYSTEM_URL_FIELD_NAME)));
//			final LuceneIndexStatistics statistics = new LuceneIndexStatistics();
//			statistics.setNewestRecordDate(new Date(this.getLong(infos, "last_log_date")));
//			statistics.setLastTasId(this.getLong(infos, "last_tas_id"));
//			indexInfo.setIndexStatistics(statistics);
//
//			for (final SharedResourceIndexGenerator<?> gen : this.generatorThreadPool.getRunningTasks()) {
//				if (resourceType.equals(gen.getResourceType())) {
//					if (gen.isRunning() == true) {
//						indexInfo.setGeneratingIndex(true);
//						indexInfo.setIndexGenerationProgress(gen.getProgressPercentage());
//					}
//				}
//			}
//			rVal.add(indexInfo);
//		}
//		return rVal;
//		}
	}

	private Collection<LuceneIndexInfo> getIndexInfos(Map<String, SystemInformation> systemInfos, boolean active, boolean generating) {
		final Collection<LuceneIndexInfo> rVal = new ArrayList<>();
		for (Map.Entry<String, SystemInformation> infoPair : systemInfos.entrySet()) {
			final String indexName = infoPair.getKey();
			final SystemInformation sysInf = infoPair.getValue();
			final LuceneIndexInfo indexInfo = new LuceneIndexInfo(); 
			indexInfo.setBasePath(indexName);
			indexInfo.setActive(active);
			
			final LuceneIndexStatistics statistics = new LuceneIndexStatistics();
			statistics.setNewestRecordDate(sysInf.getUpdaterState().getLast_log_date());
			Number lastTasId = sysInf.getUpdaterState().getLast_tas_id();
			statistics.setLastTasId((lastTasId == null) ? -1 : lastTasId.longValue());
			// TODO: fetch index statistics: statistics.setNumDocs(numDocs)
			indexInfo.setIndexStatistics(statistics);
			
			indexInfo.setGeneratingIndex(generating);
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
		if (index.isFinishedSuccesfully()) {
			this.esIndexManager.changeUnderConstructionStatus(index.getIndexName(), index.getResourceType());
		}
	}

	public LuceneDBInterface<R> getDbLogic() {
		return this.dbLogic;
	}

	public void setDbLogic(LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	public void setResourceConverter(LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @param nameOfIndexToBeActivated
	 */
	public void activateIndex(String nameOfIndexToBeActivated) {
		this.esIndexManager.activateIndex(nameOfIndexToBeActivated, this.resourceType);
		removeOutdatedIndices();
	}

	public void setGeneratorThreadPool(SimpleBlockingThreadPoolExecutor<SharedResourceIndexGenerator<? super R>> generatorThreadPool) {
		this.generatorThreadPool = generatorThreadPool;
	}

}
