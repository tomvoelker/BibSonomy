package org.bibsonomy.ibatis.params.bookmark;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.HomePageForBookmark;

public class HomePageBookmark extends HomePageForBookmark {

  public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
		
		
		}

	}