package org.bibsonomy.search.es.management;

import java.net.URI;
import java.util.Map;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;

/**
 * general entity manager that only updates a single entity
 *
 * @author dzo
 */
public class ElasticsearchEntityManager<T> extends ElasticsearchManager<T, DefaultSearchIndexSyncState> {

	private final DatabaseInformationLogic<DefaultSearchIndexSyncState> databaseInformationLogic;
	private final IndexUpdateLogic<T> updateIndexLogic;

	/**
	 * default constructor
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param databaseInformationLogic
	 * @param updateIndexLogic
	 */
	public ElasticsearchEntityManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<T, DefaultSearchIndexSyncState> generator, Converter<DefaultSearchIndexSyncState, Map<String, Object>, Object> syncStateConverter, EntityInformationProvider<T> entityInformationProvider, DatabaseInformationLogic<DefaultSearchIndexSyncState> databaseInformationLogic, IndexUpdateLogic<T> updateIndexLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, entityInformationProvider);
		this.databaseInformationLogic = databaseInformationLogic;
		this.updateIndexLogic = updateIndexLogic;
	}

	@Override
	protected void updateIndex(String indexName, DefaultSearchIndexSyncState oldState) {
		final DefaultSearchIndexSyncState targetState = this.databaseInformationLogic.getDbState();

		this.updateEntity(indexName, targetState, this.updateIndexLogic, this.entityInformationProvider);

		this.updateIndexState(indexName, oldState, targetState);
	}
}
