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
package org.bibsonomy.search.es.update;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.generator.SharedResourceIndexGenerator;
import org.bibsonomy.search.es.index.ResourceConverter;
import org.bibsonomy.search.es.management.ESIndexManager;
import org.bibsonomy.search.es.management.IndexLock;
import org.bibsonomy.search.es.management.SystemInformation;
import org.bibsonomy.search.generator.GenerateIndexCallback;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.model.SearchIndexStatistics;
import org.bibsonomy.search.update.UpdatePlugin;
import org.bibsonomy.util.SimpleBlockingThreadPoolExecutor;

/**
 * Initiates the IndexUpdater for the cronjobs to update indexes
 * 
 * @author lutful
 * @param <R>
 */

@Deprecated // TODODZO remove
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin<R>, GenerateIndexCallback<SharedResourceIndexGenerator<R>> {
	private static final Log log = LogFactory.getLog(SharedIndexUpdatePlugin.class);
	
	private final ESClient esClient;
	private final URI systemHome;
	private final ESIndexManager esIndexManager;
	private SimpleBlockingThreadPoolExecutor<SharedResourceIndexGenerator<? super R>> generatorThreadPool;
	
	/** converts post model objects to documents of the index structure */
	private ResourceConverter<R> resourceConverter;
	/** the database manager */
	protected SearchDBInterface<R> dbLogic;
	private Class<R> resourceType; // TODO: remove?

	/**
	 * @param esClient
	 * @param systemHome
	 */
	public SharedIndexUpdatePlugin(final ESClient esClient, final URI systemHome) {
		this.esClient = esClient;
		this.systemHome = systemHome;
		this.esIndexManager = new ESIndexManager(this.esClient, this.systemHome);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.UpdatePlugin#createUpdater(java.lang.String)
	 */
	@Override
	public SharedResourceIndexUpdater<R> createUpdater(final Class<R> resourceType) {
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
	
	public SharedResourceIndexUpdater<R> createUpdaterForGenerator(String indexName) {
		return this.createUpdaterInternal(this.resourceType, this.esIndexManager.aquireLockForIndexName(indexName, true, null));
	}

	private SharedResourceIndexUpdater<R> createUpdaterInternal(final Class<R> resourceType, IndexLock indexLock) {
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
	 * @param resourceType something like Bibtex or Bookmark TODO: should be Class<? extends Resource>
	 * @return returns informations on the lucene or elasticsearch indices
	 */
	public Collection<? extends SearchIndexInfo> getIndicesInfos(final Class<? extends Resource> resourceType) {
		final Collection<SearchIndexInfo> rVal = new ArrayList<>();

		final Map<String, SystemInformation> activeSystemInfos = esIndexManager.getAllActiveIndexSystemInformations(resourceType);
		final Map<String, SystemInformation> inactiveSystemInfos = esIndexManager.getAllInactiveIndexSystemInformations(resourceType);
		final Map<String, SystemInformation> generatingSystemInfos = esIndexManager.getAllGeneratingIndexSystemInformations(resourceType);
		
		rVal.addAll(getIndexInfos(activeSystemInfos, true, false));
		rVal.addAll(getIndexInfos(inactiveSystemInfos, false, false));
		rVal.addAll(getIndexInfos(generatingSystemInfos, false, true));
		
		for (final SharedResourceIndexGenerator<?> gen : this.generatorThreadPool.getRunningTasks()) {
			if (resourceType.equals(gen.getResourceType())) {
				final String indexName = gen.getIndexName();
				for (SearchIndexInfo lii : rVal) {
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
				for (SearchIndexInfo lii : rVal) {
					if (indexName.equals(lii.getBasePath())) {
						lii.setProcessInfo("waiting at index " + i + " as " + gen.toString());
					}
				}
			}
			++i;
		}
		
		return rVal;
	}

	private Collection<SearchIndexInfo> getIndexInfos(Map<String, SystemInformation> systemInfos, boolean active, boolean generating) {
		final Collection<SearchIndexInfo> rVal = new ArrayList<>();
		for (Map.Entry<String, SystemInformation> infoPair : systemInfos.entrySet()) {
			final String indexName = infoPair.getKey();
			final SystemInformation sysInf = infoPair.getValue();
			final SearchIndexInfo indexInfo = new SearchIndexInfo();
			indexInfo.setBasePath(indexName);
			indexInfo.setActive(active);
			
			final SearchIndexStatistics statistics = new SearchIndexStatistics();
			statistics.setNewestRecordDate(sysInf.getUpdaterState().getLast_log_date());
			final Number lastTasId = sysInf.getUpdaterState().getLast_tas_id();
			statistics.setLastTasId((lastTasId == null) ? -1 : lastTasId.longValue());
			// TODO: fetch other index statistics (num docs)
			indexInfo.setIndexStatistics(statistics);
			
			indexInfo.setGeneratingIndex(generating);
			rVal.add(indexInfo);
		}
		return rVal;
	}
	
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
	public void generatedIndex(final SharedResourceIndexGenerator<R> index) {
		if (index.isFinishedSuccesfully()) {
			this.esIndexManager.changeUnderConstructionStatus(index.getIndexName(), index.getResourceType());
		}
	}

	public SearchDBInterface<R> getDbLogic() {
		return this.dbLogic;
	}

	public void setDbLogic(SearchDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	public void setResourceConverter(ResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	public void setResourceType(Class<R> resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @param nameOfIndexToBeActivated
	 */
	public void activateIndex(String nameOfIndexToBeActivated) {
		this.esIndexManager.activateIndex(nameOfIndexToBeActivated, this.resourceType);
	}

	public void setGeneratorThreadPool(SimpleBlockingThreadPoolExecutor<SharedResourceIndexGenerator<? super R>> generatorThreadPool) {
		this.generatorThreadPool = generatorThreadPool;
	}

}
