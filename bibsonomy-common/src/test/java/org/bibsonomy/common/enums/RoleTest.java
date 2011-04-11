/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
		assertEquals(Role.SYNC, Role.getRole("4"));
		try {
			Role.getRole(42);
			fail("Should throw exception");
		} catch (Exception ignore) {
		}
	}
}