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
 * @author Jens Illig
 */
public class HashIDTest {

	/**
	 * tests getSimHash
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