package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.junit.Test;

/**
 * Testcase for the Group class
 */
public class GroupTest {

	/**
	 * tests a new group object
	 */
	@Test
	public void testNewGroup() {
		for (final Group group : new Group[] { new Group(), new Group(GroupID.PUBLIC), new Group(GroupID.PUBLIC.getId()), new Group("testgroup") }) {
			assertEquals(GroupID.PUBLIC.getId(), group.getGroupId());
			assertEquals(Privlevel.MEMBERS, group.getPrivlevel());
			assertEquals(false, group.isSharedDocuments());
		}
	}
}