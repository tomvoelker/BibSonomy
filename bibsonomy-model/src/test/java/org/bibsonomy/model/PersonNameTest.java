/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.util.PersonNameUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class PersonNameTest {

	/**
	 * 
	 */
	@Test
	public void testEqualsAndHashCodeWhiteSpace() {
		final PersonName p1 = new PersonName(null, "Knuth");
		final PersonName p2 = new PersonName(" ", "Knuth");
		final PersonName p3 = new PersonName("", "Knuth");
		final PersonName p4 = new PersonName("\n", "Knuth");
		assertEquals(p1, p1);
		assertEquals(p1, p2);
		assertEquals(p1, p3);
		assertEquals(p1, p4);
		assertEquals(p2, p1);
		assertEquals(p2, p2);
		assertEquals(p2, p3);
		assertEquals(p2, p4);
		assertEquals(p3, p1);
		assertEquals(p3, p2);
		assertEquals(p3, p3);
		assertEquals(p3, p4);
		assertEquals(p4, p1);
		assertEquals(p4, p2);
		assertEquals(p4, p3);
		assertEquals(p4, p4);
		assertEquals(p1.hashCode(), p1.hashCode());
		assertEquals(p1.hashCode(), p2.hashCode());
		assertEquals(p1.hashCode(), p3.hashCode());
		assertEquals(p1.hashCode(), p4.hashCode());
		assertEquals(p2.hashCode(), p1.hashCode());
		assertEquals(p2.hashCode(), p2.hashCode());
		assertEquals(p2.hashCode(), p3.hashCode());
		assertEquals(p2.hashCode(), p4.hashCode());
		assertEquals(p3.hashCode(), p1.hashCode());
		assertEquals(p3.hashCode(), p2.hashCode());
		assertEquals(p3.hashCode(), p3.hashCode());
		assertEquals(p3.hashCode(), p4.hashCode());
		assertEquals(p4.hashCode(), p1.hashCode());
		assertEquals(p4.hashCode(), p2.hashCode());
		assertEquals(p4.hashCode(), p3.hashCode());
		assertEquals(p4.hashCode(), p4.hashCode());
	}
	
	@Test
	public void testStrangeBehaviour1() throws Exception {
		final PersonName p1 = PersonNameUtils.discoverPersonNames("Lonely Writer").get(0);
	}

}
