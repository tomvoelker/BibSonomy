package org.bibsonomy.search.es.management.project;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;

import java.net.URI;
import java.util.Map;

/**
 * manager for the {@link Project} index
 *
 * @author dzo
 */
public class ElasticsearchProjectManager extends ElasticsearchManager<Project, DefaultSearchIndexSyncState> {

	/**
	 * default constructor
	 *
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 */
	public ElasticsearchProjectManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Project, DefaultSearchIndexSyncState> generator, Converter<DefaultSearchIndexSyncState, Map<String, Object>, Object> syncStateConverter, EntityInformationProvider<Project> entityInformationProvider) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, entityInformationProvider);
	}

	@Override
	protected void updateIndex(String indexName, DefaultSearchIndexSyncState oldState) {
		// TODO: implement me
	}
}
