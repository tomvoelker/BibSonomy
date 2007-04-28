package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.Bookmark;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 */
public class BookmarkParam extends ResourcesParam<Bookmark> {

	/** A single resource */
	private Bookmark resource;

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}

	public Bookmark getResource() {
		return this.resource;
	}

	public void setResource(Bookmark resource) {
		this.resource = resource;
	}
}