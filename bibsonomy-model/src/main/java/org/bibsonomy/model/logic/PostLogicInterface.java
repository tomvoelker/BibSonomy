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
package org.bibsonomy.model.logic;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.metadata.PostMetaData;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Access interface from applications (client and server) to the core
 * functionality regarding posts.
 * 
 * @author Jens Illig
 */
public interface PostLogicInterface {
	
	/**
	 * the number of tags allowed for querying the db
	 */
	public static final int MAX_TAG_SIZE = 10;
	
	/**
	 * the maximum number of items for querying the db
	 */
	public static final int MAX_QUERY_SIZE = 1000;
	
	/**  
	 * retrieves a filterable list of posts.
	 * 
	 * @param <T> resource type to be shown.
	 * @param resourceType resource type to be shown.
	 * @param grouping
	 *            grouping tells whom posts are to be shown: the posts of a
	 *            user, of a group or of the viewables.
	 * @param groupingName
	 *            name of the grouping. if grouping is user, then its the
	 *            username. if grouping is set to {@link GroupingEntity#ALL},
	 *            then its an empty string!
	 * @param tags
	 *            a set of tags. remember to parse special tags like
	 *            ->[tagname], -->[tagname] and <->[tagname]. see documentation.
	 *            if the parameter is not used, its an empty list
	 * @param hash
	 *            hash value of a resource, if one would like to get a list of
	 *            all posts belonging to a given resource. if unused, its empty
	 *            but not null.
	 * @param search - free text search
	 * @param searchType - whether to search locally or using an index shared by several systems
	 * @param filters - filter for the retrieved posts
	 * @param order - a flag indicating the way of sorting
	 * @param startDate - if given, only posts that have been created after (inclusive) startDate are returned  
	 * @param endDate - if given, only posts that have been created before (inclusive) endDate are returned 
	 * @param start - inclusive start index of the view window
	 * @param end - exclusive end index of the view window
	 * @return A filtered list of posts. may be empty but not null
	 * @since 3.1
	 */
	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, SearchType searchType, Set<Filter> filters, Order order, Date startDate, Date endDate, int start, int end);
	
	/**
	 * Returns details to a post. A post is uniquely identified by a hash of the
	 * corresponding resource and a username.
	 * 
	 * @param resourceHash hash value of the corresponding resource
	 * @param userName name of the post-owner
	 * @return the post's details, null else
	 * @throws ResourceMovedException  - when no resource 
	 * with that hash exists for that user, but once a resource 
	 * with that hash existed that has been moved. The new hash 
	 * is returned inside the exception. 
	 * @throws ObjectNotFoundException 
	 */
	public Post<? extends Resource> getPostDetails(String resourceHash, String userName) throws ResourceMovedException, ObjectNotFoundException;

	/**
	 * Removes the given posts - identified by the connected resource's hashes -
	 * from the user.
	 * 
	 * @param userName user who's posts are to be removed
	 * @param resourceHashes
	 *            hashes of the resources, which is connected to the posts to delete
	 */
	public void deletePosts(String userName, List<String> resourceHashes);
	
	/**
	 * Get the metadata for the post.
	 * @param hashType the hashtype (TODO: merge with resourceHash to hash)
	 * @param resourceHash the hash of the resource
	 * @param userName the user name
	 * @param metaDataPluginKey the kind of meta data
	 * @return a list of metadata for the specified posts
	 */
	public List<PostMetaData> getPostMetaData(final HashID hashType, final String resourceHash, final String userName, final String metaDataPluginKey);
	/**
	 * Add the posts to the database.
	 * 
	 * @param posts  the posts to add
	 * @return the resource hashes of the created posts
	 */
	public List<String> createPosts(List<Post<? extends Resource>> posts);

	/**
	 * Updates the posts in the database.
	 * 
	 * @param posts  the posts to update
	 * @param operation  which parts of the posts should be updated
	 * @return resourceHashes the (new) hashes of the updated resources
	 */
	public List<String> updatePosts(List<Post<? extends Resource>> posts, PostUpdateOperation operation);
	
	/**  
	 * retrieves the number of posts matching to the given constraints
	 * 
	 * @param resourceType resource type to be shown.
	 * @param grouping
	 *            grouping tells whom posts are to be shown: the posts of a
	 *            user, of a group or of the viewables.
	 * @param groupingName
	 *            name of the grouping. if grouping is user, then its the
	 *            username. if grouping is set to {@link GroupingEntity#ALL},
	 *            then its an empty string!
	 * @param tags
	 *            a set of tags. remember to parse special tags like
	 *            ->[tagname], -->[tagname] and <->[tagname]. see documentation.
	 *            if the parameter is not used, its an empty list
	 * @param hash
	 *            hash value of a resource, if one would like to get a list of
	 *            all posts belonging to a given resource. if unused, its empty
	 *            but not null.
	 * @param search free text search
	 * @param filters the filters for the retrieved posts
	 * @param constraints - a possible constraint on the statistics
	 * @param order a flag indicating the way of sorting
	 * @param startDate - if given, only posts that have been created after (inclusive) startDate are regarded  
	 * @param endDate - if given, only posts that have been created before (inclusive) endDate are regarded
	 * @param start inclusive start index of the view window
	 * @param end exclusive end index of the view window
	 * @return a filtered list of posts. may be empty but not null
	 */
	public Statistics getPostStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, Set<Filter> filters, Order order, Date startDate, Date endDate, int start, int end);
}