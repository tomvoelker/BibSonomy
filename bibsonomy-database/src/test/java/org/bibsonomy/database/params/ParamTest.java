/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.params;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class ParamTest {

	/**
	 * Generic tests.
	 * @param param a param object
	 */
	public void genericTest(final GenericParam param) {
		assertEquals(0, param.getTagIndex().size());
		assertFalse(param.isCaseSensitiveTagNames());

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

		final String rawSearch = "test1 test2";
		param.setSearch(rawSearch);
		assertEquals(rawSearch, param.getSearch());
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
	public void testPublication() {
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