package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByTagNames;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;


public class JoinTest extends AbstractSqlMapTest {

	/**
	 * The ByTagNames-bean is used at various locations and has been refactored to this method.
	 */
	public BookmarkByTagNames getDefaultBookmarkByTagNames() {
		final BookmarkByTagNames rVal = new BookmarkByTagNames();
		rVal.setGroupType(ConstantID.GROUP_PUBLIC);
		rVal.setLimit(5);
		rVal.setOffset(0);
		// btn.setCaseSensitive(true);
		rVal.addTagName("web");
		rVal.addTagName("online");
		rVal.addTagName("community");
		return rVal;
	}

	@SuppressWarnings("unchecked")
	public void testGetBookmarkByTagNames() {
		try {
			final BookmarkByTagNames btn = this.getDefaultBookmarkByTagNames();

			final List<Bookmark> bookmarks = this.sqlMap.queryForList("getBookmarkByTagNames", btn);

			for (final Bookmark bookmark : bookmarks) {
				System.out.println("ContentId   : " + bookmark.getContentId());
				System.out.println("Description : " + bookmark.getDescription());
				System.out.println("Extended    : " + bookmark.getExtended());
				System.out.println("Date        : " + bookmark.getDate());
				System.out.println("URL         : " + bookmark.getUrl());
				System.out.println("URLHash     : " + bookmark.getUrlHash());
				System.out.println("UserName    : " + bookmark.getUserName());
				System.out.print("Tags        : ");
				for (final Tag tag : bookmark.getTags()) {
					System.out.print(tag.getName() + " ");
				}
				System.out.println("\n");
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}