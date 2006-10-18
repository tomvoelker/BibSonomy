package org.bibsonomy.ibatis.params;

import junit.framework.TestCase;

public class ByTagNamesTest extends TestCase {

	public void testIt() {
		final ByTagNames byTagNames = new ByTagNames();

		assertEquals(0, byTagNames.getTagIndex().size());
		assertEquals(false, byTagNames.isCaseSensitive());

		byTagNames.addTagName("tag1");
		assertEquals(1, byTagNames.getTagIndex().size());
		assertEquals(1, byTagNames.getMaxTagIndex());

		byTagNames.addTagName("tag2");
		assertEquals(2, byTagNames.getTagIndex().size());
		assertEquals(2, byTagNames.getMaxTagIndex());

		byTagNames.addTagName("tag3");
		assertEquals(3, byTagNames.getTagIndex().size());
		assertEquals(3, byTagNames.getMaxTagIndex());

		for (int i = 0; i < byTagNames.getTagIndex().size(); i++) {
			final TagIndex tIdx = byTagNames.getTagIndex().get(i);
			assertEquals("tag" + (i + 1), tIdx.getTagName());
			assertEquals(i + 1, tIdx.getIndex());
		}
	}
}