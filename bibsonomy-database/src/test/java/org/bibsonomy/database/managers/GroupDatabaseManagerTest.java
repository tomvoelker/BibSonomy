package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to groups.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore // FIXME: sometimes the tests are executed in the wrong order
public class GroupDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * tests getAllGroups
	 */
	@Test
	public void getAllGroups() {
		final List<Group> allGroups = this.groupDb.getAllGroups(0, 100, this.dbSession);
		assertEquals(3, allGroups.size());

		for (final Group group : allGroups) {
			final User groupUser = group.getUsers().get(0);
			assertNotNull(groupUser);

			if (group.getName().startsWith("testgroup")) {
				final int groupNr = Integer.parseInt("" + group.getName().charAt(group.getName().length() - 1));
				assertEquals(2 + groupNr, group.getGroupId());
				assertEquals("Test Group " + groupNr, groupUser.getRealname());
				assertEquals("http://www.bibsonomy.org/group/testgroup" + groupNr, groupUser.getHomepage().toString());
			}
		}

		// make sure that limit and offset work
		assertEquals(1, this.groupDb.getAllGroups(0, 1, this.dbSession).size());
		assertEquals(1, this.groupDb.getAllGroups(1, 2, this.dbSession).size());
		assertEquals(3, this.groupDb.getAllGroups(0, 3, this.dbSession).size());
	}

	/**
	 * tests getGroupByName
	 */
	@Test
	public void getGroupByName() {
		final Group testgroup1 = this.groupDb.getGroupByName("testgroup1", this.dbSession);
		assertEquals("testgroup1", testgroup1.getName());
		assertEquals(ParamUtils.TESTGROUP1, testgroup1.getGroupId());
		assertEquals("Test Group 1", testgroup1.getUsers().get(0).getRealname());
		assertEquals("http://www.bibsonomy.org/group/testgroup1", testgroup1.getUsers().get(0).getHomepage().toString());
		assertEquals(true, testgroup1.isSharedDocuments());

		final Group testgroup2 = this.groupDb.getGroupByName("testgroup2", this.dbSession);
		assertEquals(false, testgroup2.isSharedDocuments());
		final Group testgroup3 = this.groupDb.getGroupByName("testgroup3", this.dbSession);
		assertEquals(false, testgroup3.isSharedDocuments());

		for (final String groupname : new String[] { "", " ", null }) {
			try {
				this.groupDb.getGroupByName(groupname, this.dbSession);
				fail("Should throw an exception");
			} catch (final RuntimeException ignored) {
			}
		}

		assertNull(this.groupDb.getGroupByName(ParamUtils.NOGROUP_NAME, this.dbSession));
	}

	/**
	 * tests getGroupMembers
	 */
	@Test
	public void getGroupMembers() {
		// PUBLIC group
		// every user can see the members of this group
		for (final String username : new String[] { "testuser1", "testuser2", "testuser3" }) {
			final Group publicGroup = this.groupDb.getGroupMembers(username, "testgroup1", this.dbSession);
			assertEquals(2, publicGroup.getUsers().size());
		}

		// HIDDEN group
		// "testuser1", a member of "testgroup2", can't see other members
		// "testuser2", not a member of "testgroup2", can't see members too
		for (final String username : new String[] { "testuser1", "testuser2" }) {
			final Group hiddenGroup = this.groupDb.getGroupMembers(username, "testgroup2", this.dbSession);
			assertEquals(0, hiddenGroup.getUsers().size());
		}

		// MEMBER (only) group
		// "testuser1", a member of "testgroup3", can see all members
		Group memberOnlyGroup = this.groupDb.getGroupMembers("testuser1", "testgroup3", this.dbSession);
		assertEquals(1, memberOnlyGroup.getUsers().size());
		// "testuser2" and "testuser3" aren't members of "testgroup3" and can't
		// see the members
		for (final String username : new String[] { "testuser2", "testuser3" }) {
			memberOnlyGroup = this.groupDb.getGroupMembers(username, "testgroup3", this.dbSession);
			assertEquals(0, memberOnlyGroup.getUsers().size());
		}

		// INVALID group
		final Group invalidGroup = this.groupDb.getGroupMembers(ParamUtils.NOUSER_NAME, ParamUtils.NOGROUP_NAME, this.dbSession);
		assertEquals(GroupID.INVALID.getId(), invalidGroup.getGroupId());
	}

	/**
	 * tests getGroupsForUser
	 */
	@Test
	public void getGroupsForUser() {
		// testuser1 is a member of testgroup(1|2|3)
		List<Group> groups = this.groupDb.getGroupsForUser("testuser1", this.dbSession);
		assertEquals(6, groups.size());
		this.assertStandardGroups(groups);

		// testuser2 is a member of testgroup1
		groups = this.groupDb.getGroupsForUser("testuser2", this.dbSession);
		assertEquals(4, groups.size());
		this.assertStandardGroups(groups);

		// every user has got at least three groups: public, private and friends
		for (final String username : new String[] { "testuser3", ParamUtils.NOUSER_NAME }) {
			groups = this.groupDb.getGroupsForUser(username, this.dbSession);
			assertEquals(3, groups.size());
			this.assertStandardGroups(groups);
		}

		// withouth special groups
		groups = this.groupDb.getGroupsForUser("testuser1", true, this.dbSession);
		assertEquals(3, groups.size());
	}

	/**
	 * Checks whether the list of groups contains at least the three standard
	 * groups: "public", "private" and "friends"
	 */
	private void assertStandardGroups(final List<Group> groups) {
		final Set<Integer> found = new HashSet<Integer>();
		for (final Group group : groups) {
			found.add(group.getGroupId());
		}
		assertTrue(found.contains(GroupID.PUBLIC.getId()));
		assertTrue(found.contains(GroupID.PRIVATE.getId()));
		assertTrue(found.contains(GroupID.FRIENDS.getId()));
	}

	/**
	 * tests storeGroup
	 * 
	 * @see GroupDatabaseManagerTest#deleteGroup()
	 */
	@Test
	public void storeGroup() {
		final Group newGroup = new Group();
		newGroup.setName("testuser1");
		this.groupDb.createGroup(newGroup, this.dbSession);
		final Group newGroupTest = this.groupDb.getGroupByName("testuser1", this.dbSession);
		assertEquals("testuser1", newGroupTest.getName());
		assertEquals(1, newGroupTest.getUsers().size());
		assertEquals("Test User 1", newGroupTest.getUsers().get(0).getRealname());
		assertEquals("http://www.bibsonomy.org/user/testuser1", newGroupTest.getUsers().get(0).getHomepage().toString());

		// since update isn't implemented we test both cases because the method
		// should throw an exception anyway
		for (final boolean update : new boolean[] { true, false }) {
			// test invalid, existing and non-existent group names
			for (final String groupname : new String[] { "", " ", null, "testgroup1", ParamUtils.NOUSER_NAME, ParamUtils.NOGROUP_NAME }) {
				try {
					final Group group = new Group();
					group.setName(groupname);
					this.groupDb.createGroup(group, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ignored) {
				}
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
		final Group group = this.groupDb.getGroupMembers("testuser1", "testuser1", this.dbSession);
		assertEquals(1, group.getUsers().size());

		// check that the group and all members are gone
		this.groupDb.deleteGroup("testuser1", this.dbSession);
		assertNull(this.groupDb.getGroupByName("testuser1", this.dbSession));
		for (final User user : group.getUsers()) {
			final List<Group> userGroups = this.groupDb.getGroupsForUser(user.getName(), this.dbSession);
			for (final Group userGroup : userGroups) {
				if ("testuser1".equals(userGroup.getName())) {
					fail("User ('" + user.getName() + "') shouldn't be a member of this group ('" + userGroup.getName() + "') anymore");
				}
			}
		}

		// can't delete a group that doesn't exist
		try {
			this.groupDb.deleteGroup(ParamUtils.NOGROUP_NAME, this.dbSession);
			fail("Should throw an exception");
		} catch (final RuntimeException ignored) {
		}
	}

	/**
	 * tests addUserToGroup
	 * 
	 * @see GroupDatabaseManagerTest#removeUserFromGroup()
	 */
	@Test
	public void addUserToGroup() {
		// adds and then removes a user and checks whether the groupsize grows
		// and shrinks accordingly
		Group group = this.groupDb.getGroupMembers("testuser3", "testgroup1", this.dbSession);
		assertEquals(2, group.getUsers().size());
		// add user
		this.groupDb.addUserToGroup("testgroup1", "testuser3", this.dbSession);
		group = this.groupDb.getGroupMembers("testuser3", "testgroup1", this.dbSession);
		assertEquals(2 + 1, group.getUsers().size());

		for (final String groupname : new String[] { "", " ", null, ParamUtils.NOGROUP_NAME }) {
			for (final String username : new String[] { "", " ", null, "testuser1", ParamUtils.NOUSER_NAME }) {
				try {
					this.groupDb.addUserToGroup(groupname, username, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ignored) {
				}
			}
		}

		// can't add user to a group he's already a member of
		for (final String username : new String[] { "testuser1", "testuser2" }) {
			try {
				this.groupDb.addUserToGroup("testgroup1", username, this.dbSession);
				fail("Should throw an exception");
			} catch (final RuntimeException ignored) {
			}
		}
	}

	/**
	 * tests removeUserFromGroup
	 * 
	 * @see GroupDatabaseManagerTest#addUserToGroup()
	 */
	@Test
	public void removeUserFromGroup() {
		// remove user
		this.groupDb.removeUserFromGroup("testgroup1", "testuser3", this.dbSession);
		final Group group = this.groupDb.getGroupMembers("testuser3", "testgroup1", this.dbSession);
		assertEquals(2, group.getUsers().size());

		// can't remove user from a group he isn't a member of
		for (final String groupname : new String[] { "testgroup2", "testgroup3" }) {
			for (final String username : new String[] { "testuser2", "testuser3" }) {
				try {
					this.groupDb.removeUserFromGroup(groupname, username, this.dbSession);
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
		final List<Group> groups = this.groupDb.getGroupsForContentId(8, this.dbSession);
		assertEquals(1, groups.size());
		assertEquals("testgroup2", groups.get(0).getName());
		assertEquals(ParamUtils.TESTGROUP2, groups.get(0).getGroupId());
	}

	/**
	 * tests getGroupIdsForUser
	 */
	@Test
	public void getGroupIdsForUser() {
		// testuser1 is a member of 3 groups
		assertEquals(3, this.groupDb.getGroupIdsForUser("testuser1", this.dbSession).size());
		// testuser2 a member of 1 group
		assertEquals(1, this.groupDb.getGroupIdsForUser("testuser2", this.dbSession).size());

		// invalid users or testuser3 arent't members of any group
		for (final String userName : new String[] { "", " ", null, "testuser3", ParamUtils.NOUSER_NAME }) {
			assertEquals(0, this.groupDb.getGroupIdsForUser(userName, this.dbSession).size());
		}
	}

	/**
	 * tests getGroupIdByGroupName and getGroupIdByGroupNameAndUserName
	 */
	@Test
	public void getGroupIdByGroupNameAndUserName() {
		// group exists
		assertEquals(ParamUtils.TESTGROUP1, this.groupDb.getGroupIdByGroupNameAndUserName("testgroup1", null, this.dbSession));
		assertEquals(ParamUtils.TESTGROUP1, this.groupDb.getGroupIdByGroupNameAndUserName("testgroup1", "testuser1", this.dbSession));
		assertEquals(ParamUtils.TESTGROUP1, this.groupDb.getGroupIdByGroupName("testgroup1", this.dbSession));
		// "testuser3" isn't a member of "testgroup1" and can't get the id
		assertEquals(GroupID.INVALID.getId(), this.groupDb.getGroupIdByGroupNameAndUserName("testgroup1", "testuser3", this.dbSession));

		// group doesn't exist
		assertEquals(GroupID.INVALID.getId(), this.groupDb.getGroupIdByGroupNameAndUserName(ParamUtils.NOGROUP_NAME, null, this.dbSession));
		assertEquals(GroupID.INVALID.getId(), this.groupDb.getGroupIdByGroupName(ParamUtils.NOGROUP_NAME, this.dbSession));

		// groupname is null
		for (final String groupname : new String[] { "", " ", null }) {
			for (final String username : new String[] { "", " ", null }) {
				try {
					this.groupDb.getGroupIdByGroupNameAndUserName(groupname, username, this.dbSession);
					fail("expected exception");
				} catch (Exception ignored) {
				}
			}

			try {
				this.groupDb.getGroupIdByGroupName(groupname, this.dbSession);
				fail("expected exception");
			} catch (Exception ignored) {
			}
		}
	}
}