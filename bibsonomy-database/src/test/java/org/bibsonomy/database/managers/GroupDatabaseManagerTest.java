/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.database.managers.fixtures.ExtendedGroupFixture;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.bibsonomy.testutil.TestDatabaseManager;
import org.bibsonomy.util.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Tests related to groups.
 *
 * @author Jens Illig
 * @author Christian Schenk
 */
public class GroupDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static GroupDatabaseManager groupDb = testDatabaseContext.getBean(GroupDatabaseManager.class);
	private static TestDatabaseManager testDb = new TestDatabaseManager();

	//FIXME (ada) use fixtures for all group tests.
	private ExtendedGroupFixture rootGroupFixture;
	private ExtendedGroupFixture childGroup1Fixture;
	private ExtendedGroupFixture childGroup2Fixture;
	private ExtendedGroupFixture childGroup3Depth2Fixture;


	@Before
	public void setup() {
		this.rootGroupFixture = new ExtendedGroupFixture(9, "rootgroup", Privlevel.MEMBERS, true,
				true, null, false, "Root Group", "http://www.bibsonomy.org/group/rootgroup");
		this.childGroup1Fixture = new ExtendedGroupFixture(10, "childgroup1", Privlevel.MEMBERS, true,
				true, null, false,"Child Group 1", "http://www.bibsonomy.org/group/childgroup1");
		this.childGroup2Fixture = new ExtendedGroupFixture(11, "childgroup2", Privlevel.MEMBERS, true,
				true, null, false, "Child Group 2", "http://www.bibsonomy.org/group/childgroup2");
		this.childGroup3Depth2Fixture = new ExtendedGroupFixture(12,  "childgroup3depth2", Privlevel.MEMBERS, true,
				true, null, false, "Child Group 3 Depth 2", "http://www.bibsonomy.org/group/childgroup3depth2");
	}

	/**
	 * Checks whether the list of groups contains at least the three standard
	 * groups: "public", "private" and "friends"
	 */
	private static void assertStandardGroups(final List<Group> groups) {
		final Set<Integer> found = new HashSet<>();
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
		assertEquals(8, allGroups.size());

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
	public void testGetGroupByName() {
		final Group testgroup1 = groupDb.getGroupByName("testgroup1", this.dbSession);
		assertEquals("testgroup1", testgroup1.getName());
		assertEquals(ParamUtils.TESTGROUP1, testgroup1.getGroupId());
		assertEquals("Test Group 1", testgroup1.getRealname());
		assertEquals("http://www.bibsonomy.org/group/testgroup1", testgroup1.getHomepage().toString());
		assertEquals(true, testgroup1.isSharedDocuments());

		assertGroupContainsMembers(testgroup1, Sets.asSet("testuser1", "testuser2", "testuser4", "testgroup1"));

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
	 * @param group
	 * @param members
	 */
	private static void assertGroupContainsMembers(final Group group, final Set<String> members) {
		final Set<String> actualMembers = new HashSet<>();
		for (final GroupMembership membership : group.getMemberships()) {
			actualMembers.add(membership.getUser().getName());
		}

		assertEquals(members, actualMembers);
	}

	/**
	 * tests getGroup
	 */
	@Test
	public void getGroupMemberships() {
		final String testgroup1Name = "testgroup1";
		final String testgroup2Name = "testgroup2";
		final String testgroup3Name = "testgroup3";

		final String testuser1Name = "testuser1";
		final String testuser2Name = "testuser2";
		final String testuser3Name = "testuser3";

		// PUBLIC group
		// every user can see the members of this group
		final int numberofMembershipsTestGroup1 = 4;
		this.testNumberOfMemberships(testuser1Name, testgroup1Name, numberofMembershipsTestGroup1);
		this.testNumberOfMemberships(testuser2Name, testgroup1Name, numberofMembershipsTestGroup1);
		this.testNumberOfMemberships(testuser3Name, testgroup1Name, numberofMembershipsTestGroup1);

		// HIDDEN group
		// "testuser1", a member of "testgroup2", can't see other members, but herself
		// "testuser2", not a member of "testgroup2", can't see members too
		final Group hiddenGroup = groupDb.getGroup(testuser1Name, testgroup2Name, false, false, this.dbSession);
		assertEquals(1, hiddenGroup.getMemberships().size());
		assertThat(hiddenGroup.getMemberships().get(0).getUser().getName(), equalTo(testuser1Name));

		this.testNumberOfMemberships(testuser2Name, testgroup2Name, 0);

		// MEMBER (only) group
		// "testuser1", a member of "testgroup3", can see all members (including
		// user testgroup3)
		this.testNumberOfMemberships(testuser1Name, testgroup3Name, 2);
		// "testuser2" and "testuser3" aren't members of "testgroup3" and can't
		// see the members
		for (final String username : new String[] {testuser2Name, testuser3Name}) {
			this.testNumberOfMemberships(username, testgroup3Name, 0);
		}

		// INVALID group
		final Group invalidGroup = groupDb.getGroup(ParamUtils.NOUSER_NAME, ParamUtils.NOGROUP_NAME, false, false, this.dbSession);
		assertEquals(INVALID_GROUP_ID, invalidGroup.getGroupId());
	}

	private void testNumberOfMemberships(final String username, final String groupName, int expectedNumberOfMemberships) {
		final Group group = groupDb.getGroup(username, groupName, false, false, this.dbSession);
		assertThat(group.getMemberships().size(), is(expectedNumberOfMemberships));
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

		groups = groupDb.getGroupsForUser("rootgroup", true, true, this.dbSession);
		assertThat(groups.size(), is(1));

		groups = groupDb.getGroupsForUser("testuser4", true, true, this.dbSession);
		assertThat(groups.size(), is(3));

		groups = groupDb.getGroupsForUser("testuser4", true, false, this.dbSession);
		assertThat(groups.size(), is(3));
	}

	/**
	 * tests storeGroup
	 *
	 * @see GroupDatabaseManagerTest#deleteGroup()
	 */
	@Test
	public void createGroup() {
		final Group newGroup = new Group();
		final String groupName = "testgroupnew";
		newGroup.setName(groupName.toUpperCase());
		final GroupRequest groupRequest = new GroupRequest();
		final String requestedUser = "testrequestuser1";
		groupRequest.setUserName(requestedUser);
		groupRequest.setReason("testrequestreason1");
		newGroup.setGroupRequest(groupRequest);
		newGroup.setOrganization(true);

		groupDb.createPendingGroup(newGroup, this.dbSession);
		groupDb.activateGroup(newGroup.getName(), USER_TESTUSER_1, this.dbSession);
		final Group newGroupTest = groupDb.getGroup(groupName, groupName, false, false, this.dbSession);
		assertEquals(groupName, newGroupTest.getName());
		assertGroupContainsMembers(newGroupTest, Sets.asSet(groupName, requestedUser));

		// check that organization is correctly set
		assertThat(newGroupTest.isOrganization(), equalTo(true));

		// check that the group and all members are gone
		groupDb.deleteGroup(groupName, false, USER_TESTUSER_1, this.dbSession);
		assertNull(groupDb.getGroupByName(groupName, this.dbSession));

		final List<GroupMembership> newGroupMemberships = newGroupTest.getMemberships();
		assertEquals(2, newGroupMemberships.size());
		for (final GroupMembership membership : newGroupTest.getMemberships()) {
			final User user = membership.getUser();
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
		for (final String groupname : new String[] { "", " ", null, "testgroup1", }) {
			try {
				final Group groupToCreate = new Group();
				groupToCreate.setName(groupname);
				groupDb.createPendingGroup(groupToCreate, this.dbSession);
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
				groupDb.createPendingGroup(groupToCreate, this.dbSession);
				fail("Should throw an exception: groupname '" + groupname + "'");
			} catch (final RuntimeException ignored) {
				// ignore
			}
		}
	}

	/**
	 * tests deleteGroup
	 *
	 * @see GroupDatabaseManagerTest#createGroup()
	 */
	@Test
	public void deleteGroup() {
		// note: deleteGroup tested by createGroup

		// can't delete a group that doesn't exist
		try {
			groupDb.deleteGroup(ParamUtils.NOGROUP_NAME, false, USER_TESTUSER_1, this.dbSession);
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
		final String testGroup = "testgroup1";
		Group group = groupDb.getGroup("testuser3", testGroup, false, false, this.dbSession);
		final int currentMembershipSize = 4;
		assertThat(group.getMemberships().size(), is(currentMembershipSize));
		// add user
		final String userToAdd = "testuser3";
		final boolean userSharedDocuments = false;

		groupDb.addPendingMembership(testGroup, userToAdd, userSharedDocuments, GroupRole.INVITED, USER_TESTUSER_1, this.dbSession);
		groupDb.addUserToGroup(testGroup, userToAdd, userSharedDocuments, GroupRole.USER, USER_TESTUSER_1, this.dbSession);
		group = groupDb.getGroup(userToAdd, testGroup, false, false, this.dbSession);
		assertThat(group.getMemberships().size(), is(currentMembershipSize + 1));

		// test userSharedDocuments
		for (final GroupMembership ms : group.getMemberships()) {
			if (ms.getUser().getName().equals(userToAdd)) {
				assertEquals(ms.isUserSharedDocuments(), userSharedDocuments);
			}
		}

		groupDb.removeUserFromGroup(testGroup, userToAdd, false, USER_TESTUSER_1, this.dbSession);
		group = groupDb.getGroup(userToAdd, testGroup, false, false, this.dbSession);
		assertThat(group.getMemberships().size(), is(currentMembershipSize));

		for (final String groupname : new String[] { "", " ", null, ParamUtils.NOGROUP_NAME }) {
			for (final String username : new String[] { "", " ", null, "testuser1", ParamUtils.NOUSER_NAME }) {
				try {
					groupDb.addUserToGroup(groupname, username, userSharedDocuments, GroupRole.USER, USER_TESTUSER_1, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ignored) {
					// ignore
				}
			}
		}

		// can't add user to a group he's already a member of
		for (final String username : new String[] { "testuser1", "testuser2" }) {
			try {
				groupDb.addUserToGroup(testGroup.toUpperCase(), username, userSharedDocuments, GroupRole.USER, USER_TESTUSER_1, this.dbSession);
				fail("Should throw an exception");
			} catch (final RuntimeException ignored) {
				// ignore
			}
		}

		// spammers can't be members of groups!
		try {
			groupDb.addUserToGroup(testGroup, "testspammer", userSharedDocuments, GroupRole.USER, USER_TESTUSER_1, this.dbSession);
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
					groupDb.removeUserFromGroup(groupname, username, false, USER_TESTUSER_1, this.dbSession);
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
		assertEquals(4, groupDb.getGroupIdsForUser("testuser1", false, this.dbSession).size());
		// testuser2 a member of 2 group
		assertEquals(2, groupDb.getGroupIdsForUser("testuser2", false, this.dbSession).size());

		// invalid users or testuser3 arent't members of any group
		for (final String userName : new String[] { "", " ", null, "testuser3", ParamUtils.NOUSER_NAME }) {
			assertEquals(0, groupDb.getGroupIdsForUser(userName, false, this.dbSession).size());
		}

		List<Integer> groupIds = groupDb.getGroupIdsForUser("rootgroup", true, this.dbSession);
		assertThat(groupIds.size(), equalTo(4));
		assertThat(groupIds, hasItems(9, 10, 11, 12));

		groupIds = groupDb.getGroupIdsForUser("testuser4", true, this.dbSession);
		assertThat(groupIds, hasItems(9, 10, 11, 12));
		assertThat(groupIds.size(), equalTo(5));

	}

	/**
	 * tests updateUserSharedDocuments
	 */
	@Test
	public void updateUserSharedDocuments() {
		final GroupMembership gm = new GroupMembership();
		gm.setUserSharedDocuments(true);
		gm.setUser(new User("testuser1"));
		groupDb.updateUserSharedDocuments(new Group("testgroup1"), gm, USER_TESTUSER_1, this.dbSession);

		final List<Group> groups = groupDb.getGroupsForUser("testuser1", this.dbSession);
		Group group = null;
		for (final Group g : groups) {
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
		groupDb.activateGroup(group.getName(), USER_TESTUSER_1, this.dbSession);
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

	@Test
	public void testUpdateGroupLevelPermissions() throws Exception {
		// setting: testGroup3 already exists and testUser1 is a member thereof,
		// while testUser2 is not.
		final String testGroupName = "testgroup3";
		final String testUserName = "testuser1";

		final Group testGroup3 = new Group(testGroupName);
		Group foundTestGroup3 = this.getGroupOfUser(testUserName, testGroupName);
		assertThat(foundTestGroup3.getGroupLevelPermissions(), empty());

		testGroup3.addGroupLevelPermission(GroupLevelPermission.COMMUNITY_POST_INSPECTION);
		groupDb.updateGroupLevelPermissions(testUserName, testGroup3, this.dbSession);
		foundTestGroup3 = this.getGroupOfUser(testUserName, testGroupName);

		assertThat(foundTestGroup3.getGroupLevelPermissions(), equalTo(Sets.asSet(GroupLevelPermission.COMMUNITY_POST_INSPECTION)));

		testGroup3.getGroupLevelPermissions().clear();
		groupDb.updateGroupLevelPermissions(testUserName, testGroup3, this.dbSession);
		foundTestGroup3 = this.getGroupOfUser(testUserName, testGroupName);

	}

	private Group getGroupOfUser(final String userName, final String groupName) {
		final List<Group> groupsForTestUser1 = groupDb.getGroupsForUser(userName, this.dbSession);
		Group foundTestGroup3 = null;
		for (final Group group : groupsForTestUser1) {
			if (groupName.equals(group.getName())) {
				foundTestGroup3 = group;
				break;
			}
		}
		return foundTestGroup3;
	}

	@Test
	public void testGetPendingGroup() throws Exception {
		final Group group = groupDb.getPendingGroup("testpendinggroup1", "testrequestuser1", this.dbSession);
		assertNotNull(group);

		final Group groupForOtherUser = groupDb.getPendingGroup("testpendinggroup1", "testrequestuser2", this.dbSession);
		assertNull(groupForOtherUser);
	}


	/**
	 * Tests retrieval of parent groups if set.
	 */
	@Test
	public void testGetGroupWithParent() {
		final String childName = "childgroup1";
		final String parentName = "rootgroup";

		final int expectedParentGroupid = 9;
		final boolean expectedParentIsSharedDocuments = true;

		// retrieval of groups with a parent
		Group child = groupDb.getGroup("testuser1", childName, false, false, this.dbSession);

		Group parent = child.getParent();

		assertGroupHasBasicProperties(parent, parentName, expectedParentGroupid, expectedParentIsSharedDocuments);
		assertThat(parent.getParent(), nullValue()); // (ada) we only retrieve the immediate parent.

		// correct retrieval of groups without parents
		Group groupWithoutParent = groupDb.getGroup("testuser1", "testgroup1", false, false, this.dbSession);
		assertThat(groupWithoutParent.getParent(), nullValue());
	}


	private void assertGroupHasBasicProperties(Group uut, String name, int groupId, boolean isSharedDocument) {
		assertThat(uut.getName(), equalTo(name));
		assertThat(uut.getGroupId(), equalTo(groupId));
		assertThat(uut.isSharedDocuments(), equalTo(isSharedDocument));
	}


	/**
	 * Tests group request, activation and retrieval with a parent set.
	 */
	@Test
	public void testCreateGroupWithParent() {

		final int expectedParentGroupid = 9;
		final boolean expectedParentIsSharedDocuments = true;
		final String parentGroupName = "rootgroup";

		Group parentGroup = groupDb.getGroup(parentGroupName, parentGroupName, false, false, this.dbSession);

		final Group newGroup = new Group();
		final String groupName = "newchildgroup";

		newGroup.setName(groupName.toUpperCase());
		newGroup.setParent(parentGroup);

		final GroupRequest groupRequest = new GroupRequest();
		final String requestedUser = "testrequestuser1";

		groupRequest.setUserName(requestedUser);
		groupRequest.setReason("testrequestreason1");
		newGroup.setGroupRequest(groupRequest);

		groupDb.createPendingGroup(newGroup, this.dbSession);
		groupDb.activateGroup(newGroup.getName(), USER_TESTUSER_1, this.dbSession);

		final Group newGroupTest = groupDb.getGroup(groupName, groupName, false, false, this.dbSession);
		assertEquals(groupName, newGroupTest.getName());
		assertGroupContainsMembers(newGroupTest, Sets.asSet(groupName, requestedUser));
		assertGroupHasBasicProperties(newGroupTest.getParent(), parentGroupName, expectedParentGroupid, expectedParentIsSharedDocuments);

		List<Integer> parentIds = testDb.getAllParents(newGroupTest.getGroupId());
		assertThat(parentIds.size(), equalTo(1));
		assertThat(parentIds, hasItems(9));

		// check that the group and all members are gone
		groupDb.deleteGroup(groupName, false, USER_TESTUSER_1, this.dbSession);
		assertNull(groupDb.getGroupByName(groupName, this.dbSession));
	}

	@Test
	public void testCreateGroupWithParentDeep() {
		final String parentGroupName = this.childGroup3Depth2Fixture.getName();
		Group parentGroup = groupDb.getGroup(parentGroupName, parentGroupName, false, false, this.dbSession);

		final Group newGroup = new Group();
		final String groupName = "newchildgroup";

		newGroup.setName(groupName.toUpperCase());
		newGroup.setParent(parentGroup);

		final GroupRequest groupRequest = new GroupRequest();
		final String requestedUser = "testrequestuser1";

		groupRequest.setUserName(requestedUser);
		groupRequest.setReason("testrequestreason1");
		newGroup.setGroupRequest(groupRequest);

		groupDb.createPendingGroup(newGroup, this.dbSession);
		groupDb.activateGroup(newGroup.getName(), USER_TESTUSER_1, this.dbSession);

		final Group newGroupTest = groupDb.getGroup(groupName, groupName, false, false, this.dbSession);

		assertEquals(groupName, newGroupTest.getName());
		assertGroupContainsMembers(newGroupTest, Sets.asSet(groupName, requestedUser));
		assertGroupHasBasicProperties(newGroupTest.getParent(), parentGroupName, this.childGroup3Depth2Fixture.getGroupId(), this.childGroup3Depth2Fixture.isSharedDocuments());

		List<Integer> parentIds = testDb.getAllParents(newGroupTest.getGroupId());
		assertThat(parentIds.size(), equalTo(3));
		assertThat(parentIds, hasItems(9, 10, 12));

		// check that the group and all members are gone
		groupDb.deleteGroup(groupName, false, USER_TESTUSER_1, this.dbSession);
		assertNull(groupDb.getGroupByName(groupName, this.dbSession));
	}

	/**
	 * Tests group request, activation and retrieval with a parent set.
	 */
	@Test
	public void testCreateGroupWithParentIfParentIdNotSet() {

		final int expectedParentGroupid = 9;
		final boolean expectedParentIsSharedDocuments = true;
		final String parentGroupName = "rootgroup";

		Group parentGroup = groupDb.getGroup(parentGroupName, parentGroupName, false, false, this.dbSession);

		// explicitly set the group id to the default value to checkt whether the parent group is correctly created
		// if the parent id is not set
		parentGroup.setGroupId(GroupID.INVALID.getId());

		final Group newGroup = new Group();
		final String groupName = "newchildgroup";

		newGroup.setName(groupName.toUpperCase());
		newGroup.setParent(parentGroup);

		final GroupRequest groupRequest = new GroupRequest();
		final String requestedUser = "testrequestuser1";

		groupRequest.setUserName(requestedUser);
		groupRequest.setReason("testrequestreason1");
		newGroup.setGroupRequest(groupRequest);

		groupDb.createPendingGroup(newGroup, this.dbSession);
		groupDb.activateGroup(newGroup.getName(), USER_TESTUSER_1, this.dbSession);

		final Group newGroupTest = groupDb.getGroup(groupName, groupName, false, false, this.dbSession);
		assertEquals(groupName, newGroupTest.getName());
		assertGroupContainsMembers(newGroupTest, Sets.asSet(groupName, requestedUser));
		assertGroupHasBasicProperties(newGroupTest.getParent(), parentGroupName, expectedParentGroupid, expectedParentIsSharedDocuments);

		// check that the group and all members are gone
		groupDb.deleteGroup(groupName, false, USER_TESTUSER_1, this.dbSession);
		assertNull(groupDb.getGroupByName(groupName, this.dbSession));
	}


	/**
	 * Compares a {@link Group} instance with a fixture.
	 *
	 * @param group a group instance.
	 * @param expected the expected fixture.
	 */
	private void assertGroupIsAsExpected(Group group, ExtendedGroupFixture expected) {

		assertThat(group.getName(), equalTo(expected.getName()));
		assertThat(group.getGroupId(), equalTo(expected.getGroupId()));
		assertThat(group.getRealname(), equalTo(expected.getRealName()));
		assertThat(group.getHomepage().toString(), equalTo(expected.getHomepage()));
		assertThat(group.getDescription(), equalTo(expected.getDescription()));
		assertThat(group.isSharedDocuments(), equalTo(expected.isSharedDocuments()));
		assertThat(group.isAllowJoin(), equalTo(expected.isAllowjoin()));
		assertThat(group.getPrivlevel(), equalTo(expected.getPrivlevel()));
	}

	/**
	 * Searches the list of groups for the given fixture and if found compares the two.
	 *
	 * @param expected the expected fixture.
	 * @param groups a list of groups to check.
	 */
	private void assertContainsGroup(ExtendedGroupFixture expected, List<Group> groups) {
		Optional<Group> maybeGroup = groups.stream().filter((g) -> expected.getName().equals(g.getName())).findFirst();

		if (!maybeGroup.isPresent()) {
			fail("Could not find the expected group in the list of groups.");
		}

		Group group = maybeGroup.get();
		assertGroupIsAsExpected(group, expected);
	}


	/**
	 * Tests if querying subgroups works correctly.
	 */
	@Test
	public void testGetSubgroupsFor() {
		List<ExtendedGroupFixture> expectedSubgroups = Arrays.asList(this.childGroup1Fixture, this.childGroup2Fixture);
		List<Group> subgroups = groupDb.getSubgroupsFor(this.rootGroupFixture.getGroupId(), this.dbSession);

		assertThat(subgroups.size(), equalTo(2));

		for (ExtendedGroupFixture expectedGroup: expectedSubgroups) {
			assertContainsGroup(expectedGroup, subgroups);
		}

		for(Group group: subgroups) {
			assertGroupIsAsExpected(group.getParent(), rootGroupFixture);
		}
	}

	@Test
	public void testGetGroupLoadsSubgroups() {
		List<ExtendedGroupFixture> expectedSubgroups = Arrays.asList(this.childGroup1Fixture, this.childGroup2Fixture);

		Group root = groupDb.getGroup("rootgroup", "rootgroup", false, false, this.dbSession);

		assertThat(root.getSubgroups().size(), equalTo(2));

		for (ExtendedGroupFixture expectedGroup: expectedSubgroups) {
			assertContainsGroup(expectedGroup, root.getSubgroups());
		}

		for(Group group: root.getSubgroups()) {
			assertGroupIsAsExpected(group.getParent(), rootGroupFixture);
		}
	}

	@Test
	public void testGetParentGroupsWhereUserIsMember() {
		String groupName = this.childGroup1Fixture.getName();
		String userName = this.rootGroupFixture.getName();
		Integer parentId = this.rootGroupFixture.getGroupId();

		List<Integer> results = groupDb.getParentGroupsWhereUserIsMember(groupName, userName, dbSession);
		assertThat(results.size(), equalTo(1));
		assertThat(results, hasItems(parentId));

		groupName = this.childGroup3Depth2Fixture.getName();
		results = groupDb.getParentGroupsWhereUserIsMember(groupName, userName, dbSession);
		assertThat(results.size(), equalTo(1));
		assertThat(results, hasItems(parentId));

		userName = this.childGroup1Fixture.getName();
		parentId = this.childGroup1Fixture.getGroupId();

		results = groupDb.getParentGroupsWhereUserIsMember(groupName, userName, dbSession);
		assertThat(results.size(), equalTo(1));
		assertThat(results, hasItems(parentId));
	}

	private void queryGroupByInternalIdAndValidateWithGroupname(String externalId, String groupName) {
		Group group = groupDb.getGroupByInternalId(externalId, this.dbSession);
		assertThat(group.getName(), equalTo(groupName));
	}

	@Test
	public void testGetGroupByExternalId() {
		queryGroupByInternalIdAndValidateWithGroupname("extid1", "testgroup1");
		queryGroupByInternalIdAndValidateWithGroupname("extid2", "testgroup2");
		queryGroupByInternalIdAndValidateWithGroupname("extid3", "testgroup3");
	}


	/**
	 * Tests whether the plugins log group and group membership information upon group deletion.
	 */
	@Test
	public void testDeletedGroupsAreLogged() {
		groupDb.deleteGroup("testgroup1", true, USER_TESTUSER_1, dbSession);

		List<Integer> loggedGroupIds = testDb.getLoggedGroupIds();

		assertThat(loggedGroupIds.size(), equalTo(1));
		assertThat(loggedGroupIds, contains(3));

		int numberOfLoggedUsers = testDb.getCountOfLoggedGroupMemberships();

		assertThat(numberOfLoggedUsers, equalTo(4));
	}

}
