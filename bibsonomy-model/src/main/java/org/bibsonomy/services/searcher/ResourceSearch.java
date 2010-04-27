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
import java.util.Set;

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
	 * TODO: document me
	 * 
	 * @param group
	 * @param searchTerms
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames
	 * @param limit
	 * @param offset
	 * @return
	 */
	public ResultList<Post<R>> searchPosts(String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset);


	/**
	 * TODO: document me
	 * 
	 * @param group
	 * @param search
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param tagList
	 * @param limit
	 * @param offset
	 * @return
	 */
	public ResultList<Post<R>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit,
			int offset);

	/**
	 * <em>/search/ein+lustiger+satz+group%3AmyGroup</em><br/><br/>
	 * 
	 * @param groupId group to search
	 * @param visibleGroupIDs groups the user has access to
	 * @param search the search query
	 * @param userName
	 * @param limit number of posts to display
	 * @param offset first post in the result list
	 * @param systemTags NOT IMPLEMENTED 
	 * @return
	 */
	public ResultList<Post<R>> searchGroup(final int groupId, final List<Integer> visibleGroupIDs, final String search, final String authUserName, final int limit, final int offset, Collection<? extends Tag> systemTags);

	
	/**
	 * get tag cloud for given author
	 * 
	 * @param group
	 * @param search
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param tagList
	 * @param limit 
	 * @return
	 */
	public List<Tag> getTagsByAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList);
			

	/**
	 * get tag cloud for given search string
	 * 
	 * TODO: implement getTagsByAuthor as a special case
	 * 
	 * @param group
	 * @param searchTerms
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames
	 * @param tagOrder
	 * @param limitType
	 * @param limit
	 * @return
	 */
	public List<Tag> getTagsBySearchString(String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames);

	/**
	 * get lists of post matched by title
	 * 
	 * TODO: clean up interface, like PostDatabaseManager
	 * 
	 * @param group
	 * @param searchTerms
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames
	 * @param limit
	 * @param offset
	 * @return
	 */
	public ResultList<Post<R>> getPostsByTitle(String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset);


}
