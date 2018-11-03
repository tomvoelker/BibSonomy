package org.bibsonomy.search.es.management.person;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.query.PersonSuggestionQuery;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.search.person.AbstractPersonSearchTest;
import org.bibsonomy.search.es.search.person.ElasticsearchPersonSearch;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * integration tests for {@link org.bibsonomy.search.es.management.person.ElasticsearchPersonManager}
 *
 * @author dzo
 */
public class ElasticsearchPersonManagerITCase extends AbstractPersonSearchTest {

	/** for changing person related things */
	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = testDatabaseContext.getBean(PersonDatabaseManager.class);

	/** for checking the update */
	private static final ElasticsearchPersonSearch PERSON_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchPersonSearch.class);

	/** for creating personResourceRelations */
	private static final BibTexDatabaseManager PUBLICATION_DATABASE_MANAGER = testDatabaseContext.getBean(BibTexDatabaseManager.class);

	// FIXME: move somewhere else
	private static final String RESOURCE_HASH = "b77ddd8087ad8856d77c740c8dc2864a";
	private static final String RESOURCE_INTERHASH = "097248439469d8f5a1e7fad6b02cbfcd";
	private static final String TESTUSER1_NAME = "testuser1";
	private static final User TESTUSER1 = new User("testuser1");

	/**
	 * tests {@link ElasticsearchPersonManager#updateIndex()}
	 * TODO: split test into multiple tests
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
		assertThat(person1.getAcademicDegree(), is(newAcademicDegree));

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
		PERSON_DATABASE_MANAGER.removePersonName(personName.getPersonNameChangeId(), TESTUSER1, this.dbSession);
		this.updateIndex();

		final List<Person> personSuggestionsAfterPersonNameDelete = PERSON_SEARCH.getPersonSuggestions(personQuery);
		assertThat(personSuggestionsAfterPersonNameDelete.size(), is(0));

		// test if the index is also updated when a new person is created
		final Person newPerson = new Person();
		final PersonName reynolds = new PersonName();
		reynolds.setFirstName("Malcolm");
		reynolds.setLastName("Reynolds");
		newPerson.setMainName(reynolds);
		newPerson.setChangeDate(new Date()); // FIXME: move to database manager?

		// check that the person is not in the index
		final PersonSuggestionQuery newPersonQuery = new PersonSuggestionQuery(reynolds.toString());

		final List<Person> newPersonSuggestion = PERSON_SEARCH.getPersonSuggestions(newPersonQuery);
		assertThat(newPersonSuggestion.size(), is(0));

		// create it
		PERSON_DATABASE_MANAGER.createPerson(newPerson, this.dbSession);
		// now create the person name FIXME: this should be done in the manager not the dblogic
		final String newPersonId= newPerson.getPersonId();
		reynolds.setPersonId(newPersonId);
		PERSON_DATABASE_MANAGER.createPersonName(reynolds, this.dbSession);
		this.updateIndex();

		// check that the new person is indexed by the system
		final List<Person> newPersonSuggestionAfterUpdate = PERSON_SEARCH.getPersonSuggestions(newPersonQuery);
		assertThat(newPersonSuggestionAfterUpdate.size(), is(1));

		/*
		 * now we create a new relation and check if the relation is updated
		 */
		final PersonResourceRelationType relationType = PersonResourceRelationType.ADVISOR;
		final int authorIndex = -1;

		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		resourcePersonRelation.setPerson(newPerson);
		resourcePersonRelation.setRelationType(relationType);
		resourcePersonRelation.setPersonIndex(authorIndex);
		final Post<BibTex> post = PUBLICATION_DATABASE_MANAGER.getPostDetails(TESTUSER1_NAME, RESOURCE_HASH, TESTUSER1_NAME, Collections.emptyList(), this.dbSession);
		resourcePersonRelation.setPost(post);
		resourcePersonRelation.setChangedAt(new Date()); // TODO: do this in the database manager?
		resourcePersonRelation.setChangedBy(TESTUSER1_NAME);
		final boolean updated = PERSON_DATABASE_MANAGER.addResourceRelation(resourcePersonRelation, TESTUSER1, this.dbSession);
		assertThat(updated, is(true));

		this.updateIndex();

		final List<Person> personAfterRelationAdded = PERSON_SEARCH.getPersonSuggestions(newPersonQuery);
		final List<ResourcePersonRelation> resourceRelations = personAfterRelationAdded.get(0).getResourceRelations();

		assertThat(resourceRelations.size(), is(1));

		PERSON_DATABASE_MANAGER.removeResourceRelation(newPersonId, RESOURCE_INTERHASH, authorIndex, relationType, TESTUSER1, this.dbSession);

		this.updateIndex();

		final List<Person> personsAfterRelationDelete = PERSON_SEARCH.getPersonSuggestions(newPersonQuery);
		assertThat(personsAfterRelationDelete.get(0).getResourceRelations().size(), is(0));
	}

	private void updateIndex() {
		// update both indices
		PERSON_SEARCH_MANAGER.updateIndex();
		PERSON_SEARCH_MANAGER.updateIndex();

		// wait some time for the index to update
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
	}

}