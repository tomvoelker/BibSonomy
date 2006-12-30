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
		this.db.getBookmark().getBookmarkByTagNames(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByUserFriends() {
		this.db.getBookmark().getBookmarkByUserFriends(this.bookmarkParam);
	}

	@Test
	public void getHomePageBookmark() {
		this.db.getBookmark().getHomePageBookmark(this.bookmarkParam);
	}

	@Test
	public void getBookmarkPopular() {
		this.db.getBookmark().getBookmarkPopular(this.bookmarkParam);
	}

	@Test
	public void getBookmarkSearch() {
		this.bookmarkParam.setSearch("test");
		this.db.getBookmark().getBookmarkSearch(this.bookmarkParam);
		this.bookmarkParam.setUserName(null);
		this.db.getBookmark().getBookmarkSearch(this.bookmarkParam);
	}

	@Test
	public void getBookmarkSearchCount() throws SQLException {
		this.bookmarkParam.setSearch("test");
		Integer count = -1;
		count = this.db.getBookmark().getBookmarkSearchCount(this.bookmarkParam);
		assertTrue(count >= 0);

		this.bookmarkParam.setUserName(null);
		count = -1;
		count = (Integer) this.db.getBookmark().getBookmarkSearchCount(this.bookmarkParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkViewable() {
		this.db.getBookmark().getBookmarkViewable(this.bookmarkParam);
	}
}