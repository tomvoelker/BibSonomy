package org.bibsonomy.search.es.management.person;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.update.OneToManyIndexUpdateLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * manager for persons
 *
 * @author dzo
 */
public class ElasticsearchPersonManager extends ElasticsearchManager<Person> {

	private final OneToManyIndexUpdateLogic<Person, ResourcePersonRelation> updateIndexLogic;
	private final OneToManyEntityInformationProvider<Person, ResourcePersonRelation> oneToManyEntityInformationProvider;

	/**
	 * default constructor
	 *
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param oneToManyEntityInformationProvider
	 */
	public ElasticsearchPersonManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Person> generator, OneToManyEntityInformationProvider<Person, ResourcePersonRelation> oneToManyEntityInformationProvider, final OneToManyIndexUpdateLogic<Person, ResourcePersonRelation> updateIndexLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, oneToManyEntityInformationProvider);
		this.updateIndexLogic = updateIndexLogic;
		this.oneToManyEntityInformationProvider = oneToManyEntityInformationProvider;
	}

	@Override
	protected void updateIndex(String indexName) {
		final String systemSyncStateIndexName = ElasticsearchUtils.getSearchIndexStateIndexName(this.systemId);
		final SearchIndexSyncState oldState = this.client.getSearchIndexStateForIndex(systemSyncStateIndexName, indexName);
		final IndexUpdateLogic<Person> parentUpdateLogic = this.updateIndexLogic.getIndexUpdateLogic();
		final SearchIndexSyncState targetState = parentUpdateLogic.getDbState();

		// update persons
		this.updateEntity(indexName, oldState, parentUpdateLogic, this.oneToManyEntityInformationProvider);

		// update person resource relations
		this.updateEntity(indexName, oldState, this.updateIndexLogic.getToManyIndexUpdateLogic(), this.oneToManyEntityInformationProvider.getToManyEntityInformationProvider());

		this.updateIndexState(indexName, targetState);
	}

	private <E> void updateEntity(final String indexName, final SearchIndexSyncState oldState, final IndexUpdateLogic<E> updateIndexLogic, final EntityInformationProvider<E> entityInformationProvider) {
		final Map<String, IndexData> convertedEntities = new HashMap<>();
		final long lastPersonChangeId = oldState.getLastPersonChangeId();
		final Date lastLogDate = oldState.getLast_log_date();
		int offset = 0;
		List<E> newEntity;
		do {
			newEntity = updateIndexLogic.getNewerEntities(lastPersonChangeId, lastLogDate, SearchDBInterface.SQL_BLOCKSIZE, offset);

			for (final E entity : newEntity) {

				final Map<String, Object> convertedPost = entityInformationProvider.getConverter().convert(entity);
				final IndexData indexData = new IndexData();
				indexData.setType(entityInformationProvider.getType());
				indexData.setSource(convertedPost);
				convertedEntities.put(entityInformationProvider.getEntityId(entity), indexData);
			}

			if (convertedEntities.size() >= ESConstants.BULK_INSERT_SIZE) {
				this.clearQueue(indexName, convertedEntities);
			}

			offset += SearchDBInterface.SQL_BLOCKSIZE;
		} while (newEntity.size() == SearchDBInterface.SQL_BLOCKSIZE);

		this.clearQueue(indexName, convertedEntities);
	}

	/**
	 * executes the query against the active index
	 *
	 * @param query
	 * @param limit
	 * @param offset
	 * @return
	 */
	public SearchHits search(final QueryBuilder query, int limit, int offset) {
		return this.client.search(this.getActiveLocalAlias(), this.entityInformationProvider.getType(), query, null, null, offset, limit, null, null);
	}
}
