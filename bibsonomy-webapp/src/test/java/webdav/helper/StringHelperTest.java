package webdav.helper;

import junit.framework.TestCase;

public class StringHelperTest extends TestCase {

	public void testGetPositiveCount() {
		assertEquals(true, StringHelper.getPositiveCount("1"));
		assertEquals(true, StringHelper.getPositiveCount("4"));
		assertEquals(true, StringHelper.getPositiveCount("200"));
		assertEquals(false, StringHelper.getPositiveCount("0"));
		assertEquals(false, StringHelper.getPositiveCount("-12"));
		assertEquals(false, StringHelper.getPositiveCount("string"));
		assertEquals(false, StringHelper.getPositiveCount("test test"));
	}

	public void testGetBool() {
		assertEquals(true, StringHelper.getBool("1"));
		assertEquals(true, StringHelper.getBool("4"));
		assertEquals(true, StringHelper.getBool("true"));
		assertEquals(true, StringHelper.getBool("TRUE"));
		assertEquals(true, StringHelper.getBool("TrUe"));
		assertEquals(false, StringHelper.getBool("0"));
		assertEquals(false, StringHelper.getBool("false"));
		assertEquals(false, StringHelper.getBool("blah"));
	}
}