/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

		updateIndex();

		final ProjectQuery.ProjectQueryBuilder projectQueryBuilder = new ProjectQuery.ProjectQueryBuilder();
		projectQueryBuilder.search("DeepScan");
		final List<Project> projects = PROJECT_SEARCH.getProjects(NOT_LOGGEDIN_USER, projectQueryBuilder.build());

		assertThat(projects.size(), is(1));

		final Project postProject = PROJECT_DATABASE_MANAGER.getProjectDetails("posts", true, this.dbSession);

		postProject.setSubTitle("Pragmatik und Semantik von kollaborativen Tagging-Systemen");

		final String projectId = postProject.getExternalId();
		final JobResult updateResult = PROJECT_DATABASE_MANAGER.updateProject(projectId, postProject, new User("testuser1"), this.dbSession);

		assertThat(updateResult.getStatus(), is(Status.OK));

		updateIndex();

		projectQueryBuilder.search("Pragmatik");
		final ProjectQuery pragmatikQuery = projectQueryBuilder.build();
		final List<Project> projectAfterUpdate = PROJECT_SEARCH.getProjects(NOT_LOGGEDIN_USER, pragmatikQuery);
		assertThat(projectAfterUpdate.size(), is(1));

		PROJECT_DATABASE_MANAGER.deleteProject(projectId, new User("testuser1"), this.dbSession);

		updateIndex();

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
