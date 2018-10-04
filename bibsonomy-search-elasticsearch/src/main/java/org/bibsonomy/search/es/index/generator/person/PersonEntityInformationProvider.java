package org.bibsonomy.search.es.index.generator.person;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.es.index.converter.person.PersonConverter;
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.es.index.mapping.person.PersonMappingBuilder;
import org.bibsonomy.model.Person;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * implementation of the {@link EntityInformationProvider} interface for {@link Person}s
 *
 * @author dzo
 */
public class PersonEntityInformationProvider extends OneToManyEntityInformationProvider<Person, ResourcePersonRelation> {

	/**
	 * the entity information provider
	 */
	public PersonEntityInformationProvider(final MappingBuilder<XContentBuilder> mappingBuilder, final EntityInformationProvider<ResourcePersonRelation> toManyEntityProvider) {
		super(new PersonConverter(), mappingBuilder, toManyEntityProvider);
	}

	@Override
	public int getContentId(Person person) {
		return person.getPersonChangeId();
	}

	@Override
	public String getEntityId(Person entity) {
		return String.valueOf(entity.getPersonChangeId());
	}

	@Override
	public String getType() {
		return PersonMappingBuilder.PERSON_DOCUMENT_TYPE;
	}

	@Override
	public String getRouting(Person entity) {
		return entity.getPersonId();
	}
}
