package org.bibsonomy.ibatis.params;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.model.Bookmark;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 */
public class BookmarkParam extends GenericParam<Bookmark> {

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}
}