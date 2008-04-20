package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to users.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * tests getAllUsers
	 */
	@Test
	public void getAllUsers() {
		// there're 6 users that aren't spammers
		List<User> users = this.userDb.getAllUsers(0, 10, this.dbSession);
		assertEquals(6, users.size());

		// make sure limit and offset work
		users = this.userDb.getAllUsers(0, 2, this.dbSession);
		assertEquals(2, users.size());
	}

	/**
	 * tests getUserDetails
	 */
	@Test
	public void getUserDetails() {
		final User user = this.userDb.getUserDetails("testuser1", this.dbSession);
		// TODO: checke every entity that should be present in the user object
		assertEquals("testuser1", user.getName());
		assertEquals("http://www.bibsonomy.org/user/testuser1", user.getHomepage().toString());
		assertEquals("11111111111111111111111111111111", user.getApiKey());
		assertNotNull(user.getBasket());
		assertEquals(Role.ADMIN, user.getRole());
	}

	/**
	 * Retrieve the names of users present in a group with given group ID
	 */
	@Test
	public void getUserNamesOfGroupId() {
		final List<String> users = this.userDb.getUserNamesByGroupId(ParamUtils.TESTGROUP1_ID, this.dbSession);
		final String[] testgroup1User = new String[] { "testuser1", "testuser2" };
		assertTrue(users.containsAll(Arrays.asList(testgroup1User)));
		assertEquals(testgroup1User.length, users.size());
	}

	/**
	 * tests createUser
	 */
	@Test
	public void createUser() {
		final User newUser = new User();
		newUser.setName("new-testuser");
		newUser.setRealname("New Testuser");
		newUser.setEmail("new-testuser@bibsonomy.org");
		newUser.setPassword("password");
		newUser.setApiKey("00000000000000000000000000000000");
		newUser.getSettings().setDefaultLanguage("zv");
		newUser.setSpammer(0);
		newUser.setRole(Role.DEFAULT);
		newUser.setToClassify(1);
		newUser.setAlgorithm(null);
		final String userName = this.userDb.createUser(newUser, this.dbSession);
		assertEquals("new-testuser", userName);
		final User user = this.userDb.getUserDetails("new-testuser", this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, Integer.MAX_VALUE, null, new String[] { "password", "registrationDate", "basket", "prediction", "algorithm", "updatedBy", "updatedAt", "mode" });

		try {
			this.userDb.createUser(null, this.dbSession);
			fail("expected exception");
		} catch (Exception ignore) {
		}
	}

	/**
	 * tests updateUser
	 * 
	 * FIXME: copying the user to log_user only works once, the second time we
	 * get a "duplicate key" error.
	 */
	@Ignore
	public void changeUser() {
		User newTestuser = this.userDb.getUserDetails("new-testuser", this.dbSession);
		assertEquals("New Testuser", newTestuser.getRealname());
		// FIXME: it should be possible to change almost all properties of a
		// user - implement me...
		newTestuser.setRealname("New TestUser");
		final String userName = this.userDb.changeUser(newTestuser, this.dbSession);
		assertEquals("new-testuser", userName);
		newTestuser = this.userDb.getUserDetails("new-testuser", this.dbSession);
		assertEquals("New TestUser", newTestuser.getRealname());

		// you can't change the user's name
		try {
			newTestuser.setName(newTestuser.getName() + "-changed");
			this.userDb.changeUser(newTestuser, this.dbSession);
			fail("expected exception");
		} catch (RuntimeException ignore) {
		}
	}

	/**
	 * tests updateApiKeyForUser
	 */
	@Test
	public void updateApiKeyForUser() {
		final String apiKey = this.userDb.getApiKeyForUser("new-testuser", this.dbSession);
		assertEquals(32, apiKey.length());
		this.userDb.updateApiKeyForUser("new-testuser", this.dbSession);
		final String updatedApiKey = this.userDb.getApiKeyForUser("new-testuser", this.dbSession);
		assertEquals(32, updatedApiKey.length());
		assertNotSame(apiKey, updatedApiKey);

		try {
			this.userDb.updateApiKeyForUser("this-user-doesnt-exist", this.dbSession);
			fail("expected exception");
		} catch (Exception ignore) {
		}
	}

	/**
	 * tests deleteUser
	 */
	@Test
	public void deleteUser() {
		this.userDb.deleteUser("new-testuser", this.dbSession);
		final User newTestuser = this.userDb.getUserDetails("new-testuser", this.dbSession);
		assertNull(newTestuser.getName());

		for (final String username : new String[] { "", " ", null }) {
			try {
				this.userDb.deleteUser(username, this.dbSession);
				fail("expected exception");
			} catch (RuntimeException ignore) {
			}
		}
	}

	/**
	 * Test the user authentication via API key
	 */
	@Test
	public void validateUserAccess() {
		// not logged in (wrong apikey) = unknown user
		assertNull(this.userDb.validateUserAccess("testuser1", "ThisIsJustAFakeAPIKey", this.dbSession).getName());
		// the correct key
		assertEquals("testuser1", this.userDb.validateUserAccess("testuser1", "11111111111111111111111111111111", this.dbSession).getName());

		// the user "testspammer" hasn't got an Api key
		for (final String name : new String[] { "", " ", null, "testspammer" }) {
			for (final String key : new String[] { "", " ", null, "hurz" }) {
				assertNull(this.userDb.validateUserAccess(name, key, this.dbSession).getName());
			}
		}
	}
}