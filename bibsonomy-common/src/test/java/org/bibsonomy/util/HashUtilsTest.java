package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

/**
 * @author daill
 * @version $Id$
 * 
 * Testcases for HashUtils
 */
public class HashUtilsTest {
	private final String SPECIAL_CHARS = "üöä!\"§$%&/()=,.-+#'´`";
	
	/**
	 * tests toHexString
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void toHexString() throws UnsupportedEncodingException {
		assertEquals("74657374", HashUtils.toHexString("test".getBytes("UTF-8")));
		assertEquals("6875727a", HashUtils.toHexString("hurz".getBytes("UTF-8")));
		assertEquals("c3bcc3b6c3a42122c2a72425262f28293d2c2e2d2b2327c2b460", HashUtils.toHexString(this.SPECIAL_CHARS.getBytes("UTF-8")));
	}
}
