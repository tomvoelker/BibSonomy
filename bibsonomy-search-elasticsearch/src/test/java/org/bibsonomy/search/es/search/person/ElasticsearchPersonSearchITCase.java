package org.bibsonomy.search.es.search.person;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.PersonSuggestionQuery;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link ElasticsearchPersonSearch}
 *
 * @author dzo
 */
public class ElasticsearchPersonSearchITCase extends AbstractPersonSearchTest {

	private static final ElasticsearchPersonSearch PERSON_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchPersonSearch.class);

	private static final String PERSON_ID = "h.muller";

	/**
	 * tests {@link ElasticsearchPersonSearch#getPersons(PersonSuggestionQuery)}
	 */
	@Test
	public void testGetPersonSuggestions() {
		assertPersonSuggestion("Schorsche");
		assertPersonSuggestion("schorsche");
	}

	/**
	 * test the index to also return persons with queries containing publication titles
	 */
	@Test
	public void testGetPersonSuggestionWithPublicationTitle() {
		assertPersonSuggestion("Schorsche Wurst");
	}

	private void assertPersonSuggestion(final String query) {
		final List<Person> personSuggestions = PERSON_SEARCH.getPersons(new PersonSuggestionQuery(query));
		assertThat(personSuggestions.size(), is(1));

		final Person person = personSuggestions.get(0);
		assertThat(person.getPersonId(), is(PERSON_ID));

		// check for the resource relations
		final List<ResourcePersonRelation> resourceRelations = person.getResourceRelations();
		assertThat(resourceRelations.size(), is(1));
		assertThat(resourceRelations.get(0).getPost().getResource().getTitle(), is("Wurst aufs Brot"));
	}
}