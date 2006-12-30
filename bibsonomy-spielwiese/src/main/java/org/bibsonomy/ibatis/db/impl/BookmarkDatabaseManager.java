package org.bibsonomy.ibatis.db.impl;

import java.util.List;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;

/**
 * Used to retrieve bookmarks from the database.
 *
 * @author Christian Schenk
 */
public class BookmarkDatabaseManager extends AbstractDatabaseManager {

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate this class.
	 */
	BookmarkDatabaseManager() {
	}

	public List<Bookmark> getBookmarkByTagNames(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkByTagNames", param);
	}

	public List<Bookmark> getBookmarkByUserFriends(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkByUserFriends", param);
	}

	public List<Bookmark> getHomePageBookmark(final BookmarkParam param) {
		return this.bookmarkList("getHomePageBookmark", param);
	}

	public List<Bookmark> getBookmarkPopular(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkPopular", param);
	}

	public List<Bookmark> getBookmarkSearch(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkSearch", param);
	}

	public int getBookmarkSearchCount(final BookmarkParam param) {
		return (Integer) this.queryForObject("getBookmarkSearchCount", param);
	}

	public List<Bookmark> getBookmarkViewable(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkViewable", param);
	}
}