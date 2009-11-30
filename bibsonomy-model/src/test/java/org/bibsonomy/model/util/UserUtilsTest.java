/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * Testcase for the UserUtils class
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class UserUtilsTest {

	/**
	 * tests generateApiKey
	 */
	@Test
	public void generateApiKey() {
		assertEquals(32, UserUtils.generateApiKey().length());

		/*
		 * generate some keys and make sure that they're all different
		 */
		final Set<String> keys = new HashSet<String>();
		// supporting 2^16 users with different keys should be enough for now
		final int NUMBER_OF_KEYS = (int) Math.pow(2, 16);
		for (int i = 0; i < NUMBER_OF_KEYS; i++) {
			final int oldSize = keys.size();
			keys.add(UserUtils.generateApiKey());
			if (oldSize + 1 != keys.size()) {
				fail("There's a duplicate API key");
			}
		}
	}

	/**
	 * tests setGroupsByGroupIDs
	 */
	@Test
	public void setGroupsByGroupIDs() {
		final User user = new User();
		assertEquals(0, user.getGroups().size());
		UserUtils.setGroupsByGroupIDs(user, Arrays.asList(1, 2, 3));
		assertEquals(3, user.getGroups().size());
	}

	/**
	 * tests getListOfGroupIDs
	 */
	@Test
	public void getListOfGroupIDs() {
		final User user = new User();
		UserUtils.setGroupsByGroupIDs(user, Arrays.asList(1, 2, 3));
		assertEquals(3, user.getGroups().size());
		final List<Integer> groups = UserUtils.getListOfGroupIDs(user);
		assertTrue(groups.contains(1));
		assertTrue(groups.contains(2));
		assertTrue(groups.contains(3));
		assertFalse(groups.contains(23));
		assertFalse(groups.contains(42));

		// invalid user object returns an empty list
		assertNotNull(UserUtils.getListOfGroupIDs(null));
		// every user is in the "public" group
		assertEquals(1, UserUtils.getListOfGroupIDs(null).size());
	}
}