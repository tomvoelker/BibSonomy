package org.bibsonomy.search.es.management;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.generation.AbstractSearchIndexGenerationTask;
import org.bibsonomy.search.es.management.generation.ElasticSearchIndexGenerationTask;
import org.bibsonomy.search.es.management.generation.ElasticSearchIndexRegenerationTask;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.SearchIndexManager;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.util.Sets;

import java.net.URI;
import java.util.HashSet;
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
public class ElasticsearchManager<T> implements SearchIndexManager {
	private static final Log LOG = LogFactory.getLog(ElasticsearchManager.class);

	// FIXME: change visibility
	protected final Semaphore updateLock = new Semaphore(1);
	private final Semaphore generatorLock = new Semaphore(1);

	// FIXME: change visibility
	protected ElasticsearchIndexGenerator<T> currentGenerator;
	private final ExecutorService executorService = Executors.newFixedThreadPool(1);

	/** the client to use for all interaction with elasticsearch */
	protected ESClient client;

	private EntityInformationProvider<T> entityProvider;

	private URI systemId;

	private final boolean disabledIndexing;
	private IndexGenerationLogic<T> indexGeneratorLogic;

	public ElasticsearchManager(boolean disabledIndexing) {
		this.disabledIndexing = disabledIndexing;
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
		return ElasticsearchUtils.getLocalAliasForType(this.entityProvider.getType(), this.systemId, state);
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
	public void updateIndex() {
		// TODO: think about the index update
	}

	@Override
	public List<SearchIndexInfo> getIndexInformations() {
		return null;
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

		final String newIndexName = ElasticsearchUtils.getIndexNameWithTime(this.systemId, this.entityProvider.getType());
		final ElasticsearchIndexGenerator<T> generator = this.createGenerator(newIndexName);

		final ElasticSearchIndexRegenerationTask task = new ElasticSearchIndexRegenerationTask(this, generator, newIndexName, indexNameToReplace);
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
		final String newIndexName = ElasticsearchUtils.getIndexNameWithTime(this.systemId, this.entityProvider.getType());

		final ElasticsearchIndexGenerator<T> generator = createGenerator(newIndexName);

		final ElasticSearchIndexGenerationTask task = new ElasticSearchIndexGenerationTask(this, generator, newIndexName, activeIndexAfterGeneration);
		this.executeTask(async, task);
	}

	private ElasticsearchIndexGenerator<T> createGenerator(String newIndexName) {
		return new ElasticsearchIndexGenerator<T>(newIndexName, this.systemId, this.client, this.indexGeneratorLogic, this.entityProvider);
	}

	@Override
	public void enableIndex(String indexName) {
		if (!this.updateLock.tryAcquire()) {
			LOG.error("can't enable index ");
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
	 * callback, called when generator starts generating
	 * @param generatorTask
	 */
	public void generatingIndex(final ElasticsearchIndexGenerator<T> generatorTask) {
		this.currentGenerator = generatorTask;
	}

	/**
	 * releases the generator semaphore and sets the current generator to null
	 * @param generator
	 */
	public void generatedIndex(ElasticsearchIndexGenerator<T> generator) {
		this.currentGenerator = null;
		this.generatorLock.release();
	}

	/**
	 * shut downs the executor service
	 */
	public void shutdown() {
		this.executorService.shutdownNow();
	}


}
