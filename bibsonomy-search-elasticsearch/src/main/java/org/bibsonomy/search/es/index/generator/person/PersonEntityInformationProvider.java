package org.bibsonomy.search.es.index.generator.person;

import org.bibsonomy.search.es.index.converter.person.PersonConverter;
import org.bibsonomy.search.es.index.mapping.person.PersonMappingBuilder;
import org.bibsonomy.model.Person;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;

/**
 * implementation of the {@link EntityInformationProvider} interface for projects
 *
 * @author dzo
 */
public class PersonEntityInformationProvider extends EntityInformationProvider<Person> {

	/**
	 * the entity information provider
	 */
	public PersonEntityInformationProvider() {
		super(new PersonConverter(), new PersonMappingBuilder());
	}

	@Override
	public int getContentId(Person person) {
		return person.getPersonChangeId();
	}

	@Override
	public String getEntityId(Person entity) {
		return entity.getPersonId();
	}

	@Override
	public String getType() {
		return PersonMappingBuilder.PERSON_DOCUMENT_TYPE;
	}
}
