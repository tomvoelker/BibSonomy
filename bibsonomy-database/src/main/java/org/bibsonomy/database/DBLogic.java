package org.bibsonomy.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.QueryTimeoutException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.database.managers.AuthorDatabaseManager;
import org.bibsonomy.database.managers.BasketDatabaseManager;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.CrudableContent;
import org.bibsonomy.database.managers.DocumentDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.StatisticsDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.TagRelationDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.SystemTagFactory;
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

	private static final Logger log = Logger.getLogger(DBLogic.class);

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
			return this.userDBManager.getUserFriends(loginUser.getName(), session);
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
			return this.userDBManager.getFriendsOfUser(loginUser.getName(), session);
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
		final DBSession session = openSession();
		try {
			/*
			 * We don't use userName but user.getName() in the remaining part of
			 * this method, since the name gets normalized in getUserDetails().
			 */
			final User user = this.userDBManager.getUserDetails(userName, session);
			/*
			 * add/remove some details
			 */
			if (this.permissionDBManager.isAdminOrSelf(loginUser, user.getName())) {
				/*
				 * only admin any myself may see which group I'm a member of
				 */
				user.setGroups(this.groupDBManager.getGroupsForUser(user.getName(), true, session));
				// fill user's spam informations
				this.adminDBManager.getClassifierUserDetails(user, session);
			} else {
				/*
				 * only the user himself or the admin gets the full details
				 */
				user.setEmail(null);
				user.setRealname(null);
				user.setHomepage(null);
				user.setPassword(null);
				user.setReminderPassword(null);
				user.setReminderPasswordRequestDate(null);
				user.setApiKey(null);
				// set neutral spam information
				user.setToClassify(0);
				user.setSpammer(false);
				/*
				 * FIXME: the settings and other things set in
				 * userDBManager.getUserDetails() are not cleared!
				 */
			}
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
				final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, filter, this.loginUser);
				// check permissions for displaying links to documents
				param.setDocumentsAttached(this.permissionDBManager.isAllowedToAccessUsersOrGroupDocuments(this.loginUser, grouping, groupingName, filter, session));

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

				final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, filter, this.loginUser);

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
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#getPostDetails(java.lang
	 * .String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Post<? extends Resource> getPostDetails(final String resourceHash, final String userName) {
		final DBSession session = openSession();
		try {
			for (final CrudableContent<? extends Resource, ? extends GenericParam> manager : this.allDatabaseManagers.values()) {
				final Post<? extends Resource> post = manager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				if (post != null) {
					/*
					 * add group information
					 */
					/*
					 * If one of the post's groups is neither public nor private
					 * (i.e., it is friends or a "regular" group) we must get
					 * the (remaining) groups from the grouptas table.
					 */
					final Group group = post.getGroups().iterator().next();
					if (!GroupUtils.isExclusiveGroup(group)) {
						/*
						 * neither public nor private ... ... get the groups
						 * from the grouptas table
						 */
						post.setGroups(new HashSet(groupDBManager.getGroupsForContentId(post.getContentId(), session)));
					}
					return post;
				}
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
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, null, this.loginUser);
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
		return result;
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
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, this.loginUser.getName(), null, this.loginUser.getName(), Arrays.asList(tagName), null, null, 0, 1, null, null, this.loginUser);
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
	public boolean validateSoftwareKey(@SuppressWarnings("unused") final String softwareKey) {
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
		final String[] tables = { "bookmark", "bibtex", "tas", "search_bibtex", "search_bookmark" };

		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		final User deleteUserParam = new User();

		deleteUserParam.setName(userName);

		final DBSession session = openSession();
		try {
			userDBManager.deleteUser(deleteUserParam, session);
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
	 * Adds/updates a post in the database.
	 */
	private <T extends Resource> String storePost(final Post<T> post, final boolean update) {
		final DBSession session = openSession();
		try {
			final CrudableContent<T, GenericParam> man = getFittingDatabaseManager(post);
			final String oldIntraHash = post.getResource().getIntraHash();
			post.getResource().recalculateHashes();
			this.validateGroups(post, session);

			
			/*
			 * change group IDs to spam group IDs
			 */
			PostUtils.setGroupIds(post, this.loginUser);

			SystemTag stt;
			for (Tag tag : post.getTags()) {
				stt = SystemTagFactory.createExecutableTag(this, dbSessionFactory, tag);
				if (stt != null) {
					stt.performBefore(post, session);
				}
			}

			man.storePost(this.loginUser.getName(), post, oldIntraHash, update, session);

			for (Tag tag : post.getTags()) {
				stt = SystemTagFactory.createExecutableTag(this, dbSessionFactory, tag);
				if (stt != null) {
					stt.performAfter(post, session);
				}
			}

			// if we don't get an exception here, we assume the resource has
			// been successfully stored
			return post.getResource().getIntraHash();
		} finally {
			session.close();
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

	/**
	 * Adds/updates a group in the database.
	 */
	private String storeGroup(@SuppressWarnings("unused") final Group group, @SuppressWarnings("unused") boolean update) {

		throw new UnsupportedOperationException("not yet available");

		// FIXME: unsure who may change a group -> better doing nothing
		// final DBSession session = this.openSession();
		// try {
		// this.groupDBManager.storeGroup(group, update, session);
		// } finally {
		// session.close();
		// }
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
		return this.storeGroup(group, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#updateGroup(org.bibsonomy.model
	 * .Group)
	 */
	@Override
	public String updateGroup(final Group group) {
		this.ensureLoggedIn();
		return this.storeGroup(group, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#createPosts(java.util.List)
	 */
	@Override
	public List<String> createPosts(final List<Post<?>> posts) {
		this.ensureLoggedIn();
		/*
		 * check permissions
		 */
		for (final Post<?> post : posts) {
			this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
		}
		/*
		 * insert posts TODO: more efficient implementation (transactions,
		 * deadlock handling, asynchronous, etc.)
		 */
		final List<String> hashes = new LinkedList<String>();
		for (final Post<?> post : posts) {
			hashes.add(this.storePost(post, false));
		}
		return hashes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#updatePosts(java.util.List,
	 * org.bibsonomy.common.enums.PostUpdateOperation)
	 */
	@Override
	public List<String> updatePosts(List<Post<?>> posts, PostUpdateOperation operation) {
		System.out.println("called");
		this.ensureLoggedIn();
		/*
		 * check permissions
		 */
		for (Post<?> post : posts) {
			this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
		}
		/*
		 * update posts FIXME: implement properly (see createPosts)
		 */
		final List<String> hashes = new LinkedList<String>();
		for (Post<?> post : posts) {
			hashes.add(this.storePost(post, true));
		}
		return hashes;
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
	public String updateUser(final User user) {
		/*
		 * only logged in users can update user settings
		 */
		this.ensureLoggedIn();
		/*
		 * only admins can change settings of /other/ users
		 */
		if (!loginUser.getName().equals(user.getName())) {
			this.permissionDBManager.ensureAdminAccess(loginUser);
		}

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
			DBSession session = this.openSession();
			String flagSpammerUserName = null;
			try {
				final String mode = this.adminDBManager.getClassifierSettings(ClassifierSettings.TESTING, session);
				flagSpammerUserName = this.adminDBManager.flagSpammer(user, this.getAuthenticatedUser().getName(), mode, session);
			} finally {
				session.close();
			}
			return flagSpammerUserName;
		}

		return this.storeUser(user, true);
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
				if (post != null) {
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

			final StatisticsParam param = LogicInterfaceHelper.buildParam(StatisticsParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, filter, this.loginUser);

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
			final TagRelationParam param = LogicInterfaceHelper.buildParam(TagRelationParam.class, this.loginUser.getName(), grouping, groupingName, tags, null, null, start, end, null, null, this.loginUser);
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
	public String updateConcept(final Tag concept, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		return this.storeConcept(concept, grouping, groupingName, true);
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
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, null, loginUser);
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
			final StatisticsParam param = LogicInterfaceHelper.buildParam(StatisticsParam.class, this.loginUser.getName(), grouping, groupingName, tags, null, null, start, end, null, null, this.loginUser);

			result = this.statisticsDBManager.getTagStatistics(param, session);
		} finally {
			session.close();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteUserRelationship()
	 */
	@Override
	public void deleteUserRelationship(User loggedInUser, User requestedUser, UserRelation relation) {
		this.ensureLoggedIn();

		final DBSession session = openSession();
		try {
			UserParam param = new UserParam();
			param.setUserName(loggedInUser.getName());
			param.setRequestedUserName(requestedUser.getName());

			if (UserRelation.FOLLOWER_OF.equals(relation)) {
				if (this.userDBManager.isFollowerOfUser(loggedInUser, requestedUser, session)) {
					this.userDBManager.deleteFollowerOfUser(param, session);
				}
			}
			if (UserRelation.FRIEND_OF.equals(relation)) {
				this.userDBManager.deleteFriendOfUser(param, session);
			}
		} finally {
			session.close();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#insertUserRelationship()
	 */
	@Override
	public void createUserRelationship(User loggedInUser, User requestedUser, UserRelation relation) {
		this.ensureLoggedIn();
		this.permissionDBManager.checkUserRelationship(loggedInUser, requestedUser, relation);

		final DBSession session = openSession();
		try {
			UserParam param = new UserParam();
			param.setUserName(loggedInUser.getName());
			param.setRequestedUserName(requestedUser.getName());

			if (UserRelation.FOLLOWER_OF.equals(relation)) {
				if (this.userDBManager.isFollowerOfUser(loggedInUser, requestedUser, session) == false) {
					this.userDBManager.addFollowerOfUser(param, session);
				}
			}
		} finally {
			session.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createBasketItems()
	 */
	@Override
	public int createBasketItems(List<Post<BibTex>> posts){
		this.ensureLoggedIn();
		
		final DBSession session = openSession();
		
		int basketSize = 0;
		
		try {
			for (Post<BibTex> p:posts){
				// its still not necessary to use crudable 
				p = this.bibtexDBManager.getPostDetails(this.loginUser.getName(), p.getResource().getIntraHash(), p.getUser().getName(), UserUtils.getListOfGroupIDs(this.loginUser), session);
	
				// create param
				BasketParam param = new BasketParam();
				param.setUserName(this.loginUser.getName());
				param.setContentId(p.getContentId());
				param.setRequestedUserName(p.getUser().getName());
				
				// and write it to the database
				this.basketDBManager.createItem(param, session);
				
			}
			
			// get actual basket size
			basketSize = this.basketDBManager.getNumBasketEntries(this.loginUser.getName(), session);
		} catch (Exception ex) {
			log.error(ex);
		} finally {
			session.close();
		}
		return basketSize;

	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteBasketItems()
	 */
	@Override
	public int deleteBasketItems(List<Post<BibTex>> posts, final boolean clearBasket) {
		this.ensureLoggedIn();
		
		final DBSession session = openSession();
		
		int basketSize = 0;
		
		try {
			// decide which delete function will be called
			if (clearBasket){
				// clear all in basket
				this.basketDBManager.deleteAllItems(this.loginUser.getName(), session);
			} else {
				// delete specific post
				for (Post<BibTex> p:posts){
					p = this.bibtexDBManager.getPostDetails(this.loginUser.getName(), p.getResource().getIntraHash(), p.getUser().getName(), UserUtils.getListOfGroupIDs(this.loginUser), session);
		
					// create param
					BasketParam param = new BasketParam();			
					param.setUserName(this.loginUser.getName());
					param.setContentId(p.getContentId());
					param.setRequestedUserName(p.getUser().getName());
					param.setHash(p.getResource().getIntraHash());
					
					// delete it
					this.basketDBManager.deleteItem(param, session);
				}	
			}
			
			// get actual basketsize
			basketSize = this.basketDBManager.getNumBasketEntries(this.loginUser.getName(), session);
		} catch (Exception ex) {
			log.error(ex);
		} finally {
			session.close();
		}
		
		return basketSize;

	}
}