package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.params.bibtex.BibTexByTagNames;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByTagNames;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

public class ByTagNamesTest extends AbstractSqlMapTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBookmarkByTagNames() {
		try {
			final BookmarkByTagNames btn = TestHelper.getDefaultBookmarkByTagNames();
			final List<Bookmark> bookmarks = this.sqlMap.queryForList("getBookmarkByTagNames", btn);
			printBookmarks(bookmarks);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBibTexByTagNames() {
		try {
			final BibTexByTagNames btn = TestHelper.getDefaultBibTexByTagNames();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexByTagNames", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}