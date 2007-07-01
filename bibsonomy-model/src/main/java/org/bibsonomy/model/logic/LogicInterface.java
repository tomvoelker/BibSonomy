package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * This interface is an adapter to the bibsonomys core functionality. <br/>
 * The methods returning
 * information return in general, if there are no matches, an empty set (if a
 * list is requested), or null (if a single entity is requested (a post, eg)).
 * <br/><b>Please try to be as close to the method-conventions as possible.
 * </b>If something is unclear, guess, check occurences and document your
 * result. If you have to change a convention, check all occurences and document
 * it properly! Try to check each possibility with a test-case.
 * 
 * TODO: split this interface as it might grow too much
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Jens Illig <illig@innofinity.de>
 * @version $Id$
 */
public interface LogicInterface {

	public String getAuthenticatedUser();
	
	/**
	 * Returns all users
	 * 
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(int start, int end);

	/**
	 * Returns all users who are members of the specified group
	 * 
	 * @param groupName name of the group
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(String groupName, int start, int end);

	/**
	 * Returns details about a specified user
	 * 
	 * @param userName name of the user we want to get details from
	 * @return details about a named user, null else
	 */
	public User getUserDetails(String userName);

	/**
	 * Returns a list of posts which can be filtered.
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
	 * @param added
	 *            a flag indicating the way of sorting: if true, sort by
	 *            adding-time. both flags cannot be true at the same time; an
	 *            {@link IllegalArgumentException} is expected to be thrown
	 * @param popular
	 *            a flag indicating the way of sorting: if true, sort by
	 *            popularity. both flags cannot be true at the same time; an
	 *            {@link IllegalArgumentException} is expected to be thrown
	 * @param start
	 * @param end
	 * @return a set of posts, an empty set else
	 */
	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, int start, int end);

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
	 * Returns all groups of the system.
	 * 
	 * @param end
	 * @param start
	 * @return a set of groups, an empty set else
	 */
	public List<Group> getGroups(int start, int end);

	/**
	 * Returns details of one group.
	 * 
	 * @param groupName
	 * @return the group's details, null else
	 */
	public Group getGroupDetails(String groupName);

	/**
	 * Returns a list of tags which can be filtered.
	 * 
	 * @param grouping
	 *            grouping tells whom tags are to be shown: the tags of a user,
	 *            of a group or of the viewables.
	 * @param groupingName
	 *            name of the grouping. if grouping is user, then its the
	 *            username. if grouping is set to {@link GroupingEntity#ALL},
	 *            then its an empty string!
	 * @param regex
	 *            a regular expression used to filter the tagnames
	 * @param start
	 * @param end
	 * @return a set of tags, en empty set else
	 */
	public List<Tag> getTags(GroupingEntity grouping, String groupingName, String regex, int start, int end);

	/**
	 * Returns details about a tag. Those details are:
	 * <ul>
	 * <li>details about the tag itself, like number of occurrences etc</li>
	 * <li>list of subtags</li>
	 * <li>list of supertags</li>
	 * <li>list of correlated tags</li>
	 * </ul>
	 * 
	 * @param authUserName name of the authenticated user
	 * @param tagName name of the tag
	 * @return the tag's details, null else
	 */
	public Tag getTagDetails(String tagName);

	/**
	 * Removes the given user.
	 * 
	 * @param userName the user to delete
	 */
	public void deleteUser(String userName);

	/**
	 * Removes the given group.
	 * 
	 * @param groupName the group to delete
	 */
	public void deleteGroup(String groupName);

	/**
	 * Removes an user from a group.
	 * 
	 * @param groupName the group to change
	 * @param userName the user to remove
	 */
	public void removeUserFromGroup(String groupName, String userName);

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
	 * Adds a user to the database.
	 * 
	 * @param user  the user to add
	 */
	public void createUser(User user);
	
	/**
	 * Updates a user to the database.
	 * 
	 * @param user  the user to update
	 */
	public void updateUser(User user);

	/**
	 * Add a post to the database.
	 * 
	 * @param post  the post to add
	 * @return 
	 */
	public void createPost(Post<?> post);

	/**
	 * Updates a post in the database.
	 * 
	 * @param post  the post to update
	 * @return
	 */
	public void updatePost(Post<?> post);
	
	/**
	 * Adds a group to the database.
	 * 
	 * @param group  the group to add
	 */
	public void createGroup(Group group);
	
	/**
	 * Updates a group in the database.
	 * 
	 * @param group  the group to update
	 */
	public void updateGroup(Group group);

	
	
	/**
	 * Adds an existing user to an existing group.
	 * 
	 * @param groupName  name of the existing group
	 * @param user  user to add
	 */
	public void addUserToGroup(String groupName, String userName);
}