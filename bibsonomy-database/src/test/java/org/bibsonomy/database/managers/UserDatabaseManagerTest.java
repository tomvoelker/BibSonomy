package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
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
 * @author Anton Wilhelm
 * @version $Id$
 */
public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/* name of testuser to be created / updated */
	private static String RANDOM_TESTUSER;


	/**
	 * Init user names
	 */
	@BeforeClass
	public static void setup() {
		RANDOM_TESTUSER = ParamUtils.getRandomUserName();
	}


	/**
	 * tests getFriendsOfUser
	 */
	@Test
	public void getFriendsOfUser() {
		final List<User> friends = this.userDb.getFriendsOfUser("testuser1", this.dbSession);
		assertNotNull(friends);
		assertEquals(2, friends.size());
	}
	
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
	@Ignore
	public void getUserNamesOfGroupId() {
		final List<String> users = this.userDb.getUserNamesByGroupId(ParamUtils.TESTGROUP1, this.dbSession);
		final String[] testgroup1User = new String[] { "testuser1", "testuser2" };
		assertTrue(users.containsAll(Arrays.asList(testgroup1User)));
		assertEquals(testgroup1User.length, users.size());
	}

	/**
	 * tests createUser
	 */
	@Test
	public void createUser() {
		final User newUser = new User(RANDOM_TESTUSER);
		newUser.setRealname("New Testuser");
		newUser.setEmail("new-testuser@bibsonomy.org");
		newUser.setHomepage(ParamUtils.EXAMPLE_URL);
		newUser.setPassword("password");
		newUser.setApiKey("00000000000000000000000000000000");
		newUser.getSettings().setDefaultLanguage("zv");
		newUser.setSpammer(false);
		newUser.setRole(Role.DEFAULT);
		newUser.setToClassify(1);
		newUser.setAlgorithm(null);
		final String userName = this.userDb.createUser(newUser, this.dbSession);
		assertEquals(RANDOM_TESTUSER, userName);
		final User user = this.userDb.getUserDetails(RANDOM_TESTUSER, this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, Integer.MAX_VALUE, null, new String[] { "apiKey", "IPAddress", "basket", "gender", "interests", "hobbies", "profession", "openURL", "place", "spammer", "settings", "toClassify", "updatedBy", "reminderPassword", "registrationDate", "reminderPasswordRequestDate", "updatedAt" });

		try {
			this.userDb.createUser(null, this.dbSession);
			fail("expected exception");
		} catch (Exception ignore) {
		}
	}

	/**
	 * tests updateUser
	 */
	@Test
	public void changeUser() {
		User newTestuser = this.userDb.getUserDetails(RANDOM_TESTUSER, this.dbSession);
		assertEquals("New Testuser", newTestuser.getRealname());
		// FIXME: it should be possible to change almost all properties of a
		// user - implement me...
		newTestuser.setRealname("New TestUser");
		final String userName = this.userDb.changeUser(newTestuser, this.dbSession);
		assertEquals(RANDOM_TESTUSER, userName);
		newTestuser = this.userDb.getUserDetails(RANDOM_TESTUSER, this.dbSession);
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
		final String apiKey = this.userDb.getApiKeyForUser(RANDOM_TESTUSER, this.dbSession);
		assertNotNull(apiKey);
		assertEquals(32, apiKey.length());
		this.userDb.updateApiKeyForUser(RANDOM_TESTUSER, this.dbSession);
		final String updatedApiKey = this.userDb.getApiKeyForUser(RANDOM_TESTUSER, this.dbSession);
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
	 * 
	 * This test flags a user which is not a group as spammer and all of his posts
	 * will be also flagged
	 */
	@Test
	public void deleteUser() {
		// create a new user object
		List<Group> groups = null;
		User user = new User();
		// use a name of the test databases, in this case "testuser2"
		user.setName("testuser2");
		
		// get groups for this user. testuser should be member of testgroup1
		groups = this.groupDb.getGroupsForUser(user.getName(), true, this.dbSession);
		assertNotNull(groups);
		assertEquals(1, groups.size());
		assertEquals("testgroup1", groups.get(0).getName());
		
		// calls the deleteUser method of the UserDataBaseManager class
		// this method is overloaded so you have one method with a String parameter
		// and the new one with a User object parameter
		this.userDb.deleteUser(user.getName(), this.dbSession);
		
		// get the old user details out of the testdb
		final User newTestuser = this.userDb.getUserDetails(user.getName(), this.dbSession);
		
		// the user have to be available in the test db ...
		assertNotNull(newTestuser.getName());
		
		// after deleting the user, he shouldn't have any memberships in groups
		// get groups for this user. testuser should be member of testgroup1
		groups = this.groupDb.getGroupsForUser(user.getName(), true, this.dbSession);
		assertEquals(0, groups.size());
		
		// but it should be flagged as spammer
		assertEquals(true, newTestuser.getSpammer());
				
		// create a spammer group id by adding an old group id to the interger min value
		int groupid = Integer.MAX_VALUE + 1;
		
		// get posts for this user
		List<Post<BibTex>> posts = this.bibTexDb.getBibTexForUser(user.getName(), HashID.INTER_HASH, groupid , 10, 0, this.dbSession);
		
		// there should be at least more then one post with that negative group id
		assertNotNull(posts);
				
		// create a new user object which has a groupname
		User testUserIsGroup = new User();
		testUserIsGroup.setName("testgroup1");
		
		// if anybody tries to delete a user which is a group should get an exception
		try {
			this.userDb.deleteUser(testUserIsGroup.getName(), this.dbSession);
			fail("expected exception");
		} catch (RuntimeException ignore) {
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
	
	/**
	 * tests getRelatedUsersBySimilarity 
	 */
	@Test
	public void getRelatedUsersBySimilarity() {
		/*
		 * fetch the two related users of testuser1 by jaccard measure
		 */
		final String requestedUserName = "testuser1";
		List<User> users = this.userDb.getRelatedUsersBySimilarity(requestedUserName, null, UserRelation.JACCARD, 0, 10, this.dbSession);
		assertEquals(2, users.size());
		assertEquals("testuser2", users.get(0).getName());
		assertEquals(5, users.get(0).getPrediction());
		assertEquals("testuser3", users.get(1).getName());
		assertEquals(2, users.get(1).getPrediction());
		/*
		 * we don't have data for cosine similarity in the DB
		 */
		users = this.userDb.getRelatedUsersBySimilarity(requestedUserName, null, UserRelation.COSINE, 0, 10, this.dbSession);
		assertEquals(0, users.size());
		
	}
	
	/**
	 * tests getUserFollowers
	 */
	@Test
	public void getUserFollowers(){
		final String authUser = "testuser2";
		
		List<User> userFollowers = this.userDb.getUserFollowers(authUser, this.dbSession);
		assertNotNull(userFollowers);
		assertEquals(1, userFollowers.size());
		assertEquals("testuser1", userFollowers.get(0).getName());
	}
	
	/**
	 * tests getUserFollowers
	 */
	@Test
	public void getFollowersOfUser(){
		final String authUser = "testuser1";
		
		List<User> userFollowers = this.userDb.getFollowersOfUser(authUser, this.dbSession);
		assertNotNull(userFollowers);
		assertEquals(2, userFollowers.size());
		assertEquals("testuser2", userFollowers.get(0).getName());
		assertEquals("testuser3", userFollowers.get(1).getName());
	}
	
	/**
	 * tests insert AND delete
	 */
	@Test
	public void insertAndDeleteFollowersOfUser(){
		UserParam param = new UserParam();
		param.setUserName("testuser3");
		param.setRequestedUserName("testuser1");
		
		List<User> followersOfUser = null;
		
		this.userDb.addFollowerOfUser(param, this.dbSession);
		
		followersOfUser = this.userDb.getFollowersOfUser("testuser3", this.dbSession);
		
		assertNotNull(followersOfUser);
		assertEquals(1, followersOfUser.size());
		assertEquals("testuser1", followersOfUser.get(0).getName());
		
		this.userDb.deleteFollowerOfUser(param, this.dbSession);
		
		followersOfUser = this.userDb.getFollowersOfUser("testuser3", this.dbSession);
		assertNotNull(followersOfUser);
		assertEquals(0, followersOfUser.size());		
	}
}