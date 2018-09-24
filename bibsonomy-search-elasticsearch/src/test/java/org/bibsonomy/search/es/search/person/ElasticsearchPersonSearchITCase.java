package org.bibsonomy.search.es.search.person;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.bibsonomy.model.Person;
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

	@Test
	public void testGetPersonSuggestions() {
		final List<Person> personSuggestions = PERSON_SEARCH.getPersonSuggestions(new PersonSuggestionQuery("Schorsche"));

		assertThat(personSuggestions.size(), is(1));

		final Person person = personSuggestions.get(0);
		assertThat(person.getPersonId(), equalTo("h.muller"));


		final List<Person> lowerCaseResult = PERSON_SEARCH.getPersonSuggestions(new PersonSuggestionQuery("schorsche"));
		assertThat(lowerCaseResult.size(), is(1));
	}
}