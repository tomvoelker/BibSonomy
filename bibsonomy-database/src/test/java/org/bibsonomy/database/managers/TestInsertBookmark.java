package org.bibsonomy.database.managers;

import java.util.Date;

import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

public class TestInsertBookmark {

	@Test
	public void getBookmarkDefault() {
		final BookmarkParam bookmark = new BookmarkParam();
		/*final Tag tagfirstTest = new Tag();
		final Tag tagsecondTest = new Tag();
		final Tag tagthirdTest = new Tag();*/
		final Bookmark book = new Bookmark();

		final Date date = new Date();

		// book.setContentId(657493);
		book.setTitle("TestTitle");
		/*
		 * TODO repair bookmarkExtended, return null
		 */
		//book.get("bookmark extension");
		bookmark.setUserName("grahl");

		/* TODO Extension=Description Description=Title */
		bookmark.setHash("fd6a16aaeef484e9ebb8b9a62f3a77c");
		bookmark.setGroupId(3);
		bookmark.setDate(date);
		bookmark.setResource(book);
		bookmark.setGroupId(4);
	}

	@Test
	public void testInsertBookmark() {
		// FIXME
		// DatabaseManager db = new DatabaseManager();
		// final BookmarkParam param = this.getBookmarkDefault();
		// db.bookmarkDatabaseManager.insert("insertBookmark", param);
	}
}