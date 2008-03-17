package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupIDTest {

	/**
	 * tests getSpecialGroup
	 */
	@Test
	public void getSpecialGroup() {
		assertEquals(GroupID.PUBLIC, GroupID.getSpecialGroup("PUBLIC"));
		assertEquals(GroupID.PRIVATE, GroupID.getSpecialGroup("PRIVATE"));
		assertEquals(GroupID.FRIENDS, GroupID.getSpecialGroup("FRIENDS"));

		assertEquals(null, GroupID.getSpecialGroup("KDE"));
		assertEquals(null, GroupID.getSpecialGroup("INVALID"));

		for (final String groupname : new String[] { "public", "PuBlIc" }) {
			assertEquals(GroupID.PUBLIC, GroupID.getSpecialGroup(groupname));
		}

		try {
			GroupID.getSpecialGroup("hurz");
			fail("Should throw IllegalArgumentException");
		} catch (final IllegalArgumentException ex) {
		}
	}

	/**
	 * tests isSpecialGroupId
	 */
	@Test
	public void isSpecialGroupId() {
		for (final int groupId : new int[] { 0, 1, 2 }) {
			assertTrue(GroupID.isSpecialGroupId(groupId));
		}

		for (final int groupId : new int[] { -1, 3, 42 }) {
			assertFalse(GroupID.isSpecialGroupId(groupId));
		}
	}
}