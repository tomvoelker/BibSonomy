/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class SpamStatusTest {

	/**
	 * tests isSpammer
	 */
	@Test
	public void isSpammer() {
		assertEquals("yes", SpamStatus.SPAMMER.isSpammer());
		assertEquals("no", SpamStatus.NO_SPAMMER.isSpammer());
		assertEquals("unknown", SpamStatus.SPAMMER_NOT_SURE.isSpammer());
		assertEquals("unknown", SpamStatus.NO_SPAMMER_NOT_SURE.isSpammer());
		assertEquals("unknown", SpamStatus.UNKNOWN.isSpammer());

		assertTrue(SpamStatus.isSpammer(SpamStatus.SPAMMER));
		assertTrue(SpamStatus.isSpammer(SpamStatus.SPAMMER_NOT_SURE));
		assertFalse(SpamStatus.isSpammer(SpamStatus.NO_SPAMMER));
		assertFalse(SpamStatus.isSpammer(SpamStatus.NO_SPAMMER_NOT_SURE));
		assertFalse(SpamStatus.isSpammer(SpamStatus.UNKNOWN));
	}

	/**
	 * tests getStatus
	 */
	@Test
	public void getStatus() {
		assertEquals(SpamStatus.SPAMMER, SpamStatus.getStatus(1));
		assertEquals(SpamStatus.NO_SPAMMER, SpamStatus.getStatus(0));
		assertEquals(SpamStatus.SPAMMER_NOT_SURE, SpamStatus.getStatus(3));
		assertEquals(SpamStatus.NO_SPAMMER_NOT_SURE, SpamStatus.getStatus(2));

		for (final int id : new int[] { -12, 23, 42 }) {
			assertEquals(SpamStatus.UNKNOWN, SpamStatus.getStatus(id));
		}
	}

	/**
	 * tests toString
	 */
	@Test
	public void testToString() {
		assertEquals("spammer", SpamStatus.SPAMMER.toString());
		assertEquals("no spammer", SpamStatus.NO_SPAMMER.toString());
		assertEquals("spammer, not sure", SpamStatus.SPAMMER_NOT_SURE.toString());
		assertEquals("no spammer, not sure", SpamStatus.NO_SPAMMER_NOT_SURE.toString());
		assertEquals("unknown", SpamStatus.UNKNOWN.toString());
	}
}