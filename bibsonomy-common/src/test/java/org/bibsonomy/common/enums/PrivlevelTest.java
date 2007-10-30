package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class PrivlevelTest {

	/**
	 * tests getPrivlevel what else ?
	 */
	@Test
	public void getPrivlevel() {
		assertEquals(Privlevel.PUBLIC, Privlevel.getPrivlevel(0));
		assertEquals(Privlevel.HIDDEN, Privlevel.getPrivlevel(1));
		assertEquals(Privlevel.MEMBERS, Privlevel.getPrivlevel(2));

		for (final int privlevel : new int[] { -1, 3, 42 }) {
			try {
				Privlevel.getPrivlevel(privlevel);
				fail("Should throw exception");
			} catch (final RuntimeException ex) {
			}
		}
	}
}