/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
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