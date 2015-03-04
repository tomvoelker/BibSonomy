/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Group;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class GroupUtilsTest {

	/**
	 * tests the groups
	 */
	@Test
	public void getGroup() {
		for (final Group group : new Group[] { GroupUtils.getPublicGroup(), GroupUtils.buildPrivateGroup(), GroupUtils.buildFriendsGroup(), GroupUtils.buildInvalidGroup() }) {
			assertNotNull(group);
			assertTrue(present(group.getName()));
			assertTrue(present(group.getDescription()));
			assertTrue(present(group.getGroupId()));
			assertTrue(present(group.getPrivlevel()));
		}
		
		final Group g1 = GroupUtils.getPublicGroup();
		final Group g2 = GroupUtils.getPublicGroup();
		// equals should be enough before: assertSame(g1, g2);
		assertEquals(g1, g2);
	}
	
	/**
	 * tests {@link GroupUtils#isExclusiveGroup(Group)}
	 */
	@Test
	public void testIsExclusiveGroupGroup() {
		assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.buildPrivateGroup()));
		assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.buildPrivateSpamGroup()));
		assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.getPublicGroup()));
		assertTrue(GroupUtils.isExclusiveGroup(GroupUtils.buildPublicSpamGroup()));
		assertFalse(GroupUtils.isExclusiveGroup(GroupUtils.buildFriendsGroup()));
		assertFalse(GroupUtils.isExclusiveGroup(GroupUtils.buildFriendsSpamGroup()));
	}
	
	/**
	 * tests {@link GroupUtils#isExclusiveGroup(int)}
	 */
	@Test
	public void testIsExclusiveGroupGroupId() {
		assertTrue(GroupUtils.isExclusiveGroup(GroupID.PRIVATE.getId()));
		assertTrue(GroupUtils.isExclusiveGroup(GroupID.PRIVATE_SPAM.getId()));
		assertTrue(GroupUtils.isExclusiveGroup(GroupID.PUBLIC.getId()));
		assertTrue(GroupUtils.isExclusiveGroup(GroupID.PUBLIC_SPAM.getId()));
		assertFalse(GroupUtils.isExclusiveGroup(GroupID.FRIENDS.getId()));
		assertFalse(GroupUtils.isExclusiveGroup(GroupID.FRIENDS_SPAM.getId()));
	}
	
	
	
}