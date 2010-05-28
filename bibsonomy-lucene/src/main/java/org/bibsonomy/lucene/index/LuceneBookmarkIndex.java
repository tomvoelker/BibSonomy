package org.bibsonomy.lucene.index;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * class for managing the lucene bookmark index
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBookmarkIndex extends LuceneResourceIndex<Bookmark> {

	protected LuceneBookmarkIndex(int indexId) {
		super(indexId);
	}

	@Override
	protected Class<? extends Resource> getResourceType() {
		return Bookmark.class;
	}

}
