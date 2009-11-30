/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class TagTest {

	/**
	 * tests the basics
	 */
	@Test
	public void simple() {
		// constructor
		assertNull(new Tag().getName());
		assertEquals("test-tag", new Tag("test-tag").getName());

		// lazy initialization
		assertNotNull(new Tag().getPosts());
		assertNotNull(new Tag().getSubTags());
		assertNotNull(new Tag().getSuperTags());
	}

	/**
	 * tests setSubtagsString
	 */
	@Test
	public void setSubtagsString() {
		final Tag t1 = new Tag("t1");
		t1.setSubtagsString("t2 t3 t4");
		assertEquals(3, t1.getSubTags().size());
	}
}