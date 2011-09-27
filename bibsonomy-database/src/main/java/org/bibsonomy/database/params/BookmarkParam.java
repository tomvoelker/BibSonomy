package org.bibsonomy.database.params;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Bookmark;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkParam extends ResourceParam<Bookmark> {
	
	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}
	
	/**
	 * XXX: @see {@link org.bibsonomy.database.params.BibTexParam#getResource()}
	 */
	@Override
	public Bookmark getResource() {
	    return super.getResource();
	}
	
	@Override
	public Class<Bookmark> getResourceClass() {
		return Bookmark.class;
	}
}