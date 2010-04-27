/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.rest.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RenderingFormatTest {

	@Test
	public void testGetRenderingFormat() {
		assertEquals(RenderingFormat.XML, RenderingFormat.getRenderingFormat("xml"));
		assertEquals(RenderingFormat.XML, RenderingFormat.getRenderingFormat("xMl"));

		try {
			RenderingFormat.getRenderingFormat(null);
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
		
		try {
			RenderingFormat.getRenderingFormat("someUnsupportedRenderingFormat");
			fail("Should throw exception");
		} catch (final ValidationException ex) {
		}		
	}

	@Test
	public void testToString() {
		assertEquals(RenderingFormat.XML.toString(), "XML");
	}
}