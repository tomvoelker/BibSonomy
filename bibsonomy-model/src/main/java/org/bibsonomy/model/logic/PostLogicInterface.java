package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;

/**
 * Access interface from applications (client and server) to the core
 * functionality regarding posts.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface PostLogicInterface {
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
	 * @param start inclusive start index of the view window
	 * @param end exclusive end index of the view window
	 * @param search free text search
	 * @param order a flag indicating the way of sorting
	 * @param filter filter for the retrieved posts
	 * @return a filtered list of posts. may be empty but not null
	 */
	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search);

	/**
	 * Returns details to a post. A post is uniquely identified by a hash of the
	 * corresponding resource and a username.
	 * 
	 * @param resourceHash hash value of the corresponding resource
	 * @param userName name of the post-owner
	 * @return the post's details, null else
	 */
	public Post<? extends Resource> getPostDetails(String resourceHash, String userName);

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
	 * Add the posts to the database.
	 * 
	 * @param posts  the posts to add
	 * @return String the resource hashes of the created posts
	 */
	public List<String> createPosts(List<Post<?>> posts);

	/**
	 * Updates the posts in the database.
	 * 
	 * @param posts  the posts to update
	 * @param operation  which parts of the posts should be updated
	 * @return resourceHashes the (new) hashes of the updated resources
	 */
	public List<String> updatePosts(List<Post<?>> posts, PostUpdateOperation operation);
	
	/**  
	 * retrieves the counts of a filterable list of posts.
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
	 * @param start inclusive start index of the view window
	 * @param end exclusive end index of the view window
	 * @param search free text search
	 * @param order a flag indicating the way of sorting
	 * @param filter filter for the retrieved posts
	 * @param constraint - a possible contstraint on the statistics
	 * @return a filtered list of posts. may be empty but not null
	 */
	public int getPostStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search, StatisticsConstraint constraint);
}