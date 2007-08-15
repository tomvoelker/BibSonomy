package webdav.helper;

import junit.framework.TestCase;

public class IdiomHelperTest extends TestCase {

	public void testGetTernaryExp() {
		assertEquals("left", IdiomHelper.getTernaryExp(true, "left", "right"));
		assertEquals("right", IdiomHelper.getTernaryExp(false, "left", "right"));
	}
}