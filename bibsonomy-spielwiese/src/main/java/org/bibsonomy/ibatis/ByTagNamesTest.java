package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibTexByTagNames;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByTagNames;
import org.bibsonomy.ibatis.params.generic.ByTagNames;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;

public class ByTagNamesTest extends AbstractSqlMapTest {

	/**
	 * The ByTagNames-bean is used at various locations and has been refactored
	 * to this method.
	 */
	public void setDefaultsOnByTagNamesBean(final ByTagNames btn) {
		btn.setGroupType(ConstantID.GROUP_PUBLIC);
		btn.setLimit(5);
		btn.setOffset(0);
		// btn.setCaseSensitive(true);
		// btn.addTagName("web");
		// btn.addTagName("online");
		btn.addTagName("community");
	}

	public BookmarkByTagNames getDefaultBookmarkByTagNames() {
		final BookmarkByTagNames rVal = new BookmarkByTagNames();
		this.setDefaultsOnByTagNamesBean(rVal);
		return rVal;
	}

	public BibTexByTagNames getDefaultBibTexByTagNames() {
		final BibTexByTagNames rVal = new BibTexByTagNames();
		this.setDefaultsOnByTagNamesBean(rVal);
		return rVal;
	}

	@SuppressWarnings("unchecked")
	public void testGetBookmarkByTagNames() {
		try {
			final BookmarkByTagNames btn = this.getDefaultBookmarkByTagNames();

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
			final BibTexByTagNames btn = this.getDefaultBibTexByTagNames();

			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibtexByTagNames", btn);

			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}