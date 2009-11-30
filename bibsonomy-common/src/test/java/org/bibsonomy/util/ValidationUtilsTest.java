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

package org.bibsonomy.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.common.enums.GroupID;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ValidationUtilsTest {

	/**
	 * tests present
	 */
	@Test
	public void present() {
		// String
		assertFalse(ValidationUtils.present(""));
		assertFalse(ValidationUtils.present(" "));
		assertTrue(ValidationUtils.present("hurz"));

		// Collection
		assertFalse(ValidationUtils.present(Collections.EMPTY_LIST));
		final Collection<String> c = new ArrayList<String>();
		c.add("hurz");
		assertTrue(ValidationUtils.present(c));

		// Object
		assertTrue(ValidationUtils.present(new Object()));

		// GroupID
		assertFalse(ValidationUtils.present(GroupID.INVALID));
		for (final GroupID gid : GroupID.values()) {
			if (gid == GroupID.INVALID) continue;
			assertTrue(ValidationUtils.present(gid));
			assertTrue(ValidationUtils.presentValidGroupId(gid.getId()));
		}
	}

	/**
	 * tests nullOrEqual
	 */
	@Test
	public void nullOrEqual() {
		// one ...
		assertFalse(ValidationUtils.nullOrEqual("", "hurz"));
		assertFalse(ValidationUtils.nullOrEqual("hurz", ""));
		assertTrue(ValidationUtils.nullOrEqual(null, new Object[] { null }));
		assertTrue(ValidationUtils.nullOrEqual(null, "hurz"));
		assertTrue(ValidationUtils.nullOrEqual("hurz", "hurz"));

		// two ...
		assertFalse(ValidationUtils.nullOrEqual("", "hurz", "hurz"));
		assertFalse(ValidationUtils.nullOrEqual("hurz", "", "test"));
		assertTrue(ValidationUtils.nullOrEqual(null, "", ""));
		assertTrue(ValidationUtils.nullOrEqual("", "", ""));
		assertTrue(ValidationUtils.nullOrEqual("hurz", "hurz", ""));
		assertTrue(ValidationUtils.nullOrEqual("hurz", "", "hurz"));

		// ... or even three
		assertFalse(ValidationUtils.nullOrEqual("", "hurz", "hurz", "test"));
		assertFalse(ValidationUtils.nullOrEqual("hurz", "", "test", "42"));
		assertTrue(ValidationUtils.nullOrEqual(null, "", "", ""));
		assertTrue(ValidationUtils.nullOrEqual("", "", "", ""));
		assertTrue(ValidationUtils.nullOrEqual("", "", "test", ""));
		assertTrue(ValidationUtils.nullOrEqual("hurz", "hurz", "", "test"));
		assertTrue(ValidationUtils.nullOrEqual("hurz", "", "hurz", "42"));
	}
}