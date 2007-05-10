package org.bibsonomy.database.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * This is an implementation of the LogicInterface for the REST-API.
 * 
 * TODO: ...so why is it called RestDATABASEManager and is part of the DATABASE
 * package??? i would advise introducing a three+-layer architecture
 * 
 * @version $Id$
 */
public class RestDatabaseManager implements LogicInterface {

	/** Singleton */
	private final static RestDatabaseManager singleton = new RestDatabaseManager();
	private final Map<Class<? extends Resource>, CrudableContent<? extends Resource>> allDatabaseManagers = new HashMap<Class<? extends Resource>, CrudableContent<? extends Resource>>();
	private final BookmarkDatabaseManager bookmarkDBManager;
	private final BibTexDatabaseManager bibtexDBManager;
	private final UserDatabaseManager userDBManager;
	private final TagDatabaseManager tagDBManager;
	//private GroupDatabaseManager groupDBManager = GroupDatabaseManager.getInstance();

	private RestDatabaseManager() {
		this.bibtexDBManager = BibTexDatabaseManager.getInstance();
		this.allDatabaseManagers.put(BibTex.class, this.bibtexDBManager);
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();
		this.allDatabaseManagers.put(Bookmark.class, this.bookmarkDBManager);
		
		this.userDBManager = UserDatabaseManager.getInstance();
		this.tagDBManager = TagDatabaseManager.getInstance();
	}

	public static LogicInterface getInstance() {
		return singleton;
	}

	private Transaction openSession() {
		return DatabaseUtils.getDatabaseSession();
	}

	/**
	 * Returns all users of the system
	 * 
	 * @param authUser currently logged in user's name
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(String authUser, int start, int end) {
		final Transaction session = this.openSession();
		try {
			return this.userDBManager.getUsers(authUser, start, end, session);
		} finally {
			session.close();
		}
	}

	/**
	 * Returns all users who are members of the specified group
	 * 
	 * @param authUser currently logged in user's name
	 * @param groupName name of the group
	 * @param start
	 * @param end
	 * @return  a set of users, an empty set else
	 */
	public List<User> getUsers(String authUser, String groupName, int start, int end) {
		final Transaction session = this.openSession();
		try {
			return this.userDBManager.getUsers(authUser, groupName, start, end, session);
		} finally {
			session.close();
		}
	}

	/**
	 * Returns details about a specified user
	 * 
	 * @param authUserName
	 * @param userName name of the user we want to get details from
	 * @return details about a named user, null else
	 */
	public User getUserDetails(String authUserName, String userName) {
		final Transaction session = this.openSession();
		try {
			return this.userDBManager.getUserDetails(authUserName,userName, session);
		} finally {
			session.close();
		}
	}

	/**
	 * Returns a list of posts; the list can be filtered.
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
	public <T extends Resource> List<Post<T>> getPosts(String authUser, Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final List<Post<T>> result;
		final Transaction session = this.openSession();
		try {
			/*if (resourceType == Resource.class) {
				 * yes, this IS unsave and indeed it BREAKS restrictions on generic-constraints.
				 * it is the result of two designs:
				 *  1. @ibatis: database-results should be accessible as a stream or should at least be saved using the visitor pattern (collection<? super X> arguments would do fine)
				 *  2. @bibsonomy: this method needs runtime-type-checking which is not supported by generics
				 *  so what: copy each and every entry manually or split this method to become
				 *           type-safe WITHOUT falling back to <? extends Resource> (which
				 *           means read-only) in the whole project
				 * result = bibtexDBManager.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, false);			
				 * // TODO: solve problem with limit+offset:  result.addAll(bookmarkDBManager.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, false));
				 * 
			} else */
			if (resourceType == BibTex.class) {
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = (List<Post<T>>) ((List) bibtexDBManager.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, true, session));
			} else if (resourceType == Bookmark.class) {
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = (List<Post<T>>) ((List) bookmarkDBManager.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, true, session));
			} else {
				throw new UnsupportedResourceTypeException( resourceType.toString() );
			}
		} finally {
			session.close();
		}
		return result;
	}

	/**
	 * FIXME: Where's this method called?
	 */
	public BibTexParam buildBibTexParam(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BibTexParam param = new BibTexParam();		
		param.setUserName(authUser);
		param.setOffset(start);
		param.setLimit(end - start);
		if ((groupingName != null) && (groupingName.length() == 0))	{
			groupingName = null;
		}
		if (grouping == GroupingEntity.USER) {
			param.setRequestedUserName(groupingName);
		} else if (grouping == GroupingEntity.FRIEND) {
			param.setRequestedGroupName(groupingName); // TODO: document
		} else if (grouping == GroupingEntity.GROUP) {
			param.setRequestedGroupName(groupingName); 
		}
		return param;
	}

	/**
	 * Returns details to a post. A post is uniquely identified by a hash of the
	 * corresponding resource and a username.
	 * 
	 * @param authUser authenticated user name
	 * @param resourceHash hash value of the corresponding resource
	 * @param userName name of the post-owner
	 * @return the post's details, null else
	 */
	public Post<? extends Resource> getPostDetails(String authUser, String resourceHash, String userName) {
		final Transaction session = this.openSession();
		try {
			Post<? extends Resource> rVal;
			for (CrudableContent<? extends Resource> manager : allDatabaseManagers.values()) {
				rVal = manager.getPostDetails(authUser, resourceHash, userName, session);
				if (rVal != null) {
					return rVal;
				}
			}
		} finally {
			session.close();
		}
		return null;
	}

	/**
	 * Returns all groups of the system
	 * 
	 * TODO: what is the param "string" good for??
	 * 
	 * @param end
	 * @param start
	 * @param string
	 * @return a set of groups, an empty set else
	 */
	public List<Group> getGroups(String string, int start, int end) {
		return null;
	}

	/**
	 * Returns details of one group
	 * 
	 * @param authUserName
	 * @param groupName
	 * @return the group's details, null else
	 */
	public Group getGroupDetails(String authUserName, String groupName) {
		return null;
	}

	/**
	 * Returns a list of tags; the list can be filtered.
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
		final Transaction session = this.openSession();
		try {
			return this.tagDBManager.getTags(authUser, grouping, groupingName, regex, start, end, session); 
		} finally {
			session.close();
		}
	}

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
	public Tag getTagDetails(String authUserName, String tagName) {
		final Transaction session = this.openSession();
		try {
			return tagDBManager.getTagDetails( authUserName, tagName, session);
		} finally {
			session.close();
		}
	}

	/**
	 * validates a user's access to bibsonomy.
	 * 
	 * @param username name of the user
	 * @param apiKey apiKey
	 * @return true if the user exists and has the given password
	 */
	public boolean validateUserAccess(String username, String apiKey) {
		return true;
		//TODO: implement this method!
	}

	/**
	 * Checks if the given software key is valid.
	 * 
	 * @param softwareKey the software key to check.
	 * @return true if the software key is valid, false else.
	 */
	public boolean validateSoftwareKey(String softwareKey) {
		// TODO: determine if a software key is to use
		return true;
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
		final Transaction session = this.openSession();
		try {
			// TODO would be nice to know about the resourcetype ot the instance behind this resourceHash
			for (CrudableContent<? extends Resource> man : allDatabaseManagers.values()) {
				if (man.deletePost(userName, resourceHash, session) == true) {
					break;
				}
			}
		} finally {
			session.close();
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
	public <T extends Resource> void storePost(String userName, Post<T> post) {
		final Transaction session = this.openSession();
		try {
			final CrudableContent<T> man = getFittingDatabaseManager(post);
			final String oldIntraHash = post.getResource().getIntraHash();
			post.getResource().recalculateHashes();
			man.storePost(userName, post, oldIntraHash, session);
		} finally {
			session.close();
		}
	}

	private <T extends Resource>CrudableContent<T> getFittingDatabaseManager(Post<T> post) {
		final Class resourceClass = post.getResource().getClass();
		CrudableContent<? extends Resource> man = this.allDatabaseManagers.get(resourceClass);
		if (man == null) {
			for (final Map.Entry<Class<? extends Resource>, CrudableContent<? extends Resource>> entry : this.allDatabaseManagers.entrySet()) {
				if (entry.getKey().isAssignableFrom(resourceClass)) {
					man = entry.getValue();
					break;
				}
			}
			if (man == null) {
				throw new UnsupportedResourceTypeException( resourceClass.toString() );
			}
		}
		return (CrudableContent<T>) ((CrudableContent) man);
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