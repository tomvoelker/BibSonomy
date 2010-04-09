package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
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
	private static final String RANDOM_TESTUSER = "userrandom";
	private static UserDatabaseManager userDb;
	private static GroupDatabaseManager groupDb;
	private static BibTexDatabaseManager bibTexDb;
	

	/**
	 * set up managers
	 */
	@BeforeClass
	public static void setup() {		
		// managers
		userDb = UserDatabaseManager.getInstance();
		bibTexDb = BibTexDatabaseManager.getInstance();
		groupDb = GroupDatabaseManager.getInstance();
	}

	/**
	 * tests getFriendsOfUser
	 */
	@Test
	public void getFriendsOfUser() {
		final List<User> friends = userDb.getUserRelation("testuser1", UserRelation.OF_FRIEND, this.dbSession);
		assertNotNull(friends);
		assertEquals(2, friends.size());
	}
	
	/**
	 * tests getAllUsers
	 */
	@Test
	public void getAllUsers() {
		// there're 6 users that aren't spammers
		List<User> users = userDb.getAllUsers(0, 10, this.dbSession);
		assertEquals(6, users.size());

		// make sure limit and offset work
		users = userDb.getAllUsers(0, 2, this.dbSession);
		assertEquals(2, users.size());
	}

	/**
	 * tests getUserDetails
	 */
	@Test
	public void getUserDetails() {
		final User user = userDb.getUserDetails("testuser1", this.dbSession);
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
	@Test
	public void getUserNamesOfGroupId() {
		final List<String> users = userDb.getUserNamesByGroupId(ParamUtils.TESTGROUP1, this.dbSession);
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
		final String userName = userDb.createUser(newUser, this.dbSession);
		assertEquals(RANDOM_TESTUSER, userName);
		final User user = userDb.getUserDetails(RANDOM_TESTUSER, this.dbSession);
		ModelUtils.assertPropertyEquality(newUser, user, Integer.MAX_VALUE, null, new String[] { "apiKey", "IPAddress", "basket", "gender", "interests", "hobbies", "profession", "openURL", "place", "spammer", "settings", "toClassify", "updatedBy", "reminderPassword", "registrationDate", "reminderPasswordRequestDate", "updatedAt" });

		try {
			userDb.createUser(null, this.dbSession);
			fail("expected exception");
		} catch (Exception ignore) {
		}
	}

	/**
	 * tests updateUser
	 */
	@Test
	public void changeUser() {
		User newTestuser = userDb.getUserDetails(RANDOM_TESTUSER, this.dbSession);
		assertEquals("New Testuser", newTestuser.getRealname());
		// FIXME: it should be possible to change almost all properties of a
		// user - implement me...
		newTestuser.setRealname("New TestUser");
		final String userName = userDb.changeUser(newTestuser, this.dbSession);
		assertEquals(RANDOM_TESTUSER, userName);
		newTestuser = userDb.getUserDetails(RANDOM_TESTUSER, this.dbSession);
		assertEquals("New TestUser", newTestuser.getRealname());

		// you can't change the user's name
		try {
			newTestuser.setName(newTestuser.getName() + "-changed");
			userDb.changeUser(newTestuser, this.dbSession);
			fail("expected exception");
		} catch (RuntimeException ignore) {
		}
	}

	/**
	 * tests the {@link UserUpdateOperation#UPDATE_CORE} operation
	 */
	@Test
	public void updateUserProfile() {
		final String username = "testuser1";
		final ProfilePrivlevel level = ProfilePrivlevel.FRIENDS;
		final String newRealname = "Test User 12";
		
		/*
		 * get user details
		 */
		User testUser = userDb.getUserDetails(username, this.dbSession);
		assertEquals("Test User 1", testUser.getRealname());
		
		/*
		 * change profile
		 */
		testUser.setRealname(newRealname);
		
		final UserSettings testUserSettings = testUser.getSettings();
		testUserSettings.setProfilePrivlevel(level);
		testUserSettings.setTagboxTooltip(2); // to check if UpdateCore was executed
		
		/*
		 * update profile
		 */
		userDb.updateUserProfile(testUser, this.dbSession);
		
		/*
		 * save user
		 */
		User savedTestuser = userDb.getUserDetails(username, this.dbSession);
		final UserSettings savedSettings = savedTestuser.getSettings();
		assertEquals(level, savedSettings.getProfilePrivlevel());
		assertEquals(newRealname, savedTestuser.getRealname());
		
		assertNotSame(2, savedSettings.getTagboxTooltip()); // check if more than the core was updated
	}
	
	/**
	 * tests updateApiKeyForUser
	 */
	@Test
	public void updateApiKeyForUser() {
		final String apiKey = userDb.getApiKeyForUser(RANDOM_TESTUSER, this.dbSession);
		assertNotNull(apiKey);
		assertEquals(32, apiKey.length());
		userDb.updateApiKeyForUser(RANDOM_TESTUSER, this.dbSession);
		final String updatedApiKey = userDb.getApiKeyForUser(RANDOM_TESTUSER, this.dbSession);
		assertNotNull(updatedApiKey);
		assertEquals(32, updatedApiKey.length());
		assertNotSame(apiKey, updatedApiKey);

		try {
			userDb.updateApiKeyForUser(ParamUtils.NOUSER_NAME, this.dbSession);
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
		User user = new User();
		// use a name of the test databases, in this case "testuser2"
		user.setName("testuser2");
		
		// get groups for this user. testuser should be member of testgroup1
		List<Group> groups = groupDb.getGroupsForUser(user.getName(), true, this.dbSession);
		assertNotNull(groups);
		assertEquals(1, groups.size());
		assertEquals("testgroup1", groups.get(0).getName());
		
		// calls the deleteUser method of the UserDataBaseManager class
		// this method is overloaded so you have one method with a String parameter
		// and the new one with a User object parameter
		userDb.deleteUser(user.getName(), this.dbSession);
		
		// get the old user details out of the testdb
		final User newTestuser = userDb.getUserDetails(user.getName(), this.dbSession);
		
		// the user have to be available in the test db ...
		assertNotNull(newTestuser.getName());
		
		// after deleting the user, he shouldn't have any memberships in groups
		// get groups for this user. testuser should be member of testgroup1
		groups = groupDb.getGroupsForUser(user.getName(), true, this.dbSession);
		assertEquals(0, groups.size());
		
		// but it should be flagged as spammer
		assertEquals(true, newTestuser.getSpammer());
				
		// create a spammer group id by adding an old group id to the interger min value
		int groupid = Integer.MAX_VALUE + 1;
		
		// get posts for this user
		List<Post<BibTex>> posts = bibTexDb.getPostsForUser(user.getName(), user.getName(), HashID.INTER_HASH, groupid, new ArrayList<Integer>(), null, 10, 0, null, this.dbSession);
		
		// there should be at least more then one post with that negative group id
		assertNotNull(posts);
				
		// create a new user object which has a groupname
		User testUserIsGroup = new User();
		testUserIsGroup.setName("testgroup1");
		
		// if anybody tries to delete a user which is a group should get an exception
		try {
			userDb.deleteUser(testUserIsGroup.getName(), this.dbSession);
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
		assertNull(userDb.validateUserAccess("testuser1", "ThisIsJustAFakeAPIKey", this.dbSession).getName());
		// the correct key
		assertEquals("testuser1", userDb.validateUserAccess("testuser1", "11111111111111111111111111111111", this.dbSession).getName());

		// the user "testspammer" hasn't got an Api key
		for (final String name : new String[] { "", " ", null, "testspammer" }) {
			for (final String key : new String[] { "", " ", null, "hurz" }) {
				assertNull(userDb.validateUserAccess(name, key, this.dbSession).getName());
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
		List<User> users = userDb.getRelatedUsersBySimilarity(requestedUserName, null, UserRelation.JACCARD, 0, 10, this.dbSession);
		assertEquals(2, users.size());
		assertEquals("testuser2", users.get(0).getName());
		assertEquals(5, users.get(0).getPrediction());
		assertEquals("testuser3", users.get(1).getName());
		assertEquals(2, users.get(1).getPrediction());
		/*
		 * we don't have data for cosine similarity in the DB
		 */
		users = userDb.getRelatedUsersBySimilarity(requestedUserName, null, UserRelation.COSINE, 0, 10, this.dbSession);
		assertEquals(0, users.size());
		
	}
	
	/**
	 * tests getUserFollowers
	 */
	@Test
	public void getUserFollowers(){
		final String authUser = "testuser2";
		
		List<User> userFollowers = userDb.getUserRelation(authUser, UserRelation.OF_FOLLOWER, this.dbSession);
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
		
		List<User> userFollowers = userDb.getUserRelation(authUser, UserRelation.FOLLOWER_OF, this.dbSession);
		assertNotNull(userFollowers);
		assertEquals(2, userFollowers.size());
		assertEquals("testuser2", userFollowers.get(0).getName());
		assertEquals("testuser3", userFollowers.get(1).getName());
	}
	
	/**
	 * tests insert AND delete
	 */
	@Test
	public void insertAndDeleteUserRelations(){
		String sourceUser="testuser3";
		String targetUser="testuser1";
		
		userDb.createUserRelation(sourceUser, targetUser, UserRelation.FOLLOWER_OF, this.dbSession);
		userDb.createUserRelation(sourceUser, targetUser, UserRelation.OF_FRIEND, this.dbSession);
		
		// FIXME: not FOLLOWERS but followees!
		List<User> followedByUser = userDb.getUserRelation(sourceUser, UserRelation.FOLLOWER_OF, this.dbSession);
		List<User> friendsOfUser = userDb.getUserRelation(sourceUser, UserRelation.OF_FRIEND, this.dbSession);
		
		assertEquals(1, followedByUser.size());
		assertEquals("testuser1", followedByUser.get(0).getName());
		
		assertEquals(1, friendsOfUser.size());
		assertEquals("testuser1", friendsOfUser.get(0).getName());
		
		userDb.deleteUserRelation(sourceUser, targetUser, UserRelation.FOLLOWER_OF, this.dbSession);
		userDb.deleteUserRelation(sourceUser, targetUser, UserRelation.OF_FRIEND, this.dbSession);
		
		followedByUser = userDb.getUserRelation(sourceUser, UserRelation.FOLLOWER_OF, this.dbSession);
		assertEquals(0, followedByUser.size());	
		
		friendsOfUser = userDb.getUserRelation(sourceUser, UserRelation.OF_FRIEND, this.dbSession);
		assertEquals(0, friendsOfUser.size());		
	}
}