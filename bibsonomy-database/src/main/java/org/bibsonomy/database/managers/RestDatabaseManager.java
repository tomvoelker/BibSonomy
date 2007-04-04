package org.bibsonomy.database.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.ResourceType;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * This is an implementation of the LogicInterface for the REST-API.
 * 
 * @version $Id$
 */
public class RestDatabaseManager implements LogicInterface {

	/** Singleton */
	private final static RestDatabaseManager singleton = new RestDatabaseManager();
	private final Map<ResourceType, CrudableContent> contentDBManagers;

	private RestDatabaseManager() {
		// add some default
		this.contentDBManagers = new HashMap<ResourceType, CrudableContent>();
		//this.contentDBManagers.put(ResourceType.BOOKMARK, BookmarkDatabaseManager.getInstance());
		this.contentDBManagers.put(ResourceType.BIBTEX, BibTexDatabaseManager.getInstance());
	}

	public static LogicInterface getInstance() {
		return singleton;
	}

	/**
	 * returns all users bibsonomy has
	 * 
	 * @param authUser currently logged in user's name
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(String authUser, int start, int end) {
		return null;
	}

	/**
	 * returns all users who are members of the specified group
	 * 
	 * @param authUser currently logged in user's name
	 * @param groupName name of the group
	 * @param start
	 * @param end
	 * @return  a set of users, an empty set else
	 */
	public List<User> getUsers(String authUser, String groupName, int start, int end) {
		return null;
	}

	/**
	 * returns details about a specified user
	 * 
	 * @param authUserName
	 * @param userName name of the user we want to get details from
	 * @return details about a named user, null else
	 */
	public User getUserDetails(String authUserName, String userName) {
		return null;
	}

	/**
	 * returns a list of posts. the list can be filtered
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
	 *            if the parameter is not used, its am empty set
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
	public List<Post<? extends Resource>> getPosts(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
	
		List <Post<? extends Resource>> result = new LinkedList <Post<? extends Resource>>();
		
		if (resourceType == ResourceType.ALL) {
			/*
			 * iterate over all available database managers
			 */
			for (CrudableContent man: this.contentDBManagers.values()) {
				/*
				 * TODO: this must be the "part of the window" query!
				 */
				result.addAll(man.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, false));
				// SELECT t.content_id,tt.tag_name,t.user_name,b.book_url_hash FROM (select * from tas where tag_name = "semantic" GROUP BY content_id ORDER BY date DESC LIMIT 10) AS t JOIN bookmark b USING (content_id) JOIN tas tt USING (content_id) WHERE t.content_type=1;
			}
		} else {
			CrudableContent abstractContentDBManager = this.contentDBManagers.get(resourceType);
			List<Post<? extends Resource>> posts = abstractContentDBManager.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, true);
			/*
			 * get the next end-start posts for that resourceType!
			 */
			result.addAll(posts);
		}
		return result;
	}

	/**
	 * returns details to a post. a post is uniquely identified by a hash of the corresponding resource and a username
	 * 
	 * @param authUser authenticated user name
	 * @param resourceHash hash value of the corresponding resource
	 * @param userName name of the post-owner
	 * @return the post's details, null else
	 */
	public Post<? extends Resource> getPostDetails(String authUser, String resourceHash, String userName) {
		Post<? extends Resource> post = null;
		for (CrudableContent man : this.contentDBManagers.values()) {
			post = man.getPostDetails(authUser, resourceHash, userName);
			if (post != null) break;
		}
		return post;
	}

	/**
	 * returns all groups of the system
	 * TODO: what is the param "string" good for??
	 * @param end 
	 * @param start 
	 * @param string 
	 * @return a set of groups, an empty set else
	 */
	public List<Group> getGroups(String string, int start, int end) {
		return null;
	}

	/**
	 * returns details of one group
	 * 
	 * @param authUserName
	 * @param groupName
	 * @return the group's details, null else
	 */
	public Group getGroupDetails(String authUserName, String groupName) {
		return null;
	}

	/**
	 * returns a list of tags. the list can be filtered
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
	public List<Tag> getTags(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return null;
	}

	/**
	 * returns details about a tag. those details are:
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
	public Tag getTagDetails(String authUserName, String tagName) {
		return null;
	}

	/**
	 * validates a user's access to bibsonomy.
	 * 
	 * @param username name of the user
	 * @param password password
	 * @return true if the user exists and has the given password
	 */
	public boolean validateUserAccess(String username, String password) {
		return true;
		//TODO: implement this method!
	}

	/**
	 * removes the given user from bibsonomy.
	 * 
	 * @param userName the user to delete
	 */
	public void deleteUser(String userName) {
	}

	/**
	 * removes the given group from bibsonomy.
	 * 
	 * @param groupName the group to delete
	 */
	public void deleteGroup(String groupName) {
	}

	/**
	 * removes an user from a group.
	 * 
	 * @param groupName the group to change
	 * @param userName the user to remove
	 */
	public void removeUserFromGroup(String groupName, String userName) {
	}

	/**
	 * removes the given post - identified by the connected resource's hash - from the user.
	 * 
	 * @param userName user who's post is to be removed
	 * @param resourceHash hash of the resource, which is connected to the post to delete 
	 */
	public void deletePost(String userName, String resourceHash) {
		for (CrudableContent man : this.contentDBManagers.values()) {
			if (man.deletePost(userName, resourceHash)) break;
		}
	}

	/**
	 * adds/ updates a user in the database.
	 * 
	 * @param user the user to store
	 * @param update true if its an existing user (identified by username), false if its a new user
	 */
	public void storeUser(User user, boolean update) {
	}

	/**
	 * adds/ updates a post in the database.
	 * 
	 * @param userName name of the user who posts this post
	 * @param post the post to be postet
	 * @param update true if its an existing post (identified by its resource's intrahash), false if its a new post
	 */
	public void storePost(String userName, Post post, boolean update) {
		for (CrudableContent man : this.contentDBManagers.values()) {
			if (man.storePost(userName, post, update)) break;
		}
	}

	/**
	 * adds/ updates a group in the database.
	 * 
	 * @param group the group to add
	 * @param update true if its an existing group, false if its a new group
	 */
	public void storeGroup(Group group, boolean update) {
	}

	/**
	 * adds an existing user to an existing group.
	 * 
	 * @param groupName name of the existing group
	 * @param user user to add
	 */
	public void addUserToGroup(String groupName, String userName) {
	}
}