package org.bibsonomy.ibatis;

import java.sql.SQLException;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests related to BibTex.
 * 
 * @author mgr
 * @author Christian Schenk
 */
public class BookmarkTest extends AbstractSqlMapTest {

	@Test
	public void getBookmarkByTagNames() {
		bookmarkTemplate("getBookmarkByTagNames");
	}

	@Test
	public void getBookmarkByUserFriends() {
		bookmarkTemplate("getBookmarkByUserFriends");
	}

	@Test
	public void getHomePageBookmark() {
		bookmarkTemplate("getHomePageBookmark");
	}

	@Test
	public void getBookmarkPopular() {
		bookmarkTemplate("getBookmarkPopular");
	}

	@Test
	public void getBookmarkSearch() {
		this.bookmarkParam.setSearch("test");
		bookmarkTemplate("getBookmarkSearch");
		this.bookmarkParam.setUserName(null);
		bookmarkTemplate("getBookmarkSearch");
	}

	@Test
	public void getBookmarkSearchCount() throws SQLException {
		this.bookmarkParam.setSearch("test");
		Integer count = -1;
		count = (Integer) this.sqlMap.queryForObject("getBookmarkSearchCount", this.bookmarkParam);
		assertTrue(count >= 0);

		this.bookmarkParam.setUserName(null);
		count = -1;
		count = (Integer) this.sqlMap.queryForObject("getBookmarkSearchCount", this.bookmarkParam);
		assertTrue(count >= 0);
	}
}