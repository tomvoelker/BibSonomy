package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
	 */
	@Test
	public void encodeURLExceptReservedChars() {
		// these characters shouldn't be encoded
		assertEquals("$&+,/:;?@", UrlUtils.encodeURLExceptReservedChars("$&+,/:;?@"));
		// but spaces should
		assertEquals("$+&+++,+/+:+;+?+@", UrlUtils.encodeURLExceptReservedChars("$ & + , / : ; ? @"));

		try {
			assertEquals(URLEncoder.encode("should_be_same_as_plain_encoded_']} §°^äöü*ÄÖU", "UTF-8"), UrlUtils.encodeURLExceptReservedChars("should_be_same_as_plain_encoded_']} §°^äöü*ÄÖU"));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			fail();
		}

		assertEquals("http://www.bibsonomy.org/user/%7Cthe_man%7C/?bookmark.start=10&bibtex.start=0", UrlUtils.encodeURLExceptReservedChars("http://www.bibsonomy.org/user/|the_man|/?bookmark.start=10&bibtex.start=0"));
	}

}