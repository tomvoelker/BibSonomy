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
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to users.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
//@Ignore
public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/* name of 1st testuser to be created / updated */
	public static String NEW_TESTUSER_1;
	/* name of 2nd testuser to be created / updated */
	public static String NEW_TESTUSER_2;
	
	/**
	 * Init user names
	 */
	@BeforeClass
	public static void setup() {
		// abusing API key generation to generate random usernames... 
		UserDatabaseManagerTest.NEW_TESTUSER_1 = UserUtils.generateApiKey().substring(0,12);
		UserDatabaseManagerTest.NEW_TESTUSER_2 = UserUtils.generateApiKey().substring(0,12);
	}
	
	/**
	 * tests getAllUsers
	 */
	@Ignore // depends on new local db
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
	@Ignore // depends on new local db
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
	@Ignore // depends on new local db
	public void getUserNamesOfGroupId() {
		final List<String> users = this.userDb.getUserNamesByGroupId(ParamUtils.TESTGROUP1, this.dbSession);
		final String[] testgroup1User = new String[] { "testuser1", "testuser2" };
		assertTrue(users.containsAll(Arrays.asList(testgroup1User)));
		assertEquals(testgroup1User.length, users.size());
	}

	/**
	 * tests createUser
	 */

	// FIXME: SQL ERROR
	@Ignore
	public void createUser() {
		final User newUser = new User();
		final String randomUserName = NEW_TESTUSER_1;
		newUser.setName(randomUserName);
		newUser.setRealname("New Testuser");
		newUser.setEmail("new-testuser@bibsonomy.org");
		newUser.setPassword("password");
		newUser.setApiKey("00000000000000000000000000000000");
		newUser.getSettings().setDefaultLanguage("zv");
		newUser.setSpammer(false);
		newUser.setRole(Role.DEFAULT);
		newUser.setToClassify(1);
		newUser.setAlgorithm(null);
		final String userName = this.userDb.createUser(newUser, this.dbSession);
		assertEquals(randomUserName, userName);
		final User user = this.userDb.getUserDetails(randomUserName, this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, Integer.MAX_VALUE, null, new String[] { "apiKey", "IPAddress", "basket", "gender", "interests", "hobbies", "profession", "openURL", "place", "spammer", "settings", "toClassify", "updatedBy", "reminderPassword", "registrationDate", "reminderPasswordRequestDate", "updatedAt" });

		try {
			this.userDb.createUser(null, this.dbSession);
			fail("expected exception");
		} catch (Exception ignore) {
		}
	}
	
	/**
	 * tests createUser
	 */
	@Ignore
	// FIXME: SQL ERROR
	public void createUserSpammerUnknown() {
		final User newUser = new User();
		newUser.setName(NEW_TESTUSER_2);
		newUser.setRealname("New Testuser");
		newUser.setEmail("new-testuser@bibsonomy.org");
		newUser.setPassword("password");
		newUser.setApiKey("00000000000000000000000000000000");
		newUser.getSettings().setDefaultLanguage("zv");
		//newUser.setSpammer(false);
		newUser.setRole(Role.DEFAULT);
		newUser.setToClassify(1);
		newUser.setAlgorithm(null);
		final String userName = this.userDb.createUser(newUser, this.dbSession);
		assertEquals(NEW_TESTUSER_2, userName);
		final User user = this.userDb.getUserDetails(NEW_TESTUSER_2, this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, Integer.MAX_VALUE, null, new String[] { "apiKey", "IPAddress", "basket", "gender", "interests", "hobbies", "profession", "openURL", "place", "spammer", "settings", "toClassify", "updatedBy", "reminderPassword", "registrationDate", "reminderPasswordRequestDate", "updatedAt"});

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
		User newTestuser = this.userDb.getUserDetails(NEW_TESTUSER_1, this.dbSession);
		assertEquals("New Testuser", newTestuser.getRealname());
		// FIXME: it should be possible to change almost all properties of a
		// user - implement me...
		newTestuser.setRealname("New TestUser");
		final String userName = this.userDb.changeUser(newTestuser, this.dbSession);
		assertEquals(NEW_TESTUSER_1, userName);
		newTestuser = this.userDb.getUserDetails(NEW_TESTUSER_1, this.dbSession);
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
	@Ignore
	public void updateApiKeyForUser() {
		final String apiKey = this.userDb.getApiKeyForUser(NEW_TESTUSER_1, this.dbSession);
		assertNotNull(apiKey);
		assertEquals(32, apiKey.length());
		this.userDb.updateApiKeyForUser(NEW_TESTUSER_1, this.dbSession);
		final String updatedApiKey = this.userDb.getApiKeyForUser(NEW_TESTUSER_1, this.dbSession);
		assertNotNull(updatedApiKey);
		assertEquals(32, updatedApiKey.length());
		assertNotSame(apiKey, updatedApiKey);

		try {
			this.userDb.updateApiKeyForUser(ParamUtils.NOUSER_NAME, this.dbSession);
			fail("expected exception");
		} catch (Exception ignore) {
		}
	}

	/**
	 * tests deleteUser
	 */
	@Test
	public void deleteUser() {
		this.userDb.deleteUser(NEW_TESTUSER_1, this.dbSession);
		final User newTestuser = this.userDb.getUserDetails(NEW_TESTUSER_1, this.dbSession);
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
	@Ignore // depends on new local db
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