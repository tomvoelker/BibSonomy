package org.bibsonomy.search.es.management.person;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.logic.query.PersonSuggestionQuery;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.search.person.AbstractPersonSearchTest;
import org.bibsonomy.search.es.search.person.ElasticsearchPersonSearch;
import org.junit.Test;

import java.util.List;

/**
 * integration tests for {@link org.bibsonomy.search.es.management.person.ElasticsearchPersonManager}
 *
 * @author dzo
 */
public class ElasticsearchPersonManagerITCase extends AbstractPersonSearchTest {

	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = testDatabaseContext.getBean(PersonDatabaseManager.class);
	private static final ElasticsearchPersonSearch PERSON_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchPersonSearch.class);

	/**
	 * tests {@link ElasticsearchPersonManager#updateIndex()}
	 */
	@Test
	public void testPersonUpdate() {
		final Person person = PERSON_DATABASE_MANAGER.getPersonById("h.muller", this.dbSession);
		// start with one simple case; just update a person attribute
		final String newAcademicDegree = "Dr. Dr.";
		person.setAcademicDegree(newAcademicDegree);

		PERSON_DATABASE_MANAGER.updateAcademicDegree(person, this.dbSession);
		this.updateIndex();
		final List<Person> personSuggestionsAfterAttributeUpdate = PERSON_SEARCH.getPersonSuggestions(new PersonSuggestionQuery("Müller"));
		assertThat(personSuggestionsAfterAttributeUpdate.size(), is(1));
		final Person person1 = personSuggestionsAfterAttributeUpdate.get(0);
		assertThat(person1.getAcademicDegree(), equalTo(newAcademicDegree));

		// test if the index is updated when the person changes (e.g one person name is added)
		final PersonName personName = new PersonName();
		personName.setFirstName("John");
		personName.setLastName("Doe");

		final PersonSuggestionQuery personQuery = new PersonSuggestionQuery(personName.toString());
		final List<Person> personSuggestions = PERSON_SEARCH.getPersonSuggestions(personQuery);
		assertThat(personSuggestions.size(), is(0));

		personName.setPerson(person);
		personName.setPersonId(person.getPersonId());
		PERSON_DATABASE_MANAGER.createPersonName(personName, this.dbSession);
		this.updateIndex();

		final List<Person> personSuggestionsAfterUpdate = PERSON_SEARCH.getPersonSuggestions(personQuery);
		assertThat(personSuggestionsAfterUpdate.size(), is(1));

		// test if the index is updated when a person name was removed
		PERSON_DATABASE_MANAGER.removePersonName(personName.getPersonNameChangeId(), "testuser1", this.dbSession);
		this.updateIndex();

		final List<Person> personSuggestionsAfterPersonNameDelete = PERSON_SEARCH.getPersonSuggestions(personQuery);
		assertThat(personSuggestionsAfterPersonNameDelete.size(), is(0));

		// test if the index is also updated when a new person is created
		final Person newPerson = new Person();
		final PersonName reynolds = new PersonName();
		reynolds.setFirstName("Malcolm");
		reynolds.setLastName("Reynolds");
		newPerson.setMainName(reynolds);

		// check that the person is not in the index
		final PersonSuggestionQuery newPersonQuery = new PersonSuggestionQuery(reynolds.toString());

		final List<Person> newPersonSuggestion = PERSON_SEARCH.getPersonSuggestions(newPersonQuery);
		assertThat(newPersonSuggestion.size(), is(0));

		PERSON_DATABASE_MANAGER.createPerson(newPerson, this.dbSession);
		this.updateIndex();

		// check that the new person is indexed by the system
		final List<Person> newPersonSuggestionAfterUpdate = PERSON_SEARCH.getPersonSuggestions(newPersonQuery);
		assertThat(newPersonSuggestionAfterUpdate.size(), is(1));
	}

	private void updateIndex() {
		// update both indices
		PERSON_SEARCH_MANAGER.updateIndex();
		PERSON_SEARCH_MANAGER.updateIndex();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
	}

}