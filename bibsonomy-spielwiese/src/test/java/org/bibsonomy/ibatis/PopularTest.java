package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.params.bibtex.BibTexByHash;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByTagNames;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

public class PopularTest extends AbstractSqlMapTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBookmarkPopular() {
		try {
			final BookmarkByTagNames btn = TestHelper.getDefaultBookmarkByTagNames();
			final List<Bookmark> bookmarks = this.sqlMap.queryForList("getBookmarkPopular", btn);
			printBookmarks(bookmarks);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBibTexPopular() {
		try {
			final BibTexByHash btn = ByHashTest.getDefaultBibTexByHash();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexPopular", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}