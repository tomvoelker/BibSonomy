package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

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

		// generate some keys and make sure that they're all different
		final Set<String> keys = new HashSet<String>();
		final int NUMBER_OF_KEYS = 65536;
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
}