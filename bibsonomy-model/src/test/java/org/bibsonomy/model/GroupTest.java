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

package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.junit.Test;

/**
 * Testcase for the Group class
 * 
 * @version $Id$
 */
public class GroupTest {

	/**
	 * tests a new group object
	 */
	@Test
	public void testNewGroup() {
		for (final Group group : new Group[] {new Group(GroupID.PUBLIC), new Group(GroupID.PUBLIC.getId())}) {
			assertEquals(GroupID.PUBLIC.getId(), group.getGroupId());
			assertEquals(Privlevel.MEMBERS, group.getPrivlevel());
			assertEquals(false, group.isSharedDocuments());
		}
	}


	/**
	 * per default, a group which has given no ID, should have an invalid ID 
	 */
	@Test
	public void testDefaultGroupIsInvalid() {
		final Group group = new Group();
		assertEquals(GroupID.INVALID.getId(), group.getGroupId());
	}

	/**
	 * even if the group has a name, but no explicit id set, the id should be invalid  
	 */
	@Test
	public void testGroupIsInvalidOnGivenNameOnly() {
		final Group group = new Group("foo");
		assertEquals(GroupID.INVALID.getId(), group.getGroupId());
	}

	/**
	 * If two groups have the same ID (and no name given), they should be equal
	 */
	@Test
	public void testEqualsOnId() {
		final Group first = new Group(0);
		final Group second = new Group(0);
		assertTrue(first.equals(second));
	}

	/**
	 * If two groups have the same name (and no ID given), they should be equal
	 */
	@Test
	public void testEqualsOnName() {
		final Group first = new Group("foo");
		final Group second = new Group("foo");
		assertTrue(first.equals(second));
	}

	/**
	 * If two groups have the same name and ID, they should be equal
	 */
	@Test
	public void testEqualsOnNameAndId() {
		final Group first = new Group("foo");
		final Group second = new Group("foo");
		first.setGroupId(3);
		second.setGroupId(3);
		assertTrue(first.equals(second));
	}

	/**
	 * If two groups have no IDs given, but different names, they should not be equal
	 */
	@Test
	public void testNotEqualsOnName() {
		final Group first = new Group("foo");
		final Group second = new Group("boo");
		assertFalse(first.equals(second));
	}

	/**
	 * If two groups have no names given, but different IDs, they should not be equal
	 */
	@Test
	public void testNotEqualsOnId() {
		final Group first = new Group(3);
		final Group second = new Group(7);
		assertFalse(first.equals(second));
	}


	/**
	 * When the name is the same, but the IDs differ, there should be an exception raised.
	 */
	@Test
	public void testNotEqualsOnIdAndName1() {
		final Group first = new Group("foo");
		final Group second = new Group("foo");
		first.setGroupId(3);
		second.setGroupId(4);
		try {
			first.equals(second);
			fail();
		} catch (RuntimeException e) {

		}

	}


	/**
	 * When the ID is the same, but the names differ, there should be an exception raised
	 */
	@Test
	public void testNotEqualsOnIdAndName2() {
		final Group first = new Group("bar");
		final Group second = new Group("foo");
		first.setGroupId(3);
		second.setGroupId(3);
		try {
			first.equals(second);
			fail();
		} catch (RuntimeException e) {

		}

	}


	/**
	 * One group has the ID given, the other the name. They're incomparable!
	 */
	@Test
	public void testEqualsFail1() {
		final Group first = new Group("bar");
		final Group second = new Group(1);
		try {
			first.equals(second);
			fail();
		} catch (RuntimeException e) {

		}
	}

	/**
	 * Neither name nor id given on both groups. Fail!
	 * Because: invalid groups are not comparable!
	 */
	@Test
	public void testEqualsFail2() {
		final Group first = new Group();
		final Group second = new Group();
		try {
			first.equals(second);
			fail();
		} catch (RuntimeException e) {

		}
	}

	/**
	 * equals should ignore case - this is tested here
	 */
	@Test
	public void testEqualsOnCase() {
		final Group first = new Group("puBliC");
		final Group second = new Group("pubLIC");
		assertEquals(first, second);
	}

	/**
	 * transitivity must hold for equals!
	 */
	@Test
	public void testEqualsTransitivity1() {
		final Group first = new Group("puBliC");
		final Group second = new Group("pubLIC");
		final Group third = new Group("PubLIc");

		assertEquals(first, second);
		assertEquals(second, third);
		// transitivity
		assertEquals(first, third);
	}

	/**
	 * more complicated example: two groups are equal by their name,
	 * two other by their id
	 */
	@Test
	public void testEqualsTransitivity2() {
		final Group first = new Group("puBliC");

		final Group second = new Group("pubLIC");
		second.setGroupId(4);

		final Group third = new Group("PubLIc");
		third.setGroupId(4);

		assertEquals(first, second);
		assertEquals(second, third);
		// transitivity
		assertEquals(first, third);
	}

	/**
	 * more complicated example: two groups are equal by their id,
	 * two other by their name
	 * BUT: this fails, because equals fails on given ids but differing names.
	 */
	@Test
	public void testEqualsTransitivity3() {
		final Group first = new Group(1);

		final Group second = new Group(1);
		second.setName("foo");

		final Group third = new Group("FOO");

		try {
			/*
			 * this should not work, since second has both name and ID, first not
			 */
			first.equals(second);
			fail();
		} catch (RuntimeException e) {

		}

		assertEquals(second, third);
		// transitivity
		try {
			first.equals(third);
			fail();
		} catch (RuntimeException e) {

		}
	}
	
	@Test
	public void testEqualsSpam() {
		final Group first = new Group("public");
		final Group second = new Group("publiC");
		
		first.setGroupId(GroupID.PRIVATE.getId());
		second.setGroupId(GroupID.PRIVATE_SPAM.getId());
		
		assertEquals(first, second);
		
	}


	/**
	 * Hashcode must be compatible to equals ...
	 * 
	 * "if two objects are equal according to the equals() method, they must have the same hashCode() value"
	 * 
	 * good article describing this: <a href="http://www.ibm.com/developerworks/java/library/j-jtp05273.html">at IBM developer works</a> 
	 */
	@Test
	public void testHashCode1() {
		final Group first = new Group("puBliC");
		final Group second = new Group("pubLIC");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	/**
	 * same id
	 */
	@Test
	public void testHashCode2() {
		final Group first = new Group(1);
		final Group second = new Group(1);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	/**
	 * same name, one group has id
	 */
	@Test
	public void testHashCode3() {
		final Group first = new Group("public");
		first.setGroupId(3);
		final Group second = new Group("pubLIC");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	
	/**
	 * 
	 * NOTE that one group has both ID and name, the other only name - this is OK
	 */
	@Test
	public void testEqualsNotFails() {
		final Group first = new Group("public");
		first.setGroupId(3);
		final Group second = new Group("pubLIC");
		assertEquals(first, second);
	}

	/**
	 * one group has both ID and name, the other only id -- fail
	 */
	@Test
	public void testEqualsFails1() {
		final Group first = new Group(1);
		final Group second = new Group(1);
		second.setName("foo");

		try {
			first.equals(second);
			fail();
		} catch (RuntimeException e) {

		}

	}

}