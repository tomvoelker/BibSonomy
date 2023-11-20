/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.CRISLinkDataSource;
import org.bibsonomy.model.cris.GroupPersonLinkType;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.junit.Test;

import java.util.Date;

/**
 * tests for {@link CRISLinkDatabaseManager}
 *
 * @author dzo
 */
public class CRISLinkDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final ProjectDatabaseManager PROJECT_DATABASE_MANAGER = testDatabaseContext.getBean(ProjectDatabaseManager.class);
	private static final CRISLinkDatabaseManager CRISLINK_DATABASE_MANAGER = testDatabaseContext.getBean(CRISLinkDatabaseManager.class);
	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = testDatabaseContext.getBean(PersonDatabaseManager.class);
	private static final GroupDatabaseManager GROUP_DATABASE_MANAGER = testDatabaseContext.getBean(GroupDatabaseManager.class);

	/**
	 * tests {@link CRISLinkDatabaseManager#createCRISLink(CRISLink, User, DBSession)}
	 */
	@Test
	public void testCreateCRISLink() {
		final CRISLink link = new CRISLink();

		final Project projectDetails = PROJECT_DATABASE_MANAGER.getProjectDetails(ProjectDatabaseManagerTest.PROJECT_ID, true, this.dbSession);
		link.setSource(projectDetails);

		final Person person = PERSON_DATABASE_MANAGER.getPersonById("w.test.1", this.dbSession);
		link.setTarget(person);

		final Date startDate = projectDetails.getStartDate();
		final Date endDate = projectDetails.getEndDate();
		link.setStartDate(startDate);
		link.setEndDate(endDate);

		link.setLinkType(ProjectPersonLinkType.MEMBER);
		link.setDataSource(CRISLinkDataSource.SYSTEM);

		// TODO: move test data
		final String authUserName = "testuser1";
		final User authUser = new User(authUserName);
		final JobResult result = CRISLINK_DATABASE_MANAGER.createCRISLink(link, authUser, this.dbSession);
		assertEquals(Status.OK, result.getStatus());

		final CRISLink crisLink = CRISLINK_DATABASE_MANAGER.getCRISLink(person, projectDetails, this.dbSession);
		assertEquals(endDate, crisLink.getEndDate());
		assertEquals(startDate, crisLink.getStartDate());
		assertEquals(CRISLinkDataSource.SYSTEM, crisLink.getDataSource());

		final Group group = GROUP_DATABASE_MANAGER.getGroup(authUserName, "testgroup1", false, true, this.dbSession);

		final CRISLink crisLinkGroupPerson = new CRISLink();
		crisLinkGroupPerson.setSource(group);
		crisLinkGroupPerson.setTarget(person);
		crisLinkGroupPerson.setLinkType(GroupPersonLinkType.LEADER);
		crisLinkGroupPerson.setDataSource(CRISLinkDataSource.SYSTEM);

		CRISLINK_DATABASE_MANAGER.createCRISLink(crisLinkGroupPerson, authUser, this.dbSession);
	}

	/**
	 * tests
	 */
	@Test
	public void testUpdateCRISLink() {
		final CRISLink link = this.getCRISLink();

		link.setEndDate(null);
		final ProjectPersonLinkType linkType = ProjectPersonLinkType.MEMBER;
		link.setLinkType(linkType);

		final JobResult result = CRISLINK_DATABASE_MANAGER.updateCRISLink(link, new User("testuser1"), this.dbSession);
		assertEquals(Status.OK, result.getStatus());

		final CRISLink crisLink = CRISLINK_DATABASE_MANAGER.getCRISLink(link.getSource(), link.getTarget(), this.dbSession);
		assertEquals(linkType, crisLink.getLinkType());
		assertNull(crisLink.getEndDate());
	}

	/**
	 * tests {@link CRISLinkDatabaseManager#deleteCRISLink(Linkable, Linkable, User, DBSession)}
	 */
	@Test
	public void testDeleteCRISLink() {
		final CRISLink link = this.getCRISLink();

		final JobResult deleteResult = CRISLINK_DATABASE_MANAGER.deleteCRISLink(link.getSource(), link.getTarget(), new User("testuser1"), this.dbSession);
		assertEquals(Status.OK, deleteResult.getStatus());
	}

	private CRISLink getCRISLink() {
		final CRISLink link = new CRISLink();

		final Project projectDetails = PROJECT_DATABASE_MANAGER.getProjectDetails(ProjectDatabaseManagerTest.PROJECT_ID, true, this.dbSession);
		link.setSource(projectDetails);

		final Person person = PERSON_DATABASE_MANAGER.getPersonById("h.muller", this.dbSession);
		link.setTarget(person);
		return link;
	}
}