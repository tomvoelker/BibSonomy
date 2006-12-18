package org.bibsonomy.ibatis.params;

import org.bibsonomy.ibatis.enums.ConstantID;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 */
public class BookmarkParam extends GenericParam {

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}
}