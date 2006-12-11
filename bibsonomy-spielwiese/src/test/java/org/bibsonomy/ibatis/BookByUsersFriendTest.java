package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByUserFriends;
import org.bibsonomy.model.Bookmark;

/**
 * TESTCLASS
 * 
 * initialize BookmarkByUserFriends parameters
 * 
 * @author mgr
 * 
 */
public class BookByUsersFriendTest extends AbstractSqlMapTest {

	public BookmarkByUserFriends getDefaultBookmarkByUserFriends() {
		final BookmarkByUserFriends bookVal = new BookmarkByUserFriends();

		bookVal.setUser("stumme");
		bookVal.setGroupType(ConstantID.GROUP_FRIENDS);
		bookVal.setItemCount(10);
		bookVal.setStartBook(0);

		return bookVal;
	}

	@SuppressWarnings("unchecked")
	public void testGetBookmarkByUsersFriend() {
		try {
			final BookmarkByUserFriends btn = this.getDefaultBookmarkByUserFriends();

			final List<Bookmark> bookmarks = this.sqlMap.queryForList("getBookmarkByUserFriends", btn);

			printBookmarks(bookmarks);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}