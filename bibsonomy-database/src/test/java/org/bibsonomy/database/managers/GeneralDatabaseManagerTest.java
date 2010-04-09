package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the GeneralDatabaseManager.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class GeneralDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static GeneralDatabaseManager generalDb;
	
	/**
	 * sets up the used managers
	 */
	@BeforeClass
	public static void setupDatabaseManager() {
		generalDb = GeneralDatabaseManager.getInstance();
	}
	
	/**
	 * tests isFriendOf
	 */
	@Test
	public void isFriendOf() {
		// a user is always his own friend
		for (final int i : new int[] { 1, 2, 3 }) {
			assertTrue(generalDb.isFriendOf("testuser" + i, "testuser" + i, this.dbSession));
		}

		// combinations: testuser1 has as friends testuser2 and 3, 
		//               testuser2 has as friend testuser1
		//               testuser3 has no friends at all
		assertTrue(generalDb.isFriendOf("testuser2", "testuser1", this.dbSession));
		assertTrue(generalDb.isFriendOf("testuser3", "testuser1", this.dbSession));
		assertTrue(generalDb.isFriendOf("testuser1", "testuser2", this.dbSession));
		assertFalse(generalDb.isFriendOf("testuser3", "testuser2", this.dbSession));
		assertFalse(generalDb.isFriendOf("testuser1", "testuser3", this.dbSession));
		assertFalse(generalDb.isFriendOf("testuser2", "testuser3", this.dbSession));

		// with no users set or a not existing one, no exception should be
		// thrown and the result should just be "false"
		final String[] combinations = new String[] { "", " ", null, ParamUtils.NOUSER_NAME };
		for (final String userName : combinations) {
			for (final String friendUserName : combinations) {
				if (present(userName) && present(friendUserName) && userName.equals(friendUserName)) continue;
				assertFalse(generalDb.isFriendOf(userName, friendUserName, this.dbSession));
			}
		}
	}

	/**
	 * tests isSpammer
	 */
	@Test
	public void isSpammer() {
		// these users aren't spammers
		for (final int i : new int[] { 1, 2, 3 }) {
			assertFalse(generalDb.isSpammer("testuser" + i, this.dbSession));
		}

		// this is a spammer
		assertTrue(generalDb.isSpammer("testspammer", this.dbSession));

		// Default behaviour
		for (final String userName : new String[] { "", " ", null }) {
			final GenericParam generalParam = ParamUtils.getDefaultGeneralParam();
			generalParam.setRequestedUserName(userName);
			assertEquals(false, generalDb.isSpammer(userName, this.dbSession));
		}
	}

	/**
	 * tests getNewContentId
	 */
	@Test
	public void getNewContentId() {
		final int id = generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		assertTrue(id < generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, this.dbSession));

		assertNull(generalDb.getNewContentId(ConstantID.IDS_UNDEFINED_CONTENT_ID, this.dbSession));

		try {
			generalDb.getNewContentId(null, this.dbSession);
			fail("Exception should be thrown");
		} catch (Exception ignore) {
		}
	}

	/**
	 * tests updateIds
	 */
	@Test
	public void updateIds() {
		final int curId = generalDb.getNewContentId(ConstantID.IDS_TAS_ID, this.dbSession);
		generalDb.updateIds(ConstantID.IDS_TAS_ID, this.dbSession);
		final int newId = generalDb.getNewContentId(ConstantID.IDS_TAS_ID, this.dbSession);
		assertEquals(curId + 2, newId);
	}
}