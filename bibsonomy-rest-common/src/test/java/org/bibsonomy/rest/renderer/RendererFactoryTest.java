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

package org.bibsonomy.rest.renderer;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.renderer.impl.JSONRenderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RendererFactoryTest {

	@Test
	public void testGetRenderer() {
		assertTrue(RendererFactory.getRenderer(RenderingFormat.XML) instanceof XMLRenderer);
		assertTrue(RendererFactory.getRenderer(RenderingFormat.PDF) instanceof XMLRenderer);
		
		assertTrue(RendererFactory.getRenderer(RenderingFormat.JSON) instanceof JSONRenderer);
		assertTrue(RendererFactory.getRenderer(new RenderingFormat("application", RenderingFormat.TYPE_WILDCARD)) instanceof XMLRenderer);
		
	}
	
	@Test(expected = InternServerException.class)
	public void wrongUsageGetRenderer() {
		RendererFactory.getRenderer(null);
	}
}