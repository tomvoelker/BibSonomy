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

	/**
	 * Retrieve all users
	 */
	@Test
	public void getAllUsers() {
		final List<User> users = this.userDb.getAllUsers(0, 10, this.dbSession);
		assertEquals(10, users.size());
	}

	/**
	 * Details of a given user
	 */
	@Test
	public void getUserDetails() {
		final User testUser = this.userParam.getUser();
		final User user = this.userDb.getUserDetails(testUser.getName(), this.dbSession);
		ModelUtils.assertPropertyEquality(testUser, user, new String[] { "homepage", "password", "apiKey" });
		assertEquals("http://www.kde.cs.uni-kassel.de/hotho", user.getHomepage().toString());
		assertEquals(null, user.getPassword());
		assertEquals(null, user.getApiKey());
	}

	/**
	 * Retrieve the names of users present in a group with given group ID
	 */
	@Test
	public void getUserNamesOfGroupId() {
		final List<String> users = this.userDb.getUserNamesByGroupId(GroupID.GROUP_KDE.getId(), this.dbSession);
		final String[] kdeUsers = new String[] { "kde", "schmitz", "chs", "jaeschke", "stumme", "gst", "sfi", "finis", "rja", "aho", "hotho", "grahl", "beate" };
		assertTrue(users.containsAll(Arrays.asList(kdeUsers)));
		assertEquals(kdeUsers.length, users.size());
	}

	/**
	 * Insert a new user
	 */
	@Test
	public void insertUser() {
		final User newUser = ModelUtils.getUser();
		newUser.setName("test-name");
		this.userParam.setUser(newUser);
		this.userDb.insertUser(this.userParam, this.dbSession);
		final User user = this.userDb.getUserDetails(this.userParam.getUser().getName(), this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, new String[] { "password" });
		assertEquals(null, user.getPassword());

		try {
			this.userParam.setUser(null);
			this.userDb.insertUser(this.userParam, this.dbSession);
			fail("should throw exception");
		} catch (final Exception ex) {
		}
	}

	/**
	 * Update an API key for a given user
	 */
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
	}
}