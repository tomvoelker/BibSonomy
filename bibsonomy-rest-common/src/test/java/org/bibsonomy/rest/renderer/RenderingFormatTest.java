/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
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

package org.bibsonomy.rest.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RenderingFormatTest {
	
	/**
	 * tests {@link RenderingFormat#getMediaTypeByFormat(String)}
	 */
	@Test
	public void testGetRenderingFormat() {
		assertEquals(RenderingFormat.XML, RenderingFormat.getMediaTypeByFormat("xml"));
		assertEquals(RenderingFormat.XML, RenderingFormat.getMediaTypeByFormat("xMl"));
		assertEquals(RenderingFormat.PDF, RenderingFormat.getMediaTypeByFormat("PDF"));
		assertEquals(RenderingFormat.JSON, RenderingFormat.getMediaTypeByFormat("json"));

		assertNull(RenderingFormat.getMediaTypeByFormat("someUnsupportedRenderingFormat"));
	}
	
	@Test
	public void testGetMediaType() {
		assertEquals(RenderingFormat.XML, RenderingFormat.getMediaType("text/xml"));
		assertEquals(RenderingFormat.JSON, RenderingFormat.getMediaType("application/json"));
		assertEquals(RenderingFormat.APP_XML, RenderingFormat.getMediaType("application/xml; charset=UTF-8"));
		try {
			RenderingFormat.getMediaType("someUnsupportedRenderingFormat");
			fail();
		} catch (final IllegalArgumentException e) {
			// ok
		}
	}

	/**
	 * 
	 */
	@Test
	public void testToString() {
		// only for backward compatibility (rest server)
		assertEquals(RenderingFormat.XML.toString(), "XML");
	}
}