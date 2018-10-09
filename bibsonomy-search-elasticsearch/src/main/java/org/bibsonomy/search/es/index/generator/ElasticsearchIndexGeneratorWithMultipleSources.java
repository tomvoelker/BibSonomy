package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;

import java.net.URI;
import java.util.List;

/**
 * uses n generator sources to generate an index
 *
 * @param <E> the entity class
 *
 * @author dzo
 */
public class ElasticsearchIndexGeneratorWithMultipleSources<E> extends ElasticsearchIndexGenerator<E> {

	private final List<IndexGenerationLogic<E>> generationLogics;

	/**
	 * default constructor with all required fields
	 *
	 * @param systemId                  the system id
	 * @param client                    the client to use
	 * @param generationLogics          the generator logics to use
	 * @param entityInformationProvider the entity information provider for this entity index
	 */
	public ElasticsearchIndexGeneratorWithMultipleSources(URI systemId, ESClient client, List<IndexGenerationLogic<E>> generationLogics, EntityInformationProvider<E> entityInformationProvider) {
		// FIXME: counts are not correct
		super(systemId, client, generationLogics.get(0), entityInformationProvider);

		this.generationLogics = generationLogics;
	}

	@Override
	public void insertDataIntoIndex(String indexName) {
		for (final IndexGenerationLogic<E> generationLogic : this.generationLogics) {
			this.insertDataIntoIndex(indexName, (lastContenId, limit) -> generationLogic.getEntites(lastContenId, limit), this.entityInformationProvider, new IndexVoter<E>());
		}
	}
}
