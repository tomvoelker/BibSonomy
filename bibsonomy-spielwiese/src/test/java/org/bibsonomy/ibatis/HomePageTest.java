package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.HomePageBibtex;
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
		bookVal.setItemCount(20);
		bookVal.setStartBook(0);
		bookVal.setGroupType(ConstantID.GROUP_PUBLIC);
		return bookVal;
	}

	public HomePageBibtex getDefaultHomePageBibtex() {
		final HomePageBibtex bibVal = new HomePageBibtex();
		bibVal.setGroupType(ConstantID.GROUP_FRIENDS);
		bibVal.setSimValue(ConstantID.SIM_HASH);
		bibVal.setItemCount(15);
		bibVal.setStartBib(0);
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
			final HomePageBibtex btn = this.getDefaultHomePageBibtex();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getHomePageBibTex", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}