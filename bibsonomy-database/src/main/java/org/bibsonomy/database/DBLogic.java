package org.bibsonomy.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.errors.UnspecifiedErrorMessage;
import org.bibsonomy.common.exceptions.QueryTimeoutException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.database.managers.AuthorDatabaseManager;
import org.bibsonomy.database.managers.BasketDatabaseManager;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.CrudableContent;
import org.bibsonomy.database.managers.DocumentDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.StatisticsDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.TagRelationDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PostUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * Database Implementation of the LogicInterface
 * 
 * @author Jens Illig
 * @author Christian Kramer
 * @author Christian Claus
 * @author Dominik Benz
 * @author Robert Jäschke
 * 
 * @version $Id$
 */
public class DBLogic implements LogicInterface {

	private static final Log log = LogFactory.getLog(DBLogic.class);

	private final Map<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> allDatabaseManagers;
	private final AuthorDatabaseManager authorDBManager;
	private final DocumentDatabaseManager docDBManager;
	private final PermissionDatabaseManager permissionDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;
	private final BibTexDatabaseManager bibtexDBManager;
	private final UserDatabaseManager userDBManager;
	private final GroupDatabaseManager groupDBManager;
	private final TagDatabaseManager tagDBManager;
	private final AdminDatabaseManager adminDBManager;
	private final DBSessionFactory dbSessionFactory;
	private final StatisticsDatabaseManager statisticsDBManager;
	private final TagRelationDatabaseManager tagRelationsDBManager;
	private final BasketDatabaseManager basketDBManager;
	private final InboxDatabaseManager inboxDBManager;

	private final User loginUser;

	/**
	 * Returns an implementation of the DBLogic.
	 * 
	 * @param loginUser
	 *            - the user which wants to use the logic.
	 * @param dbSessionFactory
	 */
	protected DBLogic(final User loginUser, final DBSessionFactory dbSessionFactory) {
		this.loginUser = loginUser;

		this.allDatabaseManagers = new HashMap<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>>();
		this.bibtexDBManager = BibTexDatabaseManager.getInstance();
		this.allDatabaseManagers.put(BibTex.class, this.bibtexDBManager);
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();
		this.allDatabaseManagers.put(Bookmark.class, this.bookmarkDBManager);

		this.authorDBManager = AuthorDatabaseManager.getInstance();
		this.docDBManager = DocumentDatabaseManager.getInstance();
		this.userDBManager = UserDatabaseManager.getInstance();
		this.groupDBManager = GroupDatabaseManager.getInstance();
		this.tagDBManager = TagDatabaseManager.getInstance();
		this.adminDBManager = AdminDatabaseManager.getInstance();
		this.permissionDBManager = PermissionDatabaseManager.getInstance();
		this.statisticsDBManager = StatisticsDatabaseManager.getInstance();
		this.tagRelationsDBManager = TagRelationDatabaseManager.getInstance();

		this.basketDBManager = BasketDatabaseManager.getInstance();
		this.inboxDBManager = InboxDatabaseManager.getInstance();

		this.dbSessionFactory = dbSessionFactory;
	}

	/**
	 * Returns a new database session. If a user is logged in, he gets the
	 * master connection, if not logged in, the secondary connection
	 */
	private DBSession openSession() {
		// uncomment following to access secondary datasource for not logged-in
		// users
		// if (this.loginUser.getName() == null) {
		// return this.dbSessionFactory.getDatabaseSession(DatabaseType.SLAVE);
		// }
		return this.dbSessionFactory.getDatabaseSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getUserFriends(org.bibsonomy
	 * .model.User) FIXME: use String instead of User
	 */
	@Override
	public List<User> getUserFriends(final User loginUser) {
		/*
		 * only logged in users can get a friend list
		 */
		this.ensureLoggedIn();
		/*
		 * only admins can access the friend list of another user
		 */
		if (!loginUser.getName().equals(loginUser.getName())) {
			this.permissionDBManager.ensureAdminAccess(loginUser);
		}
		final DBSession session = openSession();
		try {
			return this.userDBManager.getUserRelation(loginUser.getName(), UserRelation.FRIEND_OF, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getFriendsOfUser(org.bibsonomy
	 * .model.User) FIXME: use String instead of User
	 */
	@Override
	public List<User> getFriendsOfUser(final User loginUser) {
		/*
		 * only logged in users can get a friend list
		 */
		this.ensureLoggedIn();
		/*
		 * only admins can access the friend list of another user
		 */
		if (!loginUser.getName().equals(loginUser.getName())) {
			this.permissionDBManager.ensureAdminAccess(loginUser);
		}

		final DBSession session = openSession();
		try {
			return this.userDBManager.getUserRelation(loginUser.getName(), UserRelation.OF_FRIEND, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getUserDetails(java.lang.String)
	 * TODO: if userName = loginUser.getName() we could just return loginUser.
	 */
	@Override
	public User getUserDetails(final String userName) {
		final DBSession session = this.openSession();
		try {
			/*
			 * We don't use userName but user.getName() in the remaining part of
			 * this method, since the name gets normalized in getUserDetails().
			 */
			final User user = this.userDBManager.getUserDetails(userName, session);

			/*
			 * only admin and myself may see which group I'm a member of
			 */
			if (this.permissionDBManager.isAdminOrSelf(this.loginUser, user.getName())) {
				user.setGroups(this.groupDBManager.getGroupsForUser(user.getName(), true, session));
				// fill user's spam informations
				this.adminDBManager.getClassifierUserDetails(user, session);
				return user;
			}

			/*
			 * respect user privacy settings
			 * clear all profile attributes if current login user isn't allowed to see the profile
			 */
			if (!this.permissionDBManager.isAllowedToAccessUsersProfile(user, this.loginUser, session)) {
				/*
				 * TODO: this practically clears /all/ user information
				 */
				return new User(user.getName());
			}

			/*
			 * clear the private stuff
			 */
			user.setEmail(null);

			user.setApiKey(null);
			user.setPassword(null);

			user.setReminderPassword(null);
			user.setReminderPasswordRequestDate(null);

			user.setSettings(null);

			/*
			 * FIXME: other things set in userDBManager.getUserDetails() maybe not cleared!
			 */

			return user;
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#getPosts(java.lang.Class,
	 * org.bibsonomy.common.enums.GroupingEntity, java.lang.String,
	 * java.util.List, java.lang.String, org.bibsonomy.model.enums.Order,
	 * org.bibsonomy.common.enums.FilterEntity, int, int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Resource> List<Post<T>> getPosts(final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final FilterEntity filter, final int start, final int end, String search) {

		// check allowed start-/end-values
		if (grouping.equals(GroupingEntity.ALL) && !present(tags) && !present(search)) {
			this.permissionDBManager.checkStartEnd(loginUser, start, end, "post");
		}
		// check maximum number of allowed tags
		if (this.permissionDBManager.exceedsMaxmimumSize(tags)) {
			return new ArrayList<Post<T>>();
		}

		final List<Post<T>> result;
		final DBSession session = openSession();
		try {
			/*
			 * if (resourceType == Resource.class) { yes, this IS unsave and
			 * indeed it BREAKS restrictions on generic-constraints. it is the
			 * result of two designs: 1. @ibatis: database-results should be
			 * accessible as a stream or should at least be saved using the
			 * visitor pattern (collection<? super X> arguments would do fine)
			 * 2. @bibsonomy: this method needs runtime-type-checking which is
			 * not supported by generics so what: copy each and every entry
			 * manually or split this method to become type-safe WITHOUT falling
			 * back to <? extends Resource> (which means read-only) in the whole
			 * project result = bibtexDBManager.getPosts(authUser, grouping,
			 * groupingName, tags, hash, popular, added, start, end, false); //
			 * TODO: solve problem with limit+offset:
			 * result.addAll(bookmarkDBManager.getPosts(authUser, grouping,
			 * groupingName, tags, hash, popular, added, start, end, false));
			 * 
			 * } else
			 */
			if (resourceType == BibTex.class) {
				final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, grouping, groupingName, tags, hash, order, start, end, search, filter, this.loginUser);
				// check permissions for displaying links to documents
				final boolean allowedToAccessUsersOrGroupDocuments = this.permissionDBManager.isAllowedToAccessUsersOrGroupDocuments(this.loginUser, grouping, groupingName, filter, session);

				if (!allowedToAccessUsersOrGroupDocuments) {
					param.setFilter(FilterEntity.JUST_POSTS);
				} else if (!present(filter)) {
					param.setFilter(FilterEntity.POSTS_WITH_DOCUMENTS);
				}

				// this is save because of RTTI-check of resourceType argument
				// which is of class T
				result = ((List) this.bibtexDBManager.getPosts(param, session));
			} else if (resourceType == Bookmark.class) {

				// check filters
				// can not add filter to BookmarkParam yet, but need to add
				// group before buildParam
				if (this.permissionDBManager.checkFilterPermissions(filter, this.loginUser)) {
					/*
					 * FIXME: it is not safe, what is done here!
					 * checkFilterPermissions only checks, if ANY filter is
					 * applicable by loginUser. But here we assume
					 * ADMIN_SPAM_POSTS has been checked!
					 */
					loginUser.addGroup(new Group(GroupID.PUBLIC_SPAM));
				}

				final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, grouping, groupingName, tags, hash, order, start, end, search, filter, this.loginUser);

				// this is save because of RTTI-check of resourceType argument
				// which is of class T
				result = ((List) this.bookmarkDBManager.getPosts(param, session));
			} else {
				throw new UnsupportedResourceTypeException();
			}
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return an empty list
			return new ArrayList<Post<T>>();
		} finally {
			session.close();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.PostLogicInterface#getPostDetails(java.lang.String, java.lang.String)
	 */
	@Override
	public Post<? extends Resource> getPostDetails(final String resourceHash, final String userName) {
		final DBSession session = openSession();
		try {
			for (final CrudableContent<? extends Resource, ? extends GenericParam> manager : this.allDatabaseManagers.values()) {
				final Post<? extends Resource> post = manager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				/*
				 * if a manager found a post, return it
				 */
				if (present(post)) return post;
				/*
				 * check next manager
				 */
			}
		} finally {
			session.close();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#getGroups(int, int)
	 */
	@Override
	public List<Group> getGroups(final int start, final int end) {
		final DBSession session = openSession();
		try {
			return this.groupDBManager.getAllGroups(start, end, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getGroupDetails(java.lang.String
	 * )
	 */
	@Override
	public Group getGroupDetails(final String groupName) {
		final DBSession session = openSession();
		try {
			final Group myGroup = this.groupDBManager.getGroupByName(groupName, session);
			if (myGroup != null) {
				myGroup.setTagSets(this.groupDBManager.getGroupTagSets(groupName, session));
			}
			return myGroup;
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#getTags(java.lang.Class,
	 * org.bibsonomy.common.enums.GroupingEntity, java.lang.String,
	 * java.lang.String, java.util.List, java.lang.String,
	 * org.bibsonomy.model.enums.Order, int, int, java.lang.String,
	 * org.bibsonomy.common.enums.TagSimilarity)
	 */
	@Override
	public List<Tag> getTags(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final String regex, final List<String> tags, final String hash, final Order order, final int start, final int end, final String search, final TagSimilarity relation) {
		if (grouping.equals(GroupingEntity.ALL)) {
			this.permissionDBManager.checkStartEnd(loginUser, start, end, "Tag");
		}
		final DBSession session = openSession();
		final List<Tag> result;

		try {
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, grouping, groupingName, tags, hash, order, start, end, search, null, this.loginUser);
			param.setTagRelationType(relation);

			if (resourceType == BibTex.class || resourceType == Bookmark.class || resourceType == Resource.class) {
				// this is save because of RTTI-check of resourceType argument
				// which is of class T
				param.setRegex(regex);
				// need to switch from class to string to ensure legibility of
				// Tags.xml
				param.setContentTypeByClass(resourceType);
				result = this.tagDBManager.getTags(param, session);
			} else {
				throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
			}
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return an empty list
			return new ArrayList<Tag>();
		} finally {
			session.close();
		}
		/*
		 * XXX: workaround to make huge tag clouds smaller: 
		 * prune tags to top 100 for not-logged in users 
		 */
		pruneSetSize(result, 1000, 100);
		return result;
	}


	/**
	 * If result.size() > minSetSize and the user is not logged in, removes
	 * all but the top maxSetSize tags (sorted by count).  
	 * 
	 * @param result
	 * @param minSetSize
	 * @param maxSetSize
	 */
	private void pruneSetSize(final List<Tag> result, int minSetSize, int maxSetSize) {
		if (result != null && result.size() > minSetSize && this.loginUser.getName() == null) {
			/*
			 * sort result by count
			 */
			Collections.sort(result, 
					new Comparator<Tag>() { 
				@Override
				public int compare(Tag o1, Tag o2) {
					if (o1.getGlobalcount() == o2.getGlobalcount()) {
						if (o1.getUsercount() == o2.getUsercount()) {
							/*
							 * same counts -> use name
							 */
							return o1.getName().compareTo(o2.getName());
						}
						/*
						 * sort descending
						 */
						return o2.getUsercount() - o1.getUsercount();
					} 
					/*
					 * sort descending
					 */
					return o2.getGlobalcount() - o1.getGlobalcount();
				}
			}
			);
			/*
			 * remove all tags except first 100 
			 */
			final Iterator<Tag> iterator = result.iterator();
			int ctr = 0;
			while (iterator.hasNext()) {
				ctr++;
				iterator.next();
				if (ctr > maxSetSize) {
					iterator.remove();
				}
			}
			/*
			 * sort again alphabetically ...
			 */
			Collections.sort(result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getTagDetails(java.lang.String)
	 */
	@Override
	public Tag getTagDetails(final String tagName) {
		final DBSession session = openSession();
		try {
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, null, this.loginUser.getName(), Arrays.asList(tagName), null, null, 0, 1, null, null, this.loginUser);
			return this.tagDBManager.getTagDetails(param, session);
		} finally {
			session.close();
		}
	}

	/**
	 * Checks if the given software key is valid.
	 * 
	 * @param softwareKey
	 *            software key to be validated
	 * @return true iff the given software key is valid.
	 */
	public boolean validateSoftwareKey(final String softwareKey) {
		// FIXME: impl. a software key
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteUser(java.lang.String)
	 */
	@Override
	public void deleteUser(final String userName) {
		// TODO: take care of toLowerCase()!
		this.ensureLoggedIn();
		/*
		 * only an admin or the user himself may delete the account
		 */
		this.permissionDBManager.ensureIsAdminOrSelf(loginUser, userName);

		final DBSession session = openSession();
		try {
			userDBManager.deleteUser(userName, session);
		} finally {
			session.close();
		}

		// throw new UnsupportedOperationException("not yet available");

		// if ((this.loginUserName == null) ||
		// (this.loginUserName.equals(userName) == false)) {
		// throw new
		// ValidationException("You are not authorized to perform the requested operation");
		// }
		// final DBSession session = openSession();
		// try {
		// userDBManager.deleteUser(userName, session);
		// } finally {
		// session.close();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteGroup(java.lang.String)
	 */
	@Override
	public void deleteGroup(final String groupName) {

		throw new UnsupportedOperationException("not yet available");

		// final DBSession session = openSession();
		// try {
		// groupDBManager.deleteGroup(groupName, session);
		// } finally {
		// session.close();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#removeUserFromGroup(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void deleteUserFromGroup(final String groupName, final String userName) {
		// TODO: take care of toLowerCase()!
		// FIXME: IMPORTANT: not everybody may do this!
		// better do nothing than anything horribly wrong:
		throw new UnsupportedOperationException("not yet available");
		// final DBSession session = openSession();
		// try {
		// groupDBManager.removeUserFromGroup(groupName, userName, session);
		// } finally {
		// session.close();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#deletePosts(java.lang.String
	 * , java.util.List)
	 */
	@Override
	public void deletePosts(final String userName, final List<String> resourceHashes) {
		/*
		 * check permissions
		 */
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);
		/*
		 * to store hashes of missing resources
		 */
		final List<String> missingResources = new LinkedList<String>();

		final DBSession session = openSession();
		try {
			final String lowerCaseUserName = userName.toLowerCase();
			for (final String resourceHash : resourceHashes) {
				/*
				 * delete one resource
				 */
				boolean resourceFound = false;
				// TODO would be nice to know about the resourcetype or the
				// instance behind this resourceHash
				for (final CrudableContent<? extends Resource, ? extends GenericParam> man : this.allDatabaseManagers.values()) {
					if (man.deletePost(lowerCaseUserName, resourceHash, session) == true) {
						resourceFound = true;
						break;
					}
				}
				/*
				 * remember missing resources
				 */
				if (resourceFound == false) {
					missingResources.add(resourceHash);
				}
			}
		} finally {
			session.close();
		}
		/*
		 * throw exception for missing resources
		 */
		if (missingResources.size() > 0) {
			throw new IllegalStateException("The resource(s) with ID(s) " + missingResources + " do(es) not exist and could hence not be deleted.");
		}
	}

	/**
	 * Check for each group of a post if the groups actually exist and if the
	 * posting user is allowed to post. If yes, insert the correct group ID into
	 * the given post's groups.
	 * 
	 * @param post
	 *            the post whose groups will be modified.
	 */
	private void validateGroups(final Post<? extends Resource> post, final DBSession session) {
		/*
		 * First check for "public" and "private". Those two groups are special,
		 * they can't be assigned with another group.
		 */
		final Set<Group> groups = post.getGroups();
		if (GroupUtils.containsExclusiveGroup(groups)) {
			if (groups.size() > 1) {
				/*
				 * Those two groups are exclusive - they can not appear together
				 * or with any other group.
				 */
				throw new ValidationException("Group 'public' (or 'private') can not be combined with other groups.");
			}
			/*
			 * only one group and it is "public" or "private" -> set group id
			 * and return post
			 */
			final Group group = groups.iterator().next();
			if (group.equals(GroupUtils.getPrivateGroup())) {
				group.setGroupId(GroupUtils.getPrivateGroup().getGroupId());
			} else {
				group.setGroupId(GroupUtils.getPublicGroup().getGroupId());
			}
		} else {
			/*
			 * only non-special groups remain (including "friends") - check
			 * those
			 */
			/*
			 * retrieve the user's groups
			 */
			final Set<Integer> groupIds = new HashSet<Integer>(this.groupDBManager.getGroupIdsForUser(post.getUser().getName(), session));
			/*
			 * add "friends" group
			 */
			groupIds.add(GroupID.FRIENDS.getId());
			/*
			 * check that there are only groups the user is allowed to post to.
			 */
			for (final Group group : groups) {
				final Group testGroup = this.groupDBManager.getGroupByName(group.getName().toLowerCase(), session);
				if (testGroup == null) {
					// group does not exist
					throw new ValidationException("Group " + group.getName() + " does not exist");
				}
				if (!groupIds.contains(testGroup.getGroupId())) {
					// the posting user is not a member of this group
					throw new ValidationException("User " + post.getUser().getName() + " is not a member of group " + group.getName());
				}
				group.setGroupId(testGroup.getGroupId());
			}
		}

		// no group specified -> make it public
		if (groups.size() == 0) {
			groups.add(GroupUtils.getPublicGroup());
		}
	}

	/**
	 * Helper method to retrieve an appropriate database manager
	 * 
	 * @param <T>
	 *            extends Resource - the resource type
	 * @param post
	 *            - a post of type T
	 * @return an appropriate database manager
	 */
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
				throw new UnsupportedResourceTypeException();
			}
		}
		return ((CrudableContent) man);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#addUserToGroup(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addUserToGroup(final String groupName, final String userName) {
		// TODO: take care of toLowerCase()!
		throw new UnsupportedOperationException("not yet available");

		// final DBSession session = openSession();
		// try {
		// groupDBManager.addUserToGroup(groupName, userName, session);
		// } finally {
		// session.close();
		// }
	}

	/**
	 * helper method to check if a user is currently logged in
	 */
	private void ensureLoggedIn() {
		if (this.loginUser.getName() == null) {
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#createGroup(org.bibsonomy.model
	 * .Group)
	 */
	@Override
	public String createGroup(final Group group) {
		this.ensureLoggedIn();
		/*
		 * check permissions
		 */
		this.permissionDBManager.ensureAdminAccess(loginUser);

		final DBSession session = this.openSession();
		try {
			this.groupDBManager.createGroup(group, session);
		} finally {
			session.close();
		}
		return group.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#updateGroup(org.bibsonomy.model
	 * .Group)
	 */
	@Override
	public String updateGroup(final Group group, final GroupUpdateOperation operation) {
		this.ensureLoggedIn();

		if (!(present(group.getName()) && present(group.getGroupId()) && present(group.getPrivlevel()) && present(group.isSharedDocuments()))) {
			throw new ValidationException("The given group is not valid.");
		}

		final DBSession session = this.openSession();

		try	{
			switch(operation) {
			case UPDATE_ALL:
				//					this.groupDBManager.updateGroupSettings(group, session);
				//handle users
				throw new UnsupportedOperationException("The method " + GroupUpdateOperation.UPDATE_ALL + " is not yet implemented.");

			case UPDATE_SETTINGS:
				this.groupDBManager.updateGroupSettings(group, session);
				break;
			case ADD_NEW_USER:
				throw new UnsupportedOperationException("The method " + GroupUpdateOperation.ADD_NEW_USER + " is not yet implemented.");
			default:
				throw new UnsupportedOperationException("The given method is not yet implemented.");
			}
		} 
		finally	{
			session.close();
		}

		return group.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#createPosts(java.util.List)
	 */
	@Override
	public List<String> createPosts(final List<Post<?>> posts) {
		// TODO: Which of these checks should result in a DatabaseException,
		this.ensureLoggedIn();
		/*
		 * check permissions
		 */
		for (final Post<?> post : posts) {
			PostUtils.populatePostWithUser(post, this.loginUser);

			this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
		}
		/*
		 * insert posts TODO: more efficient implementation (transactions,
		 * deadlock handling, asynchronous, etc.)
		 */
		final List<String> hashes = new LinkedList<String>();
		/*
		 * open session to store all the posts
		 */
		final DBSession session = openSession();
		final DatabaseException collectedException = new DatabaseException();
		try{
			for (final Post<?> post : posts) {
				try {
					hashes.add(this.createPost(post, session));
				} catch (DatabaseException dbex) {
					collectedException.addErrors(dbex);
				} catch (Exception ex) {
					// some exception other than those covered in the DatabaseException was thrown					
					collectedException.addToErrorMessages(post.getResource().getIntraHash(), new UnspecifiedErrorMessage(ex));
				}
			}
		} finally {
			session.close();
		}
		if (collectedException.hasErrorMessages()) {
			throw collectedException;
		}
		return hashes;

	}


	/**
	 * Adds a post in the database.
	 */
	private <T extends Resource> String createPost(final Post<T> post, DBSession session) {
		final CrudableContent<T, GenericParam> manager = this.getFittingDatabaseManager(post);
		post.getResource().recalculateHashes();

		this.validateGroups(post, session);
		/*
		 * change group IDs to spam group IDs
		 */
		PostUtils.setGroupIds(post, this.loginUser);

		manager.createPost(post, session);

		// if we don't get an exception here, we assume the resource has
		// been successfully created
		return post.getResource().getIntraHash();
	}

	/** 
	 * The given posts are updated. If the operation is {@link PostUpdateOperation#UPDATE_TAGS}, 
	 * the posts must only contain the 
	 * <ul>
	 * <li>date, </li>
	 * <li>tags,</li>
	 * <li>intraHash,</li>
	 * <li>and optionally a username.
	 * </ul>
	 * 
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#updatePosts(java.util.List, org.bibsonomy.common.enums.PostUpdateOperation)
	 */
	@Override
	public List<String> updatePosts(final List<Post<?>> posts, final PostUpdateOperation operation) {
		// TODO: Which of these checks should result in a DatabaseException,
		// which do we want to handle otherwise (=status quo)
		this.ensureLoggedIn();
		/*
		 * check permissions
		 */
		for (final Post<?> post : posts) {
			PostUtils.populatePostWithUser(post, this.loginUser);

			this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
		}
		final List<String> hashes = new LinkedList<String>();
		/*
		 * open session
		 */
		final DBSession session = openSession();
		final DatabaseException collectedException = new DatabaseException();
		try {
			for (final Post<?> post : posts) {
				try {
					hashes.add(this.updatePost(post, operation, session));
				} catch (DatabaseException dbex) {
					collectedException.addErrors(dbex);
				} catch (Exception ex){
					// some exception other than those covered in the DatabaseException was thrown					
					collectedException.addToErrorMessages(post.getResource().getIntraHash(), new UnspecifiedErrorMessage(ex));
				}
			}
		} finally {
			session.close();
		}
		if (collectedException.hasErrorMessages()) {
			throw collectedException;
		}
		return hashes;
	}


	/**
	 * Updates a post in the database.
	 */
	private <T extends Resource> String updatePost(final Post<T> post, final PostUpdateOperation operation, final DBSession session) {
		final CrudableContent<T, GenericParam> manager = getFittingDatabaseManager(post);
		final String oldIntraHash = post.getResource().getIntraHash();
		post.getResource().recalculateHashes();

		this.validateGroups(post, session);

		/*
		 * change group IDs to spam group IDs
		 */
		PostUtils.setGroupIds(post, this.loginUser);

		/*
		 * update post
		 */
		manager.updatePost(post, oldIntraHash, operation, session);

		// if we don't get an exception here, we assume the resource has
		// been successfully updated
		return post.getResource().getIntraHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#updateTags(org.bibsonomy.model
	 * .User, java.util.List, java.util.List) <p>TODO: possible options which
	 * one might want to add:</p> <ul> <li>ignore case</li> </ul>
	 */
	@Override
	public int updateTags(final User user, final List<Tag> tagsToReplace, final List<Tag> replacementTags) {
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(loginUser, user.getName());
		/*
		 * 
		 */
		final DBSession session = this.openSession();
		try {
			/*
			 * delegate to tagDBManager
			 */
			this.tagDBManager.updateTags(user, tagsToReplace, replacementTags, session);
		} finally {
			session.close();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#createUser(org.bibsonomy.model
	 * .User)
	 */
	@Override
	public String createUser(final User user) {
		/*
		 * We ensure, that the user is logged in and has admin privileges. This
		 * seems to be a contradiction, because if a user wants to register, he
		 * is not logged in.
		 * 
		 * The current solution to this paradox is, that registration is done
		 * using an instance of the DBLogic which contains a user with role
		 * "admin".
		 */
		this.ensureLoggedIn();
		this.permissionDBManager.ensureAdminAccess(loginUser);

		return this.storeUser(user, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#updateUser(org.bibsonomy.model
	 * .User)
	 */
	@Override
	public String updateUser(final User user, final UserUpdateOperation operation) {
		/*
		 * only logged in users can update user settings
		 */
		this.ensureLoggedIn();
		/*
		 * only admins can change settings of /other/ users
		 */
		this.permissionDBManager.ensureIsAdminOrSelf(loginUser, user.getName());

		if (UserUpdateOperation.UPDATE_ALL.equals(operation)) {
			/*
			 * update only (!) spammer settings
			 */
			if (user.getPrediction() != null || user.getSpammer() != null) {
				/*
				 * only admins are allowed to change spammer settings
				 */
				log.debug("Start update this framework");

				this.permissionDBManager.ensureAdminAccess(loginUser);
				/*
				 * open session and update spammer settings
				 */
				final DBSession session = this.openSession();
				try {
					final String mode = this.adminDBManager.getClassifierSettings(ClassifierSettings.TESTING, session);
					log.debug("User prediction: " + user.getPrediction());
					return this.adminDBManager.flagSpammer(user, this.getAuthenticatedUser().getName(), mode, session);
				} finally {
					session.close();
				}
			}
			return this.storeUser(user, true);
		}


		final DBSession session = openSession();

		try {
			switch (operation) {
			case UPDATE_PASSWORD:
				return this.userDBManager.updatePasswordForUser(user, session);

			case UPDATE_SETTINGS:
				return this.userDBManager.updateUserSettingsForUser(user, session);

			case UPDATE_API:
				this.userDBManager.updateApiKeyForUser(user.getName(), session);
				break;

			case UPDATE_CORE:
				return this.userDBManager.updateUserProfile(user, session);

				//					case UPDATE_LDAP_TIMESTAMP:
					//						updatedUser = this.userDBManager.updateLastLdapRequest(user, session);
				//						break;
			}

		} finally {
			session.close();
		}

		return null;
	}

	/**
	 * Adds/updates a user in the database.
	 */
	private String storeUser(final User user, final boolean update) {

		final DBSession session = openSession();
		String updatedUser = null;

		try {
			final User existingUser = userDBManager.getUserDetails(user.getName(), session);
			if (update) {
				/*
				 * update the user
				 */
				if (!ValidationUtils.present(existingUser.getName())) {
					/*
					 * error: user name does not exist
					 */
					final String errorMsg = "user " + user.getName() + " does not exist";
					log.warn(errorMsg);
					throw new ValidationException(errorMsg);
				}
				updatedUser = this.userDBManager.changeUser(user, session);
			} else {
				/*
				 * create a new user
				 */
				if (ValidationUtils.present(existingUser.getName())) {
					/*
					 * error: user name already exists
					 */
					final String errorMsg = "user " + user.getName() + " already exists";
					log.warn(errorMsg);
					throw new ValidationException(errorMsg);
				}
				updatedUser = this.userDBManager.createUser(user, session);
			}
		} finally {
			/*
			 * TODO: check, if rollback is handled correctly!
			 */
			session.close();
		}

		/*
		 * TODO: return correct value
		 */
		return updatedUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#getAuthenticatedUser()
	 */
	@Override
	public User getAuthenticatedUser() {
		return this.loginUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getAuthors(org.bibsonomy.common
	 * .enums.GroupingEntity, java.lang.String, java.util.List,
	 * java.lang.String, org.bibsonomy.model.enums.Order,
	 * org.bibsonomy.common.enums.FilterEntity, int, int, java.lang.String)
	 */
	@Override
	public List<Author> getAuthors(GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
		/*
		 * FIXME: implement a chain or something similar
		 */
		if (GroupingEntity.ALL.equals(grouping)) {
			final DBSession session = openSession();

			try {
				return this.authorDBManager.getAuthors(session);
			} finally {
				session.close();
			}
		}
		throw new UnsupportedOperationException("Currently only ALL authors can be listed.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#addDocument(org.bibsonomy.model
	 * .Document, java.lang.String)
	 */
	@Override
	public String createDocument(final Document document, final String resourceHash) {
		final String userName = document.getUserName();
		this.ensureLoggedIn();

		/*
		 * users can only modify their own documents
		 */
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		final DBSession session = openSession();
		try {
			if (resourceHash != null) {
				/*
				 * document shall be attached to a post
				 */
				final Post<BibTex> post = bibtexDBManager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				if (present(post)) {
					/*
					 * post really exists!
					 */
					final boolean existingDoc = this.docDBManager.checkForExistingDocuments(userName, resourceHash, document.getFileName(), session);
					if (existingDoc) {
						/*
						 * the post has already a file with that name attached
						 * ...
						 */
						this.docDBManager.updateDocument(post.getContentId(), document.getFileHash(), document.getFileName(), document.getMd5hash(), session);

					} else {
						// add
						this.docDBManager.addDocument(userName, post.getContentId(), document.getFileHash(), document.getFileName(), document.getMd5hash(), session);
					}

				} else {
					throw new ValidationException("Could not find a post with hash '" + resourceHash + "'.");
				}

			} else {
				// checks whether a layout definition is already uploaded
				// if not the new one will be stored in the database
				if (this.docDBManager.getDocument(userName, document.getFileHash(), session) == null) {
					this.docDBManager.addDocument(userName, DocumentDatabaseManager.DEFAULT_CONTENT_ID, document.getFileHash(), document.getFileName(), document.getMd5hash(), session);
				}
			}
		} finally {
			session.close();
		}
		log.info("created new file " + document.getFileName() + " for user " + userName);
		return document.getFileHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getDocument(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Document getDocument(final String userName, final String fileHash) {

		Document document = null;

		final String lowerCaseUserName = userName.toLowerCase();
		this.ensureLoggedIn();

		this.permissionDBManager.ensureWriteAccess(this.loginUser, lowerCaseUserName);

		final DBSession session = openSession();

		try {
			document = docDBManager.getDocument(lowerCaseUserName, fileHash, session);
		} finally {
			session.close();
		}

		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getDocument(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Document getDocument(final String userName, final String resourceHash, final String fileName) {
		final String lowerCaseUserName = userName.toLowerCase();
		this.ensureLoggedIn();

		final DBSession session = openSession();
		try {
			if (resourceHash != null) {
				/*
				 * we just forward this task to getPostDetails from the
				 * BibTeXDatabaseManager and extract the documents.
				 */
				final Post<BibTex> post = this.bibtexDBManager.getPostDetails(this.loginUser.getName(), resourceHash, lowerCaseUserName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				if (post != null && post.getResource().getDocuments() != null) {
					/*
					 * post found and post contains documents (bibtexdbmanager
					 * checks, if user might access documents and only then
					 * inserts them)
					 */
					for (final Document document : post.getResource().getDocuments()) {
						if (document.getFileName().equals(fileName)) {
							return document;
						}
					}
				}
			} else {
				/*
				 * users can only access their own documents
				 */
				this.permissionDBManager.ensureWriteAccess(this.loginUser, lowerCaseUserName);
				/*
				 * TODO: implement access to non post-connected documents
				 */
			}
		} finally {
			session.close();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteDocument(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteDocument(final Document document, final String resourceHash) {
		final String userName = document.getUserName();
		this.ensureLoggedIn();
		/*
		 * users can only modify their own documents
		 */
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		final DBSession session = openSession();
		try {
			if (resourceHash != null) {
				/*
				 * the document belongs to a post --> check if the user owns the
				 * post
				 */
				final Post<BibTex> post = bibtexDBManager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				if (post != null) {
					/*
					 * the given resource hash belongs to a post of the user ->
					 * delete the corresponding document
					 */
					if (this.docDBManager.checkForExistingDocuments(userName, resourceHash, document.getFileName(), session)) {
						this.docDBManager.deleteDocument(post.getContentId(), userName, document.getFileName(), session);
					}
				} else {
					throw new ValidationException("Could not find a post with hash '" + resourceHash + "'.");
				}
			} else {
				/*
				 * the document does not belong to a post
				 */
				this.docDBManager.deleteDocumentWithNoPost(DocumentDatabaseManager.DEFAULT_CONTENT_ID, userName, document.getFileHash(), session);
			}
		} finally {
			session.close();
		}
		log.debug("deleted document " + document.getFileName() + " from user " + userName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#addInetAddressStatus(java.net
	 * .InetAddress, org.bibsonomy.common.enums.InetAddressStatus)
	 */
	@Override
	public void createInetAddressStatus(final InetAddress address, final InetAddressStatus status) {
		this.ensureLoggedIn();
		// only admins are allowed to change the status of an address
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			this.adminDBManager.addInetAddressStatus(address, status, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteInetAdressStatus(java.
	 * net.InetAddress)
	 */
	@Override
	public void deleteInetAdressStatus(final InetAddress address) {
		this.ensureLoggedIn();
		// only admins are allowed to change the status of an address
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			this.adminDBManager.deleteInetAdressStatus(address, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getInetAddressStatus(java.net
	 * .InetAddress)
	 */
	@Override
	public InetAddressStatus getInetAddressStatus(final InetAddress address) {
		// everybody is allowed to ask for the status of an address
		/*
		 * TODO: is this really OK? At least it is neccessary, because otherwise
		 * the RegistrationHandler can not check the status of an address.
		 */
		// this.ensureLoggedIn();
		// this.permissionDBManager.ensureAdminAccess(loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getInetAddressStatus(address, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#getPostStatistics(java.lang
	 * .Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String,
	 * java.util.List, java.lang.String, org.bibsonomy.model.enums.Order,
	 * org.bibsonomy.common.enums.FilterEntity, int, int, java.lang.String,
	 * org.bibsonomy.common.enums.StatisticsConstraint)
	 */
	@Override
	public int getPostStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search, StatisticsConstraint constraint) {
		final DBSession session = openSession();
		final Integer result;

		try {

			if (this.permissionDBManager.checkFilterPermissions(filter, this.loginUser)) {
				loginUser.addGroup(new Group(GroupID.PUBLIC_SPAM));
			}

			final StatisticsParam param = LogicInterfaceHelper.buildParam(StatisticsParam.class, grouping, groupingName, tags, hash, order, start, end, search, filter, this.loginUser);

			if (resourceType == BibTex.class || resourceType == Bookmark.class || resourceType == Resource.class) {
				param.setContentTypeByClass(resourceType);
				result = this.statisticsDBManager.getPostStatistics(param, session);
			} else {
				throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
			}
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return an empty list
			return 0;
		} finally {
			session.close();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getConcepts(java.lang.Class,
	 * org.bibsonomy.common.enums.GroupingEntity, java.lang.String,
	 * java.lang.String, java.util.List,
	 * org.bibsonomy.common.enums.ConceptStatus, int, int)
	 */
	@Override
	public List<Tag> getConcepts(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final String regex, final List<String> tags, final ConceptStatus status, final int start, final int end) {
		final DBSession session = openSession();
		try {
			final TagRelationParam param = LogicInterfaceHelper.buildParam(TagRelationParam.class, grouping, groupingName, tags, null, null, start, end, null, null, this.loginUser);
			param.setConceptStatus(status);
			return this.tagRelationsDBManager.getConcepts(param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getConceptDetails(java.lang.
	 * String, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public Tag getConceptDetails(final String conceptName, final GroupingEntity grouping, final String groupingName) {
		final DBSession session = openSession();
		final Tag concept;
		try {
			if (grouping.equals(GroupingEntity.USER) || grouping.equals(GroupingEntity.GROUP) && groupingName != null && groupingName != "") {
				concept = this.tagRelationsDBManager.getConceptForUser(conceptName, groupingName, session);
			} else if (grouping.equals(GroupingEntity.ALL)) {
				concept = this.tagRelationsDBManager.getGlobalConceptByName(conceptName, session);
			} else {
				throw new RuntimeException("Can't handle request");
			}
		} finally {
			session.close();
		}
		return concept;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#createConcept(org.bibsonomy.
	 * model.Tag, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public String createConcept(final Tag concept, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		return this.storeConcept(concept, grouping, groupingName, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteConcept(java.lang.String,
	 * org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public void deleteConcept(final String concept, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}

		final DBSession session = openSession();
		this.tagRelationsDBManager.deleteConcept(concept, groupingName, session);
		// FIXME: close session?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteRelation(java.lang.String,
	 * java.lang.String, org.bibsonomy.common.enums.GroupingEntity,
	 * java.lang.String)
	 */
	@Override
	public void deleteRelation(final String upper, final String lower, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}

		final DBSession session = openSession();
		this.tagRelationsDBManager.deleteRelation(upper, lower, groupingName, session);
		// FIXME: close session?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#updateConcept(org.bibsonomy.
	 * model.Tag, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public String updateConcept(final Tag concept, final GroupingEntity grouping, final String groupingName, final ConceptUpdateOperation operation) {
		if (!GroupingEntity.USER.equals(grouping))
			throw new UnsupportedOperationException("Currently only user's can have concepts.");

		this.permissionDBManager.ensureIsAdminOrSelf(loginUser, groupingName);

		final DBSession session = openSession();
		// now switch the operation and call the right method in the taglRelationsDBManager or DBLogic
		try {
			switch(operation){
			case UPDATE:		
				return this.storeConcept(concept, grouping, groupingName, true);
			case PICK:
				this.tagRelationsDBManager.pickConcept(concept, groupingName, session);
				break;
			case UNPICK:
				this.tagRelationsDBManager.unpickConcept(concept, groupingName, session);
				break;
			case UNPICK_ALL:
				this.tagRelationsDBManager.unpickAllConcepts(groupingName, session);
				return null;
			case PICK_ALL:
				this.tagRelationsDBManager.pickAllConcepts(groupingName, session);
				return null;
			}

			return concept.getName();
		} finally {
			session.close();
		}

	}

	/**
	 * Helper metod to store a concept
	 * 
	 * @param concept
	 * @param grouping
	 * @param groupingName
	 * @param update
	 * @return
	 */
	private String storeConcept(final Tag concept, final GroupingEntity grouping, final String groupingName, final boolean update) {
		final DBSession session = openSession();
		if (update) {
			this.tagRelationsDBManager.insertRelations(concept, groupingName, session);
		} else {
			this.deleteConcept(concept.getName(), grouping, groupingName);
			this.tagRelationsDBManager.insertRelations(concept, groupingName, session);
		}
		return concept.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#getUsers(java.lang.Class,
	 * org.bibsonomy.common.enums.GroupingEntity, java.lang.String,
	 * java.util.List, java.lang.String, org.bibsonomy.model.enums.Order,
	 * org.bibsonomy.common.enums.UserRelation, java.lang.String, int, int)
	 */
	@Override
	public List<User> getUsers(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, final List<String> tags, String hash, final Order order, UserRelation relation, String search, final int start, final int end) {
		// assemle param object
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, grouping, groupingName, tags, hash, order, start, end, search, null, loginUser);
		param.setUserRelation(relation);

		final DBSession session = openSession();
		try {
			// start chain
			return this.userDBManager.getUsers(param, session);
		} finally {
			session.close();
		}

		// try {
		// if (tags.size() == 1 && tags.get(0).startsWith("sys:user:")) {
		// // TODO: proper system tag handling
		// final String tag = tags.get(0);
		// final String username = tag.substring(tag.lastIndexOf(":") + 1,
		// tag.length());
		// System.out.println("requested user " + username);
		// return this.userDBManager.getUsersByUserAndFolkrank(username,
		// this.loginUser.getName(), end, session);
		// }
		// return this.userDBManager.getUserByFolkrank(param, session);
		// } finally {
		// session.close();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getClassifiedUsers(org.bibsonomy
	 * .common.enums.Classifier, org.bibsonomy.common.enums.SpamStatus, int)
	 */
	@Override
	public List<User> getClassifiedUsers(final Classifier classifier, final SpamStatus status, final int limit) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifiedUsers(classifier, status, limit, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getClassifierSettings(org.bibsonomy
	 * .common.enums.ClassifierSettings)
	 */
	@Override
	public String getClassifierSettings(final ClassifierSettings key) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifierSettings(key, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#updateClassifierSettings(org
	 * .bibsonomy.common.enums.ClassifierSettings, java.lang.String)
	 */
	@Override
	public void updateClassifierSettings(final ClassifierSettings key, final String value) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			this.adminDBManager.updateClassifierSettings(key, value, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getClassifiedUserCount(org.bibsonomy
	 * .common.enums.Classifier, org.bibsonomy.common.enums.SpamStatus, int)
	 */
	@Override
	public int getClassifiedUserCount(final Classifier classifier, final SpamStatus status, final int interval) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifiedUserCount(classifier, status, interval, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getClassifierHistory(java.lang
	 * .String)
	 */
	@Override
	public List<User> getClassifierHistory(final String userName) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifierHistory(userName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getClassifierComparison(int)
	 */
	@Override
	public List<User> getClassifierComparison(final int interval) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifierComparison(interval, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getOpenIDUser(java.lang.String)
	 */
	@Override
	public String getOpenIDUser(String openID) {
		final DBSession session = openSession();
		try {
			final String username = this.userDBManager.getOpenIDUser(openID, session);
			return username;
		} finally {
			session.close();
		}
	}

	/**
	 * updates date when ldap user was requested for authentication
	 * 
	 * @param loginName
	 */
	public void updateLastLdapRequest (final User user) {
		final DBSession session = openSession();
		try {
			if (null != user.getName()) {
				this.userDBManager.updateLastLdapRequest(user, session);
			}
		} finally {
			session.close();
		}		
	}	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getTagStatistics(java.lang.Class
	 * , org.bibsonomy.common.enums.GroupingEntity, java.lang.String,
	 * java.lang.String, java.util.List,
	 * org.bibsonomy.common.enums.ConceptStatus, int, int)
	 */
	@Override
	public int getTagStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		Integer result;

		final DBSession session = openSession();
		try {
			final StatisticsParam param = LogicInterfaceHelper.buildParam(StatisticsParam.class, grouping, groupingName, tags, null, null, start, end, null, null, this.loginUser);

			result = this.statisticsDBManager.getTagStatistics(param, session);
		} finally {
			session.close();
		}

		return result;
	}

	/*
	 * We create a UserRelation of the form (sourceUser, targetUser)\in relation
	 * This Method only works for the FOLLOWER_OF and the OF_FRIEND relation
	 * Other relation will result in an UnsupportedRelationException
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#insertUserRelationship()
	 */
	@Override
	public void createUserRelationship(final String sourceUser, final String targetUser, final UserRelation relation) {
		this.ensureLoggedIn();
		// this.permissionDBManager.checkUserRelationship(sourceUser,
		// targetUser, relation);
		this.permissionDBManager.ensureIsAdminOrSelf(loginUser, sourceUser);

		final DBSession session = openSession();
		try {
			this.userDBManager.createUserRelation(sourceUser, targetUser, relation, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUserRelationship(java.lang.String, org.bibsonomy.common.enums.UserRelation)
	 */
	public List<User> getUserRelationship(String sourceUser, UserRelation relation) {
		this.ensureLoggedIn();
		List<User> targetUsers;
		// ask Robert about this method
		// this.permissionDBManager.checkUserRelationship(sourceUser,
		// targetUser, relation);
		this.permissionDBManager.ensureIsAdminOrSelf(loginUser, sourceUser);

		final DBSession session = openSession();
		try {
			// get all users that are in relation with sourceUser
			targetUsers = this.userDBManager.getUserRelation(sourceUser, relation, session);
		} finally {
			// unsupported Relations will cause an UnsupportedRelationException
			session.close();
		}
		return targetUsers;
	}

	/*
	 * We delete a UserRelation of the form (sourceUser, targetUser)\in relation
	 * This Method only works for the FOLLOWER_OF and the OF_FRIEND relation
	 * Other relation will result in an UnsupportedRelationException FIXME: use
	 * Strings (usernames) instead of users
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteUserRelationship()
	 */
	@Override
	public void deleteUserRelationship(final String sourceUser, final String targetUser, final UserRelation relation) {
		this.ensureLoggedIn();
		// ask Robert about this method
		// this.permissionDBManager.checkUserRelationship(sourceUser,
		// targetUser, relation);
		this.permissionDBManager.ensureIsAdminOrSelf(loginUser, sourceUser);

		final DBSession session = openSession();
		try {
			this.userDBManager.deleteUserRelation(sourceUser, targetUser, relation, session);
		} finally {
			// unsupported Relations will cause an UnsupportedRelationException
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#createBasketItems()
	 */
	@Override
	public int createBasketItems(List<Post<? extends Resource>> posts) {
		this.ensureLoggedIn();

		final DBSession session = openSession();

		int basketSize = 0;

		try {
			for (final Post<? extends Resource> post : posts) {
				if (post.getResource() instanceof Bookmark) throw new UnsupportedResourceTypeException("Bookmarks can't be stored in the basket");
				/*
				 * get the complete post from the database
				 */
				final Post<BibTex> copy = this.bibtexDBManager.getPostDetails(this.loginUser.getName(), post.getResource().getIntraHash(), post.getUser().getName(), UserUtils.getListOfGroupIDs(this.loginUser), session);

				/*
				 * post might be null, because a) it does not exist b) user may
				 * not access it
				 */
				if (copy == null) {
					/*
					 * FIXME: proper exception message!
					 */
					throw new ValidationException("You are not authorized to perform the requested operation");
				}

				/*
				 * insert the post from the user's basket
				 */
				this.basketDBManager.createItem(this.loginUser.getName(), copy.getContentId(), session);
			}

			// get actual basket size
			return this.basketDBManager.getNumBasketEntries(this.loginUser.getName(), session);
		} catch (Exception ex) {
			log.error(ex);
		} finally {
			session.close();
		}
		return basketSize;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteBasketItems()
	 */
	@Override
	public int deleteBasketItems(List<Post<? extends Resource>> posts, final boolean clearBasket) {
		this.ensureLoggedIn();

		final DBSession session = openSession();

		try {
			// decide which delete function will be called
			if (clearBasket) {
				// clear all in basket
				this.basketDBManager.deleteAllItems(this.loginUser.getName(), session);
			} else {
				// delete specific post
				for (final Post<? extends Resource> post : posts) {
					if (post.getResource() instanceof Bookmark) throw new UnsupportedResourceTypeException("Bookmarks can't be stored in the basket");
					/*
					 * get the complete post from the database
					 */
					final Post<BibTex> copy = this.bibtexDBManager.getPostDetails(this.loginUser.getName(), post.getResource().getIntraHash(), post.getUser().getName(), UserUtils.getListOfGroupIDs(this.loginUser), session);

					/*
					 * post might be null, because a) it does not exist b) user
					 * may not access it
					 */
					if (copy == null) {
						/*
						 * FIXME: proper exception message!
						 */
						throw new ValidationException("You are not authorized to perform the requested operation");
					}

					/*
					 * delete the post from the user's basket
					 */
					this.basketDBManager.deleteItem(this.loginUser.getName(), copy.getContentId(), session);
				}
			}

			// get actual basketsize
			return this.basketDBManager.getNumBasketEntries(this.loginUser.getName(), session);
		} catch (Exception ex) {
			log.error(ex);
		} finally {
			session.close();
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteInboxMessages(java.util.List, boolean)
	 */
	public int deleteInboxMessages(final List<Post<? extends Resource>> posts, final boolean clearInbox) {
		/*
		 * check permissions
		 */
		this.ensureLoggedIn();
		/*
		 * delete one message from the inbox
		 */
		final DBSession session = openSession();
		try {
			if (clearInbox) {
				this.inboxDBManager.deleteAllInboxMessages(loginUser.getName(), session);
			} else {
				for (final Post post : posts) {
					final String sender = post.getUser().getName();
					final String receiver = loginUser.getName();
					final String resourceHash = post.getResource().getIntraHash();
					if (!present(receiver) || !present(resourceHash)) {
						throw new ValidationException("You are not authorized to perform the requested operation");
					}
					this.inboxDBManager.deleteInboxMessage(sender, receiver, resourceHash, session);
				}
			}
			return this.inboxDBManager.getNumInboxMessages(loginUser.getName(), session);
		} finally {
			session.close();
		}
	}

	/*
	 * FIXME: implement this method as chain element of getUsers()
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#getUsernameByLdapUserId()
	 */
	@Override	
	public String getUsernameByLdapUserId(final String userId) {
		final DBSession session = openSession();
		try {
			return this.userDBManager.getUsernameByLdapUser(userId, session);
		} finally {
			session.close();
		}
	}


}