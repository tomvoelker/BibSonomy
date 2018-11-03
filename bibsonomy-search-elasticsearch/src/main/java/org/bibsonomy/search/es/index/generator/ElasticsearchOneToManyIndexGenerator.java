package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.index.generator.OneToManyIndexGenerationLogic;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;

import java.net.URI;
import java.util.Map;

/**
 * generator to generate a entity with a to-many relation
 *
 * @param <T>
 * @param <M>
 */
public class ElasticsearchOneToManyIndexGenerator<T, M, S extends SearchIndexSyncState> extends ElasticsearchIndexGenerator<T, S> {

	private final OneToManyIndexGenerationLogic<T,M> generatorLogic;
	private final OneToManyEntityInformationProvider<T, M> entityInformationProvider;

	/**
	 * default construtor with all required fields
	 *
	 * @param client
	 * @param systemId
	 * @param generationLogic
	 * @param databaseInformationLogic
	 * @param indexSyncStateConverter
	 * @param entityInformationProvider
	 * @param generatorLogic
	 * @param entityInformationProvider1
	 */
	public ElasticsearchOneToManyIndexGenerator(ESClient client, URI systemId, IndexGenerationLogic<T> generationLogic, DatabaseInformationLogic<S> databaseInformationLogic, Converter<S, Map<String, Object>, Object> indexSyncStateConverter, EntityInformationProvider<T> entityInformationProvider, OneToManyIndexGenerationLogic<T, M> generatorLogic, OneToManyEntityInformationProvider<T, M> entityInformationProvider1) {
		super(client, systemId, generationLogic, databaseInformationLogic, indexSyncStateConverter, entityInformationProvider);
		this.generatorLogic = generatorLogic;
		this.entityInformationProvider = entityInformationProvider1;
	}

	@Override
	protected void insertDataIntoIndex(String indexName) {
		super.insertDataIntoIndex(indexName);

		this.insertDataIntoIndex(indexName, (lastContenId, limit) -> this.generatorLogic.getToManyEntities(lastContenId, limit), this.entityInformationProvider.getToManyEntityInformationProvider(), new IndexVoter<M>());
	}
}
