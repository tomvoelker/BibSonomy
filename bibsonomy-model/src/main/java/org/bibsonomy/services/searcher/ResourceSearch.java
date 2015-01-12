/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.services.searcher;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;

/**
 * Interface for resource search operations
 * 
 * @author fei, dzo
 *
 * @param <R>
 */
public interface ResourceSearch<R extends Resource> {

	/**
	 * search for posts using the lucene index
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param requestedRelationNames
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param order			the order to use (supported {@link Order#ADDED} and {@link Order#RANK}
	 * @param limit
	 * @param offset
	 * @return a list of posts containing the search result
	 */
	public List<Post<R>> getPosts(
			final String userName, final String requestedUserName, String requestedGroupName, 
			final List<String> requestedRelationNames,
			final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms,
			final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, Order order, final int limit, final int offset);
	

	/**
	 * get tag cloud for given search query
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param limit
	 * @param offset
	 * @return the tag cloud for the given search
	 */
	public List<Tag> getTags(
			final String userName, final String requestedUserName, String requestedGroupName, 
			final Collection<String> allowedGroups,
			final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex,
			final String year, final String firstYear, final String lastYear, List<String> negatedTags, int limit, int offset);
	
}
