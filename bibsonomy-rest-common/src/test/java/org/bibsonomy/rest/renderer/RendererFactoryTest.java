/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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
import org.bibsonomy.rest.renderer.impl.json.JSONRenderer;
import org.bibsonomy.rest.renderer.impl.xml.XMLRenderer;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class RendererFactoryTest {

	private final RendererFactory rendererFactory = new RendererFactory(new UrlRenderer("/"));
	
	@Test
	public void testGetRenderer() {
		assertTrue(this.rendererFactory.getRenderer(RenderingFormat.XML) instanceof XMLRenderer);
		assertTrue(this.rendererFactory.getRenderer(RenderingFormat.PDF) instanceof XMLRenderer);
		assertTrue(this.rendererFactory.getRenderer(RenderingFormat.APP_XML) instanceof XMLRenderer);
		
		assertTrue(this.rendererFactory.getRenderer(RenderingFormat.JSON) instanceof JSONRenderer);
	}
	
	@Test(expected = InternServerException.class)
	public void wrongUsageGetRenderer() {
		this.rendererFactory.getRenderer(null);
	}
}