package org.bibsonomy.ibatis.params;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.model.Bookmark;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 */
public class BookmarkParam extends GenericParam {

	/**
	 * A bookmark object.
	 */
	private Bookmark bookmark;

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}

	public Bookmark getBookmark() {
		return this.bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}
}