package org.bibsonomy.search.es.management.project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.database.managers.ProjectDatabaseManager;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.search.es.search.project.AbstractProjectSearchTest;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * tests for the project manager
 * @author dzo
 */
public class ElasticsearchProjectManagerITCase extends AbstractProjectSearchTest {

	private static final ProjectDatabaseManager PROJECT_DATABASE_MANAGER = testDatabaseContext.getBean(ProjectDatabaseManager.class);

	private static final User NOT_LOGGEDIN_USER = new User();

	@Test
	public void testGenerated() {
		final ProjectQuery.ProjectQueryBuilder projectQueryBuilder = new ProjectQuery.ProjectQueryBuilder();
		projectQueryBuilder.type("DFG");
		final List<Project> projects = PROJECT_SEARCH.getProjects(NOT_LOGGEDIN_USER, projectQueryBuilder.build());

		assertThat(projects.size(), is(2));
	}

	@Test
	public void testUpdateProjectIndex() {
		final Project project = new Project();
		project.setStartDate(new Date());
		project.setEndDate(new Date());
		project.setTitle("DeepScan");
		project.setType("BMBF");

		PROJECT_DATABASE_MANAGER.createProject(project, new User("testuser1"), this.dbSession);

		this.updateIndex();

		final ProjectQuery.ProjectQueryBuilder projectQueryBuilder = new ProjectQuery.ProjectQueryBuilder();
		projectQueryBuilder.search("DeepScan");
		final List<Project> projects = PROJECT_SEARCH.getProjects(NOT_LOGGEDIN_USER, projectQueryBuilder.build());

		assertThat(projects.size(), is(1));

		final Project postProject = PROJECT_DATABASE_MANAGER.getProjectDetails("posts", true, this.dbSession);

		postProject.setSubTitle("Pragmatik und Semantik von kollaborativen Tagging-Systemen");

		final String projectId = postProject.getExternalId();
		final JobResult updateResult = PROJECT_DATABASE_MANAGER.updateProject(projectId, postProject, new User("testuser1"), this.dbSession);

		assertThat(updateResult.getStatus(), is(Status.OK));

		this.updateIndex();

		projectQueryBuilder.search("Pragmatik");
		final ProjectQuery pragmatikQuery = projectQueryBuilder.build();
		final List<Project> projectAfterUpdate = PROJECT_SEARCH.getProjects(NOT_LOGGEDIN_USER, pragmatikQuery);
		assertThat(projectAfterUpdate.size(), is(1));

		PROJECT_DATABASE_MANAGER.deleteProject(projectId, new User("testuser1"), this.dbSession);

		this.updateIndex();

		final List<Project> projectsAfterDelete = PROJECT_SEARCH.getProjects(NOT_LOGGEDIN_USER, pragmatikQuery);
		assertThat(projectsAfterDelete.size(), is(0));
	}

	private static void updateIndex() {
		PROJECT_MANAGER.updateIndex();
		PROJECT_MANAGER.updateIndex();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
