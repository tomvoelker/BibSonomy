package org.bibsonomy.ibatis;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bookmark.BookmarkByUserFriends;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;



/**
 * @author mgr
 *
 */
/*
 * initialize BookmarkByUserFriends parameters
 * 
 */
public class BookByUsersFriendTest extends AbstractSqlMapTest {
	
	
	public BookmarkByUserFriends getDefaultBookmarkByUserFriends() {
		final BookmarkByUserFriends bookVal = new BookmarkByUserFriends();
		
		bookVal.setUser("rja");
		bookVal.setItemCount(10);
		bookVal.setStartBook(3);
		bookVal.setGroupType(ConstantID.GROUP_FRIENDS);
		return bookVal;
	}

	@SuppressWarnings("unchecked")
	public void testGetBookmarkByUsersFriend() {
			final BookmarkByUserFriends btn = this.getDefaultBookmarkByUserFriends();

			final List<Bookmark> bookmarks=this.sqlMap.queryForList("getBookmarkbyUsersFriend",btn);
			
			for (final Bookmark bookmark  : bookmarks) {
				System.out.println("ContentId   : " + bookmark.getContentId());
				System.out.println("Description : " + bookmark.getDescription());
				System.out.println("Extended    : " + bookmark.getExtended());
				System.out.println("Date        : " + bookmark.getDate());
				System.out.println("URL         : " + bookmark.getUrl());
				System.out.println("URLHash     : " + bookmark.getUrlHash());
				System.out.println("UserName    : " + bookmark.getUserName());
		 
			}
	
	
	}	
	

	
}