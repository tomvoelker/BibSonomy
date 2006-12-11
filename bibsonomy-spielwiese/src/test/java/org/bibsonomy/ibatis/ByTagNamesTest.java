package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.params.bibtex.BibTexByTagNames;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByTagNames;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;

public class ByTagNamesTest extends AbstractSqlMapTest {

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

	@SuppressWarnings("unchecked")
	public void testGetBibTexByTagNames() {
		try {
			final BibTexByTagNames btn = TestHelper.getDefaultBibTexByTagNames();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibtexByTagNames", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}