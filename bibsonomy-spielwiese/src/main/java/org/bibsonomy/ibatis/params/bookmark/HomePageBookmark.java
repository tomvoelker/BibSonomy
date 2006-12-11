package org.bibsonomy.ibatis.params.bookmark;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.HomePage;

/**
 * Can be used to get all Bookmark entries of the main Page
 * 
 * @author mgr
 * 
 */
public class HomePageBookmark extends HomePage {

	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}
}