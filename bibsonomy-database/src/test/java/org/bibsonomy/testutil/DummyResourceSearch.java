/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.testutil;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * @author dzo
 */
public class DummyResourceSearch implements ResourceSearch<Resource> {
	
	@Override
	public List<Post<Resource>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, List<String> requestedRelationNames, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, List<String> negatedTags, Order order, final int limit, final int offset) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, org.bibsonomy.model.es.SearchType, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.model.enums.Order, int, int)
	 */
	@Override
	public List<Post<Resource>> getPosts(String userName,
			String requestedUserName, String requestedGroupName,
			List<String> requestedRelationNames,
			Collection<String> allowedGroups, SearchType searchType,
			String searchTerms, String titleSearchTerms,
			String authorSearchTerms, Collection<String> tagIndex, String year,
			String firstYear, String lastYear, List<String> negatedTags,
			Order order, int limit, int offset) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, org.bibsonomy.common.enums.SearchType, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	@Override
	public List<Tag> getTags(String userName, String requestedUserName,
			String requestedGroupName, Collection<String> allowedGroups,
			String searchTerms, SearchType searchType, String titleSearchTerms,
			String authorSearchTerms, Collection<String> tagIndex, String year,
			String firstYear, String lastYear, List<String> negatedTags,
			int limit, int offset) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	@Override
	public List<Tag> getTags(String userName, String requestedUserName,
			String requestedGroupName, Collection<String> allowedGroups,
			String searchTerms, String titleSearchTerms,
			String authorSearchTerms, Collection<String> tagIndex, String year,
			String firstYear, String lastYear, List<String> negatedTags,
			int limit, int offset) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPostsByBibtexKey(java.lang.String, java.util.Collection, org.bibsonomy.common.enums.SearchType, java.lang.String, java.util.Collection, java.util.List, org.bibsonomy.model.enums.Order, int, int)
	 */
	@Override
	public List<Post<Resource>> getPostsByBibtexKey(String userName,
			Collection<String> allowedGroups, SearchType searchType,
			String bibtexKey, Collection<String> tagIndex,
			List<String> negatedTags, Order order, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

}
