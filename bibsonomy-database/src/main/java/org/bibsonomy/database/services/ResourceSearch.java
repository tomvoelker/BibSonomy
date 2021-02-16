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

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.database.services.query.PostSearchQuery;

/**
 * Interface for resource search operations
 *
 * @author fei, dzo
 *
 * @param <R>
 */
public interface ResourceSearch<R extends Resource> {

	/**
	 * @param loggedinUser the logged in user
	 * @param postQuery the query with all query parameters
	 * @return all posts matching the search query
	 */
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
