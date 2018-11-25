package org.bibsonomy.search.es.management;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.DeleteData;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.es.management.post.ElasticsearchPostManager;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.update.OneToManyIndexUpdateLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexDualSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.util.BasicUtils;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

		this.updateIndexState(indexName, targetState);
	}

	private <E> void updateEntity(final String indexName, final DefaultSearchIndexSyncState oldState, final IndexUpdateLogic<E> updateIndexLogic, final EntityInformationProvider<E> entityInformationProvider) {
		final long lastContentId = oldState.getLastPostContentId();
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
		final Map<String, IndexData> indexDataMap = new HashMap<>();

		BasicUtils.iterateListWithLimitAndOffset((limit, offset) -> updateIndexLogic.getNewerEntities(lastContentId, lastLogDate, limit, offset), entities -> {
			for (final E entity : entities) {
				final IndexData indexData = new IndexData();
				indexData.setRouting(entityInformationProvider.getRouting(entity));
				indexData.setType(entityInformationProvider.getType());
				indexData.setSource(entityInformationProvider.getConverter().convert(entity));

				final String entityId = entityInformationProvider.getEntityId(entity);
				indexDataMap.put(entityId, indexData);

				if (indexDataMap.size() >= ESConstants.BULK_INSERT_SIZE) {
					this.clearQueue(indexName, indexDataMap);
				}
			}
		}, ElasticsearchPostManager.SQL_BLOCKSIZE);

		this.clearQueue(indexName, indexDataMap);
	}
}
