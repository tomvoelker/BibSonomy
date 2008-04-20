package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for the GeneralDatabaseManager.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
public class GeneralDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * tests isFriendOf
	 */
	@Test
	public void isFriendOf() {
		// a user is always his own friend
		for (final int i : new int[] { 1, 2, 3 }) {
			assertTrue(this.generalDb.isFriendOf("testuser" + i, "testuser" + i, this.dbSession));
		}

		// combinations: testuser1 is a friend of testuser2 and 3, testuser2 is
		// a friend of testuser1, testuser3 has no friends at all
		assertTrue(this.generalDb.isFriendOf("testuser1", "testuser2", this.dbSession));
		assertTrue(this.generalDb.isFriendOf("testuser2", "testuser1", this.dbSession));
		assertTrue(this.generalDb.isFriendOf("testuser1", "testuser3", this.dbSession));
		assertFalse(this.generalDb.isFriendOf("testuser3", "testuser1", this.dbSession));
		assertFalse(this.generalDb.isFriendOf("testuser3", "testuser2", this.dbSession));
		assertFalse(this.generalDb.isFriendOf("testuser2", "testuser3", this.dbSession));

		// with no users set or a not existing one, no exception should be
		// thrown and the result should just be "false"
		final String[] combinations = new String[] { "", " ", null, ParamUtils.NOUSER_NAME };
		for (final String userName : combinations) {
			for (final String friendUserName : combinations) {
				if (present(userName) && present(friendUserName) && userName.equals(friendUserName)) continue;
				assertFalse(this.generalDb.isFriendOf(userName, friendUserName, this.dbSession));
			}
		}
	}

	/**
	 * tests getFriendsOfUser
	 */
	@Test
	public void getFriendsOfUser() {
		final List<User> friends = this.generalDb.getFriendsOfUser("testuser1", this.dbSession);
		assertNotNull(friends);
		assertEquals(2, friends.size());
	}

	/**
	 * tests isSpammer
	 */
	@Test
	public void isSpammer() {
		// these users aren't spammers
		for (final int i : new int[] { 1, 2, 3 }) {
			assertFalse(this.generalDb.isSpammer("testuser" + i, this.dbSession));
		}

		// this is a spammer
		assertTrue(this.generalDb.isSpammer("testspammer", this.dbSession));

		// Default behaviour
		for (final String userName : new String[] { "", " ", null }) {
			this.generalParam.setRequestedUserName(userName);
			assertEquals(false, this.generalDb.isSpammer(userName, this.dbSession));
		}
	}

	/**
	 * tests getNewContentId
	 */
	@Test
	public void getNewContentId() {
		final int id = this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		assertTrue(id < this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, this.dbSession));

		assertNull(this.generalDb.getNewContentId(ConstantID.IDS_UNDEFINED_CONTENT_ID, this.dbSession));

		try {
			this.generalDb.getNewContentId(null, this.dbSession);
			fail("Exception should be thrown");
		} catch (Exception ignore) {
		}
	}

	/**
	 * tests updateIds
	 */
	@Test
	public void updateIds() {
		Integer curId = null;
		Integer newId = null;
		curId = this.generalDb.getNewContentId(ConstantID.IDS_TAS_ID, this.dbSession);
		this.generalDb.updateIds(ConstantID.IDS_TAS_ID, this.dbSession);
		newId = this.generalDb.getNewContentId(ConstantID.IDS_TAS_ID, this.dbSession);
		assertTrue(newId == curId + 2);
	}
}