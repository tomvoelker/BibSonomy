package org.bibsonomy.search.index.generator.project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link ProjectIndexGenerationLogic}
 *
 * @author dzo
 */
public class ProjectIndexGenerationLogicTest extends AbstractDatabaseManagerTest {
	private static final ProjectIndexGenerationLogic LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(ProjectIndexGenerationLogic.class);


	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = LOGIC.getNumberOfEntities();
		assertThat(numberOfEntities, is(2));
	}

	@Test
	public void testGetEntities() {
		final List<Project> entites = LOGIC.getEntites(0, 10);

		assertThat(entites.size(), is(2));

		final Project project = entites.get(0);
		assertThat(project.getTitle(), is("PoSTs"));
	}

}