package org.bibsonomy.search.index.generator.project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.index.generator.GeneralIndexGenerationLogic;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link GeneralIndexGenerationLogic} for {@link Project}s
 *
 * @author dzo
 */
public class ProjectIndexGenerationLogicTest extends AbstractDatabaseManagerTest {
	private static final GeneralIndexGenerationLogic<Project> LOGIC = (GeneralIndexGenerationLogic<Project>) SearchSpringContextWrapper.getBeanFactory().getBean("projectGenerationDBLogic");

	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = LOGIC.getNumberOfEntities();
		assertThat(numberOfEntities, is(2));
	}

	@Test
	public void testGetEntities() {
		final List<Project> entities = LOGIC.getEntities(0, 10);

		assertThat(entities.size(), is(2));

		final Project project = entities.get(0);
		assertThat(project.getTitle(), is("PoSTs"));
	}

}