package org.bibsonomy.search.es.index;

import java.net.URI;
import java.util.Map;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Post;

/**
 * converter for {@link GoldStandardBookmark}
 *
 * @author dzo
 */
public class CommunityBookmarkConverter extends BookmarkConverter {

	/**
	 * @param systemURI
	 */
	public CommunityBookmarkConverter(URI systemURI) {
		super(systemURI);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.BookmarkConverter#createNewResource()
	 */
	@Override
	protected Bookmark createNewResource() {
		return new GoldStandardBookmark();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#fillUser(org.bibsonomy.model.Post, java.lang.String)
	 */
	@Override
	protected void fillUser(Post<Bookmark> post, String userName) {
		// nothing to do
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#fillIndexDocument(org.bibsonomy.model.Post, java.util.Map)
	 */
	@Override
	protected void fillIndexDocumentUser(Post<Bookmark> post, Map<String, Object> jsonDocument) {
		// nothing to do
	}

}
