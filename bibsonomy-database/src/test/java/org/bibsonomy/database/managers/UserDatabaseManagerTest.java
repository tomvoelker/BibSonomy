package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * tests getAllUsers
	 */
	@Ignore
	public void getAllUsers() {
		List<User> users = this.userDb.getAllUsers(0, 10, this.dbSession);
		assertEquals(3, users.size());
		users = this.userDb.getAllUsers(0, 2, this.dbSession);
		assertEquals(2, users.size());
	}

	/**
	 * tests getUserDetails
	 */
	@Ignore
	public void getUserDetails() {
		final User testUser = this.userParam.getUser();
		final User user = this.userDb.getUserDetails(testUser.getName(), this.dbSession);
		ModelUtils.assertPropertyEquality(testUser, user, Integer.MAX_VALUE, null, new String[] { "homepage", "password", "apiKey", "IPAddress", "gender", "basket", "registrationDate"});
		assertEquals("http://www.bibsonomy.org/", user.getHomepage().toString());
		// assertEquals(null, user.getPassword()); // TODO: why?
		assertEquals("11111111111111111111111111111111", user.getApiKey());
		assertNotNull(user.getBasket());
	}

	/**
	 * test if getUserDetails returns the (correct) role of the user
	 */
	@Test
	public void getUserDetailsRole() {
		final User user = this.userDb.getUserDetails("jaeschke", this.dbSession);
		assertEquals(Role.ADMIN, user.getRole());
	}
	
	/**
	 * Retrieve the names of users present in a group with given group ID
	 */
	@Ignore
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
		final User newUser = this.userParam.getUser();
		newUser.setName("new-testuser");
		newUser.setSpammer(0);
		newUser.setRole(Role.DEFAULT);
		this.userDb.createUser(newUser, this.dbSession);
		final User user = this.userDb.getUserDetails(this.userParam.getUser().getName(), this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, Integer.MAX_VALUE, null, new String[] { "password", "registrationDate", "basket"});
		// assertEquals(null, user.getPassword()); FIXME: why? is not rendered anyway

		try {
			this.userDb.createUser(null, this.dbSession);
			fail("should throw exception");
		} catch (final Exception ignore) {
		}
	}

	/**
	 * tests updateUser
	 */
	@Test
	@Ignore
	// FIXME doesn't work... ("Unknown column 'id' in 'field list'")
	public void changeUser() {
		User testuser1 = this.userDb.getUserDetails("testuser1", this.dbSession);

		// you can't change the user's name
		testuser1.setName("testuser1-changed");
		this.userDb.changeUser(testuser1, this.dbSession);
		testuser1 = this.userDb.getUserDetails("testuser1", this.dbSession);
		assertEquals("testuser1", testuser1.getName());
	}

	/**
	 * tests deleteUser
	 */
	@Test
	public void deleteUser() {
		try {
			this.userDb.deleteUser("", this.dbSession);
			fail("should throw exception");
		} catch (final UnsupportedOperationException ignore) {
		}
	}

	/**
	 * tests updateApiKeyForUser
	 */
	@Ignore
	public void updateApiKeyForUser() {
		assertEquals("11111111111111111111111111111111", this.userDb.getApiKeyForUser("testuser1", this.dbSession));
		this.userDb.updateApiKeyForUser("testuser1", this.dbSession);
		assertEquals(32, this.userDb.getApiKeyForUser("testuser1", this.dbSession).length());

		try {
			this.userDb.updateApiKeyForUser("this-user-doesnt-exist", this.dbSession);
			fail("should throw exception");
		} catch (final Exception ignore) {
		}
	}

	/**
	 * Test the user authentication via API key
	 */
	@Ignore
	public void validateUserAccess() {
		// create an unknown user
		User unknownUser = new User();
		// not logged in (wrong apikey) = unknown user
		assertEquals(unknownUser, this.userDb.validateUserAccess("testuser1", "ThisIsJustAFakeAPIKey", this.dbSession));
		// the correct key
		assertEquals("testuser1", this.userDb.validateUserAccess("testuser1", "11111111111111111111111111111111", this.dbSession).getName());

		// the user "testspammer" hasn't got an Api key
		for (final String name : new String[] { "", " ", null, "testspammer" }) {
			for (final String key : new String[] { "", " ", null, "hurz" }) {
				assertEquals(unknownUser, this.userDb.validateUserAccess(name, key, this.dbSession));
			}
		}
	}
}