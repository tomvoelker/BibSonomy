package org.bibsonomy.search.es.management.person;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.DeleteData;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.index.database.person.PersonDatabaseInformationLogic;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.update.OneToManyIndexUpdateLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * manager for persons
 *
 * @author dzo
 */
public class ElasticsearchPersonManager extends ElasticsearchManager<Person, DefaultSearchIndexSyncState> {

	private final OneToManyIndexUpdateLogic<Person, ResourcePersonRelation> updateIndexLogic;
	private final OneToManyEntityInformationProvider<Person, ResourcePersonRelation> oneToManyEntityInformationProvider;
	private final PersonDatabaseInformationLogic dbInfoLogic;

	/**
	 * default constructor
	 *  @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param updateIndexLogic
	 * @param dbInfoLogic
	 */
	public ElasticsearchPersonManager(final URI systemId, final boolean disabledIndexing, final boolean updateEnabled, final ESClient client, final ElasticsearchIndexGenerator<Person, DefaultSearchIndexSyncState> generator, final Converter<DefaultSearchIndexSyncState, Map<String, Object>, Object> syncStateConverter, final OneToManyEntityInformationProvider<Person, ResourcePersonRelation> entityInformationProvider, final OneToManyIndexUpdateLogic<Person, ResourcePersonRelation> updateIndexLogic, final PersonDatabaseInformationLogic dbInfoLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, entityInformationProvider);
		this.updateIndexLogic = updateIndexLogic;
		this.oneToManyEntityInformationProvider = entityInformationProvider;
		this.dbInfoLogic = dbInfoLogic;
	}

	@Override
	protected void updateIndex(String indexName, final DefaultSearchIndexSyncState oldState) {
		final IndexUpdateLogic<Person> parentUpdateLogic = this.updateIndexLogic.getIndexUpdateLogic();
		final DefaultSearchIndexSyncState targetState = dbInfoLogic.getDbState();

		// update persons
		this.updateEntity(indexName, oldState, parentUpdateLogic, this.oneToManyEntityInformationProvider);

		// update person resource relations
		this.updateEntity(indexName, oldState, this.updateIndexLogic.getToManyIndexUpdateLogic(), this.oneToManyEntityInformationProvider.getToManyEntityInformationProvider());

		this.updateIndexState(indexName, targetState);
	}

	private <E> void updateEntity(final String indexName, final DefaultSearchIndexSyncState oldState, final IndexUpdateLogic<E> updateIndexLogic, final EntityInformationProvider<E> entityInformationProvider) {
		final long lastPersonChangeId = oldState.getLastPersonChangeId();
		final Date lastLogDate = oldState.getLast_log_date();
		final String entityType = entityInformationProvider.getType();

		/*
		 * delete old entities
		 */
		final List<E> deletedEntities = updateIndexLogic.getDeletedEntities(lastLogDate);

		// convert the entities to the list of delete data
		final List<DeleteData> idsToDelete = deletedEntities.stream().map(entity -> {
			final DeleteData deleteData = new DeleteData();
			deleteData.setType(entityType);
			deleteData.setId(entityInformationProvider.getEntityId(entity));
			deleteData.setRouting(entityInformationProvider.getRouting(entity));
			return deleteData;
		}).collect(Collectors.toList());

		this.client.deleteDocuments(indexName, idsToDelete);

		/*
		 * insert new or updated entities
		 */
		final Map<String, IndexData> convertedEntities = new HashMap<>();
		int offset = 0;
		List<E> newEntity;
		do {
			newEntity = updateIndexLogic.getNewerEntities(lastPersonChangeId, lastLogDate, SearchDBInterface.SQL_BLOCKSIZE, offset);

			for (final E entity : newEntity) {
				final Map<String, Object> convertedPost = entityInformationProvider.getConverter().convert(entity);
				final IndexData indexData = new IndexData();
				indexData.setType(entityType);
				indexData.setSource(convertedPost);
				indexData.setRouting(entityInformationProvider.getRouting(entity));
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
