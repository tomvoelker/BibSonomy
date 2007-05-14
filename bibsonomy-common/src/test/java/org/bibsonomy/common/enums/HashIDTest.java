package org.bibsonomy.common.enums;

import junit.framework.TestCase;

public class HashIDTest extends TestCase {

	public void testGetSimHash() {
		assertEquals(HashID.SIM_HASH0, HashID.getSimHash(0));
		assertEquals(HashID.SIM_HASH1, HashID.getSimHash(1));
		assertEquals(HashID.SIM_HASH2, HashID.getSimHash(2));
		assertEquals(HashID.SIM_HASH3, HashID.getSimHash(3));

		try {
			HashID.getSimHash(42);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}