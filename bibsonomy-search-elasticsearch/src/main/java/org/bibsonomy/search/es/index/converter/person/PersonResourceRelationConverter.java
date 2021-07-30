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
