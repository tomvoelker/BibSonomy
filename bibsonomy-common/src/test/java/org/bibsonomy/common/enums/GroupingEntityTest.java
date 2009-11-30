/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedGroupingException;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GroupingEntityTest {

	/**
	 * tests getGroupingEntity
	 */
	@Test
	public void testGetGroupingEntity() {
		assertEquals(GroupingEntity.ALL, GroupingEntity.getGroupingEntity("all"));
		assertEquals(GroupingEntity.GROUP, GroupingEntity.getGroupingEntity("group"));
		assertEquals(GroupingEntity.USER, GroupingEntity.getGroupingEntity("user"));
		assertEquals(GroupingEntity.VIEWABLE, GroupingEntity.getGroupingEntity("viewable"));
		assertEquals(GroupingEntity.FRIEND, GroupingEntity.getGroupingEntity("friend"));
		assertEquals(GroupingEntity.ALL, GroupingEntity.getGroupingEntity(" All"));
		assertEquals(GroupingEntity.GROUP, GroupingEntity.getGroupingEntity("GROUP"));
		assertEquals(GroupingEntity.USER, GroupingEntity.getGroupingEntity("uSeR "));
		assertEquals(GroupingEntity.VIEWABLE, GroupingEntity.getGroupingEntity("ViewAble"));
		assertEquals(GroupingEntity.FRIEND, GroupingEntity.getGroupingEntity("FrIend"));

		for (final String test : new String[] { "", " ", null }) {
			try {
				ConceptStatus.getConceptStatus(test);
				fail("Should throw exception");
			} catch (InternServerException ignore) {
			}
		}

		try {
			GroupingEntity.getGroupingEntity("foo bar");
			fail("Should throw exception");
		} catch (final UnsupportedGroupingException ex) {
		}
	}

	/**
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	@Test
	public void testToString() {
		assertEquals("GROUP", GroupingEntity.GROUP.toString());
		assertEquals("USER", GroupingEntity.USER.toString());
		assertEquals("VIEWABLE", GroupingEntity.VIEWABLE.toString());
		assertEquals("ALL", GroupingEntity.ALL.toString());
		assertEquals("FRIEND", GroupingEntity.FRIEND.toString());
	}
}