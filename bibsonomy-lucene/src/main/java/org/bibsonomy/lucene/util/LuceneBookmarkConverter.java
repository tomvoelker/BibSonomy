package org.bibsonomy.lucene.util;

import org.bibsonomy.model.Bookmark;

/**
 * class for converting Bookmark post objects to lucene documents and vice versa
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBookmarkConverter extends LuceneResourceConverter<Bookmark> {

	@Override
	protected Bookmark createNewResource() {
		return new Bookmark();
	}
}
