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
package org.bibsonomy.search.es.index.converter.person;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.util.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * converts {@link ResourcePersonRelation} to the elasticsearch model
 */
public class PersonResourceRelationConverter implements Converter<ResourcePersonRelation, Map<String, Object>, Object> {

	private final Converter<Post<? extends BibTex>, Map<String, Object>, Object> postConverter;

	/**
	 * default constructor
	 * @param postConverter
	 */
	public PersonResourceRelationConverter(Converter<Post<? extends BibTex>, Map<String, Object>, Object> postConverter) {
		this.postConverter = postConverter;
	}

	@Override
	public Map<String, Object> convert(ResourcePersonRelation source) {
		final Map<String, Object> mapping = new HashMap<>();

		// some general information
		final int personIndex = source.getPersonIndex();
		mapping.put(PersonFields.RelationFields.INDEX, personIndex);
		mapping.put(PersonFields.RelationFields.RELATION_TYPE, source.getRelationType().toString());
		mapping.put(PersonFields.CHANGE_DATE, ElasticsearchUtils.dateToString(source.getChangedAt()));
		mapping.put(PersonFields.PERSON_DATABASE_ID, source.getPersonRelChangeId());

		final Map<String, Object> convertedPost = this.postConverter.convert(source.getPost());
		mapping.put(PersonFields.RelationFields.POST, convertedPost);

		// and the type of the one to many relation that is stored in the person index
		final Map<Object, Object> relation = new HashMap<>();
		relation.put("name", PersonFields.TYPE_RELATION);
		relation.put("parent", source.getPerson().getPersonId());
		mapping.put(PersonFields.JOIN_FIELD, relation);
		return mapping;
	}

	@Override
	public ResourcePersonRelation convert(Map<String, Object> source, Object options) {
		final Map<String, Object> postData = (Map<String, Object>) source.get(PersonFields.RelationFields.POST);
		final Post<? extends BibTex> post = this.postConverter.convert(postData, Collections.emptySet());

		final ResourcePersonRelation relation = new ResourcePersonRelation();
		relation.setPost(post);
		relation.setRelationType(PersonResourceRelationType.valueOf((String) source.get(PersonFields.RelationFields.RELATION_TYPE)));
		relation.setPersonIndex((Integer) source.get(PersonFields.RelationFields.INDEX));
		relation.setChangedAt(ElasticsearchUtils.parseDate(source, PersonFields.CHANGE_DATE));
		return relation;
	}
}
