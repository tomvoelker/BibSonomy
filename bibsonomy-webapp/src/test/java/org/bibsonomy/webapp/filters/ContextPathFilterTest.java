/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * @author rja
 */
public class ContextPathFilterTest {

	@Test
	public void testStripContextPath() {
		final ContextPathFilter.ContextPathFreeRequest req = new ContextPathFilter.ContextPathFreeRequest(new MockHttpServletRequest());
		
		assertEquals("/login", stripContextPath(req, "/bibsonomy-webapp/login", "/bibsonomy-webapp"));
		assertEquals("/login", stripContextPath(req, "/login", ""));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", stripContextPath(req, "http://my.biblicious.org/bibsonomy2/login_openid?rememberMe=true", "/bibsonomy2"));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", stripContextPath(req, "http://my.biblicious.org/login_openid?rememberMe=true", ""));
	}
	
	@Test
	public void testStripContextPath2() {
	final ContextPathFilter.ContextPathFreeRequest req = new ContextPathFilter.ContextPathFreeRequest(new MockHttpServletRequest());
		
		assertEquals("/login", req.stripContextPath("/bibsonomy-webapp/login", "/bibsonomy-webapp"));
		assertEquals("/login", req.stripContextPath("/login", ""));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", req.stripContextPath("http://my.biblicious.org/bibsonomy2/login_openid?rememberMe=true", "/bibsonomy2"));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", req.stripContextPath("http://my.biblicious.org/login_openid?rememberMe=true", ""));
	}
	
	private String stripContextPath(final ContextPathFilter.ContextPathFreeRequest req, final String s, final String c) {
		return req.stripContextPath(new StringBuffer(s), c).toString();
	}
}
