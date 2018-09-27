package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.index.generator.OneToManyIndexGenerationLogic;

import java.net.URI;

/**
 * generator to
 * @param <T>
 * @param <M>
 */
public class ElasticsearchOneToManyIndexGenerator<T, M> extends ElasticsearchIndexGenerator<T> {

	private final OneToManyIndexGenerationLogic<T,M> generatorLogic;
	private final OneToManyEntityInformationProvider<T, M> entityInformationProvider;

	/**
	 * default construtor with all required fields
	 *
	 * @param systemId
	 * @param client
	 * @param generationLogic
	 * @param entityInformationProvider
	 */
	public ElasticsearchOneToManyIndexGenerator(URI systemId, ESClient client, OneToManyIndexGenerationLogic<T, M> generationLogic, OneToManyEntityInformationProvider<T, M> entityInformationProvider) {
		super(systemId, client, generationLogic, entityInformationProvider);

		this.generatorLogic = generationLogic;
		this.entityInformationProvider = entityInformationProvider;
	}

	@Override
	protected void insertDataIntoIndex(String indexName) {
		super.insertDataIntoIndex(indexName);

		this.insertDataIntoIndex(indexName, (lastContenId, limit) -> this.generatorLogic.getToManyEntities(lastContenId, limit), this.entityInformationProvider.getToManyEntityInformationProvider());
	}
}
