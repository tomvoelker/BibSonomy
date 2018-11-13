package org.bibsonomy.search.es.management.project;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.DeleteData;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.management.post.ElasticsearchPostManager;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.util.BasicUtils;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * manager for the {@link Project} index
 *
 * @author dzo
 */
public class ElasticsearchProjectManager extends ElasticsearchManager<Project, DefaultSearchIndexSyncState> {

	private final IndexUpdateLogic<Project> projectIndexUpdateLogic;
	// TODO: move down to manager
	private final DatabaseInformationLogic<DefaultSearchIndexSyncState> databaseInformationLogic;

	/**
	 * default constructor with all required fields
	 *
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param projectIndexUpdateLogic
	 * @param databaseInformationLogic
	 */
	public ElasticsearchProjectManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Project, DefaultSearchIndexSyncState> generator, Converter<DefaultSearchIndexSyncState, Map<String, Object>, Object> syncStateConverter, EntityInformationProvider<Project> entityInformationProvider, IndexUpdateLogic<Project> projectIndexUpdateLogic, DatabaseInformationLogic<DefaultSearchIndexSyncState> databaseInformationLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, entityInformationProvider);
		this.projectIndexUpdateLogic = projectIndexUpdateLogic;
		this.databaseInformationLogic = databaseInformationLogic;
	}

	@Override
	protected void updateIndex(String indexName, DefaultSearchIndexSyncState oldState) {
		final Date oldStateLastLogDate = oldState.getLast_log_date();
		final DefaultSearchIndexSyncState targetState = this.databaseInformationLogic.getDbState();

		final Map<String, IndexData> indexDataMap = new HashMap<>();

		/*
		 * add new and updated projects
		 */
		BasicUtils.iterateListWithLimitAndOffset((limit, offset) -> this.projectIndexUpdateLogic.getNewerEntities(oldState.getLastPostContentId(), oldStateLastLogDate, limit, offset), (projects) -> {
			for (final Project project : projects) {

				final IndexData indexData = new IndexData();
				indexData.setRouting(this.entityInformationProvider.getRouting(project));
				indexData.setType(this.entityInformationProvider.getType());
				indexData.setSource(this.entityInformationProvider.getConverter().convert(project));

				final String entityId = this.entityInformationProvider.getEntityId(project);
				indexDataMap.put(entityId, indexData);

				if (indexDataMap.size() >= ESConstants.BULK_INSERT_SIZE) {
					this.clearQueue(indexName, indexDataMap);
				}
			}
		}, ElasticsearchPostManager.SQL_BLOCKSIZE);

		this.clearQueue(indexName, indexDataMap);

		/*
		 * delete deleted projects
		 */
		final List<Project> deletedEntities = this.projectIndexUpdateLogic.getDeletedEntities(oldStateLastLogDate);
		final List<DeleteData> documentsToDelete = deletedEntities.stream().map(this::convertProjectToDelteData).collect(Collectors.toList());
		this.client.deleteDocuments(indexName, documentsToDelete);

		this.updateIndexState(indexName, targetState);
	}

	private DeleteData convertProjectToDelteData(final Project project) {
		final DeleteData deleteData = new DeleteData();
		deleteData.setId(this.entityInformationProvider.getEntityId(project));
		deleteData.setId(this.entityInformationProvider.getType());
		deleteData.setRouting(this.entityInformationProvider.getRouting(project));
		return deleteData;
	}
}
