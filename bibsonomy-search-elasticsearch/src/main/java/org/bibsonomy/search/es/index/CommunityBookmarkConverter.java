/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
