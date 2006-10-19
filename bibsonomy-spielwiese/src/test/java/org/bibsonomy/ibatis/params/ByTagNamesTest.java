package org.bibsonomy.ibatis.params;

import org.bibsonomy.ibatis.params.beans.TagIndex;

import junit.framework.TestCase;

public class ByTagNamesTest extends TestCase {

	public void testIt() {
		final ByTagNames byTagNames = new ByTagNames();

		assertEquals(0, byTagNames.getTagIndex().size());
		assertEquals(false, byTagNames.isCaseSensitive());

		for (final int i : new int[] { 1, 2, 3 }) {
			byTagNames.addTagName("tag" + i);
			assertEquals(i, byTagNames.getTagIndex().size());
			assertEquals(i, byTagNames.getMaxTagIndex());
		}

		for (int i = 0; i < byTagNames.getTagIndex().size(); i++) {
			final TagIndex tIdx = byTagNames.getTagIndex().get(i);
			assertEquals("tag" + (i + 1), tIdx.getTagName());
			assertEquals(i + 1, tIdx.getIndex());
		}
	}
}