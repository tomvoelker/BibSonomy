package org.bibsonomy.search.es.index.generator.person;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.mapping.person.PersonMappingBuilder;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

/**
 * person resource relation
 *
 * @author dzo
 */
public class PersonResourceRelationEntityInformationProvider extends EntityInformationProvider<ResourcePersonRelation> {

	/**
	 * default constructor
	 *
	 * @param converter the converter to use
	 */
	public PersonResourceRelationEntityInformationProvider(Converter<ResourcePersonRelation, Map<String, Object>, ?> converter) {
		// the mapping is already created by the parent entity relation
		super(converter, null);
	}

	@Override
	public MappingBuilder<XContentBuilder> getMappingBuilder() {
		throw new UnsupportedOperationException("no mapping for this to-many entity");
	}

	@Override
	public int getContentId(ResourcePersonRelation resourcePersonRelation) {
		return resourcePersonRelation.getPersonRelChangeId();
	}

	@Override
	public String getEntityId(ResourcePersonRelation entity) {
		return String.valueOf(entity.getPersonRelChangeId());
	}

	/**
	 * since elasticsearch 6.4 there is only one type supported but still supports one to many joins
	 * @return
	 */
	@Override
	public String getType() {
		return PersonMappingBuilder.PERSON_DOCUMENT_TYPE;
	}

	/**
	 * sets the routing to the id of the person
	 * @param entity
	 * @return
	 */
	@Override
	public String getRouting(ResourcePersonRelation entity) {
		return entity.getPerson().getPersonId();
	}
}
