package org.bibsonomy.ibatis;

import org.junit.Test;

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
}