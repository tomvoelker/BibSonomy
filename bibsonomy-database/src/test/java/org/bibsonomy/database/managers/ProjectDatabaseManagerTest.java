package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * tests for {@link ProjectDatabaseManager}
 *
 * @author dzo
 */
public class ProjectDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static final String PROJECT_ID = "posts";
	private static final String TESTUSER_1_NAME = "testuser1";

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

		PROJECT_DATABASE_MANAGER.createProject(project, new User(TESTUSER_1_NAME), this.dbSession);

		final Project projectDetails = PROJECT_DATABASE_MANAGER.getProjectDetails(project.getExternalId(), true, this.dbSession);

		assertNotNull(projectDetails);
		assertEquals(budget, projectDetails.getBudget(), 0.001);
		assertEquals(projectTitle, projectDetails.getTitle());
		assertEquals(projectType, projectDetails.getType());
		assertEquals(startDate, projectDetails.getStartDate());
		assertEquals(endDate, projectDetails.getEndDate());
		assertEquals("regio", projectDetails.getExternalId());
	}

	/**
	 * tests {@link ProjectDatabaseManager#updateProject(String, Project, User, DBSession)}
	 */
	@Test
	public void testUpdateProject() {
		final Project posts = PROJECT_DATABASE_MANAGER.getProjectDetails(PROJECT_ID, true, this.dbSession);
		float newBuget = 1000.0f;
		posts.setBudget(newBuget);
		final int dbId = posts.getId();

		final JobResult result = PROJECT_DATABASE_MANAGER.updateProject(posts.getExternalId(), posts, new User(TESTUSER_1_NAME), this.dbSession);

		assertEquals(Status.OK, result.getStatus());

		final Project postsAfterUpdate = PROJECT_DATABASE_MANAGER.getProjectDetails(PROJECT_ID, true, this.dbSession);

		assertEquals(newBuget, postsAfterUpdate.getBudget(), 0.001);
		assertNotEquals(dbId, postsAfterUpdate.getId()); // check for id change
	}

	@Test
	public void testUpdateProjectNotExistingAndValidation() {
		// try to update a non-existing project
		final Project project = new Project();

		final JobResult result = PROJECT_DATABASE_MANAGER.updateProject("regio", project, new User(TESTUSER_1_NAME), this.dbSession);
		assertEquals(Status.FAIL, result.getStatus());
		assertEquals(1, result.getErrors().size());

		// test validation
		final Project posts = PROJECT_DATABASE_MANAGER.getProjectDetails(PROJECT_ID, true, this.dbSession);
		posts.setEndDate(null);

		final JobResult result2 = PROJECT_DATABASE_MANAGER.updateProject(posts.getExternalId(), posts, new User(TESTUSER_1_NAME), this.dbSession);

		// validation should fail
		assertEquals(Status.FAIL, result2.getStatus());
		assertEquals(1, result2.getErrors().size());
	}

	/**
	 * tests the generation of the external project id
	 */
	@Test
	public void testProjectIdGeneration() {
		final Project project = new Project();
		project.setTitle("Posts");

		project.setEndDate(new Date());
		project.setStartDate(new Date());

		project.setBudget(0.0f);
		project.setType("DFG");

		final JobResult result = PROJECT_DATABASE_MANAGER.createProject(project, new User(TESTUSER_1_NAME), this.dbSession);
		assertEquals(Status.OK, result.getStatus());

		final Project projectDetails = PROJECT_DATABASE_MANAGER.getProjectDetails("posts.1", true, this.dbSession);

		assertNotNull(projectDetails);
	}

	/**
	 *
	 */
	@Test
	public void testDeleteProject() {
		final JobResult result = PROJECT_DATABASE_MANAGER.deleteProject(PROJECT_ID, new User(TESTUSER_1_NAME), this.dbSession);

		assertEquals(Status.OK, result.getStatus());

		final Project projectDetails = PROJECT_DATABASE_MANAGER.getProjectDetails(PROJECT_ID, true, this.dbSession);
		assertNull(projectDetails);
	}
}