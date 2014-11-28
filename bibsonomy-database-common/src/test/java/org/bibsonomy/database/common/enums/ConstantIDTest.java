/**
 * BibSonomy-Database-Common - Helper classes for database interaction
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
package org.bibsonomy.database.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.junit.Test;

/**
 * @author rja
 */
public class ConstantIDTest {

	@Test
	public void testGetContentTypeByClass() {
		assertEquals(ConstantID.BOOKMARK_CONTENT_TYPE, ConstantID.getContentTypeByClass(Bookmark.class));
		assertEquals(ConstantID.BIBTEX_CONTENT_TYPE, ConstantID.getContentTypeByClass(BibTex.class));
		assertEquals(ConstantID.BIBTEX_CONTENT_TYPE, ConstantID.getContentTypeByClass(GoldStandardPublication.class));
		assertEquals(ConstantID.ALL_CONTENT_TYPE, ConstantID.getContentTypeByClass(Resource.class));
	}

	@Test
	public void testGetClassByContentType() {
		assertEquals(Bookmark.class, ConstantID.getClassByContentType(ConstantID.BOOKMARK_CONTENT_TYPE));
		assertEquals(BibTex.class, ConstantID.getClassByContentType(ConstantID.BIBTEX_CONTENT_TYPE));
		assertEquals(Resource.class, ConstantID.getClassByContentType(ConstantID.ALL_CONTENT_TYPE));
	}

	@Test
	public void testGetClassByContentTypeInt() throws Exception {
		assertEquals(Resource.class, ConstantID.getClassByContentType(0));
		assertEquals(Bookmark.class, ConstantID.getClassByContentType(1));
		assertEquals(BibTex.class, ConstantID.getClassByContentType(2));
		try {
			ConstantID.getClassByContentType(3);
			fail("expected " + UnsupportedResourceTypeException.class.getName());
		} catch (final UnsupportedResourceTypeException e) {
			
		}
		try {
			ConstantID.getClassByContentType(-1);
			fail("expected " + UnsupportedResourceTypeException.class.getName());
		} catch (final UnsupportedResourceTypeException e) {
			
		}

	}
	
}
