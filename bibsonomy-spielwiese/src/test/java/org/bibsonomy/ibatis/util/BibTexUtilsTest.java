package org.bibsonomy.ibatis.util;

import junit.framework.TestCase;

public class BibTexUtilsTest extends TestCase {

	public void testGetBibtexSelect() {
		assertEquals(339, BibTexUtils.getBibtexSelect("b").length());
		assertFalse("Found trailing comma", BibTexUtils.getBibtexSelect("b").endsWith(","));
	}
}