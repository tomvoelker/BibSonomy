/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.services.searcher;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;

/**
 * Interface for resource search operations
 * 
 * TODO: This interface got a bit messy, as more and more queries were
 *       added. We should redesign and clean the interface.
 * 
 * @author fei, dzo
 *
 * @param <R>
 */
public interface ResourceSearch<R extends Resource> {

	/**
	 * search for posts using the lucene index
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param allowedGroups
	 * @param friends
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param limit
	 * @param offset
	 * @return
	 */
	public ResultList<Post<R>> getPosts(
			final String userName, final String requestedUserName, String requestedGroupName, 
			final Collection<String> allowedGroups,
			final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex,
			final String year, final String firstYear, final String lastYear, int limit, int offset);
	

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
	 * @param limit
	 * @param offset
	 * @return
	 */
	public List<Tag> getTags(
			final String userName, final String requestedUserName, String requestedGroupName, 
			final Collection<String> allowedGroups,
			final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex,
			final String year, final String firstYear, final String lastYear, int limit, int offset);
	
}
