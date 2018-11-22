package org.bibsonomy.search.index.generator.cris;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for the generation logic of cris links that connect projects and persons
 *
 * @author dzo
 */
public class CRISLinkIndexGenerationLogicProjectPersonTest extends AbstractDatabaseManagerTest {

	private static final CRISLinkIndexGenerationLogic GENERATION_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("crisLinkProjectPersonGenerationLogic", CRISLinkIndexGenerationLogic.class);

	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = GENERATION_LOGIC.getNumberOfEntities();
		assertThat(numberOfEntities, is(1));
	}

	@Test
	public void testGetEntities() {
		final List<CRISLink> entities = GENERATION_LOGIC.getEntites(0, 10);

		assertThat(entities.size(), is(1));

		final CRISLink crisLink = entities.get(0);
		final Linkable target = crisLink.getTarget();
		assertThat(target, instanceOf(Person.class));

		final Person person = (Person) target;
		assertThat(person.getNames().size(), is(2));
		assertThat(person.getPersonId(), is("h.muller"));

		final Linkable source = crisLink.getSource();
		assertThat(source, instanceOf(Project.class));

		final Project project = (Project) source;
		assertThat(project.getExternalId(), is("posts"));
	}
}