/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 */
public class WebUtilsTest {
	
	/**
	 * 
	 */
	@Test
	public void testExtractCharset1() {
		assertEquals(StringUtils.CHARSET_UTF_8, WebUtils.extractCharset("text/html; charset=utf-8; qs=1"));
	}

	/**
	 * 
	 */
	@Test
	public void testExtractCharset2() {
		assertEquals("ISO-8859-1", WebUtils.extractCharset("text/html; charset=ISO-8859-1"));
	}

	/**
	 * 
	 */
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
		assertEquals("https://www.bibsonomy.org/groups", WebUtils.getRedirectUrl(new URL("http://www.bibsonomy.org/group")).toString());
	}
	
	/**
	 * tests three-level redirect
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	@Ignore
	public void testRedirectUrl() throws MalformedURLException {
		assertEquals("https://www.cambridge.org/core/journals/visual-neuroscience/article/abs/image-statistics-at-the-point-of-gaze-during-human-navigation/0C985D25DDBD65829EA88D1E37ECEFBF", WebUtils.getRedirectUrl(new URL("https://dx.doi.org/10.1017/S0952523808080978")).toString());
	}
	
	/**
	 * 
	 */
	@Test
	public void testBuildCookieString() {
		final List<String> cookies = new LinkedList<>();
		assertEquals("", WebUtils.buildCookieString(cookies));
	}

	/**
	 * 
	 */
	@Test
	public void testBuildCookieString1() {
		final List<String> cookies = Collections.singletonList("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811; Path=/");
		assertEquals("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811; Path=/", WebUtils.buildCookieString(cookies));
	}

	/**
	 * 
	 */
	@Test
	public void testBuildCookieString3() {
		final List<String> cookies = Arrays.asList("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811", "Path=/");
		assertEquals("Set-Cookie: JSESSIONID=39246A4F2932FD42D73F2058B00C4811;Path=/", WebUtils.buildCookieString(cookies));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGetContentAsString2() throws Exception {
		/*
		 * Just check, if we get some output from BibSonomy.
		 */
		final String s = WebUtils.getContentAsString(new URL("https://www.bibsonomy.org/tag/web?items=1000"), null);
		assertTrue(s.length() > 0);
		/*
		 * We have a 3MB limit ...
		 */
		assertTrue(s.length() < 3 * 1024 * 1024);
	}
	
	
}
