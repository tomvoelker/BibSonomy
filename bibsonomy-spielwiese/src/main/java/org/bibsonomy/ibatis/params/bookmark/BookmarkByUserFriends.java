package org.bibsonomy.ibatis.params.bookmark;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByUserFriends;

/**
 * show all bookmark of users friends
 * 
 * @author mgr
 * 
 */
public class BookmarkByUserFriends extends ByUserFriends {

	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}
}