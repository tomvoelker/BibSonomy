package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * tests for {@link ProjectDatabaseManager}
 * @author dzo
 */
public class ProjectDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static ProjectDatabaseManager PROJECT_DATABASE_MANAGER;

	@BeforeClass
	public static void setProjectDatabaseManager() {
		PROJECT_DATABASE_MANAGER = testDatabaseContext.getBean(ProjectDatabaseManager.class);
	}

	/**
	 * tests {@link ProjectDatabaseManager#createProject(Project, org.bibsonomy.model.User, DBSession)}
	 */
	@Test
	public void testCreateProject() {
		final Project project = new Project();
		final float budget = 13000.45f;
		project.setBudget(budget);
		final Date startDate = new DateTime().withMillisOfSecond(0).toDate();
		project.setStartDate(startDate);
		final Date endDate = new DateTime(startDate.getTime() + 50 * 10000).withMillisOfSecond(0).toDate();

		project.setEndDate(endDate);
		final String projectTitle = "REGIO";
		project.setTitle(projectTitle);
		final String projectType = "BMBF";
		project.setType(projectType);
		final String internalId = "122323-2323";
		project.setInternalId(internalId);

		PROJECT_DATABASE_MANAGER.createProject(project, new User("testuser1"), this.dbSession);

		final Project projectDetails = PROJECT_DATABASE_MANAGER.getProjectDetails(project.getExternalId(), true, this.dbSession);

		assertNotNull(projectDetails);
		assertEquals(budget, projectDetails.getBudget(), 0.001);
		assertEquals(projectTitle, projectDetails.getTitle());
		assertEquals(projectType, projectDetails.getType());
		assertEquals(startDate, projectDetails.getStartDate());
		assertEquals(endDate, projectDetails.getEndDate());
	}
}