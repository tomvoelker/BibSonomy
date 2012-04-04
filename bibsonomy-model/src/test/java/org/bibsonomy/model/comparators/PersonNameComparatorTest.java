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

package org.bibsonomy.model.comparators;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class PersonNameComparatorTest {

	@Test
	public void testCompare() throws Exception {
		final PersonName p1 = new PersonName("Malcolm", "Reynolds");
		final PersonName p2 = new PersonName("Zoe Alleyne", "Washburne");
		final PersonName p3 = new PersonName("River", "Tam");
		final PersonName p4 = new PersonName("Hoban", "Washburne");
		
		final List<PersonName> all = Arrays.asList(p1, p2, p3, p4);
		Collections.sort(all, new PersonNameComparator());
		
		assertEquals(p1, all.get(0));
		assertEquals(p3, all.get(1));
		assertEquals(p4, all.get(2));
		assertEquals(p2, all.get(3));
	}

}
