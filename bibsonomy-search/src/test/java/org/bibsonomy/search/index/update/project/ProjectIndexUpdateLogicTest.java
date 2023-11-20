/**
 * BibSonomy Search - Helper classes for search modules.
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