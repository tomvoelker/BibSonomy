package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.model.Group;
import org.junit.Test;

public class GroupDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getAllGroups() {
		final List<Group> allGroups = this.groupDb.getAllGroups();

		for (final Group group : allGroups) {
			final String realname = group.getUsers().get(0).getRealname();
			final String homepage = group.getUsers().get(0).getHomepage().toString();

			if (group.getName().equals("kde")) {
				assertEquals("Knowledge and Data Engineering Group", realname);
				assertEquals("http://www.kde.cs.uni-kassel.de/", homepage);
			} else if (group.getName().equals("ls3wim")) {
				assertEquals("Research Group Knowledge Management, AIFB, Kalrsruhe, Germany", realname);
				assertEquals("http://www.aifb.uni-karlsruhe.de/Forschungsgruppen/WBS/", homepage);
			}
		}
	}

	@Test
	public void getGroupByName() {
		final Group kdeGroup = this.groupDb.getGroupByName(this.groupParam);
		assertEquals("kde", kdeGroup.getName());
		assertEquals("Knowledge and Data Engineering Group", kdeGroup.getUsers().get(0).getRealname());
		assertEquals("http://www.kde.cs.uni-kassel.de/", kdeGroup.getUsers().get(0).getHomepage().toString());
	}

	@Test
	public void getGroupMembers() {
		final Group kdeGroup = this.groupDb.getGroupMembers(this.groupParam);
		assertEquals("kde", kdeGroup.getName());
		assertEquals(13, kdeGroup.getUsers().size());	
	}
}