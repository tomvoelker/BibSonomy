package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
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
import org.junit.Test;

/**
 * Tests related to groups.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private final int NUM_KDE_GROUP_MEMBERS = 13;

	@Test
	public void getAllGroups() {
		final List<Group> allGroups = this.groupDb.getAllGroups(0, 100, this.dbSession);

		for (final Group group : allGroups) {
			final String realname = group.getUsers().get(0).getRealname();
			final String homepage = group.getUsers().get(0).getHomepage().toString();

			if (group.getName().equals("kde")) {
				assertEquals(GroupID.KDE.getId(), group.getGroupId());
				assertEquals("Knowledge and Data Engineering Group", realname);
				assertEquals("http://www.kde.cs.uni-kassel.de/", homepage);
			} else if (group.getName().equals("ls3wim")) {
				assertEquals(6, group.getGroupId());
				// Yes, that's "Kalrsruhe" and not "Karlsruhe" ;)
				assertEquals("Research Group Knowledge Management, AIFB, Kalrsruhe, Germany", realname);
				assertEquals("http://www.aifb.uni-karlsruhe.de/Forschungsgruppen/WBS/", homepage);
			}
		}

		assertEquals(5, this.groupDb.getAllGroups(0, 5, this.dbSession).size());
	}

	@Test
	public void getGroupByName() {
		final Group kdeGroup = this.groupDb.getGroupByName("kde", this.dbSession);
		assertEquals("kde", kdeGroup.getName());
		assertEquals(GroupID.KDE.getId(), kdeGroup.getGroupId());
		assertEquals("Knowledge and Data Engineering Group", kdeGroup.getUsers().get(0).getRealname());
		assertEquals("http://www.kde.cs.uni-kassel.de/", kdeGroup.getUsers().get(0).getHomepage().toString());

		for (final String groupname : new String[] { "", " ", null }) {
			try {
				this.groupDb.getGroupByName(groupname, this.dbSession);
				fail("Should throw an exception");
			} catch (final RuntimeException ex) {
			}
		}

		assertNull(this.groupDb.getGroupByName(ParamUtils.NOGROUP_NAME, this.dbSession));
	}

	@Test
	public void getGroupMembers() {
		final Group kdeGroup = this.groupDb.getGroupMembers("stumme", "kde", this.dbSession);
		assertEquals("kde", kdeGroup.getName());
		assertEquals(GroupID.KDE.getId(), kdeGroup.getGroupId());
		assertEquals(this.NUM_KDE_GROUP_MEMBERS, kdeGroup.getUsers().size());

		// "xamde", a member of "ls3wim", can't see other members
		// "stumme", not a member of "ls3wim", can't see members too
		for (final String username : new String[] { "xamde", "stumme" }) {
			final Group hiddenGroup = this.groupDb.getGroupMembers(username, "ls3wim", this.dbSession);
			assertEquals(0, hiddenGroup.getUsers().size());
		}

		// "hotho", a member of "kde_stud", can see all members
		Group memberOnlyGroup = this.groupDb.getGroupMembers("hotho", "kde_stud", this.dbSession);
		assertEquals(6, memberOnlyGroup.getUsers().size());
		// "xamde" isn't a member of "kde_stud" and can't see the members
		memberOnlyGroup = this.groupDb.getGroupMembers("xamde", "kde_stud", this.dbSession);
		assertEquals(0, memberOnlyGroup.getUsers().size());

		try {
			this.groupDb.getGroupMembers(ParamUtils.NOUSER_NAME, ParamUtils.NOGROUP_NAME, this.dbSession);
			fail("Should throw an exception");
		} catch (final RuntimeException ex) {
		}
	}

	@Test
	public void getGroupsForUser() {
		List<Group> groups = this.groupDb.getGroupsForUser("stumme", this.dbSession);
		assertEquals(6, groups.size());
		this.assertStandardGroups(groups);

		// every user has got at least three groups: "public", "private" and "friends"
		groups = this.groupDb.getGroupsForUser(ParamUtils.NOUSER_NAME, this.dbSession);
		assertEquals(3, groups.size());
		this.assertStandardGroups(groups);
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

	@Test
	public void storeGroup() {
		final Group newGroup = new Group();
		newGroup.setName("stumme");
		this.groupDb.storeGroup(newGroup, false, this.dbSession);
		Group group = this.groupDb.getGroupByName("stumme", this.dbSession);
		assertEquals("stumme", group.getName());
		assertEquals(1, group.getUsers().size());
		assertEquals("Gerd Stumme", group.getUsers().get(0).getRealname());
		assertEquals("http://www.kde.cs.uni-kassel.de/stumme", group.getUsers().get(0).getHomepage().toString());

		for (final boolean update : new boolean[] { true, false }) {
			for (final String groupname : new String[] { "", " ", null, "kde", ParamUtils.NOUSER_NAME }) {
				try {
					group = new Group();
					group.setName(groupname);
					this.groupDb.storeGroup(group, update, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ex) {
				}
			}
		}
	}

	@Test
	public void deleteGroup() {
		final Group group = this.groupDb.getGroupMembers("stumme", "kde", this.dbSession);
		assertEquals(this.NUM_KDE_GROUP_MEMBERS, group.getUsers().size());

		// check that the group and all members are gone
		this.groupDb.deleteGroup("kde", this.dbSession);
		assertNull(this.groupDb.getGroupByName("kde", this.dbSession));
		for (final User user : group.getUsers()) {
			final List<Group> userGroups = this.groupDb.getGroupsForUser(user.getName(), this.dbSession);
			for (final Group userGroup : userGroups) {
				if ("kde".equals(userGroup.getName())) {
					fail("User ('" + user.getName() + "') shouldn't be a member of this group ('" + userGroup.getName() + "') anymore");
				}
			}
		}

		// can't delete a group that doesn't exist
		try {
			this.groupDb.deleteGroup(ParamUtils.NOGROUP_NAME, this.dbSession);
			fail("Should throw an exception");
		} catch (final RuntimeException ex) {
		}
	}

	@Test
	public void addUserToGroupANDremoveUserFromGroup() {
		// adds and then removes a user "cschenk" to the group "kde" and checks
		// whether the groupsize grows and shrinks accordingly
		Group group = this.groupDb.getGroupMembers("stumme", "kde", this.dbSession);
		assertEquals(this.NUM_KDE_GROUP_MEMBERS, group.getUsers().size());
		this.groupDb.addUserToGroup("kde", "cschenk", this.dbSession);
		group = this.groupDb.getGroupMembers("cschenk", "kde", this.dbSession);
		assertEquals(this.NUM_KDE_GROUP_MEMBERS + 1, group.getUsers().size());
		this.groupDb.removeUserFromGroup("kde", "cschenk", this.dbSession);
		group = this.groupDb.getGroupMembers("stumme", "kde", this.dbSession);
		assertEquals(this.NUM_KDE_GROUP_MEMBERS, group.getUsers().size());

		for (final String username : new String[] { "", " ", null, "cschenk", ParamUtils.NOUSER_NAME }) {
			for (final String groupname : new String[] { "", " ", null, ParamUtils.NOGROUP_NAME }) {
				try {
					this.groupDb.addUserToGroup(groupname, username, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ex) {
				}
			}
		}

		// can't add user to a group he's already a member of
		try {
			this.groupDb.addUserToGroup("kde", "stumme", this.dbSession);
			fail("Should throw an exception");
		} catch (final RuntimeException ex) {
		}
		// can't remove user from a group he isn't a member of
		try {
			this.groupDb.removeUserFromGroup("kde", "cschenk", this.dbSession);
			fail("Should throw an exception");
		} catch (final RuntimeException ex) {
		}
	}
}