package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bookmark.HomePageBookmark;
import org.bibsonomy.model.Bookmark;

/**
 * TESTCLASS
 * 
 * initialize HomePageforBookmark parameters
 * 
 * @author mgr
 * 
 */
public class HomePageBookmarkTest extends AbstractSqlMapTest {

	public HomePageBookmark getDefaultHomePageBookmark() {
		final HomePageBookmark bookVal = new HomePageBookmark();
		bookVal.setItemCount(20);
		bookVal.setStartBook(0);
		bookVal.setGroupType(ConstantID.GROUP_PUBLIC);
		return bookVal;
	}

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
}