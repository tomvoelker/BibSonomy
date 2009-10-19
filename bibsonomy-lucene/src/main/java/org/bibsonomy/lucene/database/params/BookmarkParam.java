package org.bibsonomy.lucene.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.Bookmark;

/**
 * Parameters that are specific to bookmarks.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkParam extends ResourcesParam<Bookmark> implements SingleResourceParam<Bookmark> {

	/** A single resource */
	private Bookmark resource;

	@Override
	public int getContentType() {
		return ConstantID.BOOKMARK_CONTENT_TYPE.getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.params.SingleResourceParam#getResource()
	 */
	@Override
	public Bookmark getResource() {
		return this.resource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.params.SingleResourceParam#setResource(org.bibsonomy.model.Resource)
	 */
	@Override
	public void setResource(Bookmark resource) {
		this.resource = resource;
	}	
}