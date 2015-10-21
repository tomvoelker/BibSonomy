package org.bibsonomy.search.es.index;

import java.net.URI;
import java.util.Map;

import org.bibsonomy.model.Bookmark;

/**
 * {@link ResourceConverter} for {@link Bookmark}s
 *
 * @author dzo
 */
public class BookmarkConverter extends ResourceConverter<Bookmark> {
	
	/**
	 * @param systemURI
	 */
	public BookmarkConverter(URI systemURI) {
		super(systemURI);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertResource(java.util.Map, org.bibsonomy.model.Resource)
	 */
	@Override
	protected void convertResource(Map<String, Object> jsonDocument, Bookmark resource) {
		jsonDocument.put("url", resource.getUrl());
	}
}