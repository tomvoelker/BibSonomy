package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.junit.BeforeClass;
import org.junit.Test;

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

		link.setStartDate(projectDetails.getStartDate());
		link.setEndDate(projectDetails.getEndDate());

		link.setLinkType(ProjectPersonLinkType.MEMBER);

		final JobResult result = CRISLINK_DATABASE_MANAGER.createCRISLink(link, new User("testuser1"), this.dbSession);
		assertEquals(Status.OK, result.getStatus());
	}

	/**
	 * tests
	 */
	@Test
	public void testUpdateCRISLink() {
		assertFalse(true);
	}

	/**
	 * tests {@link CRISLinkDatabaseManager#deleteCRISLink(CRISLink, User, DBSession)}
	 */
	@Test
	public void testDeleteCRISLink() {
		final CRISLink link = new CRISLink();

		final Project projectDetails = PROJECT_DATABASE_MANAGER.getProjectDetails(ProjectDatabaseManagerTest.PROJECT_ID, true, this.dbSession);
		link.setSource(projectDetails);

		final Person person = PERSON_DATABASE_MANAGER.getPersonById("h.muller", this.dbSession);
		link.setTarget(person);

		final JobResult deleteResult = CRISLINK_DATABASE_MANAGER.deleteCRISLink(link, new User("testuser1"), this.dbSession);
		assertEquals(Status.OK, deleteResult.getStatus());
	}
}