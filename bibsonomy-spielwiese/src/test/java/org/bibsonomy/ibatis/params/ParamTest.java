package org.bibsonomy.ibatis.params;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.beans.TagIndex;
import org.junit.Test;

public class ParamTest {

	/**
	 * Generic tests.
	 */
	public void genericTest(final GenericParam param) {
		assertEquals(0, param.getTagIndex().size());
		assertEquals(ConstantID.GROUP_PUBLIC.getId(), param.getGroupType());
		assertEquals(false, param.isCaseSensitiveTagNames());

		for (final int i : new int[] { 1, 2, 3 }) {
			param.addTagName("tag" + i);
			assertEquals(i, param.getTagIndex().size());
			assertEquals(i, param.getMaxTagIndex());
		}

		for (int i = 0; i < param.getTagIndex().size(); i++) {
			final TagIndex tIdx = param.getTagIndex().get(i);
			assertEquals("tag" + (i + 1), tIdx.getTagName());
			assertEquals(i + 1, tIdx.getIndex());
			assertEquals(i + 2, tIdx.getIndex2());
		}
	}

	@Test
	public void testBookmark() {
		final BookmarkParam param = new BookmarkParam();
		this.genericTest(param);
		// special tests
		assertEquals(ConstantID.BOOKMARK_CONTENT_TYPE.getId(), param.getContentType());
	}

	@Test
	public void testBibtex() {
		final BibTexParam param = new BibTexParam();
		this.genericTest(param);
		// special tests
		assertEquals(ConstantID.BIBTEX_CONTENT_TYPE.getId(), param.getContentType());
	}
}