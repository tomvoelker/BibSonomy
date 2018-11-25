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
import org.bibsonomy.search.update.SearchIndexDualSyncState;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link PersonIndexGenerationLogic}
 *
 * @author dzo
 */
public class PersonResourcePersonRelationIndexGeneationLogicTest extends AbstractDatabaseManagerTest {
	private static final PersonResourcePersonRelationIndexGeneationLogic PERSON_INDEX_GENERATIONLOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(PersonResourcePersonRelationIndexGeneationLogic.class);

	/**
	 * test {@link PersonIndexGenerationLogic#getEntites(int, int)}
	 */
	@Test
	public void testGetEntities() {
		final List<Person> entites = PERSON_INDEX_GENERATIONLOGIC.getEntites(0, 100);
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
	 * tests {@link PersonIndexGenerationLogic#getDbState()}
	 */
	@Test
	public void testGetDbState() {
		final SearchIndexDualSyncState dbState = PERSON_INDEX_GENERATIONLOGIC.getDbState();
		assertThat(dbState.getFirstState().getLastPersonChangeId(), is(36l));
	}

	/**
	 * tests {@link PersonResourcePersonRelationIndexGeneationLogic#getToManyEntities(int, int)}
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
	 * tests {@link PersonResourcePersonRelationIndexGeneationLogic#getNumberOfToManyEntities()}
	 */
	@Test
	public void testGetNumberOfToManyEntities() {
		final int numberOfToManyEntities = PERSON_INDEX_GENERATIONLOGIC.getNumberOfToManyEntities();
		assertThat(numberOfToManyEntities, is(9));
	}
}