package org.bibsonomy.search.es.index.generator;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.util.BasicUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * abstract class to generate an index of the specified type
 *
 * @param <T> the model type of the index to generate
 *
 * @author dzo
 */
public class ElasticsearchIndexGenerator<T, S extends SearchIndexSyncState> {
	private static final Log LOG = LogFactory.getLog(ElasticsearchIndexGenerator.class);

	private final ESClient client;
	private final URI systemId;

	private final IndexGenerationLogic<T> generationLogic;
	private final DatabaseInformationLogic<S> databaseInformationLogic;

	private final Converter<S, Map<String, Object>, Object> indexSyncStateConverter;

	protected final EntityInformationProvider<T> entityInformationProvider;

	private boolean generating = false;

	private int numberOfEntities;

	private int writtenEntities;
	private String indexName;

	@FunctionalInterface
	protected interface Generator<E> {
		List<E> getEntites(int lastContenId, int limit);
	}

	/**
	 * an entity is added to the index iff the provided index voter votes for the entity
	 *
	 * (maybe an implementation wants to check the already added
	 *
	 * @param <E>
	 */
	protected class IndexVoter<E> {
		public boolean indexEntity(final E entity) {
			return true;
		}
	}

	/**
	 * default constructor
	 *  @param client
	 * @param systemId
	 * @param generationLogic
	 * @param databaseInformationLogic
	 * @param indexSyncStateConverter
	 * @param entityInformationProvider
	 */
	public ElasticsearchIndexGenerator(ESClient client, URI systemId, IndexGenerationLogic<T> generationLogic, DatabaseInformationLogic<S> databaseInformationLogic, Converter<S, Map<String, Object>, Object> indexSyncStateConverter, EntityInformationProvider<T> entityInformationProvider) {
		this.client = client;
		this.systemId = systemId;
		this.generationLogic = generationLogic;
		this.databaseInformationLogic = databaseInformationLogic;
		this.indexSyncStateConverter = indexSyncStateConverter;
		this.entityInformationProvider = entityInformationProvider;
	}

	/**
	 * method that generates a new ElasticSearch index
	 */
	public void generateIndex(final String indexName) {
		this.generating = true;
		this.indexName = indexName;
		this.createNewIndex(indexName);
		this.fillIndexWithData(indexName);
		this.indexCreated(indexName);
		this.indexName = null;
		this.generating = false;
	}

	/**
	 * this methods creates the new index
	 */
	private void createNewIndex(final String indexName) {
		this.client.waitForReadyState();

		// check if the index already exists if not, it creates empty index
		final boolean indexExists = this.client.existsIndexWithName(indexName);
		if (indexExists) {
			throw new IllegalStateException("index '" + indexName + "' already exists while generating an index");
		}

		final Mapping<XContentBuilder> mapping = this.entityInformationProvider.getMappingBuilder().getMapping();
		LOG.info("generating a new index ('" + indexName + "')");

		final boolean created = this.client.createIndex(indexName, mapping, ESConstants.SETTINGS);
		if (!created) {
			throw new RuntimeException("can not create index '" + indexName + "'"); // TODO: use specific exception
		}

		this.client.createAlias(indexName, ElasticsearchUtils.getLocalAliasForType(this.entityInformationProvider.getType(), this.systemId, SearchIndexState.GENERATING));
	}

	/**
	 * inserts the posts form the database into the index
	 */
	private void fillIndexWithData(final String indexName) {
		LOG.info("Filling index with database post entries.");

		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfEntities = this.generationLogic.getNumberOfEntities();
		LOG.info("Number of post entries: " + this.numberOfEntities);

		// initialize variables
		// FIXME: introduce a index state for each entity
		final S newState = this.databaseInformationLogic.getDbState();
		newState.setMappingVersion(BasicUtils.VERSION);

		this.insertDataIntoIndex(indexName);
		this.writeMetaInfoToIndex(indexName, newState);
	}

	protected void insertDataIntoIndex(String indexName) {
		this.insertDataIntoIndex(indexName, (lastContenId, limit) -> ElasticsearchIndexGenerator.this.generationLogic.getEntites(lastContenId, limit), this.entityInformationProvider, new IndexVoter<>());
	}

	protected final <E> void insertDataIntoIndex(String indexName, Generator<E> generator, EntityInformationProvider<E> entityInformationProvider, final IndexVoter<E> indexVoter) {
		LOG.info("Start writing entities to index");

		final Converter<E, Map<String, Object>, ?> converter = entityInformationProvider.getConverter();

		// read block wise all posts
		List<E> entityList;
		int skip = 0;
		int lastContenId = -1;
		int entityListSize;
		final Map<String, IndexData> docsToWrite = new HashMap<>();
		do {
			entityList = generator.getEntites(lastContenId, SearchDBInterface.SQL_BLOCKSIZE);
			entityListSize = entityList.size();
			skip += entityListSize;
			LOG.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final E entity : entityList) {
				if (indexVoter.indexEntity(entity)) {
					final Map<String, Object> convertedEntity = converter.convert(entity);

					final IndexData indexData = new IndexData();
					indexData.setSource(convertedEntity);
					indexData.setType(entityInformationProvider.getType());
					indexData.setRouting(entityInformationProvider.getRouting(entity));

					docsToWrite.put(entityInformationProvider.getEntityId(entity), indexData);

					if (docsToWrite.size() > ESConstants.BULK_INSERT_SIZE) {
						this.clearQueue(indexName, docsToWrite);
					}
				}
			}

			this.clearQueue(indexName, docsToWrite);

			if (entityListSize > 0) {
				lastContenId = entityInformationProvider.getContentId(entityList.get(entityListSize - 1));
			}
		} while (entityListSize == SearchDBInterface.SQL_BLOCKSIZE);
	}

	/**
	 * @param docsToWrite
	 */
	private void clearQueue(final String indexName, final Map<String, IndexData> docsToWrite) {
		if (present(docsToWrite)) {
			this.client.updateOrCreateDocuments(indexName, docsToWrite);
			this.writtenEntities += docsToWrite.size();
			docsToWrite.clear();
		}
	}

	/**
	 * @param newState
	 */
	private void writeMetaInfoToIndex(final String indexName, final S newState) {
		final Map<String, Object> values = this.indexSyncStateConverter.convert(newState);

		final String systemIndexName = ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId);
		final IndexData systemIndexData = new IndexData();
		systemIndexData.setSource(values);
		systemIndexData.setType(ESConstants.SYSTEM_INFO_INDEX_TYPE);
		final boolean inserted = this.client.insertNewDocument(systemIndexName, indexName, systemIndexData);

		if (!inserted) {
			throw new RuntimeException("failed to save systeminformation for index " + indexName);
		}

		LOG.info("updated systeminformation of index " + indexName + " to " + values);
	}

	/**
	 * methods removes the index state generating and adds the index state alias standby to the generated index
	 */
	private void indexCreated(final String indexName) {
		this.client.deleteAlias(indexName, ElasticsearchUtils.getLocalAliasForType(this.entityInformationProvider.getType(), this.systemId, SearchIndexState.GENERATING));

		this.client.createAlias(indexName, ElasticsearchUtils.getLocalAliasForType(this.entityInformationProvider.getType(), this.systemId, SearchIndexState.STANDBY));
	}

	/**
	 * @return the progress
	 */
	public double getProgress() {
		if (this.numberOfEntities == 0) {
			return 0;
		}
		return this.writtenEntities / (double) this.numberOfEntities;
	}

	/**
	 * @return the generating
	 */
	public boolean isGenerating() {
		return generating;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return indexName;
	}
}
