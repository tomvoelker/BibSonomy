package org.bibsonomy.database.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.LogicInterfaceHelper;
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
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class RestDatabaseManager implements LogicInterface {

	/** Singleton */
	private final static RestDatabaseManager singleton = new RestDatabaseManager();
	private final Map<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> allDatabaseManagers;
	private final BookmarkDatabaseManager bookmarkDBManager;
	private final BibTexDatabaseManager bibtexDBManager;
	private final UserDatabaseManager userDBManager;
	private final TagDatabaseManager tagDBManager;
	//private GroupDatabaseManager groupDBManager = GroupDatabaseManager.getInstance();

	private RestDatabaseManager() {
		this.allDatabaseManagers = new HashMap<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>>();
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
	 * @param authUser
	 *            currently logged in user's name
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(final String authUser, final int start, final int end) {
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
	 * @param authUser
	 *            currently logged in user's name
	 * @param groupName
	 *            name of the group
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(final String authUser, final String groupName, final int start, final int end) {
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
	 * @param userName
	 *            name of the user we want to get details from
	 * @return details about a named user, null else
	 */
	public User getUserDetails(final String authUserName, final String userName) {
		final Transaction session = this.openSession();
		try {
			return this.userDBManager.getUserDetails(authUserName, userName, session);
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
	 * @param ordering
	 * @param start
	 * @param end
	 * @return a set of posts, an empty set else
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Post<T>> getPosts(final String authUser, final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end) {
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
				final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, authUser, grouping, groupingName, tags, hash, order, start, end);
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = (List<Post<T>>) ((List) this.bibtexDBManager.getPosts(param, session));
			} else if (resourceType == Bookmark.class) {
				final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, authUser, grouping, groupingName, tags, hash, order, start, end);
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = (List<Post<T>>) ((List) this.bookmarkDBManager.getPosts(param, session));
			} else {
				throw new UnsupportedResourceTypeException(resourceType.toString());
			}
		} finally {
			session.close();
		}
		return result;
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
	public Post<? extends Resource> getPostDetails(final String authUser, final String resourceHash, final String userName) {
		final Transaction session = this.openSession();
		try {
			Post<? extends Resource> rVal;
			for (final CrudableContent<? extends Resource, ? extends GenericParam> manager : allDatabaseManagers.values()) {
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
	public List<Group> getGroups(final String string, final int start, final int end) {
		return null;
	}

	/**
	 * Returns details of one group
	 * 
	 * @param authUserName
	 * @param groupName
	 * @return the group's details, null else
	 */
	public Group getGroupDetails(final String authUserName, final String groupName) {
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
	public List<Tag> getTags(final String authUser, final GroupingEntity grouping, final String groupingName, final String regex, final int start, final int end) {
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
	public Tag getTagDetails(final String authUserName, final String tagName) {
		final Transaction session = this.openSession();
		try {
			return this.tagDBManager.getTagDetails(authUserName, tagName, session);
		} finally {
			session.close();
		}
	}

	/**
	 * Validates user access.
	 * 
	 * @param username
	 *            name of the user
	 * @param apiKey
	 *            apiKey
	 * @return true if the user exists and has the given password
	 */
	public boolean validateUserAccess(final String username, final String apiKey) {
		final Transaction session = this.openSession();
		try {
			return this.userDBManager.validateUserAccess(username, apiKey, session);
		} finally {
			session.close();
		}
	}

	/**
	 * Checks if the given software key is valid.
	 * 
	 * @param softwareKey the software key to check.
	 * @return true if the software key is valid, false else.
	 */
	public boolean validateSoftwareKey(final String softwareKey) {
		// FIXME: impl. a software key
		return true;
	}

	/**
	 * Removes the given user from bibsonomy.
	 * 
	 * @param userName the user to delete
	 */
	public void deleteUser(final String userName) {
	}

	/**
	 * Removes the given group from bibsonomy.
	 * 
	 * @param groupName the group to delete
	 */
	public void deleteGroup(final String groupName) {
	}

	/**
	 * Removes an user from a group.
	 * 
	 * @param groupName the group to change
	 * @param userName the user to remove
	 */
	public void removeUserFromGroup(final String groupName, final String userName) {
	}

	/**
	 * Removes the given post - identified by the connected resource's hash - from the user.
	 * 
	 * @param userName user who's post is to be removed
	 * @param resourceHash hash of the resource, which is connected to the post to delete 
	 */
	public void deletePost(final String userName, final String resourceHash) {
		final Transaction session = this.openSession();
		try {
			// TODO would be nice to know about the resourcetype or the instance behind this resourceHash
			for (final CrudableContent<? extends Resource, ? extends GenericParam> man : this.allDatabaseManagers.values()) {
				if (man.deletePost(userName, resourceHash, session) == true) {
					break;
				}
			}
		} finally {
			session.close();
		}
	}

	/**
	 * Adds/updates a user in the database.
	 * 
	 * @param user the user to store
	 * @param update true if its an existing user (identified by username), false if its a new user
	 */
	public void storeUser(final User user, final boolean update) {
	}

	/**
	 * Adds/updates a post in the database.
	 * 
	 * @param userName name of the user who posts this post
	 * @param post the post to be postet
	 * @param update true if its an existing post (identified by its resource's intrahash), false if its a new post
	 */
	public <T extends Resource> void storePost(final String userName, final Post<T> post) {
		final Transaction session = this.openSession();
		try {
			final CrudableContent<T, GenericParam> man = getFittingDatabaseManager(post);
			final String oldIntraHash = post.getResource().getIntraHash();
			post.getResource().recalculateHashes();
			man.storePost(userName, post, oldIntraHash, session);
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Resource> CrudableContent<T, GenericParam> getFittingDatabaseManager(final Post<T> post) {
		final Class resourceClass = post.getResource().getClass();
		CrudableContent<? extends Resource, ? extends GenericParam> man = this.allDatabaseManagers.get(resourceClass);
		if (man == null) {
			for (final Map.Entry<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> entry : this.allDatabaseManagers.entrySet()) {
				if (entry.getKey().isAssignableFrom(resourceClass)) {
					man = entry.getValue();
					break;
				}
			}
			if (man == null) {
				throw new UnsupportedResourceTypeException(resourceClass.toString());
			}
		}
		return (CrudableContent<T, GenericParam>) ((CrudableContent) man);
	}

	/**
	 * Adds/updates a group in the database.
	 * 
	 * @param group the group to add
	 * @param update true if its an existing group, false if its a new group
	 */
	public void storeGroup(final Group group, final boolean update) {
	}

	/**
	 * Adds an existing user to an existing group.
	 * 
	 * @param groupName name of the existing group
	 * @param user user to add
	 */
	public void addUserToGroup(final String groupName, final String userName) {
	}
}