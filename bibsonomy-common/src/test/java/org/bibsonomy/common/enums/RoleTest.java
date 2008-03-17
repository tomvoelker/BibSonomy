package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RoleTest {

	/**
	 * tests getRole
	 */
	@Test
	public void getRole() {
		assertEquals(0, Role.ADMIN.getRole());
		assertEquals(1, Role.DEFAULT.getRole());

		assertEquals(Role.ADMIN, Role.getRole("0"));
		assertEquals(Role.DEFAULT, Role.getRole("1"));
		try {
			Role.getRole("42");
			fail("Should throw exception");
		} catch (Exception ignore) {
		}

		assertEquals(Role.ADMIN, Role.getRole(0));
		assertEquals(Role.DEFAULT, Role.getRole(1));
		try {
			Role.getRole(42);
			fail("Should throw exception");
		} catch (Exception ignore) {
		}
	}
}