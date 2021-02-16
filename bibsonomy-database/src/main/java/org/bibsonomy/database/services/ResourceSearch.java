/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.database.services;

import java.util.List;

import org.bibsonomy.common.SortCriterium;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.querybuilder.PublicationSuggestionQueryBuilder;
import org.bibsonomy.model.User;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.query.PostSearchQuery;

import java.util.Collection;
import java.util.List;

/**
 * Interface for resource search operations
 *
 * @author fei, dzo
 *
 * @param <R>
 */
public interface ResourceSearch<R extends Resource> {

	/**
	 * search for posts using a full text search index
	 *
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param requestedRelationNames @Deprecated TODO: (spheres) remove
	 * @param allowedGroups
	 * @param searchType
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexKey
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param sortKey			the order to use (supported {@link SortKey}
	 * @param limit
	 * @param offset
	 * @param systemTags
	 * @return a list of posts containing the search result
	 */
	public List<Post<R>> getPosts(
			final String userName, final String requestedUserName, String requestedGroupName, final List<String> requestedRelationNames,
			final Collection<String> allowedGroups, final SearchType searchType, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms,
			final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags,
			final SortKey sortKey, final int limit, final int offset, final Collection<SystemTag> systemTags);

	/**
	 * search for posts using a full text search index
	 *
	 * @param loggedinUser the logged in user
	 * @param postQuery the query with all query parameters
	 * @return all posts matching the search query
	 *
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param requestedRelationNames @Deprecated TODO: (spheres) remove
	 * @param allowedGroups
	 * @param searchType
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexKey
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param sortCriteriums			the list of sort criteriums to use (supported {@link SortCriterium}
	 * @param limit
	 * @param offset
	 * @param systemTags
	 * @return a list of posts containing the search result
	 */
	public List<Post<R>> getPosts(
			final String userName, final String requestedUserName, String requestedGroupName, final List<String> requestedRelationNames,
			final Collection<String> allowedGroups, final SearchType searchType, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms,
			final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags,
			final List<SortCriterium> sortCriteriums, final int limit, final int offset, final Collection<SystemTag> systemTags);

	List<Post<R>> getPosts(final User loggedinUser, final PostSearchQuery<?> postQuery);

	/**
	 * statistics about the posts matching the query
	 *
	 * @param loggedinUser
	 * @param postQuery
	 * @return
	 */
	Statistics getStatistics(final User loggedinUser, final PostSearchQuery<?> postQuery);

	/**
	 * get tag cloud for given search query
	 *
	 * @param loggedinUser
	 * @param postQuery
	 * @return tags that are used for the posts matching the search query
	 */
	List<Tag> getTags(final User loggedinUser, final PostSearchQuery<?> postQuery);
}
