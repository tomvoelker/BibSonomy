package org.bibsonomy.search.index.update.project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.ProjectDatabaseManager;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * tests for {@link ProjectIndexUpdateLogic}
 * @author dzo
 */
public class ProjectIndexUpdateLogicTest extends AbstractDatabaseManagerTest {

	private static final ProjectIndexUpdateLogic LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(ProjectIndexUpdateLogic.class);
	private static final ProjectDatabaseManager PROJECT_DATABASE_MANAGER = testDatabaseContext.getBean(ProjectDatabaseManager.class);

	@Test
	public void testGetNewEntities() {
		final List<Project> newerEntities = LOGIC.getNewerEntities(0, new Date(), 10, 0);
		assertThat(newerEntities.size(), is(2));
	}

	@Test
	public void testGetDeletedEntities() {
		final Date lastLogDate = new Date();
		final String projectId = "posts";
		final JobResult jobresult = PROJECT_DATABASE_MANAGER.deleteProject(projectId, new User("testuser1"), this.dbSession);
		assertThat(jobresult.getStatus(), is(Status.OK));

		final List<Project> deletedEntities = LOGIC.getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(1));

		final Project deletedProject = deletedEntities.get(0);
		assertThat(deletedProject.getExternalId(), is(projectId));
	}
}