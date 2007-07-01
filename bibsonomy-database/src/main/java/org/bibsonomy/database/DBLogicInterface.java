package org.bibsonomy.database;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.Order;

/**
 * This interface is an adapter to the database. <br/> The methods returning
 * information return in general, if there are no matches, an empty set (if a
 * list is requested), or null (if a single entity is requested (a post, eg)).
 * <br/><b>Please try to be as close to the method-conventions as possible.
 * </b>If something is unclear, guess, check occurences and document your
 * result. If you have to change a convention, check all occurences and document
 * it properly! Try to check each possibility with a test-case.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public interface DBLogicInterface {

	/**
	 * Returns all users
	 * 
	 * @param authUser
	 *            currently logged in user's name
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public abstract List<User> getUsers(String authUser, int start, int end);

	/**
	 * Returns all users who are members of the specified group
	 * 
	 * @param authUser
	 *            currently logged in user's name
	 * @param groupName
	 *            name of the group
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public abstract List<User> getUsers(String authUser, String groupName, int start, int end);

	/**
	 * Returns details about a specified user
	 * 
	 * @param authUserName
	 * @param userName
	 *            name of the user we want to get details from
	 * @return details about a named user, null else
	 */
	public abstract User getUserDetails(String authUserName, String userName);

	/**
	 * Returns a list of posts which can be filtered.
	 * 
	 * @param authUser
	 *            name of the authenticated user
	 * @param resourceType
	 *            resource type to be shown.
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
	public abstract <T extends Resource> List<Post<T>> getPosts(String authUser, Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, int start, int end);

	/**
	 * Returns details to a post. A post is uniquely identified by a hash of the
	 * corresponding resource and a username.
	 * 
	 * @param authUser
	 *            authenticated user name
	 * @param resourceHash
	 *            hash value of the corresponding resource
	 * @param userName
	 *            name of the post-owner
	 * @return the post's details, null else
	 */
	public abstract Post<? extends Resource> getPostDetails(String authUser, String resourceHash, String userName);

	/**
	 * Returns all groups of the system.
	 * 
	 * @param authUser
	 * @param end
	 * @param start
	 * @return a set of groups, an empty set else
	 */
	public abstract List<Group> getGroups(String authUser, int start, int end);

	/**
	 * Returns details of one group.
	 * 
	 * @param authUserName
	 * @param groupName
	 * @return the group's details, null else
	 */
	public abstract Group getGroupDetails(String authUserName, String groupName);

	/**
	 * Returns a list of tags which can be filtered.
	 * 
	 * @param authUser
	 *            name of the authenticated user
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
	public abstract List<Tag> getTags(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end);

	/**
	 * Returns details about a tag. Those details are:
	 * <ul>
	 * <li>details about the tag itself, like number of occurrences etc</li>
	 * <li>list of subtags</li>
	 * <li>list of supertags</li>
	 * <li>list of correlated tags</li>
	 * </ul>
	 * 
	 * @param authUserName
	 *            name of the authenticated user
	 * @param tagName
	 *            name of the tag
	 * @return the tag's details, null else
	 */
	public abstract Tag getTagDetails(String authUserName, String tagName);

	/**
	 * Validates a user's access.
	 * 
	 * @param username
	 *            name of the user
	 * @param apiKey
	 *            apiKey
	 * @return true if the user exists and has the given apiKey
	 */
	public abstract boolean validateUserAccess(String username, String apiKey);

	/**
	 * Checks if the given api key is valid.
	 * 
	 * @param apiKey
	 *            the api key to check.
	 * @return true if the key is valid, false else.
	 */
	public abstract boolean validateSoftwareKey(String apiKey);

	/**
	 * Removes the given user.
	 * 
	 * @param userName
	 *            the user to delete
	 */
	public abstract void deleteUser(String userName);

	/**
	 * Removes the given group.
	 * 
	 * @param groupName
	 *            the group to delete
	 */
	public abstract void deleteGroup(String groupName);

	/**
	 * Removes an user from a group.
	 * 
	 * @param groupName
	 *            the group to change
	 * @param userName
	 *            the user to remove
	 */
	public abstract void removeUserFromGroup(String groupName, String userName);

	/**
	 * Removes the given post - identified by the connected resource's hash -
	 * from the user.
	 * 
	 * @param userName  user who's post is to be removed
	 * @param resourceHash
	 *            hash of the resource, which is connected to the post to delete
	 */
	public abstract void deletePost(String userName, String resourceHash);

	/**
	 * Adds/updates a user in the database.
	 * @param authUserName  currently logged in user's name
	 * @param user  the user to store
	 */
	public abstract void storeUser(String authUserName, User user);

	/**
	 * Adds/updates a post in the database.
	 * 
	 * @param userName  name of the user who posts this post
	 * @param post  the post to be postet
	 */
	public abstract <T extends Resource> void storePost(String userName, Post<T> post);

	/**
	 * Adds/updates a group in the database.
	 * 
	 * @param authUserName  currently logged in user's name
	 * @param group  the group to add
	 */
	public abstract void storeGroup(String authUserName, Group group);

	/**
	 * Adds an existing user to an existing group.
	 * 
	 * @param groupName  name of the existing group
	 * @param user  user to add
	 */
	public abstract void addUserToGroup(String groupName, String userName);
}