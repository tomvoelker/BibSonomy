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
}