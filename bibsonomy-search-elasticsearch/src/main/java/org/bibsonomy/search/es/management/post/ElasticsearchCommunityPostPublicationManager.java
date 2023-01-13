/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.es.management.post;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
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
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.util.BasicUtils;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Elasticsearch manager for goldstandard/community publication indices
 * This is a special implementation of {@link ElasticsearchCommunityPostManager} to update publication specific fields.
 * These fields are:
 *
 * - {@link ResourcePersonRelation}
 *
 * @author dzo
 */
public class ElasticsearchCommunityPostPublicationManager<G extends BibTex> extends ElasticsearchCommunityPostManager<G> {

	private static final String INDEX_KEY = "index";
	private static final String RELATION_KEY = "relation";
	private static final String PERSON_ID_KEY = "personID";
	private static final String TYPE_KEY = "type";
	private static final String COLLEGE_KEY = "college";

	private static final String ADD_PERSON_ID_TO_AUTHOR = buildAddPersonScript(ESConstants.Fields.Publication.AUTHORS);
	private static final String ADD_PERSON_ID_TO_EDITOR = buildAddPersonScript(ESConstants.Fields.Publication.EDITORS);
	private static final String ADD_OTHER_RELATION = "ctx._source." + ESConstants.Fields.Publication.OTHER_PERSON_RESOURCE_RELATIONS + ".add(params." + RELATION_KEY + ")";

	private static final String REMOVE_PERSON_ID_TO_AUTHOR = buildRemovePersonScript(ESConstants.Fields.Publication.AUTHORS);
	private static final String REMOVE_PERSON_ID_TO_EDITOR = buildRemovePersonScript(ESConstants.Fields.Publication.EDITORS);
	private static final String REMOVE_OTHER_RELATION = "ctx._source." + ESConstants.Fields.Publication.OTHER_PERSON_RESOURCE_RELATIONS + " = ctx._source." + ESConstants.Fields.Publication.OTHER_PERSON_RESOURCE_RELATIONS + ".stream().filter(x -> x." + ESConstants.Fields.Publication.PERSON_ID + " != params." + RELATION_KEY + "." + PERSON_ID_KEY + " && x." + ESConstants.Fields.Publication.PERSON_RELATION_TYPE + " != params." + RELATION_KEY + "." + TYPE_KEY + ").collect(Collectors.toList())";

	private static String buildAddPersonScript(final String field) {
		return buildAddFieldScript(field, ESConstants.Fields.Publication.PERSON_ID, PERSON_ID_KEY) + ";\n" + buildAddFieldScript(field, ESConstants.Fields.Publication.PERSON_COLLEGE, COLLEGE_KEY);
	}

	private static String buildAddFieldScript(String field, String key, String valueKey) {
		return "ctx._source." + field + "[params." + INDEX_KEY + "]." + key + " = params." + RELATION_KEY + "." + valueKey;
	}

	private static String buildRemovePersonScript(final String field) {
		return buildRemoveFieldScript(field, ESConstants.Fields.Publication.PERSON_COLLEGE) + ";\n" + buildRemoveFieldScript(field, ESConstants.Fields.Publication.PERSON_ID);
	}

	private static String buildRemoveFieldScript(final String field, final String key) {
		return "ctx._source." + field + "[params." + INDEX_KEY + "].remove('" + key + "')";
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
	 * Default constructor
	 *
	 * @param systemURI
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param indexEnabled
	 * @param updateEnabled
	 * @param regenerateEnabled
	 * @param inputLogic
	 * @param databaseInformationLogic
	 * @param postUpdateLogic
	 * @param communityPostUpdateLogic
	 * @param personResourceRelationUpdateLogic
	 */
	public ElasticsearchCommunityPostPublicationManager(URI systemURI,
														ESClient client,
														ElasticsearchIndexGenerator<Post<G>, SearchIndexState> generator,
														Converter syncStateConverter,
														EntityInformationProvider entityInformationProvider,
														boolean indexEnabled,
														boolean updateEnabled,
														boolean regenerateEnabled,
														final SearchDBInterface<G> inputLogic,
														final DatabaseInformationLogic databaseInformationLogic,
														final CommunityPostIndexUpdateLogic<G> postUpdateLogic,
														final CommunityPostIndexCommunityUpdateLogic<G> communityPostUpdateLogic,
														final PersonResourceRelationUpdateLogic personResourceRelationUpdateLogic) {
		super(systemURI, client, generator, syncStateConverter, entityInformationProvider, indexEnabled, updateEnabled, regenerateEnabled, inputLogic, databaseInformationLogic, postUpdateLogic, communityPostUpdateLogic);
		this.personResourceRelationUpdateLogic = personResourceRelationUpdateLogic;
	}

	@Override
	protected void updateResourceSpecificFields(final String indexName, final SearchIndexState oldState, final SearchIndexState targetState) {

		final List<Pair<String, UpdateData>> updateDataMap = new LinkedList<>();
		/*
		 * add new resource relations
		 */
		this.loop(indexName, updateDataMap, ElasticsearchCommunityPostPublicationManager::getAddScriptForPersonResourceRelation, (limit, offset) -> this.personResourceRelationUpdateLogic.getNewerEntities(oldState.getLastPersonChangeId(), oldState.getLastRelationChangeDate(), limit, offset));

		/*
		 * remove resource relations
		 */
		this.loop(indexName, updateDataMap, ElasticsearchCommunityPostPublicationManager::getRemoveScriptForPersonResourceRelation, (limit, offset) -> this.personResourceRelationUpdateLogic.getDeletedEntities(oldState.getLastRelationChangeDate()));
		
		this.clearUpdateQueue(indexName, updateDataMap);
	}

	private void loop(final String indexName, final List<Pair<String, UpdateData>> updateDataMap, final Function<PersonResourceRelationType, String> scriptFunction, BiFunction<Integer, Integer, List<ResourcePersonRelation>> relationRetrieveMethod) {
		BasicUtils.iterateListWithLimitAndOffset(relationRetrieveMethod, relations -> {
			for (final ResourcePersonRelation relation : relations) {
				final Map<String, Object> params = new HashMap<>();
				final Map<String, String> relationInfos = new HashMap<>();
				params.put(INDEX_KEY, relation.getPersonIndex());
				final Person person = relation.getPerson();
				relationInfos.put(PERSON_ID_KEY, person.getPersonId());
				relationInfos.put(TYPE_KEY, relation.getRelationType().getRelatorCode());
				relationInfos.put(COLLEGE_KEY, person.getCollege());
				params.put(RELATION_KEY, relationInfos);

				// get the update script based on the relation
				final PersonResourceRelationType type = relation.getRelationType();
				final String code = scriptFunction.apply(type);
				final Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, code, params);

				final UpdateData updateData = new UpdateData();
				updateData.setScript(script);
				// XXX: not nice :(
				final Post<G> post = (Post<G>) relation.getPost();

				updateData.setRouting(this.entityInformationProvider.getRouting(post));
				updateData.setType(this.entityInformationProvider.getType());
				final String entityId = this.entityInformationProvider.getEntityId(post);
				updateDataMap.add(new Pair<>(entityId, updateData));

				if (updateDataMap.size() >= ESConstants.BULK_INSERT_SIZE) {
					this.clearUpdateQueue(indexName, updateDataMap);
				}
			}
		}, ElasticsearchPostManager.SQL_BLOCKSIZE);
	}
}
