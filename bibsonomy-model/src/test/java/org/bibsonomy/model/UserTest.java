package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;

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
}