package org.bibsonomy.search.es.index.generator;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.util.BasicUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.net.URI;
import java.util.Date;
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
public class ElasticsearchIndexGenerator<T> {
	private static final Log LOG = LogFactory.getLog(ElasticsearchIndexGenerator.class);


	private final ESClient client;

	private final URI systemId;

	private final IndexGenerationLogic<T> generationLogic;

	private final Converter<T, Map<String, Object>, ?> converter;

	private final EntityInformationProvider<T> entityInformationProvider;

	private boolean generating = false;

	private int numberOfEntities;

	private int writtenEntities;
	private String indexName;

	/**
	 * default construtor with all required fields
	 * @param systemId
	 * @param client
	 * @param generationLogic
	 * @param entityInformationProvider
	 */
	public ElasticsearchIndexGenerator(URI systemId, ESClient client, IndexGenerationLogic<T> generationLogic, EntityInformationProvider<T> entityInformationProvider) {
		this.client = client;
		this.systemId = systemId;
		this.generationLogic = generationLogic;
		this.entityInformationProvider = entityInformationProvider;
		this.converter = this.entityInformationProvider.getConverter();
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
		final SearchIndexSyncState newState = this.generationLogic.getDbState();
		newState.setMappingVersion(BasicUtils.VERSION);
		if (newState.getLast_log_date() == null) {
			newState.setLast_log_date(new Date(System.currentTimeMillis() - 1000));
		}

		LOG.info("Start writing entites to index");

		// read block wise all posts
		List<T> entityList = null;
		int skip = 0;
		int lastContenId = -1;
		int entityListSize = 0;
		do {
			entityList = this.generationLogic.getEntites(lastContenId, SearchDBInterface.SQL_BLOCKSIZE);
			entityListSize = entityList.size();
			skip += entityListSize;
			LOG.info("Read " + skip + " entries.");
			final Map<String, Map<String, Object>> docsToWrite = new HashMap<>();
			// cycle through all posts of currently read block
			for (final T entity : entityList) {
				final Map<String, Object> convertedEntity = this.converter.convert(entity);

				docsToWrite.put(this.entityInformationProvider.getEntityId(entity), convertedEntity);

				if (docsToWrite.size() > ESConstants.BULK_INSERT_SIZE) {
					this.clearQueue(indexName, docsToWrite);
				}
			}

			this.clearQueue(indexName, docsToWrite);

			if (entityListSize > 0) {
				lastContenId = this.entityInformationProvider.getContentId(entityList.get(entityListSize - 1));
			}
		} while (entityListSize == SearchDBInterface.SQL_BLOCKSIZE);

		this.writeMetaInfoToIndex(indexName, newState);
	}

	/**
	 * @param docsToWrite
	 */
	private void clearQueue(final String indexName, final Map<String, Map<String, Object>> docsToWrite) {
		if (present(docsToWrite)) {
			this.client.insertNewDocuments(indexName, this.entityInformationProvider.getType(), docsToWrite);
			this.writtenEntities += docsToWrite.size();
			docsToWrite.clear();
		}
	}

	/**
	 * @param newState
	 */
	private void writeMetaInfoToIndex(final String indexName, final SearchIndexSyncState newState) {
		final Map<String, Object> values = ElasticsearchUtils.serializeSearchIndexState(newState);

		final String systemIndexName = ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId);
		final boolean inserted = this.client.insertNewDocument(systemIndexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, indexName, values);

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
