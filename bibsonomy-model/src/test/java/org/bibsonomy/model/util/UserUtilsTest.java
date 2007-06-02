package org.bibsonomy.model.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UserUtilsTest {

	@Test
	public void generateApiKey() {
		assertEquals(32, UserUtils.generateApiKey().length());

		final String key1 = UserUtils.generateApiKey();
		final String key2 = UserUtils.generateApiKey();
		final String key3 = UserUtils.generateApiKey();
		assertFalse(key1.equals(key2));
		assertFalse(key2.equals(key3));
	}
}