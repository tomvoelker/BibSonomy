package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
		assertFalse(user.isSpammer());

		// is a spammer
		for (final Integer spammer : new Integer[] { 1, new Integer(1) }) {
			user.setSpammer(spammer);
			assertTrue(user.isSpammer());
		}

		// isn't a spammer
		for (final Integer spammer : new Integer[] { null, -1, 0, 23, 42, new Integer(23) }) {
			user.setSpammer(spammer);
			assertFalse(user.isSpammer());
		}
	}
}