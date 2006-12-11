package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bibtex.BibTexByUserFriends;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByUserFriends;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

/**
 * TESTCLASS
 * 
 * initialize BibTexUserFriends parameters
 * 
 * @author mgr
 * 
 */
public class ByUserFriendsTest extends AbstractSqlMapTest {

	public BookmarkByUserFriends getDefaultBookmarkByUserFriends() {
		final BookmarkByUserFriends bookVal = new BookmarkByUserFriends();
		bookVal.setUser("stumme");
		bookVal.setGroupType(ConstantID.GROUP_FRIENDS);
		bookVal.setLimit(10);
		bookVal.setOffset(0);
		return bookVal;
	}

	public BibTexByUserFriends getDefaultBibTexByUserFriends() {
		final BibTexByUserFriends bibVal = new BibTexByUserFriends();
		bibVal.setUser("hotho");
		bibVal.setLimit(10);
		bibVal.setOffset(0);
		bibVal.setGroupType(ConstantID.GROUP_FRIENDS);
		return bibVal;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBookmarkByUserFriend() {
		try {
			final BookmarkByUserFriends btn = this.getDefaultBookmarkByUserFriends();
			final List<Bookmark> bookmarks = this.sqlMap.queryForList("getBookmarkByUserFriends", btn);
			printBookmarks(bookmarks);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetBibtexByUserFriend() {
		try {
			final BibTexByUserFriends btn = this.getDefaultBibTexByUserFriends();
			final List<BibTex> bibtexs = this.sqlMap.queryForList("getBibTexByUserFriends", btn);
			printBibTex(bibtexs);
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}