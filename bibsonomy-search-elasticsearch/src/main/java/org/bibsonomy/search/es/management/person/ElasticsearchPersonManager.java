package org.bibsonomy.search.es.management.person;

import org.bibsonomy.model.Person;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * manager for persons
 *
 * @author dzo
 */
public class ElasticsearchPersonManager extends ElasticsearchManager<Person> {

	private final IndexUpdateLogic<Person> updateIndexLogic;

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
	public ElasticsearchPersonManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Person> generator, EntityInformationProvider<Person> entityInformationProvider, final IndexUpdateLogic<Person> updateIndexLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, entityInformationProvider);
		this.updateIndexLogic = updateIndexLogic;
	}

	@Override
	protected void updateIndex(String indexName) {
		final String systemSyncStateIndexName = ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId);
		final SearchIndexSyncState oldState = this.client.getSearchIndexStateForIndex(systemSyncStateIndexName, indexName);
		final SearchIndexSyncState targetState = this.updateIndexLogic.getDbState();

		// update person index by first update persons (name changes, attribute updates) and new created persons
		final Map<String, IndexData> convertedEntities = new HashMap<>();
		final long lastPersonChangeId = oldState.getLastPersonChangeId();
		int offset = 0;
		List<Person> newPersons;
		do {
			newPersons = this.updateIndexLogic.getNewerEntities(lastPersonChangeId, SearchDBInterface.SQL_BLOCKSIZE, offset);

			for (final Person person : newPersons) {
				final Map<String, Object> convertedPost = this.entityInformationProvider.getConverter().convert(person);


				final IndexData indexData = new IndexData();
				indexData.setType(this.entityInformationProvider.getType());
				indexData.setSource(convertedPost);
				convertedEntities.put(this.entityInformationProvider.getEntityId(person), indexData);
			}

			if (convertedEntities.size() >= ESConstants.BULK_INSERT_SIZE) {
				this.clearQueue(indexName, convertedEntities);
			}

			offset += SearchDBInterface.SQL_BLOCKSIZE;
		} while (newPersons.size() == SearchDBInterface.SQL_BLOCKSIZE);

		this.clearQueue(indexName, convertedEntities);

		// update person resource relations


		// set the new state of the index
		this.updateIndexState(indexName, targetState);
	}

	/**
	 * executes the query on the activve index
	 * @param query
	 * @param limit
	 * @param offset
	 * @return
	 */
	public SearchHits search(final QueryBuilder query, int limit, int offset) {
		return this.client.search(this.getActiveLocalAlias(), this.entityInformationProvider.getType(), query, null, null, offset, limit, null, null);
	}
}
