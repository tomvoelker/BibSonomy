package org.bibsonomy.database.params;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.params.beans.TagIndex;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ParamTest {

	/**
	 * Generic tests.
	 * @param param a param object
	 */
	public void genericTest(final GenericParam param) {
		assertEquals(0, param.getTagIndex().size());
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

		param.setSearch("test1 test2");
		assertEquals(" +test1 +test2", param.getSearch());
	}

	/**
	 * tests bookmark
	 */
	@Test
	public void testBookmark() {
		final BookmarkParam param = new BookmarkParam();
		this.genericTest(param);
		// special tests
		assertEquals(ConstantID.BOOKMARK_CONTENT_TYPE.getId(), param.getContentType());
	}

	/**
	 * tests bibtex
	 */
	@Test
	public void testBibtex() {
		final BibTexParam param = new BibTexParam();
		this.genericTest(param);
		// special tests
		assertEquals(ConstantID.BIBTEX_CONTENT_TYPE.getId(), param.getContentType());
	}

	/**
	 * tests tag
	 */
	@Test
	public void testTag() {
		final TagParam param = new TagParam();
		this.genericTest(param);
		// special tests
		param.setTagName("Test");
		assertEquals("Test", param.getTagName());
		assertEquals("test", param.getTagNameLower());
	}
}