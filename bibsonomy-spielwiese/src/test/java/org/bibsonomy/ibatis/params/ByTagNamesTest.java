package org.bibsonomy.ibatis.params;

import junit.framework.TestCase;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.beans.TagIndex;
import org.bibsonomy.ibatis.params.bibtex.BibTexByTagNames;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByTagNames;
import org.bibsonomy.ibatis.params.generic.ByTagNames;

public class ByTagNamesTest extends TestCase {

	/**
	 * Tests for all classes that are derived from
	 * {@link org.bibsonomy.ibatis.params.generic.ByTagNames}.
	 */
	public void genericTest(final ByTagNames byTagNames) {
		assertEquals(0, byTagNames.getTagIndex().size());
		assertEquals(ConstantID.GROUP_PUBLIC.getId(), byTagNames.getGroupType());
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
			assertEquals(i + 2, tIdx.getIndex2());
		}
	}

	public void testBookmark() {
		final BookmarkByTagNames byTagNames = new BookmarkByTagNames();
		this.genericTest(byTagNames);
		// special tests
		assertEquals(ConstantID.BOOKMARK_CONTENT_TYPE.getId(), byTagNames.getContentType());
	}

	public void testBibtex() {
		final BibTexByTagNames byTagNames = new BibTexByTagNames();
		this.genericTest(byTagNames);
		// special tests
		assertEquals(ConstantID.BIBTEX_CONTENT_TYPE.getId(), byTagNames.getContentType());
		assertNotNull(byTagNames.getBibtexSelect().length());
	}
}