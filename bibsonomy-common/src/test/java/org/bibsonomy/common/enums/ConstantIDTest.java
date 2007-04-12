package org.bibsonomy.common.enums;

import junit.framework.TestCase;

public class ConstantIDTest extends TestCase {

	public void testGetSimHash() {
		assertEquals(ConstantID.SIM_HASH0, ConstantID.getSimHash(0));
		assertEquals(ConstantID.SIM_HASH1, ConstantID.getSimHash(1));
		assertEquals(ConstantID.SIM_HASH2, ConstantID.getSimHash(2));
		assertEquals(ConstantID.SIM_HASH3, ConstantID.getSimHash(3));

		try {
			ConstantID.getSimHash(42);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}