package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * Tests related to users.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * Retrieve all users
	 */
	@Test
	public void getAllUsers() {
		final List<User> users = this.userDb.getAllUsers(this.userParam, this.dbSession);
		assertEquals(1568, users.size());
	}

	/**
	 * Details of a given user
	 */
	@Test
	public void getUserDetails() {
		this.userDb.getUserDetails(this.userParam, this.dbSession);
	}

	/**
	 * Privacy level of a user
	 */
	@Test
	public void getPrivlevelOfUser() {
		assertEquals(GroupID.GROUP_PUBLIC.getId(), this.userDb.getPrivlevelOfUser(this.userParam, this.dbSession));
	}

	/**
	 * Retrieve the names of users present in a group with given group ID
	 */
	@Test
	public void getUserNamesOfGroupId() {
		final List<String> users = this.userDb.getUserNamesByGroupId(GroupID.GROUP_KDE.getId(), dbSession);
		final String[] kdeUsers = new String[] { "kde", "schmitz", "chs", "jaeschke", "stumme", "gst", "sfi", "finis", "rja", "aho", "hotho", "grahl", "beate" };
		assertTrue(users.containsAll(Arrays.asList(kdeUsers)));
		assertEquals(kdeUsers.length, users.size());
	}

	/**
	 * Retrieve users who belong to the group "public" 
	 * 
	 * TODO: implement and @Test
	 */
	public void getUsersOfGroupPublic() {
		final List<User> users = this.userDb.getUsersOfGroupPublic(this.userParam, this.dbSession);
		assertEquals(13, users.size());
	}

	/**
	 * Retrieve users who belong to the group "hidden"
	 */
	@Test
	public void getUsersOfGroupHidden() {
		final List<User> users = this.userDb.getUsersOfGroupPrivate(this.userParam, this.dbSession);
		assertEquals(1, users.size());
	}

	/**
	 * Insert a new user
	 */
	@Test
	public void testInsertUser() {
		final UserParam param = new UserParam();
		try {
			final URL url = new URL("http://www.db.de");
			param.setUserName("neuerUser");
			param.setEmail("mgr@cs.uni-kassel.de");
			param.setHomepage(url);
			param.setPassword("dhdhd");
			param.setRealname("mira");
			this.userDb.insertUser(param, this.dbSession);

			final User user = this.userDb.getUserDetails(param, this.dbSession);
			assertEquals(user.getName(), "neuerUser");
			assertEquals(user.getEmail(), "mgr@cs.uni-kassel.de");
			assertEquals(user.getHomepage(), url);
			assertEquals(user.getRealname(), "mira");
			assertEquals(user.getApiKey(), param.getApiKey());
		} catch (final MalformedURLException ex) {
			fail("Malformed URL: " + ex.getMessage());
		}
	}

	/**
	 *  Update an API key for a given user
	 */
	@Test
	public void updateApiKeyForUser() {
		this.userDb.updateApiKeyForUser(this.userParam, this.dbSession);
		assertEquals(this.userParam.getApiKey(), this.userDb.getApiKeyForUser(this.userParam, this.dbSession));
	}

	/**
	 * Re-Generate API keys for all users - handle with care! 
	 */
	@Test
	public void generateApiKeysForAllUsers() {
		// this.userDb.generateApiKeysForAllUsers(this.dbSession);
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