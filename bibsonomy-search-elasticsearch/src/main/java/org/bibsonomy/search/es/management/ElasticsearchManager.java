/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.management;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.generation.AbstractSearchIndexGenerationTask;
import org.bibsonomy.search.es.management.generation.ElasticSearchIndexGenerationTask;
import org.bibsonomy.search.es.management.generation.ElasticSearchIndexRegenerationTask;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.management.SearchIndexManager;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.model.SearchIndexStatistics;
import org.bibsonomy.util.Sets;
import org.elasticsearch.index.IndexNotFoundException;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * manager that manages the indices for the specified type
 * @param <T>
 *
 * @author dzo
 */
public abstract class ElasticsearchManager<T> implements SearchIndexManager {
	private static final Log LOG = LogFactory.getLog(ElasticsearchManager.class);


	private final Semaphore updateLock = new Semaphore(1);
	private final Semaphore generatorLock = new Semaphore(1);

	private final boolean disabledIndexing;
	private final boolean updateEnabled;

	private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	private final ElasticsearchIndexGenerator<T> generator;

	/** the client to use for all interaction with elasticsearch */
	protected final ESClient client;

	protected final EntityInformationProvider<T> entityInformationProvider;
	protected final URI systemId;

	/**
	 * default constructor
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 */
	public ElasticsearchManager(final URI systemId, boolean disabledIndexing, final boolean updateEnabled, final ESClient client, final ElasticsearchIndexGenerator<T> generator, final EntityInformationProvider<T> entityInformationProvider) {
		this.systemId = systemId;
		this.generator = generator;
		this.entityInformationProvider = entityInformationProvider;
		this.disabledIndexing = disabledIndexing;
		this.updateEnabled = updateEnabled;
		this.client = client;
	}

	/**
	 * @param indexName
	 */
	@Override
	public void deleteIndex(final String indexName) {
		if (!this.updateLock.tryAcquire()) {
			throw new IllegalStateException("You cannot delete indices while update is in progress.");
		}

		try {
			// check if index is the current active one
			final String currentActiveIndexName = this.client.getIndexNameForAlias(this.getActiveLocalAlias());
			final String inactiveIndex = this.client.getIndexNameForAlias(this.getInactiveLocalAlias());
			if (currentActiveIndexName.equals(indexName) && present(inactiveIndex)) {
				this.switchActiveAndInactiveIndex();
			}

			this.client.deleteIndex(indexName);
		} finally {
			this.updateLock.release();
		}
	}

	/**
	 * @param state
	 * @return
	 */
	protected String getAliasNameForState(final SearchIndexState state) {
		return ElasticsearchUtils.getLocalAliasForType(this.entityInformationProvider.getType(), this.systemId, state);
	}

	/**
	 * @return
	 */
	protected String getInactiveLocalAlias() {
		return this.getAliasNameForState(SearchIndexState.INACTIVE);
	}

	/**
	 * @return
	 */
	protected String getActiveLocalAlias() {
		return this.getAliasNameForState(SearchIndexState.ACTIVE);
	}

	/**
	 * switch active and inactive index
	 */
	protected void switchActiveAndInactiveIndex() {
		final String localActiveAlias = this.getActiveLocalAlias();
		final String localInactiveAlias = this.getInactiveLocalAlias();
		final String activeIndexName = this.client.getIndexNameForAlias(localActiveAlias);
		final String inactiveIndexName = this.client.getIndexNameForAlias(localInactiveAlias);

		@SuppressWarnings("unchecked")
		final Set<Pair<String, String>> aliasesToAdd = Sets.asSet(
						new Pair<>(inactiveIndexName, localActiveAlias),
						new Pair<>(activeIndexName, localInactiveAlias)
		);
		@SuppressWarnings("unchecked")
		final Set<Pair<String, String>> aliasesToRemove = Sets.asSet(
						new Pair<>(activeIndexName, localActiveAlias),
						new Pair<>(inactiveIndexName, localInactiveAlias)
		);

		LOG.debug("switching index (current state I=" + inactiveIndexName + ", A=" + activeIndexName);
		// update the aliases (atomic, see elastic search docu)
		this.client.updateAliases(aliasesToAdd, aliasesToRemove);
	}

	/**
	 * @param 	newIndexName
	 * @param 	indexToDelete which index should be deleted;
	 * 			<code>null</code> iff no preference
	 */
	public void activateNewIndex(String newIndexName, String indexToDelete) {
		try {
			this.updateLock.tryAcquire(5, TimeUnit.MINUTES);
			/*
			 * change alias:
			 * 1. new index will be the new active index
			 * 2. old active index will be the new inactive index
			 * 3. if present the inactive or indexToDelete index will be deleted
			 */
			final String localActiveAlias = this.getActiveLocalAlias();
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final String activeIndexName = this.client.getIndexNameForAlias(localActiveAlias);
			final String inactiveIndexName = this.client.getIndexNameForAlias(localInactiveAlias);
			// set the preferedIndexToDelete to the inactive one iff no index specified
			if (!present(indexToDelete)) {
				indexToDelete = inactiveIndexName;
			}

			final Set<Pair<String, String>> aliasesToAdd = new HashSet<>();
			aliasesToAdd.add(new Pair<>(newIndexName, localActiveAlias));

			final Set<Pair<String, String>> aliasesToRemove = new HashSet<>();
			// remove the standby alias
			aliasesToRemove.add(new Pair<>(newIndexName, this.getAliasNameForState(SearchIndexState.STANDBY)));
			// only set the alias if the index should not be deleted
			final boolean preferedDeletedActiveIndex = present(activeIndexName) && activeIndexName.equals(indexToDelete);
			if (present(activeIndexName)) {
				// remove active alias from the current active index
				aliasesToRemove.add(new Pair<>(activeIndexName, localActiveAlias));
				// we use the index as inactive index if we do not want to delete it
				if (!preferedDeletedActiveIndex) {
					aliasesToAdd.add(new Pair<>(activeIndexName, localInactiveAlias));
				}
			}

			// only remove the alias if the other index should be deleted
			if (present(inactiveIndexName) && !preferedDeletedActiveIndex) {
				aliasesToRemove.add(new Pair<>(inactiveIndexName, localInactiveAlias));
			}

			this.client.updateAliases(aliasesToAdd, aliasesToRemove);
			if (present(indexToDelete)) {
				this.client.deleteIndex(indexToDelete);
			}
		} catch (final InterruptedException e) {
			LOG.error("can't acquire lock to update aliases", e);
		} finally {
			this.updateLock.release();
		}
	}

	@Override
	public final void updateIndex() {
		if (!this.updateEnabled) {
			LOG.debug("skipping updating index, update disabled");
			return;
		}

		if (!this.updateLock.tryAcquire()) {
			LOG.warn("Another update in progress. Skipping update.");
			return;
		}

		try {
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final String realIndexName = this.client.getIndexNameForAlias(localInactiveAlias);
			if (!present(realIndexName)) {
				LOG.error("no inactive index found for " + this.entityInformationProvider.getType());
				return;
			}
			this.updateIndex(realIndexName);
			this.switchActiveAndInactiveIndex();
		} catch (final IndexNotFoundException e) {
			LOG.error("Can't update " + this.entityInformationProvider.getType() + " index. No inactive index available.");
		} finally {
			this.updateLock.release();
		}
	}

	protected abstract void updateIndex(final String indexName);

	/**
	 * @return informations about the indices managed by this manager
	 */
	@Override
	public List<SearchIndexInfo> getIndexInformations() {
		final List<SearchIndexInfo> infos = new LinkedList<>();

		// get infos about the current active index
		try {
			final String localActiveAlias = this.getActiveLocalAlias();
			final String localActiveIndexName = this.client.getIndexNameForAlias(localActiveAlias);
			if (present(localActiveIndexName)) {
				final SearchIndexInfo searchIndexInfo = getIndexInfoForIndex(localActiveIndexName, SearchIndexState.ACTIVE, true);
				infos.add(searchIndexInfo);
			}
		} catch (final IndexNotFoundException e) {
			// ignore
		}

		// get infos about the current inactive index
		try {
			final String localInactiveAlias = this.getInactiveLocalAlias();
			final String localInactiveIndexName = this.client.getIndexNameForAlias(localInactiveAlias);
			if (present(localInactiveIndexName)) {
				final SearchIndexInfo searchIndexInfo = getIndexInfoForIndex(localInactiveIndexName, SearchIndexState.INACTIVE, true);
				infos.add(searchIndexInfo);
			}
		} catch (final IndexNotFoundException e) {
			// ignore
		}

		// get infos about the standby indices
		final List<String> indices = this.getAllStandByIndices();
		for (final String indexName : indices) {
			final SearchIndexInfo searchIndexInfoStandBy = getIndexInfoForIndex(indexName, SearchIndexState.STANDBY, true);
			searchIndexInfoStandBy.setId(indexName);
			infos.add(searchIndexInfoStandBy);
		}

		// get info about the index which is currently generated
		if (this.generator.isGenerating()) {
			try {
				final SearchIndexInfo searchIndexInfoGeneratingIndex = new SearchIndexInfo();
				searchIndexInfoGeneratingIndex.setState(SearchIndexState.GENERATING);
				searchIndexInfoGeneratingIndex.setIndexGenerationProgress(this.generator.getProgress());
				searchIndexInfoGeneratingIndex.setId(this.generator.getIndexName());
				infos.add(searchIndexInfoGeneratingIndex);
			} catch (final IndexNotFoundException e) {
				// ignore
			}
		}

		// TODO: include indices that could not be generated (dead indices)
		return infos;
	}

	/**
	 * @return all standby index names
	 */
	private List<String> getAllStandByIndices() {
		return this.client.getIndexNamesForAlias(this.getAliasNameForState(SearchIndexState.STANDBY));
	}

	/**
	 * @param indexName
	 * @param state
	 * @param loadSyncState
	 * @return
	 */
	private SearchIndexInfo getIndexInfoForIndex(final String indexName, final SearchIndexState state, boolean loadSyncState) {
		final SearchIndexInfo searchIndexInfo = new SearchIndexInfo();
		searchIndexInfo.setState(state);
		searchIndexInfo.setId(indexName);

		if (loadSyncState) {
			searchIndexInfo.setSyncState(this.client.getSearchIndexStateForIndex(ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId), indexName));
		}

		final SearchIndexStatistics statistics = new SearchIndexStatistics();
		final long count = this.client.getDocumentCount(indexName, this.entityInformationProvider.getType(), null);
		statistics.setNumberOfDocuments(count);
		searchIndexInfo.setStatistics(statistics);
		return searchIndexInfo;
	}

	@Override
	public void regenerateIndex(String indexName) throws IndexAlreadyGeneratingException {
		regenerateIndex(indexName, true);
	}

	/**
	 * @param indexNameToReplace
	 * @param async
	 * @throws IndexAlreadyGeneratingException
	 */
	protected void regenerateIndex(final String indexNameToReplace, final boolean async) throws IndexAlreadyGeneratingException {
		if (!this.generatorLock.tryAcquire()) {
			throw new IndexAlreadyGeneratingException();
		}

		final String newIndexName = ElasticsearchUtils.getIndexNameWithTime(this.systemId, this.entityInformationProvider.getType());
		final ElasticSearchIndexRegenerationTask task = new ElasticSearchIndexRegenerationTask(this, this.generator, newIndexName, indexNameToReplace);
		this.executeTask(async, task);
	}

	/**
	 * @param async
	 * @param task
	 */
	private void executeTask(final boolean async, final AbstractSearchIndexGenerationTask<T> task) {
		if (async) {
			this.executorService.submit(task);
		} else {
			try {
				task.call();
			} catch (Exception e) {
				LOG.error("error while running synchronous generation task.", e);
				throw new RuntimeException(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexManager#regenerateAllIndices()
	 */
	@Override
	public void regenerateAllIndices() {
		if (this.disabledIndexing) {
			return;
		}
		try {
			final String activeIndex = this.client.getIndexNameForAlias(this.getActiveLocalAlias());
			if (present(activeIndex)) {
				this.regenerateIndex(activeIndex, false);
			} else {
				this.generateIndex(false, true);
			}
			final String currentActiveIndex = this.client.getIndexNameForAlias(this.getActiveLocalAlias());
			final String currentInactiveIndex = this.client.getIndexNameForAlias(this.getInactiveLocalAlias());

			final String secondIndexToRegenerate;
			if (present(activeIndex) && !activeIndex.equals(currentActiveIndex)) {
				secondIndexToRegenerate = currentActiveIndex;
			} else {
				secondIndexToRegenerate = currentInactiveIndex;
			}

			if (present(secondIndexToRegenerate)) {
				this.regenerateIndex(secondIndexToRegenerate, false);
			} else {
				this.generateIndex(false, true);
			}
		} catch (final IndexAlreadyGeneratingException e) {
			LOG.error("error while regeneration all indices", e);
		}
	}

	/**
	 * generates a new index for the resource
	 * @throws IndexAlreadyGeneratingException
	 */
	@Override
	public void generateIndex() throws IndexAlreadyGeneratingException {
		this.generateIndex(true, true);
	}

	/**
	 * @param async
	 * @param activeIndexAfterGeneration
	 * @throws IndexAlreadyGeneratingException
	 */
	protected void generateIndex(final boolean async, final boolean activeIndexAfterGeneration) throws IndexAlreadyGeneratingException {
		if (!this.generatorLock.tryAcquire()) {
			throw new IndexAlreadyGeneratingException();
		}
		final String newIndexName = ElasticsearchUtils.getIndexNameWithTime(this.systemId, this.entityInformationProvider.getType());
		final ElasticSearchIndexGenerationTask task = new ElasticSearchIndexGenerationTask(this, this.generator, newIndexName, activeIndexAfterGeneration);
		this.executeTask(async, task);
	}

	@Override
	public void enableIndex(String indexName) {
		if (!this.updateLock.tryAcquire()) {
			LOG.error("can't enable index");
			return;
		}
		try {
			final String activeAlias = this.getActiveLocalAlias();
			final String inactiveAlias = this.getInactiveLocalAlias();
			final String standbyAlias = this.getAliasNameForState(SearchIndexState.STANDBY);

			final List<String> indices = this.client.getIndexNamesForAlias(standbyAlias);
			if (!present(indexName) || !indices.contains(indexName)) {
				throw new IllegalStateException("index not in state " + SearchIndexState.STANDBY);
			}

			final String activeIndexName = this.client.getIndexNameForAlias(activeAlias);
			final String inactiveIndexName = this.client.getIndexNameForAlias(inactiveAlias);

			final Set<Pair<String, String>> aliasesToAdd = new HashSet<>();
			final Set<Pair<String, String>> aliasesToRemove = new HashSet<>();

			// current active index => inactive index
			if (present(activeIndexName)) {
				aliasesToRemove.add(new Pair<>(activeIndexName, activeAlias));
				aliasesToAdd.add(new Pair<>(activeIndexName, inactiveAlias));
			}

			// current inactive index => standby index
			if (present(inactiveIndexName)) {
				aliasesToAdd.add(new Pair<>(inactiveIndexName, standbyAlias));
				aliasesToRemove.add(new Pair<>(inactiveIndexName, inactiveAlias));
			}

			// enabled index => active index
			aliasesToAdd.add(new Pair<>(indexName, activeAlias));
			aliasesToRemove.add(new Pair<>(indexName, standbyAlias));

			this.client.updateAliases(aliasesToAdd, aliasesToRemove);
		} finally {
			this.updateLock.release();
		}
	}

	/**
	 * releases the generator semaphore
	 */
	public void generatedIndex() {
		this.generatorLock.release();
	}

	/**
	 * @return all public fields of the managed index
	 */
	public Set<String> getPublicFields() {
		return this.entityInformationProvider.getPublicFields();
	}

	/**
	 * @return all private fields of the managed index
	 */
	public Set<String> getPrivateFields() {
		return this.entityInformationProvider.getPrivateFields();
	}

	/**
	 * shut downs the executor service
	 */
	public void shutdown() {
		this.executorService.shutdownNow();
	}

}
