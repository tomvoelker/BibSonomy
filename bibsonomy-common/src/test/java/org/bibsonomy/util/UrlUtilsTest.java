package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
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
		assertEquals("http://www.test.com?lang=en", 
					UrlUtils.setParam("http://www.test.com?lang=de", "lang", "en"));
		assertEquals("http://www.test.com?bla=blub&param=newValue&pi=po#anchor1", 
				UrlUtils.setParam("http://www.test.com?bla=blub&param=oldValue&pi=po#anchor1", "param", "newValue"));		
		// append parameter when no query exists
		assertEquals("http://www.test.123.com?param=value", 
				UrlUtils.setParam("http://www.test.123.com", "param", "value"));
		// append parameter when query exists
		assertEquals("http://www.test.123.com?bla=blub&param=value", 
				UrlUtils.setParam("http://www.test.123.com?bla=blub", "param", "value"));
	}
}