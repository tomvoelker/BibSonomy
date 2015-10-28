package org.bibsonomy.search.es.index;

import java.net.URI;
import java.util.Map;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.search.es.ESConstants.Fields;

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
		jsonDocument.put(Fields.Bookmark.URL, resource.getUrl());
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#createNewResource()
	 */
	@Override
	protected Bookmark createNewResource() {
		return new Bookmark();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#convertResourceInternal(org.bibsonomy.model.Resource, java.util.Map)
	 */
	@Override
	protected void convertResourceInternal(Bookmark resource, Map<String, Object> source) {
		resource.setUrl((String) source.get(Fields.Bookmark.URL));
	}
}