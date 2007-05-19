package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Group;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests related to groups.
 *
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final Logger log = Logger.getLogger(GroupDatabaseManagerTest.class);

	@Test
	public void getAllGroups() {
		final List<Group> allGroups = this.groupDb.getAllGroups(this.dbSession);

		for (final Group group : allGroups) {
			final String realname = group.getUsers().get(0).getRealname();
			final String homepage = group.getUsers().get(0).getHomepage().toString();

			if (group.getName().equals("kde")) {
				assertEquals(GroupID.GROUP_KDE.getId(), group.getGroupId());
				assertEquals("Knowledge and Data Engineering Group", realname);
				assertEquals("http://www.kde.cs.uni-kassel.de/", homepage);
			} else if (group.getName().equals("ls3wim")) {
				assertEquals(6, group.getGroupId());
				// Yes, that's "Kalrsruhe" and not "Karlsruhe"
				assertEquals("Research Group Knowledge Management, AIFB, Kalrsruhe, Germany", realname);
				assertEquals("http://www.aifb.uni-karlsruhe.de/Forschungsgruppen/WBS/", homepage);
			}
		}
	}

	@Test
	public void getGroupByName() {
		final Group kdeGroup = this.groupDb.getGroupByName(this.groupParam, this.dbSession);
		assertEquals("kde", kdeGroup.getName());
		assertEquals(GroupID.GROUP_KDE.getId(), kdeGroup.getGroupId());
		assertEquals("Knowledge and Data Engineering Group", kdeGroup.getUsers().get(0).getRealname());
		assertEquals("http://www.kde.cs.uni-kassel.de/", kdeGroup.getUsers().get(0).getHomepage().toString());
	}

	@Test
	public void getGroupMembers() {
		final Group kdeGroup = this.groupDb.getGroupMembers(this.groupParam, this.dbSession);
		assertEquals("kde", kdeGroup.getName());
		assertEquals(GroupID.GROUP_KDE.getId(), kdeGroup.getGroupId());
		assertEquals(13, kdeGroup.getUsers().size());	
	}

	@Test
	public void getGroupsForUser() {
		final List<Group> groups = this.groupDb.getGroupsForUser(this.groupParam, this.dbSession);
		final Set<Integer> found = new HashSet<Integer>();
		for (final Group g : groups) {
			log.debug(g.getName());
			found.add(g.getGroupId());
		}
		Assert.assertTrue( found.contains(GroupID.GROUP_PRIVATE.getId()) );
		Assert.assertTrue( found.contains(GroupID.GROUP_PUBLIC.getId()) );
		Assert.assertTrue( found.contains(GroupID.GROUP_FRIENDS.getId()) );
		assertEquals(6, groups.size());
	}
}