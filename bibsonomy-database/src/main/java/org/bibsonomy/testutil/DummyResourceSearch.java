/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.testutil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.querybuilder.PublicationSuggestionQueryBuilder;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * @author dzo
 */
public class DummyResourceSearch implements ResourceSearch<Resource> {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, org.bibsonomy.model.es.SearchType, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.model.enums.Order, int, int)
	 */
	@Override
	public List<Post<Resource>> getPosts(String userName,
			String requestedUserName, String requestedGroupName,
			List<String> requestedRelationNames,
			Collection<String> allowedGroups, SearchType searchType,
			String searchTerms, String titleSearchTerms,
			String authorSearchTerms, String bibtexKey, Collection<String> tagIndex, String year,
			String firstYear, String lastYear, List<String> negatedTags,
			Order order, int limit, int offset) {
		return new LinkedList<>();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPublicationSuggestions(java.lang.String)
	 */
	@Override
	public List<Post<BibTex>> getPublicationSuggestions(PublicationSuggestionQueryBuilder options) {
		return new LinkedList<>();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	@Override
	public List<Tag> getTags(String userName, String requestedUserName, String requestedGroupName, Collection<String> allowedGroups, String searchTerms, String titleSearchTerms, String authorSearchTerms, String bibtexkey, Collection<String> tagIndex, String year, String firstYear, String lastYear, List<String> negatedTags, int limit, int offset) {
		return new LinkedList<>();
	}

}
