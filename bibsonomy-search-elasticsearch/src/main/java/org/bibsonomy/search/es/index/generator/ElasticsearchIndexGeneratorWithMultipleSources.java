package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Converter;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * uses n generator sources to generate an index
 *
 * @param <E> the entity class
 *
 * @author dzo
 */
public class ElasticsearchIndexGeneratorWithMultipleSources<E, S extends SearchIndexSyncState> extends ElasticsearchIndexGenerator<E, S> {

	private final List<IndexGenerationLogic<E>> generationLogics;

	/**
	 * default constructor with all required fields
	 *
	 * @param client                    the client to use
	 * @param systemId
	 * @param generationLogics          the generator logics to use
	 * @param databaseInformationLogic
	 * @param indexSyncStateConverter
	 * @param entityInformationProvider the entity information provider for this entity index@param entityInformationProvider the entity information provider for this entity index
	 * @param generationLogics
	 */
	public ElasticsearchIndexGeneratorWithMultipleSources(ESClient client, URI systemId, DatabaseInformationLogic<S> databaseInformationLogic, Converter<S, Map<String, Object>, Object> indexSyncStateConverter, EntityInformationProvider<E> entityInformationProvider, List<IndexGenerationLogic<E>> generationLogics) {
		super(client, systemId, generationLogics.get(0), databaseInformationLogic, indexSyncStateConverter, entityInformationProvider);
		this.generationLogics = generationLogics;
	}

	@Override
	public void insertDataIntoIndex(String indexName) {
		for (final IndexGenerationLogic<E> generationLogic : this.generationLogics) {
			this.insertDataIntoIndex(indexName, (lastContenId, limit) -> generationLogic.getEntites(lastContenId, limit), this.entityInformationProvider, new IndexVoter<E>());
		}
	}
}
