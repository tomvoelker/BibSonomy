/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
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
package org.bibsonomy.rest.renderer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dzo
 */
public class UrlRendererTest {

	private static final UrlRenderer RENDERER = new UrlRenderer("");

	@Test
	public void testCreateHrefForUser() throws Exception {
		assertEquals("/users/testuser%201", RENDERER.createHrefForUser("testuser 1"));
	}

	@Test
	public void testCreateHrefForTag() throws Exception {
		assertEquals("/tags/test%20test", RENDERER.createHrefForTag("test test"));
	}

	@Test
	public void testCreateHrefForGroup() throws Exception {
		assertEquals("/groups/testgroup%201", RENDERER.createHrefForGroup("testgroup 1"));
	}

	@Test
	public void testCreateHrefForResource() throws Exception {
		assertEquals("/users/testuser%201/posts/123123", RENDERER.createHrefForResource("testuser 1", "123123"));
	}

	@Test
	public void testCreateHrefForResourceDocument() throws Exception {
		assertEquals("/users/testuser1/posts/112/documents/Test%20123.pdf", RENDERER.createHrefForResourceDocument("testuser1", "112", "Test 123.pdf"));
	}
}
