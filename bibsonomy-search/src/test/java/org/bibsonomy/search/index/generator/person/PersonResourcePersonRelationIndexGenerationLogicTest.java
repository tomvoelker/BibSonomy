package org.bibsonomy.search.index.generator.person;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link PersonIndexGenerationLogic}
 *
 * @author dzo
 */
public class PersonResourcePersonRelationIndexGenerationLogicTest extends AbstractDatabaseManagerTest {
	private static final PersonResourcePersonRelationIndexGenerationLogic PERSON_INDEX_GENERATIONLOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(PersonResourcePersonRelationIndexGenerationLogic.class);

	/**
	 * test {@link PersonIndexGenerationLogic#getEntities(int, int)}
	 */
	@Test
	public void testGetEntities() {
		final List<Person> entites = PERSON_INDEX_GENERATIONLOGIC.getEntities(0, 100);
		assertThat(entites.size(), is(5));

		final Person testPerson = entites.get(0);
		final List<PersonName> personNames = testPerson.getNames();
		assertThat(personNames.size(), is(2));

		final PersonName mainName = testPerson.getMainName();
		assertThat(mainName.getFirstName(), is("Henner"));
		assertThat(mainName.getLastName(), is("Schorsche"));

		assertThat(testPerson.getChangeDate(), notNullValue());
	}

	/**
	 * tests {@link PersonIndexGenerationLogic#getNumberOfEntities()}
	 */
	@Test
	public void testGetNumberOfEntities() {
		final int numberOfPersons = PERSON_INDEX_GENERATIONLOGIC.getNumberOfEntities();
		assertThat(numberOfPersons, is(5));
	}

	/**
	 * tests {@link PersonResourcePersonRelationIndexGenerationLogic#getToManyEntities(int, int)}
	 */
	@Test
	public void testGetToManyEntities() {
		final List<ResourcePersonRelation> toManyEntities = PERSON_INDEX_GENERATIONLOGIC.getToManyEntities(0, 100);
		assertThat(toManyEntities.size(), is(9));

		final ResourcePersonRelation relation = toManyEntities.get(0);
		assertThat(relation.getRelationType(), is(PersonResourceRelationType.AUTHOR));
		assertThat(relation.getPersonIndex(), is(0));
		assertThat(relation.getPerson().getPersonId(), is("h.muller"));
		assertThat(relation.getPerson().getPersonChangeId(), is(5));
		assertThat(relation.getPost().getResource().getTitle(), is("Wurst aufs Brot"));
	}

	/**
	 * tests {@link PersonResourcePersonRelationIndexGenerationLogic#getNumberOfToManyEntities()}
	 */
	@Test
	public void testGetNumberOfToManyEntities() {
		final int numberOfToManyEntities = PERSON_INDEX_GENERATIONLOGIC.getNumberOfToManyEntities();
		assertThat(numberOfToManyEntities, is(9));
	}
}