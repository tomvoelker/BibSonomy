package org.bibsonomy.ibatis.params.bookmark;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByUserFriendsBookmark;




/**
 * @author mgr
 *
 */



/*
 * show all bookmark of users friends 
 */

public class BookmarkByUserFriends extends ByUserFriendsBookmark {

  public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
		
		
		}

	}