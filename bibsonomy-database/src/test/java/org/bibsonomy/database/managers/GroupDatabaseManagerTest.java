package org.bibsonomy.database.managers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to groups.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 */
public class GroupDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static GroupDatabaseManager groupDb;
	private static UserDatabaseManager userDb;
	
	/**
	 * sets up the manager
	 */
	@BeforeClass
	public static void setupManager() {
		groupDb = GroupDatabaseManager.getInstance();
		userDb = UserDatabaseManager.getInstance();
	}
	
	/**
	 * Checks whether the list of groups contains at least the three standard
	 * groups: "public", "private" and "friends"
	 */
	private static void assertStandardGroups(final List<Group> groups) {
		final Set<Integer> found = new HashSet<Integer>();
		for (final Group group : groups) {
			found.add(group.getGroupId());
		}
		assertTrue(found.contains(PUBLIC_GROUP_ID));
		assertTrue(found.contains(PRIVATE_GROUP_ID));
		assertTrue(found.contains(FRIENDS_GROUP_ID));
	}
	
	/**
	 * tests getAllGroups
	 */
	@Test
	public void getAllGroups() {
		final List<Group> allGroups = groupDb.getAllGroups(0, 100, this.dbSession);
		assertEquals(4, allGroups.size());

		for (final Group group : allGroups) {
			if (group.getName().startsWith("testgroup")) {
				final int groupNr = Integer.parseInt("" + group.getName().charAt(group.getName().length() - 1));
				assertEquals(2 + groupNr, group.getGroupId());
				assertEquals("Test Group " + groupNr, group.getRealname());
				assertEquals("http://www.bibsonomy.org/group/testgroup" + groupNr, group.getHomepage().toString());
			}
		}

		// make sure that limit and offset work
		assertEquals(1, groupDb.getAllGroups(0, 1, this.dbSession).size());
		assertEquals(1, groupDb.getAllGroups(1, 2, this.dbSession).size());
		assertEquals(3, groupDb.getAllGroups(0, 3, this.dbSession).size());
	}
	
	/**
	 * tests getPendingGroups
	 */
	@Test
	public void getPendingGroups() {
		final List<Group> pendingGroups = groupDb.getPendingGroups(0, 100, this.dbSession);
		assertEquals(2, pendingGroups.size());

		for (final Group group : pendingGroups) {
			if (group.getName().startsWith("testpendinggroup")) {
				final int groupNr = Integer.parseInt("" + group.getName().charAt(group.getName().length() - 1));
				assertEquals(6 + groupNr, group.getGroupId());
			}
		}

		// make sure that limit and offset work
		assertEquals(1, groupDb.getPendingGroups(0, 1, this.dbSession).size());
		assertEquals(1, groupDb.getPendingGroups(1, 2, this.dbSession).size());
		assertEquals(2, groupDb.getPendingGroups(0, 3, this.dbSession).size());
	}

	/**
	 * tests getGroupByName
	 */
	@Test
	public void getGroupByName() {
		final Group testgroup1 = groupDb.getGroupByName("testgroup1", this.dbSession);
		assertEquals("testgroup1", testgroup1.getName());
		assertEquals(ParamUtils.TESTGROUP1, testgroup1.getGroupId());
		assertEquals("Test Group 1", testgroup1.getRealname());
		assertEquals("http://www.bibsonomy.org/group/testgroup1", testgroup1.getHomepage().toString());
		assertEquals(true, testgroup1.isSharedDocuments());

		final Group testgroup2 = groupDb.getGroupByName("testgroup2", this.dbSession);
		assertEquals(false, testgroup2.isSharedDocuments());
		final Group testgroup3 = groupDb.getGroupByName("testgroup3", this.dbSession);
		assertEquals(false, testgroup3.isSharedDocuments());

		for (final String groupname : new String[] { "", " ", null }) {
			try {
				groupDb.getGroupByName(groupname, this.dbSession);
				fail("Should throw an exception");
			} catch (final RuntimeException ignored) {
				// ignore
			}
		}

		assertNull(groupDb.getGroupByName(ParamUtils.NOGROUP_NAME, this.dbSession));
	}

	/**
	 * tests getGroupMembers
	 */
	@Test
	public void getGroupMemberships() {
		// PUBLIC group
		// every user can see the members of this group
		for (final String username : new String[] { "testuser1", "testuser2", "testuser3" }) {
			final Group publicGroup = groupDb.getGroupMembers(username, "testgroup1", this.dbSession);
			assertEquals(3, publicGroup.getMemberships().size());
		}

		// HIDDEN group
		// "testuser1", a member of "testgroup2", can't see other members
		// "testuser2", not a member of "testgroup2", can't see members too
		for (final String username : new String[] { "testuser1", "testuser2" }) {
			final Group hiddenGroup = groupDb.getGroupMembers(username, "testgroup2", this.dbSession);
			assertEquals(0, hiddenGroup.getMemberships().size());
		}

		// MEMBER (only) group
		// "testuser1", a member of "testgroup3", can see all members (including user testgroup3)
		Group memberOnlyGroup = groupDb.getGroupMembers("testuser1", "testgroup3", this.dbSession);
		assertEquals(2, memberOnlyGroup.getMemberships().size());
		// "testuser2" and "testuser3" aren't members of "testgroup3" and can't
		// see the members
		for (final String username : new String[] { "testuser2", "testuser3" }) {
			memberOnlyGroup = groupDb.getGroupMembers(username, "testgroup3", this.dbSession);
			assertEquals(0, memberOnlyGroup.getMemberships().size());
		}

		// INVALID group
		final Group invalidGroup = groupDb.getGroupMembers(ParamUtils.NOUSER_NAME, ParamUtils.NOGROUP_NAME, this.dbSession);
		assertEquals(INVALID_GROUP_ID, invalidGroup.getGroupId());
	}

	/**
	 * tests getGroupsForUser
	 */
	@Test
	public void getGroupsForUser() {
		// testuser1 is a member of testgroup(1|2|3|4)
		List<Group> groups = groupDb.getGroupsForUser("testuser1", this.dbSession);
		assertEquals(7, groups.size());
		assertStandardGroups(groups);

		// testuser2 is a member of testgroup(1|4)
		groups = groupDb.getGroupsForUser("testuser2", this.dbSession);
		assertEquals(5, groups.size());
		assertStandardGroups(groups);

		// every user has got at least three groups: public, private and friends
		for (final String username : new String[] { "testuser3", ParamUtils.NOUSER_NAME }) {
			groups = groupDb.getGroupsForUser(username, this.dbSession);
			assertEquals(3, groups.size());
			assertStandardGroups(groups);
		}

		// without special groups
		groups = groupDb.getGroupsForUser("testuser1", true, this.dbSession);
		assertEquals(4, groups.size());
	}

	/**
	 * tests storeGroup
	 * 
	 * @see GroupDatabaseManagerTest#deleteGroup()
	 */
	@Test
	public void createGroup() {
		final Group newGroup = new Group();
		newGroup.setName("testgroupnew".toUpperCase());
		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName("testrequestuser1");
		groupRequest.setReason("testrequestreason1");
		newGroup.setGroupRequest(groupRequest);
		
		groupDb.createGroup(newGroup, this.dbSession);
		groupDb.activateGroup(newGroup.getName(), this.dbSession);
		final Group newGroupTest = groupDb.getGroupMembers("testgroupnew", "testgroupnew", this.dbSession);
		assertEquals("testgroupnew", newGroupTest.getName());
		assertEquals(2, newGroupTest.getMemberships().size());

		// check that the group and all members are gone
		groupDb.deleteGroup("testgroupnew", this.dbSession);
		assertNull(groupDb.getGroupByName("testgroupnew", this.dbSession));
		for (final User user : newGroupTest.getUsers()) {
			final List<Group> userGroups = groupDb.getGroupsForUser(user.getName(), this.dbSession);
			for (final Group userGroup : userGroups) {
				if ("testuser1".equals(userGroup.getName())) {
					fail("User ('" + user.getName() + "') shouldn't be a member of this group ('" + userGroup.getName() + "') anymore");
				}
			}
		}
		// since update isn't implemented we test both cases because the method
		// should throw an exception anyway
		// test invalid and existing group names
		for (final String groupname : new String[] { "", " ", null, "testgroup1", }) {//ParamUtils.NOUSER_NAME, ParamUtils.NOGROUP_NAME }) {
			try {
				final Group groupToCreate = new Group();
				groupToCreate.setName(groupname);
				groupDb.createGroup(groupToCreate, this.dbSession);
				fail("Should throw an exception: groupname '" + groupname + "'");
			} catch (final RuntimeException ignored) {
				// ignore
			}
		}
		
		// check for reserved names
		for (final String groupname : new String[] { GroupUtils.FRIENDS_GROUP_NAME, GroupUtils.PRIVATE_GROUP_NAME, GroupUtils.PUBLIC_GROUP_NAME }) {
			try {
				final Group groupToCreate = new Group();
				groupToCreate.setName(groupname);
				groupDb.createGroup(groupToCreate, this.dbSession);
				fail("Should throw an exception: groupname '" + groupname + "'");
			} catch (final RuntimeException ignored) {
				// ignore
			}
		}
	}

	/**
	 * tests deleteGroup
	 * 
	 * @see GroupDatabaseManagerTest#storeGroup()
	 */
	@Test
	public void deleteGroup() {
		// note: deleteGroup tested by createGroup

		// can't delete a group that doesn't exist
		try {
			groupDb.deleteGroup(ParamUtils.NOGROUP_NAME, this.dbSession);
			fail("Should throw an exception");
		} catch (final RuntimeException ignored) {
			// ignore
		}
	}

	/**
	 * tests addUserToGroup
	 * 
	 * @see GroupDatabaseManagerTest#addUserToGroup()
	 */
	@Test
	public void addUserToGroup() {
		// adds and then removes a user and checks whether the groupsize grows
		// and shrinks accordingly
		String testGroup = "testgroup1";
		Group group = groupDb.getGroupMembers("testuser3", testGroup, this.dbSession);
		assertEquals(3, group.getMemberships().size());
		// add user
		final String userToAdd = "testuser3";
		GroupMembership ms = new GroupMembership(new User(userToAdd), GroupRole.INVITED, false);
		groupDb.addPendingMembership(testGroup, ms, dbSession);
		groupDb.addUserToGroup(testGroup, userToAdd, GroupRole.USER, this.dbSession);
		group = groupDb.getGroupMembers(userToAdd, testGroup, this.dbSession);
		assertEquals(3 + 1, group.getMemberships().size());
		
		groupDb.removeUserFromGroup(testGroup, userToAdd, this.dbSession);
		group = groupDb.getGroupMembers(userToAdd, testGroup, this.dbSession);
		assertEquals(3, group.getMemberships().size());
		
		for (final String groupname : new String[] { "", " ", null, ParamUtils.NOGROUP_NAME }) {
			for (final String username : new String[] { "", " ", null, "testuser1", ParamUtils.NOUSER_NAME }) {
				try {
					groupDb.addUserToGroup(groupname, username, GroupRole.USER, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ignored) {
					// ignore
				}
			}
		}

		// can't add user to a group he's already a member of
		for (final String username : new String[] { "testuser1", "testuser2" }) {
			try {
				groupDb.addUserToGroup(testGroup.toUpperCase(), username, GroupRole.USER, this.dbSession);
				fail("Should throw an exception");
			} catch (final RuntimeException ignored) {
				// ignore
			}
		}
		
		// spammers can't be members of groups!
		try {
			groupDb.addUserToGroup(testGroup, "testspammer", GroupRole.USER, this.dbSession);
			fail("Should throw an exception");
		} catch (final RuntimeException ignored) {
			// ignore
		}
	}

	/**
	 * tests removeUserFromGroup
	 * 
	 * @see GroupDatabaseManagerTest#removeUserFromGroup()
	 */
	@Test
	public void removeUserFromGroup() {
		// note: remove user is tested by addUserToGroup
		// can't remove user from a group he isn't a member of
		for (final String groupname : new String[] { "testgroup2", "testgroup3" }) {
			for (final String username : new String[] { "testuser2", "testuser3" }) {
				try {
					groupDb.removeUserFromGroup(groupname, username, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ignored) {
				}
			}
		}
	}

	/**
	 * tests getGroupsByContentId
	 */
	@Test
	public void getGroupsByContentId() {
		final List<Group> groups = groupDb.getGroupsForContentId(8, this.dbSession);
		assertEquals(1, groups.size());
		assertEquals("testgroup2", groups.get(0).getName());
		assertEquals(ParamUtils.TESTGROUP2, groups.get(0).getGroupId());
	}

	/**
	 * tests getGroupIdsForUser
	 */
	@Test
	public void getGroupIdsForUser() {
		// testuser1 is a member of 4 groups
		assertEquals(4, groupDb.getGroupIdsForUser("testuser1", this.dbSession).size());
		// testuser2 a member of 2 group
		assertEquals(2, groupDb.getGroupIdsForUser("testuser2", this.dbSession).size());

		// invalid users or testuser3 arent't members of any group
		for (final String userName : new String[] { "", " ", null, "testuser3", ParamUtils.NOUSER_NAME }) {
			assertEquals(0, groupDb.getGroupIdsForUser(userName, this.dbSession).size());
		}
	}
	
	/**
	 * tests updateUserSharedDocuments
	 */
	@Test
	public void updateUserSharedDocuments() {
		final Group g1 = new Group(TESTGROUP1_ID);
		final GroupMembership gm = new GroupMembership();
		gm.setUserSharedDocuments(true);
		gm.setUser(new User("testuser1"));
		groupDb.updateUserSharedDocuments(g1, gm, this.dbSession);
		
		List<Group> groups = groupDb.getGroupsForUser("testuser1", this.dbSession);
		Group group = null;
		for (Group g : groups) {
			if (g.getGroupId() == TESTGROUP1_ID) {
				group = g;
				break;
			}
		}
		assertNotNull("no group found", group);
		assertTrue(group.getGroupMembershipForUser("testuser1").isUserSharedDocuments());
	}


	/**
	 * tests activateGroup
	 */
	@Test
	public void activateGroup() {
		final Group group = groupDb.getPendingGroups(0, Integer.MAX_VALUE, this.dbSession).get(0);
		groupDb.activateGroup(group.getName(), this.dbSession);
		final Group testgroup = groupDb.getGroupByName(group.getName(), this.dbSession);
		assertEquals("testpendinggroup1", testgroup.getName());
	}
	
	/**
	 * tests getGroupIdByGroupName and getGroupIdByGroupNameAndUserName
	 */
	@Test
	public void getGroupIdByGroupNameAndUserName() {
		// group exists
		assertThat(groupDb.getGroupIdByGroupNameAndUserName("testgroup1", null, this.dbSession), equalTo(ParamUtils.TESTGROUP1));
		assertThat(groupDb.getGroupIdByGroupNameAndUserName("testgroup1", "testuser1", this.dbSession), equalTo(ParamUtils.TESTGROUP1));
		assertThat(groupDb.getGroupIdByGroupName("testgroup1", this.dbSession), equalTo(ParamUtils.TESTGROUP1));
		// "testuser3" isn't a member of "testgroup1" and can't get the id
		assertThat(groupDb.getGroupIdByGroupNameAndUserName("testgroup1", "testuser3", this.dbSession), equalTo(INVALID_GROUP_ID));

		// group doesn't exist
		assertThat(groupDb.getGroupIdByGroupNameAndUserName(ParamUtils.NOGROUP_NAME, null, this.dbSession), equalTo(INVALID_GROUP_ID));
		assertThat(groupDb.getGroupIdByGroupName(ParamUtils.NOGROUP_NAME, this.dbSession), equalTo(INVALID_GROUP_ID));

		// groupname is null
		for (final String groupname : new String[] { "", " ", null }) {
			for (final String username : new String[] { "", " ", null }) {
				try {
					groupDb.getGroupIdByGroupNameAndUserName(groupname, username, this.dbSession);
					fail("expected exception");
				} catch (final Exception ignored) {
				}
			}

			try {
				groupDb.getGroupIdByGroupName(groupname, this.dbSession);
				fail("expected exception");
			} catch (final Exception ignored) {
			}
		}
	}
}
