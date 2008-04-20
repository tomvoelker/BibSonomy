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
	 * tests getGroupId
	 */
	@Test
	public void getGroupId() {
		for (int i = 0; i < 42; i++) {
			// flag
			assertEquals(Integer.MIN_VALUE + i, UserUtils.getGroupId(i, true));
			assertEquals(i, UserUtils.getGroupId(i, false));
			// unflag
			assertEquals(i, UserUtils.getGroupId(UserUtils.getGroupId(i, true), false));
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
		assertEquals(0, UserUtils.getListOfGroupIDs(null).size());
	}
}