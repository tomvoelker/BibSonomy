package org.bibsonomy.search.es.management.post;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.client.UpdateData;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.person.PersonResourceRelationUpdateLogic;
import org.bibsonomy.search.index.update.post.CommunityPostIndexCommunityUpdateLogic;
import org.bibsonomy.search.index.update.post.CommunityPostIndexUpdateLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexDualSyncState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.util.BasicUtils;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * special implementation for {@link ElasticsearchCommunityPostManager} to update publication specific fields
 * these fields are:
 *
 * - person resource relations
 *
 * @author dzo
 */
public class ElasticsearchCommunityPostPublicationManager<G extends BibTex> extends ElasticsearchCommunityPostManager<G> {

	private static final String INDEX_KEY = "index";
	private static final String RELATION_KEY = "relation";
	private static final String PERSON_ID_KEY = "personID";
	private static final String TYPE_KEY = "type";

	private static final String ADD_PERSON_ID_TO_AUTHOR = buildAddPersonScript(ESConstants.Fields.Publication.AUTHORS);
	private static final String ADD_PERSON_ID_TO_EDITOR = buildAddPersonScript(ESConstants.Fields.Publication.EDITORS);
	private static final String ADD_OTHER_RELATION = "ctx._source." + ESConstants.Fields.Publication.OTHER_PERSON_RESOURCE_RELATIONS + ".add(params." + RELATION_KEY + ")";

	private static final String REMOVE_PERSON_ID_TO_AUTHOR = buildRemovePersonScript(ESConstants.Fields.Publication.AUTHORS);
	private static final String REMOVE_PERSON_ID_TO_EDITOR = buildRemovePersonScript(ESConstants.Fields.Publication.EDITORS);
	private static final String REMOVE_OTHER_RELATION = "ctx._source." + ESConstants.Fields.Publication.OTHER_PERSON_RESOURCE_RELATIONS + " = ctx._source." + ESConstants.Fields.Publication.OTHER_PERSON_RESOURCE_RELATIONS + ".stream().filter(x -> x." + ESConstants.Fields.Publication.PERSON_ID + " != params." + RELATION_KEY + "." + PERSON_ID_KEY + " && x." + ESConstants.Fields.Publication.PERSON_RELATION_TYPE + " != params." + RELATION_KEY + "." + TYPE_KEY + ").collect(Collectors.toList())";

	private static final String buildAddPersonScript(final String field) {
		return "ctx._source." + field + "[params." + INDEX_KEY + "]." + ESConstants.Fields.Publication.PERSON_ID + " = params." + RELATION_KEY + "." + PERSON_ID_KEY;
	}

	private static String buildRemovePersonScript(final String field) {
		return "ctx._source." + field + "[params." + INDEX_KEY + "].remove('" + ESConstants.Fields.Publication.PERSON_ID + "')";
	}

	private static String getRemoveScriptForPersonResourceRelation(PersonResourceRelationType type) {
		switch (type) {
			case AUTHOR:
				return REMOVE_PERSON_ID_TO_AUTHOR;
			case EDITOR:
				return REMOVE_PERSON_ID_TO_EDITOR;
		}

		// add it to the other resource relation fields
		return REMOVE_OTHER_RELATION;
	}

	private static String getAddScriptForPersonResourceRelation(PersonResourceRelationType type) {
		switch (type) {
			case AUTHOR:
				return ADD_PERSON_ID_TO_AUTHOR;
			case EDITOR:
				return ADD_PERSON_ID_TO_EDITOR;
		}

		// add it to the other resource relation fields
		return ADD_OTHER_RELATION;
	}

	private final PersonResourceRelationUpdateLogic personResourceRelationUpdateLogic;

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
	 * @param inputLogic
	 * @param communityPostUpdateLogic
	 * @param postUpdateLogic
	 * @param databaseInformationLogic
	 * @param personResourceRelationUpdateLogic
	 */
	public ElasticsearchCommunityPostPublicationManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Post<G>, SearchIndexDualSyncState> generator, Converter syncStateConverter, EntityInformationProvider entityInformationProvider, SearchDBInterface<G> inputLogic, CommunityPostIndexCommunityUpdateLogic<G> communityPostUpdateLogic, CommunityPostIndexUpdateLogic<G> postUpdateLogic, DatabaseInformationLogic<SearchIndexDualSyncState> databaseInformationLogic, PersonResourceRelationUpdateLogic personResourceRelationUpdateLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, entityInformationProvider, inputLogic, communityPostUpdateLogic, postUpdateLogic, databaseInformationLogic);
		this.personResourceRelationUpdateLogic = personResourceRelationUpdateLogic;
	}

	@Override
	protected void updateResourceSpecificFields(final String indexName, final SearchIndexDualSyncState oldState, final SearchIndexDualSyncState targetState) {
		final DefaultSearchIndexSyncState communitySearchIndexState = oldState.getFirstState();

		final Map<String, UpdateData> updateDataMap = new HashMap<>();
		/*
		 * add new resource relations
		 */
		this.loop(indexName, updateDataMap, ElasticsearchCommunityPostPublicationManager::getAddScriptForPersonResourceRelation, (limit, offset) -> this.personResourceRelationUpdateLogic.getNewerEntities(communitySearchIndexState.getLastPersonChangeId(), communitySearchIndexState.getLastPersonLogDate(), limit, offset));

		/*
		 * remove resource relations
		 */
		this.loop(indexName, updateDataMap, ElasticsearchCommunityPostPublicationManager::getRemoveScriptForPersonResourceRelation, (limit, offset) -> this.personResourceRelationUpdateLogic.getDeletedEntities(communitySearchIndexState.getLastPersonLogDate()));
		
		this.clearUpdateQueue(indexName, updateDataMap);
	}

	private void loop(final String indexName, final Map<String, UpdateData> updateDataMap, final Function<PersonResourceRelationType, String> getScriptFunction, BiFunction<Integer, Integer, List<ResourcePersonRelation>> relationRetrieveMethod) {
		BasicUtils.iterateListWithLimitAndOffset(relationRetrieveMethod, relations -> {
			for (final ResourcePersonRelation relation : relations) {
				final Map<String, Object> params = new HashMap<>();
				final Map<String, String> relationInfos = new HashMap<>();
				params.put(INDEX_KEY, relation.getPersonIndex());
				relationInfos.put(PERSON_ID_KEY, relation.getPerson().getPersonId());
				relationInfos.put(TYPE_KEY, relation.getRelationType().getRelatorCode());
				params.put(RELATION_KEY, relationInfos);

				// get the update script based on the relation
				final PersonResourceRelationType type = relation.getRelationType();
				final String code = getScriptFunction.apply(type);
				final Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, code, params);

				final UpdateData updateData = new UpdateData();
				updateData.setScript(script);
				// XXX: not nice :(
				final Post<G> post = (Post<G>) relation.getPost();

				updateData.setRouting(this.entityInformationProvider.getRouting(post));
				updateData.setType(this.entityInformationProvider.getType());
				final String entityId = this.entityInformationProvider.getEntityId(post);
				updateDataMap.put(entityId, updateData);

				if (updateDataMap.size() >= ESConstants.BULK_INSERT_SIZE) {
					this.clearUpdateQueue(indexName, updateDataMap);
				}

			}
		}, ElasticsearchPostManager.SQL_BLOCKSIZE);
	}
}
