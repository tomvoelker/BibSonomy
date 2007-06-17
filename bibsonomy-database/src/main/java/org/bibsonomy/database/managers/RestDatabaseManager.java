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
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.database.util.DBSession;
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
	private final GroupDatabaseManager groupDBManager;
	private final TagDatabaseManager tagDBManager;
	private DBSessionFactory dbSessionFactory;

	private RestDatabaseManager() {
		this.allDatabaseManagers = new HashMap<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>>();
		this.bibtexDBManager = BibTexDatabaseManager.getInstance();
		this.allDatabaseManagers.put(BibTex.class, this.bibtexDBManager);
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();
		this.allDatabaseManagers.put(Bookmark.class, this.bookmarkDBManager);
		
		this.userDBManager = UserDatabaseManager.getInstance();
		this.groupDBManager = GroupDatabaseManager.getInstance();
		this.tagDBManager = TagDatabaseManager.getInstance();
		this.dbSessionFactory = DatabaseUtils.getDBSessionFactory();
	}

	public static LogicInterface getInstance() {
		return singleton;
	}

	/**
	 * Returns a new database session.
	 */
	private DBSession openSession() {
		return dbSessionFactory.getDatabaseSession();
	}

	/*
	 * Returns all users of the system
	 */
	public List<User> getUsers(final String authUser, final int start, final int end) {
		final DBSession session = this.openSession();
		try {
			return this.userDBManager.getAllUsers(start, end, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Returns all users who are members of the specified group
	 */
	public List<User> getUsers(final String authUser, final String groupName, final int start, final int end) {
		final DBSession session = this.openSession();
		try {
			return this.groupDBManager.getGroupMembers(authUser, groupName, session).getUsers();
		} finally {
			session.close();
		}
	}

	/*
	 * Returns details about a specified user
	 */
	public User getUserDetails(final String authUserName, final String userName) {
		final DBSession session = this.openSession();
		try {
			return this.userDBManager.getUserDetails(userName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Returns a list of posts; the list can be filtered.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Post<T>> getPosts(final String authUser, final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end) {
		final List<Post<T>> result;
		final DBSession session = this.openSession();
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

	/*
	 * Returns details to a post. A post is uniquely identified by a hash of the
	 * corresponding resource and a username.
	 */
	public Post<? extends Resource> getPostDetails(final String authUser, final String resourceHash, final String userName) {
		final DBSession session = this.openSession();
		try {
			Post<? extends Resource> rVal;
			for (final CrudableContent<? extends Resource, ? extends GenericParam> manager : this.allDatabaseManagers.values()) {
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

	/*
	 * Returns all groups of the system
	 */
	public List<Group> getGroups(final String authUser, final int start, final int end) {
		final DBSession session = this.openSession();
		try {
			return this.groupDBManager.getAllGroups(start, end, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Returns details of one group
	 */
	public Group getGroupDetails(final String authUserName, final String groupName) {
		final DBSession session = this.openSession();
		try {
			return this.groupDBManager.getGroupByName(groupName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Returns a list of tags; the list can be filtered.
	 */
	public List<Tag> getTags(final String authUser, final GroupingEntity grouping, final String groupingName, final String regex, final int start, final int end) {
		final DBSession session = this.openSession();
		try {
			return this.tagDBManager.getTags(authUser, grouping, groupingName, regex, start, end, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Returns details about a tag.
	 */
	public Tag getTagDetails(final String authUserName, final String tagName) {
		final DBSession session = this.openSession();
		try {
			return this.tagDBManager.getTagDetails(authUserName, tagName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Validates user access.
	 */
	public boolean validateUserAccess(final String username, final String apiKey) {
		final DBSession session = this.openSession();
		try {
			return this.userDBManager.validateUserAccess(username, apiKey, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Checks if the given software key is valid.
	 */
	public boolean validateSoftwareKey(final String softwareKey) {
		// FIXME: impl. a software key
		return true;
	}

	/*
	 * Removes the given user.
	 */
	public void deleteUser(final String userName) {
		final DBSession session = this.openSession();
		try {
			this.userDBManager.deleteUser(userName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Removes the given group.
	 */
	public void deleteGroup(final String groupName) {
		final DBSession session = this.openSession();
		try {
			this.groupDBManager.deleteGroup(groupName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Removes an user from a group.
	 */
	public void removeUserFromGroup(final String groupName, final String userName) {
		final DBSession session = this.openSession();
		try {
			this.groupDBManager.removeUserFromGroup(groupName, userName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Removes the given post - identified by the connected resource's hash -
	 * from the user.
	 */
	public void deletePost(final String userName, final String resourceHash) {
		final DBSession session = this.openSession();
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

	/*
	 * Adds/updates a user in the database.
	 */
	public void storeUser(final User user, final boolean update) {
		final DBSession session = this.openSession();
		try {
			this.userDBManager.storeUser(user, update, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Adds/updates a post in the database.
	 */
	public <T extends Resource> void storePost(final String userName, final Post<T> post) {
		final DBSession session = this.openSession();
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

	/*
	 * Adds/updates a group in the database.
	 */
	public void storeGroup(final Group group, final boolean update) {
		final DBSession session = this.openSession();
		try {
			this.groupDBManager.storeGroup(group, update, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Adds an existing user to an existing group.
	 */
	public void addUserToGroup(final String groupName, final String userName) {
		final DBSession session = this.openSession();
		try {
			this.groupDBManager.addUserToGroup(groupName, userName, session);
		} finally {
			session.close();
		}
	}

	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}
}