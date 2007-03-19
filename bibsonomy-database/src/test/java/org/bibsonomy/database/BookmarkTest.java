package org.bibsonomy.database;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.bibsonomy.common.enums.ConstantID;
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
		this.db.getBookmark().getBookmarkByTagNames(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByTagNamesForUser() {
		this.db.getBookmark().getBookmarkByTagNamesForUser(this.bookmarkParam);
		this.resetParameters();
		this.bookmarkParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.db.getBookmark().getBookmarkByTagNamesForUser(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByConceptForUser() {
		this.db.getBookmark().getBookmarkByConceptForUser(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByUserFriends() {
		this.db.getBookmark().getBookmarkByUserFriends(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForHomepage() {
		this.db.getBookmark().getBookmarkForHomepage(this.bookmarkParam);
	}

	@Test
	public void getBookmarkPopular() {
		this.db.getBookmark().getBookmarkPopular(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByHash() {
		this.db.getBookmark().getBookmarkByHash(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByHashCount() {
		Integer count = -1;
		count = this.db.getBookmark().getBookmarkByHashCount(this.bookmarkParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkByHashForUser() {
		this.db.getBookmark().getBookmarkByHashForUser(this.bookmarkParam);
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

	@Test
	public void getBookmarkForGroup() {
		this.db.getBookmark().getBookmarkForGroup(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForGroupCount() {
		Integer count = -1;
		count = this.db.getBookmark().getBookmarkForGroupCount(this.bookmarkParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkForGroupByTag() {
		this.db.getBookmark().getBookmarkForGroupByTag(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForUser() {
		this.db.getBookmark().getBookmarkForUser(this.bookmarkParam);
		this.resetParameters();
		this.bookmarkParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.db.getBookmark().getBookmarkForUser(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForUserCount() {
		this.db.getBookmark().getBookmarkForUserCount(this.bookmarkParam);
		this.resetParameters();
		this.bookmarkParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.db.getBookmark().getBookmarkForUserCount(this.bookmarkParam);
	}

	/**
	 * Test for setting bookmarks of a user in database regarding different
	 * statements
	 */
	@Test
	public void insertBookmark() {
		// FIXME
		// this.db.getBookmark().insertBookmark(this.bookmarkParam);
	}

	@Test
	public void insertBookmarkLog() {
		// FIXME
		// this.db.getBookmark().insertBookmarkLog(this.bookmarkParam);
	}

	@Test
	public void insertBookmarkHash() {
		// to prevent duplicate key error
		this.bookmarkParam.setHash("1234567890");
		//this.db.getBookmark().insertBookmarkHash(this.bookmarkParam);
	}

	@Test
	public void updateBookmarkHashDec() {
		// FIXME
		// this.db.getBookmark().updateBookmarkHashDec(this.bookmarkParam);
	}

	@Test
	public void updateBookmarkLog() {
		// FIXME
		// this.db.getBookmark().updateBookmarkLog(this.bookmarkParam);
	}

	@Test
	public void deleteBookmarkByContentId() {
		// FIXME
		// this.db.getBookmark().deleteBookmarkByContentId(this.bookmarkParam);
	}

	@Test
	public void updateIds() {
		this.db.getBookmark().updateIds(this.bookmarkParam);
	}

	@Test
	public void getNewContentID() {
		// FIXME
		// this.db.getBookmark().getNewContentID(this.bookmarkParam);
	}

	@Test
	public void getContentIDForBookmark() {
		// TODO not tested
		this.db.getBookmark().getContentIDForBookmark(this.bookmarkParam);
	}
}