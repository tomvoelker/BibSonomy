/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
			GroupID.getSpecialGroup("unknown");
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {
		}
	}

	/**
	 * tests isSpecialGroupId
	 */
	@Test
	public void isSpecialGroupId() {
		// int
		for (final int groupId : new int[] { 0, 1, 2 }) {
			assertTrue(GroupID.isSpecialGroupId(groupId));
		}
		for (final int groupId : new int[] { -1, 3, 42 }) {
			assertFalse(GroupID.isSpecialGroupId(groupId));
		}

		// GroupID objects
		for (final GroupID groupId : new GroupID[] { GroupID.PUBLIC, GroupID.PRIVATE, GroupID.FRIENDS }) {
			assertTrue(GroupID.isSpecialGroupId(groupId));
		}
		for (final GroupID groupId : new GroupID[] { GroupID.INVALID }) {
			assertFalse(GroupID.isSpecialGroupId(groupId));
		}
	}

	/**
	 * tests isSpecialGroup
	 */
	@Test
	public void isSpecialGroup() {
		// strings
		for (final String groupId : new String[] { "PUBLIC", "PRIVATE", "FRIENDS" }) {
			assertTrue(GroupID.isSpecialGroup(groupId));
			assertTrue(GroupID.isSpecialGroup(groupId.toLowerCase()));
		}
		for (final String groupId : new String[] { "", " ", null, "test1" }) {
			assertFalse(GroupID.isSpecialGroup(groupId));
		}
	}
}