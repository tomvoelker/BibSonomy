package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Group;
import org.junit.Test;

/**
 * Tests related to groups.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getAllGroups() {
		final List<Group> allGroups = this.groupDb.getAllGroups(0, 100, this.dbSession);

		for (final Group group : allGroups) {
			final String realname = group.getUsers().get(0).getRealname();
			final String homepage = group.getUsers().get(0).getHomepage().toString();

			if (group.getName().equals("kde")) {
				assertEquals(GroupID.GROUP_KDE.getId(), group.getGroupId());
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
		assertEquals(GroupID.GROUP_KDE.getId(), kdeGroup.getGroupId());
		assertEquals("Knowledge and Data Engineering Group", kdeGroup.getUsers().get(0).getRealname());
		assertEquals("http://www.kde.cs.uni-kassel.de/", kdeGroup.getUsers().get(0).getHomepage().toString());
	}

	@Test
	public void getGroupMembers() {
		final Group kdeGroup = this.groupDb.getGroupMembers("stumme", "kde", this.dbSession);
		assertEquals("kde", kdeGroup.getName());
		assertEquals(GroupID.GROUP_KDE.getId(), kdeGroup.getGroupId());
		assertEquals(13, kdeGroup.getUsers().size());

		// "xamde", a member of "ls3wim", can't see other members
		Group hiddenGroup = this.groupDb.getGroupMembers("xamde", "ls3wim", this.dbSession);
		assertEquals(0, hiddenGroup.getUsers().size());
		// "stumme", not a member of "ls3wim", can't see members too
		hiddenGroup = this.groupDb.getGroupMembers("stumme", "ls3wim", this.dbSession);
		assertEquals(0, hiddenGroup.getUsers().size());

		// "hotho" can see all members
		Group memberOnlyGroup = this.groupDb.getGroupMembers("hotho", "kde_stud", this.dbSession);
		assertEquals(6, memberOnlyGroup.getUsers().size());
		// "xamde" isn't a member of "kde_stud" and can't see the members
		memberOnlyGroup = this.groupDb.getGroupMembers("xamde", "kde_stud", this.dbSession);
		assertEquals(0, memberOnlyGroup.getUsers().size());
	}

	@Test
	public void getGroupsForUser() {
		final List<Group> groups = this.groupDb.getGroupsForUser("stumme", this.dbSession);
		final Set<Integer> found = new HashSet<Integer>();
		for (final Group group : groups) {
			found.add(group.getGroupId());
		}
		assertTrue(found.contains(GroupID.GROUP_PUBLIC.getId()));
		assertTrue(found.contains(GroupID.GROUP_PRIVATE.getId()));
		assertTrue(found.contains(GroupID.GROUP_FRIENDS.getId()));
		assertEquals(6, groups.size());
	}
}