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
public class BookmarkTest extends AbstractDatabaseTest {

	@Test
	public void getBookmarkByTagNames() {
		this.bookmarkDb.getBookmarkByTagNames(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByTagNamesForUser() {
		this.bookmarkDb.getBookmarkByTagNamesForUser(this.bookmarkParam);
		this.resetParameters();
		this.bookmarkParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.bookmarkDb.getBookmarkByTagNamesForUser(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByConceptForUser() {
		this.bookmarkDb.getBookmarkByConceptForUser(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByUserFriends() {
		this.bookmarkDb.getBookmarkByUserFriends(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForHomepage() {
		this.bookmarkDb.getBookmarkForHomepage(this.bookmarkParam);
	}

	@Test
	public void getBookmarkPopular() {
		this.bookmarkDb.getBookmarkPopular(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByHash() {
		this.bookmarkDb.getBookmarkByHash(this.bookmarkParam);
	}

	@Test
	public void getBookmarkByHashCount() {
		Integer count = -1;
		count = this.bookmarkDb.getBookmarkByHashCount(this.bookmarkParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkByHashForUser() {
		this.bookmarkDb.getBookmarkByHashForUser(this.bookmarkParam);
	}

	@Test
	public void getBookmarkSearch() {
		this.bookmarkParam.setSearch("test");
		this.bookmarkDb.getBookmarkSearch(this.bookmarkParam);
		this.bookmarkParam.setUserName(null);
		this.bookmarkDb.getBookmarkSearch(this.bookmarkParam);
	}

	@Test
	public void getBookmarkSearchCount() throws SQLException {
		this.bookmarkParam.setSearch("test");
		Integer count = -1;
		count = this.bookmarkDb.getBookmarkSearchCount(this.bookmarkParam);
		assertTrue(count >= 0);

		this.bookmarkParam.setUserName(null);
		count = -1;
		count = (Integer) this.bookmarkDb.getBookmarkSearchCount(this.bookmarkParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkViewable() {
		this.bookmarkDb.getBookmarkViewable(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForGroup() {
		this.bookmarkDb.getBookmarkForGroup(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForGroupCount() {
		Integer count = -1;
		count = this.bookmarkDb.getBookmarkForGroupCount(this.bookmarkParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkForGroupByTag() {
		this.bookmarkDb.getBookmarkForGroupByTag(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForUser() {
		this.bookmarkDb.getBookmarkForUser(this.bookmarkParam);
		this.resetParameters();
		this.bookmarkParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.bookmarkDb.getBookmarkForUser(this.bookmarkParam);
	}

	@Test
	public void getBookmarkForUserCount() {
		this.bookmarkDb.getBookmarkForUserCount(this.bookmarkParam);
		this.resetParameters();
		this.bookmarkParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.bookmarkDb.getBookmarkForUserCount(this.bookmarkParam);
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
		this.bookmarkDb.updateIds(this.bookmarkParam);
	}

	@Test
	public void getNewContentID() {
		// FIXME
		// this.db.getBookmark().getNewContentID(this.bookmarkParam);
	}

	@Test
	public void getContentIDForBookmark() {
		// TODO not tested
		this.bookmarkDb.getContentIDForBookmark(this.bookmarkParam);
	}
}