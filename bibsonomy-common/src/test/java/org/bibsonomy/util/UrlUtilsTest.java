/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class UrlUtilsTest {

	/**
	 * tests cleanUrl
	 */
	@Test
	public void cleanUrl() {
		assertEquals(null, UrlUtils.cleanUrl(null));
		assertEquals("", UrlUtils.cleanUrl(""));

		for (final String url : new String[] { "http://", "ftp://", "file://", "/brokenurl#", "gopher://", "https://" }) {
			assertEquals(url, UrlUtils.cleanUrl(url));
			assertEquals(6000, UrlUtils.cleanUrl(generateStringWithNChars(url, 8000)).length());
		}

		assertEquals("test", UrlUtils.cleanUrl("\\url{test}"));
		assertEquals(6000, UrlUtils.cleanUrl(generateStringWithNChars("\\url{test}", 8000)).length());

		assertEquals("/brokenurl#broken", UrlUtils.cleanUrl("broken"));
	}

	private static final String generateStringWithNChars(final String prefix, final int n) {
		final StringBuilder buf = new StringBuilder();
		buf.append(prefix);
		for (int i = 0; i < n; i++) {
			buf.append("x");
		}
		return buf.toString();
	}

	/**
	 * tests generateStringWithNChars
	 */
	@Test
	public void generateStringWithNChars() {
		assertEquals("test", generateStringWithNChars("test", 0));
		assertEquals("testxx", generateStringWithNChars("test", 2));
		assertEquals(1000, generateStringWithNChars("test", 996).length());
	}

	/**
	 * tests setParam
	 */
	@Test
	public void setParam() {
		// modifying existing parameter
		assertEquals("http://www.test.com?lang=en", UrlUtils.setParam("http://www.test.com?lang=de", "lang", "en"));
		assertEquals("http://www.test.com?bla=blub&param=newValue&pi=po#anchor1", UrlUtils.setParam("http://www.test.com?bla=blub&param=oldValue&pi=po#anchor1", "param", "newValue"));
		// append parameter when no query exists
		assertEquals("http://www.test.123.com?param=value", UrlUtils.setParam("http://www.test.123.com", "param", "value"));
		// append parameter when query exists
		assertEquals("http://www.test.123.com?bla=blub&param=value", UrlUtils.setParam("http://www.test.123.com?bla=blub", "param", "value"));
	}
	
	/**
	 * tests removeParam
	 */
	@Test
	public void removeParam() {
		// remove existing parameter
		assertEquals("http://www.test.com", UrlUtils.removeParam("http://www.test.com?lang=de", "lang"));		
		// remove existing parameter, adapt other ones (change & to ?)
		assertEquals("http://www.test.com?param=oldValue&pi=po#anchor1", UrlUtils.removeParam("http://www.test.com?bla=blub&param=oldValue&pi=po#anchor1", "bla"));
		// remove existing parameter, leave other ones as they are
		assertEquals("http://www.test.com?bla=blub&pi=po#anchor1", UrlUtils.removeParam("http://www.test.com?bla=blub&param=oldValue&pi=po#anchor1", "param"));
		// try to remove non-existent parameter when no param exists
		assertEquals("http://www.test.123.com", UrlUtils.removeParam("http://www.test.123.com", "param"));		
	}

	/**
	 * tests encodeURLExceptReservedChars
	 * @throws UnsupportedEncodingException iff utf-8 is not supported
	 */
	@Test
	public void encodeURLExceptReservedChars() throws UnsupportedEncodingException{
		// these characters shouldn't be encoded
		assertEquals("$&+,/:;?@", UrlUtils.encodeURLExceptReservedChars("$&+,/:;?@"));
		// but spaces should
		assertEquals("$+&+++,+/+:+;+?+@", UrlUtils.encodeURLExceptReservedChars("$ & + , / : ; ? @"));
		
		final String testString = "should_be_same_as_plain_encoded_']} §°^äöü*ÄÖU";
		assertEquals(URLEncoder.encode(testString, StringUtils.CHARSET_UTF_8), UrlUtils.encodeURLExceptReservedChars(testString));
		assertEquals("http://www.bibsonomy.org/user/%7Cthe_man%7C/?bookmark.start=10&bibtex.start=0", UrlUtils.encodeURLExceptReservedChars("http://www.bibsonomy.org/user/|the_man|/?bookmark.start=10&bibtex.start=0"));
	}
	
	/**
	 * tests {@link UrlUtils#getFirstPathElement(String)}
	 */
	@Test
	public void testGetFirstPathElement(){
		assertEquals("a", UrlUtils.getFirstPathElement("a"));
		assertEquals("a", UrlUtils.getFirstPathElement("/a"));
		assertEquals("a", UrlUtils.getFirstPathElement("/a/"));
		assertEquals("a", UrlUtils.getFirstPathElement("/a/b"));
		assertEquals("a", UrlUtils.getFirstPathElement("/a/b/"));
		assertEquals("a", UrlUtils.getFirstPathElement("/a/b/c"));
		assertEquals("aa", UrlUtils.getFirstPathElement("aa"));
		assertEquals("aa", UrlUtils.getFirstPathElement("/aa"));
		assertEquals("aa", UrlUtils.getFirstPathElement("/aa/"));
		assertEquals("aa", UrlUtils.getFirstPathElement("/aa/b"));
		assertEquals("aa", UrlUtils.getFirstPathElement("/aa/b/"));
		assertEquals("aa", UrlUtils.getFirstPathElement("/aa/b/c"));
		assertEquals("aaaaaa", UrlUtils.getFirstPathElement("/aaaaaa/b/c"));
	}

}