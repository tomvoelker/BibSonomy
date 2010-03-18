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

package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ResourceUtilsTest {

	/**
	 * tests getResource
	 */
	@Test
	public void getResource() {
		assertEquals(Resource.class, ResourceUtils.getResource("all"));
		assertEquals(BibTex.class, ResourceUtils.getResource("bibtex"));
		assertEquals(Bookmark.class, ResourceUtils.getResource("bookmark"));

		assertEquals(Resource.class, ResourceUtils.getResource(" All"));
		assertEquals(BibTex.class, ResourceUtils.getResource("BIBTEX"));
		assertEquals(Bookmark.class, ResourceUtils.getResource("BookMark "));

		for (final String resourceType : new String[] { "", " ", null, "foo bar" }) {
			try {
				ResourceUtils.getResource(resourceType);
				fail("Expected UnsupportedResourceTypeException");
			} catch (final UnsupportedResourceTypeException ignored) {
			}
		}
	}

	/**
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	@Test
	public void testToString() {
		assertEquals("BIBTEX", ResourceUtils.toString(BibTex.class));
		assertEquals("BOOKMARK", ResourceUtils.toString(Bookmark.class));
		assertEquals("ALL", ResourceUtils.toString(Resource.class));
	}
}