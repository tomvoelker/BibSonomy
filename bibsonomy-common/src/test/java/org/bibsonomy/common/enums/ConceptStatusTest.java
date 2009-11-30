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
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedConceptStatusException;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ConceptStatusTest {

	/**
	 * tests getConceptStatus
	 */
	@Test
	public void getConceptStatus() {
		assertEquals(ConceptStatus.PICKED, ConceptStatus.getConceptStatus("picked"));
		assertEquals(ConceptStatus.PICKED, ConceptStatus.getConceptStatus("PiCkEd"));
		assertEquals(ConceptStatus.UNPICKED, ConceptStatus.getConceptStatus("unpicked"));
		assertEquals(ConceptStatus.UNPICKED, ConceptStatus.getConceptStatus("UnPiCkEd"));
		assertEquals(ConceptStatus.ALL, ConceptStatus.getConceptStatus("all"));
		assertEquals(ConceptStatus.ALL, ConceptStatus.getConceptStatus("AlL"));

		for (final String test : new String[] { "", " ", null }) {
			try {
				ConceptStatus.getConceptStatus(test);
				fail("Should throw exception");
			} catch (InternServerException ignore) {
			}
		}

		for (final String test : new String[] { "test", "picked-1" }) {
			try {
				ConceptStatus.getConceptStatus(test);
				fail("Should throw exception");
			} catch (UnsupportedConceptStatusException ignore) {
			}
		}
	}

	/**
	 * tests toString
	 */
	@Test
	public void testToString() {
		assertEquals("picked", ConceptStatus.PICKED.toString());
		assertEquals("unpicked", ConceptStatus.UNPICKED.toString());
		assertEquals("all", ConceptStatus.ALL.toString());
	}
}