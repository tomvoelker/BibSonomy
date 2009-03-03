package org.bibsonomy.util;

import static org.junit.Assert.*;

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

	
	
}
