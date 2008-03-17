package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Testcase for the StringUtils class
 */
public class StringUtilsTest {

	private final String SPECIAL_CHARS = "üöä!\"§$%&/()=,.-+#'´`";

	/**
	 * tests getMD5Hash what else ?
	 */
	@Test
	public void getMD5Hash() {
		assertEquals("098f6bcd4621d373cade4e832627b4f6", StringUtils.getMD5Hash("test"));
		assertEquals("04a80d0dd1f311afd2f5b652504eb39d", StringUtils.getMD5Hash("hurz"));
		assertEquals("c54b92795c745d5fc2c7e1dcf782a489", StringUtils.getMD5Hash(this.SPECIAL_CHARS));
	}

	/**
	 * tests toHexString what else ?
	 */
	@Test
	public void toHexString() {
		assertEquals("74657374", StringUtils.toHexString("test".getBytes()));
		assertEquals("6875727a", StringUtils.toHexString("hurz".getBytes()));
		assertEquals("9f9a8a2122a42425262f28293d2c2e2d2b2327ab60", StringUtils.toHexString(this.SPECIAL_CHARS.getBytes()));
	}

	/**
	 * tests matchExtension what else ?
	 */
	@Test
	public void matchExtension() {
		assertTrue(StringUtils.matchExtension("test.ps", "ps"));
		assertTrue(StringUtils.matchExtension("test.ps", "PS"));
		assertTrue(StringUtils.matchExtension("ps", "ps"));
		assertFalse(StringUtils.matchExtension("test.ps", "dvi"));
		assertFalse(StringUtils.matchExtension("test.ps", "dvi", "DOC", "pdf"));
	}

	/**
	 * tests getStringFromList what else ?
	 */
	@Test
	public void getStringFromList() {
		assertEquals("[]", StringUtils.getStringFromList(Collections.<String> emptyList()));

		final List<String> someStrings = new ArrayList<String>();
		for (final int i : new int[] { 1, 2, 3 })
			someStrings.add("test" + i);
		assertEquals("[test1,test2,test3]", StringUtils.getStringFromList(someStrings));
	}
}