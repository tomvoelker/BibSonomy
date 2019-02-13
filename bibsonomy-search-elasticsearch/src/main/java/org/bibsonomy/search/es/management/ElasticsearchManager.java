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
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.DeleteData;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.client.UpdateData;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.generation.AbstractSearchIndexGenerationTask;
import org.bibsonomy.search.es.management.generation.ElasticSearchIndexGenerationTask;
import org.bibsonomy.search.es.management.generation.ElasticSearchIndexRegenerationTask;
import org.bibsonomy.search.es.management.post.ElasticsearchPostManager;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.management.SearchIndexManager;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.model.SearchIndexStatistics;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.util.BasicUtils;
import org.bibsonomy.util.Sets;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * manager that manages the indices for the specified type
 * @param <T>
 *
 * @author dzo
 */
public abstract class ElasticsearchManager<T, S extends SearchIndexSyncState> implements SearchIndexManager {
	private static final Log LOG = LogFactory.getLog(ElasticsearchManager.class);


	private final Semaphore updateLock = new Semaphore(1);
	private final Semaphore generatorLock = new Semaphore(1);

	private final boolean disabledIndexing;
	private final boolean updateEnabled;

	private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	private final ElasticsearchIndexGenerator<T, S> generator;

	/** the client to use for all interaction with elasticsearch */
	protected final ESClient client;
	protected final Converter<S, Map<String, Object>, Object> syncStateConverter;

	protected final EntityInformationProvider<T> entityInformationProvider;
	protected final URI systemId;

	/**
	 * default constructor
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 */
	public ElasticsearchManager(final URI systemId, final boolean disabledIndexing, final boolean updateEnabled, final ESClient client, ElasticsearchIndexGenerator<T, S> generator, final Converter<S, Map<String, Object>, Object> syncStateConverter, final EntityInformationProvider<T> entityInformationProvider) {
		this.disabledIndexing = disabledIndexing;
		this.updateEnabled = updateEnabled;
		this.generator = generator;
		this.client = client;
		this.syncStateConverter = syncStateConverter;
		this.entityInformationProvider = entityInformationProvider;
		this.systemId = systemId;
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
			if (present(currentActiveIndexName) && currentActiveIndexName.equals(indexName) && present(inactiveIndex)) {
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

			final String systemSyncStateIndexName = ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId);

			final S oldState = this.client.getSearchIndexStateForIndex(systemSyncStateIndexName, realIndexName, this.syncStateConverter);
			this.updateIndex(realIndexName, oldState);
			this.switchActiveAndInactiveIndex();
		} catch (final IndexNotFoundException e) {
			LOG.error("Can't update " + this.entityInformationProvider.getType() + " index. No inactive index available.");
		} finally {
			this.updateLock.release();
		}
	}

	protected abstract void updateIndex(final String indexName, S oldState);

	protected <E> void updateEntity(final String indexName, final DefaultSearchIndexSyncState oldState, final IndexUpdateLogic<E> updateIndexLogic, final EntityInformationProvider<E> entityInformationProvider) {
		final long lastContentId = oldState.getLastPostContentId();
		final Date lastLogDate = oldState.getLast_log_date();
		final String entityType = entityInformationProvider.getType();

		/*
		 * delete old entities
		 */
		final List<E> deletedEntities = updateIndexLogic.getDeletedEntities(lastLogDate);

		// convert the entities to the list of delete data
		final List<DeleteData> idsToDelete = deletedEntities.stream().map(entity -> {
			final DeleteData deleteData = new DeleteData();
			deleteData.setType(entityType);
			deleteData.setId(entityInformationProvider.getEntityId(entity));
			deleteData.setRouting(entityInformationProvider.getRouting(entity));
			return deleteData;
		}).collect(Collectors.toList());

		this.client.deleteDocuments(indexName, idsToDelete);

		/*
		 * insert new or updated entities
		 */
		final Map<String, IndexData> indexDataMap = new HashMap<>();

		LOG.debug("last content id is " + lastContentId + " lastLogDate is " + lastLogDate);

		BasicUtils.iterateListWithLimitAndOffset((limit, offset) -> updateIndexLogic.getNewerEntities(lastContentId, lastLogDate, limit, offset), entities -> {
			for (final E entity : entities) {
				final IndexData indexData = new IndexData();
				indexData.setRouting(entityInformationProvider.getRouting(entity));
				indexData.setType(entityInformationProvider.getType());
				indexData.setSource(entityInformationProvider.getConverter().convert(entity));

				final String entityId = entityInformationProvider.getEntityId(entity);
				LOG.debug("inserting new entity " + entityId);
				indexDataMap.put(entityId, indexData);

				if (indexDataMap.size() >= ESConstants.BULK_INSERT_SIZE) {
					this.clearQueue(indexName, indexDataMap);
				}
			}
		}, ElasticsearchPostManager.SQL_BLOCKSIZE);

		this.clearQueue(indexName, indexDataMap);
	}

	/**
	 * @param indexName
	 * @param convertedEntities
	 */
	protected void clearQueue(final String indexName, final Map<String, IndexData> convertedEntities) {
		if (!present(convertedEntities)) {
			// nothing to insert
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("inserting docs with the ids " + convertedEntities.keySet().stream().collect(Collectors.joining(", ")));
		}
		/*
		 * maybe we are updating an updated entity in es
		 */
		this.client.updateOrCreateDocuments(indexName, convertedEntities);
		convertedEntities.clear();
	}

	protected void clearUpdateQueue(String indexName, List<Pair<String, UpdateData>> updates) {
		if (!present(updates)) {
			// nothing to update
			return;
		}

		this.client.updateDocuments(indexName, updates);
	}

	/**
	 * @param indexName
	 * @param state
	 */
	protected void updateIndexState(final String indexName, final S oldState, final S state) {
		final IndexData indexData = new IndexData();
		indexData.setType(ESConstants.SYSTEM_INFO_INDEX_TYPE);

		/*
		 * here we use the mapping version info of the old state
		 * BasicUtils#VERSION maybe contain a new deployed version
		 */
		state.setMappingVersion(oldState.getMappingVersion());
		indexData.setSource(this.syncStateConverter.convert(state));
		this.client.insertNewDocument(ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId), indexName, indexData);
	}

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
			searchIndexInfo.setSyncState(this.client.getSearchIndexStateForIndex(ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId), indexName, this.syncStateConverter));
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
		final ElasticSearchIndexRegenerationTask<T> task = new ElasticSearchIndexRegenerationTask<>(this, this.generator, newIndexName, indexNameToReplace);
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
		final ElasticSearchIndexGenerationTask<T> task = new ElasticSearchIndexGenerationTask<>(this, this.generator, newIndexName, activeIndexAfterGeneration);
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
	 * execute a search
	 * @param query the query to use
	 * @param aggregationBuilder
	 * @param order the order
	 * @param offset the offset
	 * @param limit the limit
	 * @param minScore the min score
	 * @param fieldsToRetrieve the fields to retrieve
	 * @return
	 */
	public SearchHits search(final QueryBuilder query, AggregationBuilder aggregationBuilder, final Pair<String, SortOrder> order, int offset, int limit, Float minScore, final Set<String> fieldsToRetrieve) {
		return this.client.search(this.getActiveLocalAlias(), this.entityInformationProvider.getType(), query, aggregationBuilder, null, order, offset, limit, minScore, fieldsToRetrieve);
	}

	/**
	 * executes the query against the active index
	 *
	 * @param query
	 * @param limit
	 * @param offset
	 * @return
	 */
	public SearchHits search(final QueryBuilder query, int limit, int offset) {
		return this.search(query, null, null, offset, limit, null, null);
	}

	public long getDocumentCount(QueryBuilder query) {
		return this.client.getDocumentCount(this.getActiveLocalAlias(), this.entityInformationProvider.getType(), query);
	}

	/**
	 * shut downs the executor service
	 */
	public void shutdown() {
		this.executorService.shutdownNow();
	}
}
