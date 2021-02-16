/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
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

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.SortCriterium;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.services.searcher.query.PostSearchQuery;
import org.bibsonomy.model.logic.querybuilder.PublicationSuggestionQueryBuilder;
import org.bibsonomy.database.services.ResourceSearch;

/**
 * @author dzo
 */
public class DummyResourceSearch implements ResourceSearch<Resource> {

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, org.bibsonomy.model.es.SearchType, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.common.enums.SortKey, int, int)
	 */
	@Override
	public List<Post<R>> getPosts(User loggedinUser, PostSearchQuery<?> postQuery) {
	public List<Post<Resource>> getPosts(String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames,
										 Collection<String> allowedGroups, SearchType searchType, String searchTerms, String titleSearchTerms,
										 String authorSearchTerms, String bibtexKey, Collection<String> tagIndex, String year,
										 String firstYear, String lastYear, List<String> negatedTags, SortKey sortKey,
										 int limit, int offset, Collection<SystemTag> systemTags) {
		return new LinkedList<>();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, org.bibsonomy.model.es.SearchType, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.common.SortCriterium, int, int)
	 */
	@Override
	public List<Post<Resource>> getPosts(String userName, String requestedUserName, String requestedGroupName, List<String> requestedRelationNames,
										 Collection<String> allowedGroups, SearchType searchType, String searchTerms, String titleSearchTerms,
										 String authorSearchTerms, String bibtexKey, Collection<String> tagIndex, String year,
										 String firstYear, String lastYear, List<String> negatedTags, List<SortCriterium> sortCriteriums,
										 int limit, int offset, Collection<SystemTag> systemTags) {
		return new LinkedList<>();
	}
	@Override
	public Statistics getStatistics(User loggedinUser, PostSearchQuery<?> postQuery) {
		return new Statistics();
	}
	@Override
	public List<Tag> getTags(User loggedinUser, PostSearchQuery<?> postQuery) {
		return new LinkedList<>();
	}
}
