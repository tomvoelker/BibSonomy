package org.bibsonomy.search.index.generator.person;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link PersonIndexGenerationLogic}
 *
 * @author dzo
 */
public class PersonIndexGenerationLogicTest extends AbstractDatabaseManagerTest {
	private static final PersonIndexGenerationLogic PERSON_INDEX_GENERATIONLOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(PersonIndexGenerationLogic.class);

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
	}

	/**
	 * tests {@link PersonIndexGenerationLogic#getNumberOfEntities()}
	 */
	@Test
	public void testGetNumberOfEntities() {
		final int numberOfPersons = PERSON_INDEX_GENERATIONLOGIC.getNumberOfEntities();
		assertThat(numberOfPersons, is(5));
	}

	@Test
	public void testGetDbState() {
		final SearchIndexSyncState dbState = PERSON_INDEX_GENERATIONLOGIC.getDbState();
		assertThat(dbState.getLastPersonChangeId(), is(23l));
	}
}