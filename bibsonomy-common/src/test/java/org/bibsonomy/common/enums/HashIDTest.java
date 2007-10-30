package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class HashIDTest {

	/**
	 * tests getSimHash what else ?
	 */
	@Test
	public void getSimHash() {
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

	/**
	 * tests getHashRange what else ?
	 */
	@Test
	public void getHashRange() {
		assertEquals(4, HashID.getHashRange().length);
	}
}