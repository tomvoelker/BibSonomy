package org.bibsonomy.search.es.management;

import java.net.URI;
import java.util.Map;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.OneToManyIndexUpdateLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexDualSyncState;
import org.bibsonomy.search.util.Converter;

/**
 * a manager that can update a one to many index
 *
 * @author dzo
 * @param <T>
 * @param <M> the many type
 */
public class ElasticsearchOneToManyManager<T, M> extends ElasticsearchManager<T, SearchIndexDualSyncState> {

	private final OneToManyIndexUpdateLogic<T, M> updateIndexLogic;
	private final DatabaseInformationLogic<SearchIndexDualSyncState> databaseInformationLogic;
	private final OneToManyEntityInformationProvider<T, M> oneToManyEntityInformationProvider;

	/**
	 * constructor to build a new one to many manager
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param updateIndexLogic
	 * @param databaseInformationLogic
	 * @param oneToManyEntityInformationProvider
	 */
	public ElasticsearchOneToManyManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<T, SearchIndexDualSyncState> generator, Converter<SearchIndexDualSyncState, Map<String, Object>, Object> syncStateConverter, OneToManyIndexUpdateLogic<T, M> updateIndexLogic, DatabaseInformationLogic<SearchIndexDualSyncState> databaseInformationLogic, OneToManyEntityInformationProvider<T, M> oneToManyEntityInformationProvider) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, oneToManyEntityInformationProvider);
		this.updateIndexLogic = updateIndexLogic;
		this.databaseInformationLogic = databaseInformationLogic;
		this.oneToManyEntityInformationProvider = oneToManyEntityInformationProvider;
	}

	@Override
	protected void updateIndex(String indexName, SearchIndexDualSyncState oldState) {
		final DefaultSearchIndexSyncState oldFirstState = oldState.getFirstState();
		final DefaultSearchIndexSyncState oldSecondState = oldState.getSecondState();
		final SearchIndexDualSyncState targetState = this.databaseInformationLogic.getDbState();

		this.updateEntity(indexName, oldFirstState, this.updateIndexLogic.getIndexUpdateLogic(), this.oneToManyEntityInformationProvider);

		// update many relations
		this.updateEntity(indexName, oldSecondState, this.updateIndexLogic.getToManyIndexUpdateLogic(), this.oneToManyEntityInformationProvider.getToManyEntityInformationProvider());

		this.updateIndexState(indexName, oldState, targetState);
	}
}
