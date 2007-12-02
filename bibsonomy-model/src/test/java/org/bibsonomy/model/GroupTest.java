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
	 * tests getPrivlevel what else ?
	 */
	@Test
	public void getPrivlevel() {
		final Group group = new Group();
		assertEquals(GroupID.PUBLIC.getId(), group.getGroupId());
		assertEquals(Privlevel.MEMBERS.getId(), group.getPrivlevel());
	}
}