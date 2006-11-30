package org.bibsonomy.ibatis.params.bookmark;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByUserFriendsBookmark;



public class BookmarkByUserFriends extends ByUserFriendsBookmark {

  public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
		
		
		}

	}