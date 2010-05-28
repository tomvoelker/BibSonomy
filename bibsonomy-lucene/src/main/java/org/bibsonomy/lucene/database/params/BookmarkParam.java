package org.bibsonomy.lucene.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.Bookmark;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkParam extends ResourcesParam<Bookmark> {

	/** A single resource */
	private Bookmark resource;

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}

	/**
	 * @return the bookmark
	 */
	public Bookmark getResource() {
		return this.resource;
	}

	/**
	 * @param resource the bookmark to set
	 */
	public void setResource(Bookmark resource) {
		this.resource = resource;
	}	
}