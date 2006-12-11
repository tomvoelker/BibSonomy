package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.HomePageBibTex;
import org.bibsonomy.ibatis.params.bookmark.HomePageBookmark;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

/**
 * TESTCLASS
 * 
 * initialize HomePageforBibtex parameters
 * 
 * @author mgr
 * 
 */
public class HomePageTest extends AbstractSqlMapTest {

	public HomePageBookmark getDefaultHomePageBookmark() {
		final HomePageBookmark bookVal = new HomePageBookmark();
		bookVal.setLimit(20);
		bookVal.setOffset(0);
		bookVal.setGroupType(ConstantID.GROUP_PUBLIC);
		return bookVal;
	}

	public HomePageBibTex getDefaultHomePageBibtex() {
		final HomePageBibTex bibVal = new HomePageBibTex();
		bibVal.setGroupType(ConstantID.GROUP_FRIENDS);
		bibVal.setLimit(15);
		bibVal.setOffset(0);
		return bibVal;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testHomePageBookmarkTest() {
		try {
			final HomePageBookmark btn = this.getDefaultHomePageBookmark();
			final List<Bookmark> bookmarks = this.sqlMap.queryForList("getHomePageBookmark", btn);
			printBookmarks(bookmarks);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetHomePageBibtexTest() {
		try {
			final HomePageBibTex btn = this.getDefaultHomePageBibtex();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getHomePageBibTex", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}