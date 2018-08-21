package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.CRISLinkDataSource;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

/**
 * tests for {@link CRISLinkDatabaseManager}
 *
 * @author dzo
 */
public class CRISLinkDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static ProjectDatabaseManager PROJECT_DATABASE_MANAGER;
	private static CRISLinkDatabaseManager CRISLINK_DATABASE_MANAGER;
	private static PersonDatabaseManager PERSON_DATABASE_MANAGER;

	@BeforeClass
	public static void setupDatabaseManagers() {
		PROJECT_DATABASE_MANAGER = testDatabaseContext.getBean(ProjectDatabaseManager.class);
		CRISLINK_DATABASE_MANAGER = testDatabaseContext.getBean(CRISLinkDatabaseManager.class);
		PERSON_DATABASE_MANAGER = testDatabaseContext.getBean(PersonDatabaseManager.class);
	}

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

		final JobResult result = CRISLINK_DATABASE_MANAGER.createCRISLink(link, new User("testuser1"), this.dbSession);
		assertEquals(Status.OK, result.getStatus());

		final CRISLink crisLink = CRISLINK_DATABASE_MANAGER.getCRISLink(person, projectDetails, this.dbSession);
		assertEquals(endDate, crisLink.getEndDate());
		assertEquals(startDate, crisLink.getStartDate());
		assertEquals(CRISLinkDataSource.SYSTEM, crisLink.getDataSource());
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