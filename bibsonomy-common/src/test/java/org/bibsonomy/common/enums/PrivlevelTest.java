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
public class PrivlevelTest {

	/**
	 * tests getPrivlevel
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