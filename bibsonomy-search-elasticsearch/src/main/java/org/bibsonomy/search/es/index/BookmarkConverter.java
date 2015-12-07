/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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