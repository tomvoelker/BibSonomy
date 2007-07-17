package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

/**
 * Tests related to users.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getAllUsers() {
		final List<User> users = this.userDb.getAllUsers(0, 10, this.dbSession);
		assertEquals(10, users.size());
	}

	@Test
	public void getUserDetails() {
		final User testUser = this.userParam.getUser();
		final User user = this.userDb.getUserDetails(testUser.getName(), this.dbSession);
		ModelUtils.assertPropertyEquality(testUser, user, Integer.MAX_VALUE, new String[] { "homepage", "password", "apiKey" });
		assertEquals("http://www.kde.cs.uni-kassel.de/hotho", user.getHomepage().toString());
		assertEquals(null, user.getPassword());
		assertEquals(null, user.getApiKey());
	}

	/**
	 * Retrieve the names of users present in a group with given group ID
	 */
	@Test
	public void getUserNamesOfGroupId() {
		final List<String> users = this.userDb.getUserNamesByGroupId(GroupID.KDE.getId(), this.dbSession);
		final String[] kdeUsers = new String[] { "kde", "schmitz", "chs", "jaeschke", "stumme", "gst", "sfi", "finis", "rja", "aho", "hotho", "grahl", "beate" };
		assertTrue(users.containsAll(Arrays.asList(kdeUsers)));
		assertEquals(kdeUsers.length, users.size());
	}

	@Test
	public void createUser() {
		final User newUser = this.userParam.getUser();
		newUser.setName("test-name");
		this.userDb.createUser(newUser, this.dbSession);
		final User user = this.userDb.getUserDetails(this.userParam.getUser().getName(), this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, Integer.MAX_VALUE, new String[] { "password" });
		assertEquals(null, user.getPassword());
		try {
			this.userDb.createUser(null, this.dbSession);
			fail("should throw exception");
		} catch (final Exception ex) {
		}
	}

	@Test
	public void deleteUser() {
		try {
			this.userDb.deleteUser("", this.dbSession);
			fail("should throw exception");
		} catch (final UnsupportedOperationException ex) {
		}
	}

	@Test
	public void updateApiKeyForUser() {
		this.userDb.updateApiKeyForUser(this.userParam.getUser(), this.dbSession);
		assertEquals(this.userParam.getUser().getApiKey(), this.userDb.getApiKeyForUser(this.userParam.getUser().getName(), this.dbSession));

		try {
			this.userParam.getUser().setName("this-user-doesnt-exist");
			this.userDb.updateApiKeyForUser(this.userParam.getUser(), this.dbSession);
			fail("should throw exception");
		} catch (final Exception ex) {
		}
	}

	/**
	 * Test the user authentication via API key
	 */
	@Test
	public void validateUserAccess() {
		final String username = "dbenz";
		String apiKey = "ThisIsJustAFakeAPIKey";
		assertFalse(this.userDb.validateUserAccess(username, apiKey, this.dbSession));
		// the correct key
		apiKey = "a9999a44a48879d28bd34fd32bdfa0c1";
		assertTrue(this.userDb.validateUserAccess(username, apiKey, this.dbSession));

		// the user "14summerdays" hasn't got an Api key
		for (final String name : new String[] { "", " ", null, "14summerdays" }) {
			for (final String key : new String[] { "", " ", null, "hurz" }) {
				assertFalse(this.userDb.validateUserAccess(name, key, this.dbSession));
			}
		}
	}
}