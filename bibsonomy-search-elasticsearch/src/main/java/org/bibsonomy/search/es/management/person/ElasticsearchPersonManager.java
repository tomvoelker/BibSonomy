package org.bibsonomy.search.es.management.person;

import org.bibsonomy.model.Person;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;

import java.net.URI;

/**
 * manager for persons
 *
 * @author dzo
 */
public class ElasticsearchPersonManager extends ElasticsearchManager<Person> {

	/**
	 * default constructor
	 *
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param entityInformationProvider
	 */
	public ElasticsearchPersonManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Person> generator, EntityInformationProvider<Person> entityInformationProvider) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, entityInformationProvider);
	}

	@Override
	protected void updateIndex(String indexName) {

	}
}
