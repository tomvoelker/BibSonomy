package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;

/**
 * Access interface from applications (client and server) to
 * the core functionality regarding posts.
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
	 * Removes the given post - identified by the connected resource's hash -
	 * from the user.
	 * 
	 * @param userName user who's post is to be removed
	 * @param resourceHash
	 *            hash of the resource, which is connected to the post to delete
	 */
	public void deletePost(String userName, String resourceHash);
	

	/**
	 * Add a post to the database.
	 * 
	 * @param post  the post to add
	 * @return String the resource hash of the created post
	 */
	public String createPost(Post<?> post);

	/**
	 * Updates a post in the database.
	 * 
	 * @param post  the post to update
	 * @return resourceHash the hash of the updated resource
	 */
	public String updatePost(Post<?> post);
}
