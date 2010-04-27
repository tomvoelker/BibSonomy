/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class UserTest {

	/**
	 * tests addGroup
	 */
	@Test
	public void addGroup() {
		User user = new User();
		assertEquals(0, user.getGroups().size());
		user.addGroup(new Group());
		user.addGroup(new Group());
		assertEquals(2, user.getGroups().size());

		// don't call getGroups before addGroup
		user = new User();
		user.addGroup(new Group());
		user.addGroup(new Group());
		assertEquals(2, user.getGroups().size());
	}

	/**
	 * tests addFriends
	 */
	@Test
	public void addFriends() {
		User user = new User();
		assertEquals(0, user.getFriends().size());
		user.addFriend(new User());
		user.addFriend(new User());
		assertEquals(2, user.getFriends().size());
		List<User> friendsList = new ArrayList<User>();
		friendsList.add(new User());
		friendsList.add(new User());
		user.addFriends(friendsList);
		assertEquals(4, user.getFriends().size());

		// don't call getFriends before addFriend
		user = new User();
		user.addFriend(new User());
		user.addFriend(new User());
		assertEquals(2, user.getFriends().size());
	}

	/**
	 * tests that the user's name is always set to lowercase
	 * 
	 * @see DocumentTest#userName()
	 */
	@Test
	public void name() {
		assertEquals(null, new User().getName());
		assertEquals("testuser", new User("TeStUsEr").getName());

		final User user = new User();
		user.setName("TeStUsEr");
		assertEquals("testuser", user.getName());
		user.setName(null);
		assertNull(user.getName());
	}

	/**
	 * tests isSpammer
	 */
	@Test
	public void isSpammer() {
		final User user = new User();
		
		/*
		 * default: unknown spam status
		 */
		assertNull(user.getSpammer());

		// is a spammer
		for (final Boolean spammer : new Boolean[] { true, new Boolean("true") }) {
			user.setSpammer(spammer);
			assertTrue(user.getSpammer());
		}

		// isn't a spammer
		for (final Boolean spammer : new Boolean[] { false, new Boolean("false"), new Boolean("aslkjdfh") }) {
			user.setSpammer(spammer);
			assertFalse(user.getSpammer());
		}

		// isn't a spammer
		// isSpammer maps null to false
		for (final Boolean spammer : new Boolean[] { null, false, new Boolean("false"), new Boolean("aslkjdfh") }) {
			user.setSpammer(spammer);
			assertFalse(user.isSpammer());
		}

	}
}