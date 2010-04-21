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

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class WebUtilsTest {
	
	@Test
	public void testExtractCharset1() {
		assertEquals("UTF-8", WebUtils.extractCharset("text/html; charset=utf-8; qs=1"));
	}

	@Test
	public void testExtractCharset2() {
		assertEquals("ISO-8859-1", WebUtils.extractCharset("text/html; charset=ISO-8859-1"));
	}

	@Test
	public void testExtractCharset3() {
		assertEquals("LATIN1", WebUtils.extractCharset("text/html; charset=latin1; qs=1"));
	}

	/**
	 * tests one-level redirect
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	public void testRedirectUrl1() throws MalformedURLException {
		assertEquals("http://www.bibsonomy.org/groups", WebUtils.getRedirectUrl(new URL("http://www.bibsonomy.org/group")).toString());
	}
	
	/**
	 * tests three-level redirect
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	@Ignore
	public void testRedirectUrl() throws MalformedURLException {
		assertEquals("http://journals.cambridge.org/action/displayAbstract?fromPage=online&aid=5123720", WebUtils.getRedirectUrl(new URL("http://dx.doi.org/10.1017/S0952523808080978")).toString());
	}
	
	@Test
	public void testBuildCookieString() {
		final List<String> cookies = new LinkedList<String>();
		assertEquals("", WebUtils.buildCookieString(cookies));
	}

	@Test
	public void testBuildCookieString1() {
		final List<String> cookies = Arrays.asList("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811; Path=/");
		assertEquals("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811; Path=/", WebUtils.buildCookieString(cookies));
	}

	@Test
	public void testBuildCookieString3() {
		final List<String> cookies = Arrays.asList("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811", "Path=/");
		assertEquals("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811;Path=/", WebUtils.buildCookieString(cookies));
	}
	
}
