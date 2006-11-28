package org.bibsonomy.ibatis.params.bookmark;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByTagNames;

public class BookmarkByTagNames extends ByTagNames {

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}
}