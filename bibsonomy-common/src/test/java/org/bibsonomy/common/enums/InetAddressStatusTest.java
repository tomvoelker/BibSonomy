package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Robert Jäschke
 * @version $Id$
 */
public class InetAddressStatusTest {

	/**
	 * Tests if conversion from int/string to enum and back work.
	 */
	@Test
	public void getInetAddressStatus() {
		// Enum -> int
		InetAddressStatus status = InetAddressStatus.WRITEBLOCKED;
		assertEquals(1, status.getInetAddressStatus());
		
		// String -> Enum
		status = InetAddressStatus.getInetAddressStatus("1");
		assertEquals(InetAddressStatus.WRITEBLOCKED, status);
		
		// int -> enum
		status = InetAddressStatus.getInetAddressStatus(1);
		assertEquals(InetAddressStatus.WRITEBLOCKED, status);
	}
	
}
