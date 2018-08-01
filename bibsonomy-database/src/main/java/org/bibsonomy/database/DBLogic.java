/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.common.enums.PostAccess;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.SyncSettingsUpdateOperation;
import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.errors.UnspecifiedErrorMessage;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.QueryTimeoutException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.database.managers.AuthorDatabaseManager;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BibTexExtraDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.ClipboardDatabaseManager;
import org.bibsonomy.database.managers.CrudableContent;
import org.bibsonomy.database.managers.DocumentDatabaseManager;
import org.bibsonomy.database.managers.GoldStandardBookmarkDatabaseManager;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.PostDatabaseManager;
import org.bibsonomy.database.managers.ProjectDatabaseManager;
import org.bibsonomy.database.managers.StatisticsDatabaseManager;
import org.bibsonomy.database.managers.StatisticsProvider;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.TagRelationDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.managers.WikiDatabaseManager;
import org.bibsonomy.database.managers.discussion.CommentDatabaseManager;
import org.bibsonomy.database.managers.discussion.DiscussionDatabaseManager;
import org.bibsonomy.database.managers.discussion.DiscussionItemDatabaseManager;
import org.bibsonomy.database.managers.discussion.ReviewDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.systemstags.SystemTagsExtractor;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.database.systemstags.search.SearchSystemTag;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Comment;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.Review;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.logic.GoldStandardPostLogicInterface;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.query.Query;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PublicationSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.metadata.PostMetaData;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PostUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.sync.SynchronizationDatabaseManager;
import org.bibsonomy.util.ExceptionUtils;
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
 */
public class DBLogic implements LogicInterface {
	private static final Log log = LogFactory.getLog(DBLogic.class);
	/*
	 * help maps for post managers, statistics and discussion managers
	 */
	private final Map<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> allDatabaseManagers = new HashMap<>();
	private final Map<Class<? extends DiscussionItem>, DiscussionItemDatabaseManager<? extends DiscussionItem>> allDiscussionManagers = new HashMap<>();

	private final Map<Class<? extends Query>, StatisticsProvider<? extends Query>> allStatisticDatabaseMangers = new HashMap<>();

	private final AuthorDatabaseManager authorDBManager;
	private final DocumentDatabaseManager docDBManager;
	private final PermissionDatabaseManager permissionDBManager;

	private final PostDatabaseManager<Bookmark, BookmarkParam> bookmarkDBManager;
	private final BibTexDatabaseManager publicationDBManager;
	private final GoldStandardPublicationDatabaseManager goldStandardPublicationDBManager;
	private final GoldStandardBookmarkDatabaseManager goldStandardBookmarkDBManager;
	private final BibTexExtraDatabaseManager bibTexExtraDBManager;

	private final DiscussionDatabaseManager discussionDatabaseManager;
	private final ReviewDatabaseManager reviewDBManager;
	private final CommentDatabaseManager commentDBManager;

	private final UserDatabaseManager userDBManager;
	private final GroupDatabaseManager groupDBManager;
	private final PersonDatabaseManager personDBManager;
	private final TagDatabaseManager tagDBManager;
	private final AdminDatabaseManager adminDBManager;

	private final StatisticsDatabaseManager statisticsDBManager;
	private final TagRelationDatabaseManager tagRelationsDBManager;
	private final ClipboardDatabaseManager clipboardDBManager;
	private final InboxDatabaseManager inboxDBManager;
	private final WikiDatabaseManager wikiDBManager;

	private ProjectDatabaseManager projectDatabaseManager;

	private final SynchronizationDatabaseManager syncDBManager;

	private DBSessionFactory dbSessionFactory;
	private BibTexReader publicationReader;
	private User loginUser;

	/**
	 * Returns an implementation of the DBLogic.
	 */
	protected DBLogic() {
		// publication db manager
		this.publicationDBManager = BibTexDatabaseManager.getInstance();

		// bookmark db manager
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();

		// gold standard publication db manager
		this.goldStandardPublicationDBManager = GoldStandardPublicationDatabaseManager.getInstance();
		this.goldStandardBookmarkDBManager = GoldStandardBookmarkDatabaseManager.getInstance();

		// discussion and discussion item db manager
		this.commentDBManager = CommentDatabaseManager.getInstance();
		this.reviewDBManager = ReviewDatabaseManager.getInstance();
		this.discussionDatabaseManager = DiscussionDatabaseManager.getInstance();
		this.authorDBManager = AuthorDatabaseManager.getInstance();
		this.docDBManager = DocumentDatabaseManager.getInstance();
		this.userDBManager = UserDatabaseManager.getInstance();
		this.groupDBManager = GroupDatabaseManager.getInstance();
		this.tagDBManager = TagDatabaseManager.getInstance();
		this.adminDBManager = AdminDatabaseManager.getInstance();
		this.permissionDBManager = PermissionDatabaseManager.getInstance();
		this.statisticsDBManager = StatisticsDatabaseManager.getInstance();
		this.tagRelationsDBManager = TagRelationDatabaseManager.getInstance();
		this.personDBManager = PersonDatabaseManager.getInstance();

		this.clipboardDBManager = ClipboardDatabaseManager.getInstance();
		this.inboxDBManager = InboxDatabaseManager.getInstance();

		this.wikiDBManager = WikiDatabaseManager.getInstance();

		this.syncDBManager = SynchronizationDatabaseManager.getInstance();

		this.bibTexExtraDBManager = BibTexExtraDatabaseManager.getInstance();
	}

	protected void initializeMaps() {
		// register the statistic database managers
		this.allStatisticDatabaseMangers.put(ProjectQuery.class, this.projectDatabaseManager);

		this.allDiscussionManagers.put(Comment.class, this.commentDBManager);
		this.allDiscussionManagers.put(Review.class, this.reviewDBManager);

		this.allDatabaseManagers.put(BibTex.class, this.publicationDBManager);
		this.allDatabaseManagers.put(Bookmark.class, this.bookmarkDBManager);
		this.allDatabaseManagers.put(GoldStandardPublication.class, this.goldStandardPublicationDBManager);
		this.allDatabaseManagers.put(GoldStandardBookmark.class, this.goldStandardBookmarkDBManager);
	}


	/**
	 * Returns a new database session. If a user is logged in, he gets the
	 * master connection, if not logged in, the secondary connection
	 */
	private DBSession openSession() {
		return this.dbSessionFactory.getDatabaseSession();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getUserDetails(java.lang.String)
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
			 * group admins may see the details of the group's dummy user (in
			 * that case, the group's name is user.getName()
			 */
			if (this.permissionDBManager.isAdminOrSelf(this.loginUser, user.getName())
					|| this.permissionDBManager.isAdminOrHasGroupRoleOrHigher(this.loginUser, user.getName(), GroupRole.ADMINISTRATOR)) {
				user.setGroups(this.groupDBManager.getGroupsForUser(user.getName(), true, session));
				user.setPendingGroups(this.groupDBManager.getPendingMembershipsForUser(userName, session));
				// inject the reported spammers.
				final List<User> reportedSpammersList = this.userDBManager.getUserRelation(user.getName(), UserRelation.SPAMMER, NetworkRelationSystemTag.BibSonomySpammerSystemTag, session);
				user.setReportedSpammers(new HashSet<User>(reportedSpammersList));
				// fill user's spam informations
				this.adminDBManager.getClassifierUserDetails(user, session);
				return user;
			}

			/*
			 * return a complete empty user, in case of a deleted user
			 */
			if (user.getRole() == Role.DELETED) {
				return new User();
			}

			/*
			 * respect user privacy settings
			 * clear all profile attributes if current login user isn't allowed
			 * to see the profile
			 */
			if (!this.permissionDBManager.isAllowedToAccessUsersProfile(user, this.loginUser, session)) {
				/*
				 * TODO: this practically clears /all/ user information
				 */
				/*
				 * FIXME: This is necessary to avoid null pointer Exceptions
				 * when the user's picture is not visible.
				 * The fileLogic should do this instead by setting the default
				 * pic in such cases.
				 */
				final User dummyUser = this.userDBManager.createEmptyUser();
				dummyUser.setName(user.getName());

				return dummyUser;
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
			 * FIXME: other things set in userDBManager.getUserDetails() maybe
			 * not cleared!
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
	 * org.bibsonomy.model.sync.SyncLogicInterface#getSynchronization(java.lang
	 * .String, java.lang.Class, java.util.List,
	 * org.bibsonomy.model.sync.ConflictResolutionStrategy, java.lang.String)
	 */
	@Override
	public List<SynchronizationPost> getSyncPlan(final String userName, final URI service, final Class<? extends Resource> resourceType, final List<SynchronizationPost> clientPosts, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		// handle resourceType = null
		if (!present(resourceType)) {
			throw new IllegalArgumentException("no resourceType was given - abort getSyncPlan()");
		}

		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		if (!present(strategy)) {
			log.error("no conflict resolution strategy received in getSyncPlan method! Use LAST WINS");
		}

		Date lastSuccessfulSyncDate = null;

		final List<SynchronizationPost> posts;

		final DBSession session = this.openSession();
		try {
			final SynchronizationData data = this.syncDBManager.getLastSyncData(userName, service, resourceType, null, session);
			
			/*
			 * check for a running synchronization
			 */
			if (present(data) && SynchronizationStatus.RUNNING.equals(data.getStatus())) {
				// running synchronization
				// FIXME: if synchronization fails, we can't recover
				throw new SynchronizationRunningException();
			}
			/*
			 * check for last successful synchronization
			 */
			final SynchronizationData lsd = this.syncDBManager.getLastSyncData(userName, service, resourceType, SynchronizationStatus.DONE, session);
			if (present(lsd)) {
				lastSuccessfulSyncDate = lsd.getLastSyncDate();
			} else if (!SynchronizationDirection.BOTH.equals(direction)) {
				// be sure that both systems are in sync before only syncing only in one direction
				throw new IllegalStateException("sync request rejected! The client hasn't performed an initial sync in both directions!");
			}
			/*
			 * flag synchronization as planned
			 * FIXME: if the client is not in the sync_services table, this
			 * statements silently fails. :-(
			 */
			log.debug("try to set syncdata as planned");

			final SyncService syncService = this.syncDBManager.getSyncServiceDetails(service, session);
			if (present(syncService)) {
				this.syncDBManager.insertSynchronizationData(userName, service, resourceType, new Date(), SynchronizationStatus.PLANNED, session);
			} else {
				log.error("no SyncService found with URI: " + service.toString());
				throw new IllegalArgumentException("no SyncService found with URI: " + service.toString());
			}

			/*
			 * get posts from server (=this machine)
			 */
			final Map<String, SynchronizationPost> serverPosts;
			if (BibTex.class.equals(resourceType)) {
				serverPosts = this.publicationDBManager.getSyncPostsMapForUser(userName, session);
			} else if (Bookmark.class.equals(resourceType)) {
				serverPosts = this.bookmarkDBManager.getSyncPostsMapForUser(userName, session);
			} else {
				throw new UnsupportedResourceTypeException();
			}

			/*
			 * if necessary, set the synchronization date to some distant old
			 * value
			 */
			if (!present(lastSuccessfulSyncDate)) {
				lastSuccessfulSyncDate = new Date(0);
			}
			/*
			 * calculate synchronization plan
			 */
			posts = this.syncDBManager.getSyncPlan(serverPosts, clientPosts, lastSuccessfulSyncDate, strategy, direction);

			/*
			 * attach "real" posts to the synchronization posts, which will be
			 * updated (or created) on the client
			 */
			final CrudableContent<? extends Resource, ? extends GenericParam> resourceTypeDatabaseManager = this.allDatabaseManagers.get(resourceType);
			final List<Integer> listOfGroupIDs = UserUtils.getListOfGroupIDs(this.loginUser);
			final String loginUserName = this.loginUser.getName();
			for (final SynchronizationPost post : posts) {
				switch (post.getAction()) {
				case CREATE_CLIENT:
					// $FALL-THROUGH$
				case UPDATE_CLIENT:
					// FIXME: this is horribly expensive!
					post.setPost(resourceTypeDatabaseManager.getPostDetails(loginUserName, post.getIntraHash(), userName, listOfGroupIDs, session));
					break;
				default:
					break;
				}
			}

		} finally {
			session.close();
		}

		return posts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#createSyncService()
	 */
	@Override
	public void createSyncService(final SyncService service, final boolean server) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = this.openSession();
		try {
			this.syncDBManager.createSyncService(service, server, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#deleteSyncService(java.net
	 * .URI, boolean)
	 */
	@Override
	public void deleteSyncService(final URI service, final boolean server) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = this.openSession();
		try {
			this.syncDBManager.deleteSyncService(service, server, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#createSyncServer(java.lang
	 * .String, org.bibsonomy.model.sync.SyncService)
	 */
	@Override
	public void createSyncServer(final String userName, final SyncService server) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			this.syncDBManager.createSyncServerForUser(userName, server, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#updateSyncServer(java.lang
	 * .String, java.net.URI, java.util.Properties)
	 */
	@Override
	public void updateSyncServer(final String userName, final SyncService service, final SyncSettingsUpdateOperation operation) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			this.syncDBManager.updateSyncServerForUser(userName, service, operation, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#deleteSyncServer(java.lang
	 * .String, java.net.URI)
	 */
	@Override
	public void deleteSyncServer(final String userName, final URI service) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			this.syncDBManager.deleteSyncServerForUser(userName, service, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#getSyncServiceSettings()
	 */
	@Override
	public List<SyncService> getSyncServiceSettings(final String userName, final URI service, final boolean server) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			return this.syncDBManager.getSyncServiceSettings(userName, service, server, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getSyncServiceDetails()
	 */
	@Override
	public SyncService getSyncServiceDetails(final URI serviceURI) {
		final DBSession session = this.openSession();
		try {
			return this.syncDBManager.getSyncServiceDetails(serviceURI, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#getLastSynchronizationData
	 * (java.lang.String, int, int)
	 */
	@Override
	public SynchronizationData getLastSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			final SynchronizationData lastSyncData = this.syncDBManager.getLastSyncData(userName, service, resourceType, null, session);
			if (present(lastSyncData)) {
				return lastSyncData;
			}
			/*
			 * no sync found -> return very "old" date to bypass NPE later on
			 * FIXME: is this correct or does it break something?
			 */
			final SynchronizationData synchronizationData = new SynchronizationData();
			// fill: ss.uri, sd.user_name, sd.content_type, sd.last_sync_date,
			// sd.status, sd.info
			synchronizationData.setService(service);
			synchronizationData.setResourceType(resourceType);
			synchronizationData.setLastSyncDate(new Date(0));
			synchronizationData.setStatus(SynchronizationStatus.UNDONE);
			return synchronizationData;
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#setCurrentSyncDone(org.bibsonomy
	 * .model.sync.SynchronizationData)
	 */
	@Override
	public void updateSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType, final Date syncDate, final SynchronizationStatus status, final String info, final Date newDate) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			this.syncDBManager.updateSyncData(userName, service, resourceType, syncDate, status, info, newDate, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#setCurrentSyncDone(org.bibsonomy
	 * .model.sync.SynchronizationData)
	 */
	@Override
	public void deleteSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType, final Date syncDate) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			if (Resource.class.equals(resourceType)) {
				// XXX: more generic
				this.syncDBManager.deleteSyncData(userName, service, Bookmark.class, syncDate, session);
				this.syncDBManager.deleteSyncData(userName, service, BibTex.class, syncDate, session);
			} else {
				this.syncDBManager.deleteSyncData(userName, service, resourceType, syncDate, session);
			}
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.sync.SyncLogicInterface#getPostsForSync(java.lang
	 * .Class, java.lang.String)
	 */
	@Override
	public List<SynchronizationPost> getSyncPosts(final String userName, final Class<? extends Resource> resourceType) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
		final DBSession session = this.openSession();
		try {
			if (resourceType == BibTex.class) {
				return this.publicationDBManager.getSyncPostsListForUser(userName, session);
			} else if (resourceType == Bookmark.class) {
				return this.bookmarkDBManager.getSyncPostsListForUser(userName, session);
			} else {
				throw new UnsupportedResourceTypeException();
			}
		} finally {
			session.close();
		}
	}

	/**
	 * TODO: rename method doesn't validate anything
	 * Method to handle privacy settings of posts for synchronization
	 *
	 * @param post
	 */
	private static void validateGroupsForSynchronization(final Post<? extends Resource> post) {
		/*
		 * if post has group make it private
		 */
		if (!GroupUtils.containsExclusiveGroup(post.getGroups())) {
			post.setGroups(Collections.singleton(GroupUtils.buildPrivateGroup()));
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T extends Resource> List<Post<T>> getPosts(final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final SearchType searchType, final Set<Filter> filters, final Order order, final Date startDate, final Date endDate, final int start, final int end) {
		// check allowed start-/end-values
		this.permissionDBManager.checkStartEnd(this.loginUser, grouping, start, end, "posts");

		this.handleAdminFilters(filters);

		// check for systemTags disabling this resourceType
		if (!systemTagsAllowResourceType(tags, resourceType)) {
			return new ArrayList<Post<T>>();
		}
		final DBSession session = this.openSession();
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
			 */
			if (ValidationUtils.safeContains(filters, FilterEntity.HISTORY) && !(resourceType == GoldStandardPublication.class || resourceType == GoldStandardBookmark.class)) {
				this.permissionDBManager.ensureIsAdminOrSelfOrHasGroupRoleOrHigher(this.loginUser, groupingName, GroupRole.USER);
			}
			if (resourceType == BibTex.class) {
				final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, resourceType, grouping, groupingName, tags, hash, order, start, end, startDate, endDate, search, filters, this.loginUser);
				// sets the search type to ealasticSearch
				param.setSearchType(searchType);

				// check permissions for displaying links to documents
				final boolean allowedToAccessUsersOrGroupDocuments = this.permissionDBManager.isAllowedToAccessUsersOrGroupDocuments(this.loginUser, grouping, groupingName, session);
				if (!allowedToAccessUsersOrGroupDocuments) {
					if (ValidationUtils.safeContains(filters, FilterEntity.JUST_PDF)) {
						throw new AccessDeniedException("error.pdf_only_not_authorized_for_" + grouping.toString().toLowerCase());
					}
					param.setPostAccess(PostAccess.POST_ONLY);
				} else {
					// user can access all post details (including docs)
					param.setPostAccess(PostAccess.FULL);
				}

				// this is save because of RTTI-check of resourceType argument
				// which is of class T
				final List<Post<T>> publications = (List) this.publicationDBManager.getPosts(param, session);
				SystemTagsExtractor.handleHiddenSystemTags(publications, this.loginUser.getName());
				return publications;
			}

			if (resourceType == Bookmark.class) {
				final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, resourceType, grouping, groupingName, tags, hash, order, start, end, startDate, endDate, search, filters, this.loginUser);
				// sets the search type to ealasticSearch
				param.setSearchType(searchType);
				final List<Post<T>> bookmarks = (List) this.bookmarkDBManager.getPosts(param, session);
				SystemTagsExtractor.handleHiddenSystemTags(bookmarks, this.loginUser.getName());
				return bookmarks;
			}

			if (resourceType == GoldStandardPublication.class) {
				final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, resourceType, grouping, groupingName, tags, hash, order, start, end, startDate, endDate, search, filters, this.loginUser);
				// sets the search type to ealasticSearch
				param.setSearchType(searchType);

				return (List) this.goldStandardPublicationDBManager.getPosts(param, session);
			}

			if (resourceType == GoldStandardBookmark.class) {
				final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, resourceType, grouping, groupingName, tags, hash, order, start, end, startDate, endDate, search, filters, this.loginUser);
				// sets the search type to ealasticSearch
				param.setSearchType(searchType);

				return (List) this.goldStandardBookmarkDBManager.getPosts(param, session);
			}

			throw new UnsupportedResourceTypeException();
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return an empty list
			return new ArrayList<Post<T>>();
		} finally {
			session.close();
		}
	}

	private static boolean systemTagsAllowResourceType(final Collection<String> tags, final Class<? extends Resource> resourceType) {
		if (present(tags)) {
			for (final String tagName : tags) {
				final SearchSystemTag sysTag = SystemTagsUtil.createSearchSystemTag(tagName);
				if (present(sysTag)) {
					if (!sysTag.allowsResource(resourceType)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#getPostDetails(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public Post<? extends Resource> getPostDetails(final String resourceHash, final String userName) throws ResourceMovedException, ObjectNotFoundException {
		final DBSession session = this.openSession();
		try {
			return this.getPostDetails(resourceHash, userName, session);
		} finally {
			session.close();
		}
	}

	private Post<? extends Resource> getPostDetails(final String resourceHash, final String userName, final DBSession session) {
		for (final CrudableContent<? extends Resource, ? extends GenericParam> manager : this.allDatabaseManagers.values()) {
			final Post<? extends Resource> post = manager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
			/*
			 * if a manager found a post, return it
			 */
			if (present(post)) {
				/*
				 * XXX: can't be added to the postDatabaseManager; calls
				 * getPostDetails with an empty list of visible groups
				 */
				final Resource resource = post.getResource();
				final List<DiscussionItem> discussionSpace = this.discussionDatabaseManager.getDiscussionSpace(this.loginUser, resource.getInterHash(), session);
				resource.setDiscussionItems(discussionSpace);
				SystemTagsExtractor.handleHiddenSystemTags(post, this.loginUser.getName());
				return post;
			}
			/*
			 * check next manager
			 */
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.model.logic.LogicInterface#getGroups(int, int)
	 */
	@Override
	public List<Group> getGroups(final boolean pending, final String userName, final int start, final int end) {
		final DBSession session = this.openSession();
		try {
			if (pending) {
				if (present(userName)) {
					this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
					return this.groupDBManager.getPendingGroups(userName, start, end, session);
				}
				this.permissionDBManager.ensureAdminAccess(this.loginUser);
				return this.groupDBManager.getPendingGroups(null, start, end, session);
			}
			return this.groupDBManager.getAllGroups(start, end, session);
		} finally {
			session.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDeletedGroupUsers(int, int)
	 */
	public List<User> getDeletedGroupUsers(int start, int end) {
		final DBSession session = this.openSession();
		try {
			this.permissionDBManager.ensureAdminAccess(this.loginUser);
			return this.userDBManager.getDeletedGroupUsers(start, end, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getSyncServices(final boolean server)
	 */
	@Override
	public List<SyncService> getAutoSyncServer() {
		final DBSession session = this.openSession();
		try {
			return this.syncDBManager.getAutoSyncServer(session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getAutoSyncServer()
	 */
	@Override
	public List<SyncService> getSyncServices(final boolean server, final String sslDn) {
		final DBSession session = this.openSession();
		try {
			return this.syncDBManager.getSyncServices(server, sslDn, session);
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
	public Group getGroupDetails(final String groupName, final boolean pending) {
		final DBSession session = this.openSession();
		try {
			if (pending) {
				final String requestingUser;
				if (this.permissionDBManager.isAdmin(this.loginUser)) {
					requestingUser = null;
				} else {
					requestingUser = this.loginUser.getName();
				}
				return this.groupDBManager.getPendingGroup(groupName, requestingUser, session);
			}

			final Group myGroup = this.groupDBManager.getGroupMembers(this.loginUser.getName(), groupName, true, this.permissionDBManager.isAdmin(this.loginUser), session);
			if (!GroupUtils.isValidGroup(myGroup)) {
				return null;
			}
			myGroup.setTagSets(this.groupDBManager.getGroupTagSets(groupName, session));
			if (this.permissionDBManager.isAdminOrHasGroupRoleOrHigher(this.loginUser, groupName, GroupRole.MODERATOR)) {
				final Group pendingMembershipsGroup = this.groupDBManager.getGroupWithPendingMemberships(groupName, session);
				if (present(pendingMembershipsGroup)) {
					myGroup.setPendingMemberships(pendingMembershipsGroup.getMemberships());
				}
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
	public List<Tag> getTags(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final String regex, final TagSimilarity relation, final Order order, final Date startDate, final Date endDate, final int start, final int end) {
		return this.getTags(resourceType, grouping, groupingName, tags, hash, search, SearchType.LOCAL, regex, relation, order, startDate, endDate, start, end);
	}

	@Override
	public List<Tag> getTags(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final SearchType searchType, final String regex, final TagSimilarity relation, final Order order, final Date startDate, final Date endDate, final int start, final int end) {
		if (GroupingEntity.ALL.equals(grouping)) {
			this.permissionDBManager.checkStartEnd(this.loginUser, grouping, start, end, "tags");
		}

		final DBSession session = this.openSession();
		try {
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, resourceType, grouping, groupingName, tags, hash, order, start, end, startDate, endDate, search, null, this.loginUser);
			param.setTagRelationType(relation);
			param.setSearchType(searchType);

			if (resourceType == BibTex.class || resourceType == Bookmark.class || resourceType == Resource.class) {
				// this is save because of RTTI-check of resourceType argument
				// which is of class T
				param.setRegex(regex);
				// need to switch from class to string to ensure legibility of
				// Tags.xml
				param.setContentTypeByClass(resourceType);
				param.setResourceType(resourceType);
				return this.tagDBManager.getTags(param, session);
			}

			throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return an empty list
			return new ArrayList<Tag>();
		} finally {
			session.close();
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
		final DBSession session = this.openSession();
		try {
			return this.tagDBManager.getTagDetails(this.loginUser, tagName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteUser(java.lang.String)
	 */
	@Override
	public void deleteUser(final String userName) {
		final DBSession session = this.openSession();
		try {
			// TODO: take care of toLowerCase()!
			this.ensureLoggedIn();
			/*
			 * only an admin or the user himself may delete the account
			 */
			this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);
			this.userDBManager.deleteUser(userName, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteGroup(java.lang.String)
	 */
	@Override
	public void deleteGroup(final String groupName, final boolean pending, final boolean quickDelete) {
		// needs login.
		this.ensureLoggedIn();

		final DBSession session = this.openSession();

		if (pending) {
			try {
				session.beginTransaction();
				this.permissionDBManager.ensureAdminAccess(this.loginUser);
				final Group pendingGroup = this.groupDBManager.getPendingGroup(groupName, null, session);
				if (!present(pendingGroup)) {
					throw new IllegalStateException("pending group '" + groupName + "' does not exist");
				}
				this.groupDBManager.deletePendingGroup(groupName, session);
				session.commitTransaction();
				return;
			} finally {
				session.endTransaction();
				session.close();
			}
		}

		// only group and system admins are allowed to delete the group
		this.permissionDBManager.ensureIsAdminOrHasGroupRoleOrHigher(this.loginUser, groupName, GroupRole.ADMINISTRATOR);

		try {
			session.beginTransaction();
			// make sure that the group exists
			final Group group = this.groupDBManager.getGroupMembers(this.loginUser.getName(), groupName, true, true, session);

			if (!present(group)) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupName + "') doesn't exist");
			}
			
			if (!quickDelete) {
				// ensure that the group has no members except the admin (please not the group user of older groups has role ADMIN)
				final List<GroupMembership> groupMemberships = GroupUtils.getGroupMemberShipsWithoutDummyUser(group.getMemberships());
				if (groupMemberships.size() > 1) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + group.getName() + "') has at least one member beside the administrator.");
				}
			}

			// all the posts/discussions of the group members (one admin and the dummy user) need to be edited as well before deleting the group
			for (final GroupMembership membership : group.getMemberships()) {
				this.updateUserItemsForLeavingGroup(group, membership.getUser().getName(), session);
			}

			this.groupDBManager.deleteGroup(groupName, quickDelete, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
			session.close();
		}
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

		this.permissionDBManager.ensureIsAdminOrSelfOrHasGroupRoleOrHigher(this.loginUser, userName, GroupRole.MODERATOR);

		/*
		 * to store hashes of missing resources
		 */
		final List<String> missingResources = new LinkedList<String>();

		final DBSession session = this.openSession();
		try {
			final String lowerCaseUserName = present(userName) ? userName.toLowerCase() : null;
			for (final String resourceHash : resourceHashes) {
				/*
				 * delete one resource
				 */
				boolean resourceFound = false;
				// TODO would be nice to know about the resourcetype or the
				// instance behind this resourceHash
				for (final CrudableContent<? extends Resource, ? extends GenericParam> man : this.allDatabaseManagers.values()) {
					if (man.deletePost(lowerCaseUserName, resourceHash, this.loginUser, session)) {
						resourceFound = true;
						break;
					}
				}
				/*
				 * remember missing resources
				 */
				if (!resourceFound) {
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
	 * Check for each group actually exist and if the
	 * posting user is allowed to post. If yes, insert the correct group ID into
	 * the given post's groups.
	 * @param user
	 * @param groups the groups to validate
	 * @param session
	 */
	protected void validateGroups(final User user, final Set<Group> groups, final DBSession session) {
		/*
		 * First check for "public" and "private". Those two groups are special,
		 * they can't be assigned with another group.
		 */
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
			if (group.equals(GroupUtils.buildPrivateGroup())) {
				group.setGroupId(GroupUtils.buildPrivateGroup().getGroupId());
			} else {
				group.setGroupId(GroupUtils.buildPublicGroup().getGroupId());
			}
		} else {
			/*
			 * only non-special groups remain (including "friends") - check
			 * those
			 */
			/*
			 * retrieve the user's groups
			 */
			final Set<Integer> groupIds = new HashSet<Integer>(this.groupDBManager.getGroupIdsForUser(user.getName(), session));
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
					throw new ValidationException("User " + user.getName() + " is not a member of group " + group.getName());
				}
				group.setGroupId(testGroup.getGroupId());
			}
		}

		// no group specified -> make it public
		if (groups.isEmpty()) {
			groups.add(GroupUtils.buildPublicGroup());
		}
	}

	/**
	 * Helper method to retrieve an appropriate database manager
	 *
	 * @param <T>
	 *        extends Resource - the resource type
	 * @param post
	 *        - a post of type T
	 * @return an appropriate database manager
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Resource> CrudableContent<T, GenericParam> getFittingDatabaseManager(final Post<T> post) {
		final Class<?> resourceClass = post.getResource().getClass();
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
		return (CrudableContent) man;
	}

	/**
	 * helper method to check if a user is currently logged in
	 */
	private void ensureLoggedIn() {
		if (this.loginUser.getName() == null) {
			throw new AccessDeniedException("Please log in!");
		}
	}

	private void ensureLoggedInAndNoSpammer() {
		this.ensureLoggedIn();
		if (this.loginUser.isSpammer()) {
			throw new AccessDeniedException("You are not allowed to use this function!");
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
		if (this.loginUser.isSpammer()) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "The user is flagged as spammer - cannot create a group with this name");
		}
		final DBSession session = this.openSession();
		try {
			this.groupDBManager.createGroup(group, session);

			return group.getName();
		} finally {
			session.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#restoreGroup(org.bibsonomy.model.Group)
	 */
	public String restoreGroup(final Group group) {
		// check admin permissions
		this.permissionDBManager.ensureAdminAccess(loginUser);
		
		final DBSession session = this.openSession();
		try {
			this.groupDBManager.restoreGroup(group, session);
			return group.getName();
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#updateGroup(org.bibsonomy.model
	 * .Group, org.bibsonomy.common.enums.GroupUpdateOperation,
	 * org.bibsonomy.model.GroupMembership)
	 */
	@Override
	public String updateGroup(final Group paramGroup, final GroupUpdateOperation operation, final GroupMembership membership) {
		final String groupName = paramGroup.getName();
		if (!present(paramGroup) || !present(groupName)) {
			throw new ValidationException("No group name given.");
		}

		final String requestedUserName = present(membership) && present(membership.getUser()) && present(membership.getUser().getName()) ? membership.getUser().getName() : null;
		final boolean userSharedDocuments = present(membership) ? membership.isUserSharedDocuments() : false;

		final DBSession session = this.openSession();

		/*
		 * for every operation the user must at least be logged in
		 */
		this.ensureLoggedIn();

		/*
		 * perform operations
		 */
		try {
			session.beginTransaction();

			// check the groups existence and retrieve the current group
			final Group group = this.groupDBManager.getGroupMembers(this.loginUser.getName(), groupName, false, this.permissionDBManager.isAdmin(this.loginUser), session);
			if (!GroupUtils.isValidGroup(group) && !(GroupUpdateOperation.ACTIVATE.equals(operation) || GroupUpdateOperation.DELETE_GROUP_REQUEST.equals(operation))) {
				throw new IllegalArgumentException("Group does not exist");
			}
			final GroupMembership currentGroupMembership = group.getGroupMembershipForUser(requestedUserName);

			// perform actual operation
			switch (operation) {
			case UPDATE_ALL:
				throw new UnsupportedOperationException("The method " + GroupUpdateOperation.UPDATE_ALL + " is not yet implemented.");
			case UPDATE_SETTINGS:
				this.permissionDBManager.ensureGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.ADMINISTRATOR);
				this.groupDBManager.updateGroupSettings(paramGroup, session);
				break;
			case UPDATE_GROUPROLE:

				if (!present(currentGroupMembership)) {
					throw new IllegalArgumentException("The requested user " + requestedUserName + " is not a member of group " + group.getName());
				}

				this.permissionDBManager.ensureGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.MODERATOR);

				// extra check if role change concerns an administrator
				final GroupRole requestedGroupRole = membership.getGroupRole();
				final GroupRole currentGroupRole = currentGroupMembership.getGroupRole();
				if (GroupRole.ADMINISTRATOR.equals(requestedGroupRole) || GroupRole.ADMINISTRATOR.equals(currentGroupRole)) {
					this.permissionDBManager.ensureGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.ADMINISTRATOR);
					// make sure that we keep at least one admin
					if (!GroupRole.ADMINISTRATOR.equals(requestedGroupRole) && this.groupDBManager.hasExactlyOneAdmin(group, session)) {
						throw new IllegalArgumentException("Group has only this admin left, cannot remove this user.");
					}
				}

				this.groupDBManager.updateGroupRole(this.loginUser, group.getName(), requestedUserName, requestedGroupRole, session);
				break;
			case ADD_MEMBER:
				// we need to query the groupMembership, since the group object
				// might not contain the memberships if the loginUser is not
				// allowed to see them
				final GroupMembership groupMembership = this.groupDBManager.getPendingMembershipForUserAndGroup(requestedUserName, group.getName(), session);

				// We need to be careful with the exception, since it reveals
				// information about pending memberships
				if (!present(groupMembership)) {
					if (this.permissionDBManager.isAdminOrSelf(this.loginUser, requestedUserName)) {
						throw new AccessDeniedException("You have not been invited to this group");
					}
					this.permissionDBManager.ensureGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.MODERATOR);
					throw new AccessDeniedException("The user can not be added to the group since they did not request to become a member.");
				}

				switch (groupMembership.getGroupRole()) {
				case INVITED:
					// only the user themselves can accept an invitation
					this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, requestedUserName);
					this.groupDBManager.addUserToGroup(group.getName(), requestedUserName, userSharedDocuments, GroupRole.USER, session);
					break;
				case REQUESTED:
					// only mods or admins can accept requests
					this.permissionDBManager.ensureGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.MODERATOR);
					this.groupDBManager.addUserToGroup(group.getName(), requestedUserName, groupMembership.isUserSharedDocuments(), GroupRole.USER, session);
					break;
				default:
					throw new AccessDeniedException("Can't add this member to the group");
				}
				break;
			case REMOVE_MEMBER:
				// Check for correct role that can remove the user
				if (!present(currentGroupMembership)) {
					throw new IllegalArgumentException("User cannot be removed from group");
				}
				final GroupRole roleOfUserToRemove = currentGroupMembership.getGroupRole();
				if (GroupRole.USER.equals(roleOfUserToRemove)) {
					if (!this.permissionDBManager.isAdminOrSelf(this.loginUser, requestedUserName)) {
						this.permissionDBManager.ensureGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.MODERATOR);
					}
				} else {
					this.permissionDBManager.ensureIsAdminOrHasGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.ADMINISTRATOR);
					// we need at least one admin in the group at all times.
					if (GroupRole.ADMINISTRATOR.equals(roleOfUserToRemove) && this.groupDBManager.hasExactlyOneAdmin(group, session)) {
						throw new IllegalArgumentException("Group has only this admin left, cannot remove this user.");
					}
				}

				this.groupDBManager.removeUserFromGroup(group.getName(), requestedUserName, false, session);
				this.updateUserItemsForLeavingGroup(group, requestedUserName, session);
				break;
			case UPDATE_USER_SHARED_DOCUMENTS:
				this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, requestedUserName);
				this.groupDBManager.updateUserSharedDocuments(paramGroup, membership, session);
				break;
			case UPDATE_GROUP_REPORTING_SETTINGS:
				this.permissionDBManager.ensureIsAdminOrHasGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.ADMINISTRATOR);
				this.groupDBManager.updateGroupPublicationReportingSettings(paramGroup, session);
				break;
			case ACTIVATE:
				this.permissionDBManager.ensureAdminAccess(this.loginUser);
				// Use paramGroup since group is unretrievable from the database.
				this.groupDBManager.activateGroup(groupName, session);
				break;
			case DELETE_GROUP_REQUEST:
				final Group requestedGroup = this.groupDBManager.getPendingGroup(groupName, this.loginUser.getName(), session);
				if (!present(requestedGroup)) {
					throw new AccessDeniedException("You can only delete group requests of groups you have requested.");
				}

				this.groupDBManager.deletePendingGroup(groupName, session);
				break;
			case ADD_INVITED:
				this.permissionDBManager.ensureIsAdminOrHasGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.MODERATOR);
				this.groupDBManager.addPendingMembership(group.getName(), requestedUserName, userSharedDocuments, GroupRole.INVITED, session);
				break;
			case ADD_REQUESTED:
				// TODO: check for banned users in this group
				// check if the group allows join requests
				if (!group.isAllowJoin()) {
					throw new AccessDeniedException("The group does not allow join group requests.");
				}
				this.groupDBManager.addPendingMembership(group.getName(), requestedUserName, userSharedDocuments, GroupRole.REQUESTED, session);
				break;
				// TODO: Refactor to one GroupUpdateOperation
			case REMOVE_INVITED:
			case DECLINE_JOIN_REQUEST:
				final GroupMembership currentMembership = this.groupDBManager.getPendingMembershipForUserAndGroup(requestedUserName, group.getName(), session);

				if (!present(currentMembership) || !GroupRole.PENDING_GROUP_ROLES.contains(currentMembership.getGroupRole())) {
					throw new AccessDeniedException("You are not allowed to decline this request/invitation");
				}
				if (GroupRole.INVITED.equals(currentMembership.getGroupRole()) || GroupRole.REQUESTED.equals(currentMembership.getGroupRole())) {
					if (this.permissionDBManager.isAdminOrSelf(this.loginUser, requestedUserName) || this.permissionDBManager.isAdminOrHasGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.ADMINISTRATOR)) {
						this.groupDBManager.removePendingMembership(group.getName(), requestedUserName, session);
					}
				}
				break;
			case UPDATE_PERMISSIONS:
				this.permissionDBManager.ensureAdminAccess(this.loginUser);
				this.groupDBManager.updateGroupLevelPermissions(this.loginUser.getName(), paramGroup, session);
				break;
			default:
				throw new UnsupportedOperationException("The requested method is not yet implemented.");
			}
			session.commitTransaction();
			session.endTransaction();
		} finally {
			session.close();
		}
		return groupName;
	}

	/**
	 * @param group
	 * @param userName
	 * @param session
	 */
	private void updateUserItemsForLeavingGroup(final Group group, final String userName, final DBSession session) {
		// get the id of the group
		final int groupId = group.getGroupId();

		// set all tas shared with the group to private (groupID 1)
		this.tagDBManager.updateTasInGroupFromLeavingUser(userName, groupId, session);

		// FIXME: handle group tas?

		/*
		 * update the visibility of the post that are "assigned" to the group
		 * XXX: a loop over all resource database managers that allow groups
		 */
		this.publicationDBManager.updatePostsInGroupFromLeavingUser(userName, groupId, session);
		this.bookmarkDBManager.updatePostsInGroupFromLeavingUser(userName, groupId, session);

		// set all discussions in the group to private (groupID 1)
		this.discussionDatabaseManager.updateDiscussionsInGroupFromLeavingUser(userName, groupId, session);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.PostLogicInterface#createPosts(java.util.List)
	 */
	@Override
	public List<String> createPosts(List<Post<?>> posts) {
		// TODO: Which of these checks should result in a DatabaseException,
		this.ensureLoggedIn();
		/*
		 * check permissions
		 */
		for (final Post<?> post : posts) {
			PostUtils.populatePost(post, this.loginUser);
			this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
		}

		// XXX: find other solution which does not use BibTex subclasses
		posts = this.replaceImportResources(posts);

		/*
		 * insert posts TODO: more efficient implementation (transactions,
		 * deadlock handling, asynchronous, etc.)
		 */
		final List<String> hashes = new LinkedList<String>();
		/*
		 * open session to store all the posts
		 */
		final DBSession session = this.openSession();
		final DatabaseException collectedException = new DatabaseException();
		try {
			for (final Post<?> post : posts) {
				try {
					hashes.add(this.createPost(post, session));
				} catch (final DatabaseException dbex) {
					collectedException.addErrors(dbex);
					log.warn("error message due to exception", dbex);
				} catch (final Exception ex) {
					// some exception other than those covered in the
					// DatabaseException was thrown
					collectedException.addToErrorMessages(PostUtils.getKeyForPost(post), new UnspecifiedErrorMessage(ex));
					log.warn("'unspecified' error message due to exception", ex);
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

	private List<Post<?>> replaceImportResources(final List<? extends Post<? extends Resource>> posts) {
		final List<Post<?>> replacedPosts = new LinkedList<>();
		for (final Post<? extends Resource> post : posts) {
			replacedPosts.add(this.replaceImportResource(post));
		}

		return replacedPosts;
	}

	private Post<?> replaceImportResource(final Post<?> post) {
		final Resource resource = post.getResource();
		if (resource instanceof ImportResource) {
			final BibTex parsedResource = this.parsePublicationImportResource((ImportResource) resource);

			final Post<BibTex> replacedPost = new Post<>(post, true);
			replacedPost.setResource(parsedResource);
			return replacedPost;
		}

		return post;
	}

	private BibTex parsePublicationImportResource(final ImportResource resource) {
		final Collection<BibTex> publications = this.publicationReader.read(resource);
		if (!present(publications)) {
			throw new IllegalStateException("bibtexReader did not throw exception and returned empty result");
		}
		return publications.iterator().next();
	}

	/**
	 * Adds a post in the database.
	 */
	private <T extends Resource> String createPost(final Post<T> post, final DBSession session) {
		final CrudableContent<T, GenericParam> manager = this.getFittingDatabaseManager(post);
		post.getResource().recalculateHashes();

		/*
		 * check and set post visibility for synchronization
		 */
		if (Role.SYNC.equals(this.loginUser.getRole())) {
			validateGroupsForSynchronization(post);
		}

		this.validateGroups(post.getUser(), post.getGroups(), session);

		PostUtils.limitedUserModification(post, this.loginUser);
		/*
		 * change group IDs to spam group IDs
		 */
		PostUtils.setGroupIds(post, this.loginUser);

		manager.createPost(post, this.loginUser, session);

		// if we don't get an exception here, we assume the resource has
		// been successfully created
		return post.getResource().getIntraHash();
	}

	/**
	 * The given posts are updated. If the operation is
	 * {@link PostUpdateOperation#UPDATE_TAGS},
	 * the posts must only contain the
	 * <ul>
	 * <li>date,</li>
	 * <li>tags,</li>
	 * <li>intraHash,</li>
	 * <li>and optionally a username.
	 * </ul>
	 *
	 * @see org.bibsonomy.model.logic.PostLogicInterface#updatePosts(java.util.List,
	 *      org.bibsonomy.common.enums.PostUpdateOperation)
	 */
	@Override
	public List<String> updatePosts(final List<Post<?>> posts, final PostUpdateOperation operation) {
		/*
		 * TODO: Which of these checks should result in a DatabaseException,
		 * which do we want to handle otherwise (=status quo)
		 */
		this.ensureLoggedIn();
		/*
		 * check permissions
		 */
		for (final Post<?> post : posts) {
			PostUtils.populatePost(post, this.loginUser);
			this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
			this.permissionDBManager.ensureApprovalStatusAllowed(post, this.loginUser);
		}

		final List<String> hashes = new LinkedList<String>();
		/*
		 * open session
		 */
		final DBSession session = this.openSession();
		final DatabaseException collectedException = new DatabaseException();
		try {
			for (final Post<?> post : posts) {
				try {
					hashes.add(this.updatePost(post, operation, session));
				} catch (final DatabaseException dbex) {
					collectedException.addErrors(dbex);
				} catch (final Exception ex) {
					// some exception other than those covered in the
					// DatabaseException was thrown
					log.error("updating post " + post.getResource().getIntraHash() + "/" + this.loginUser.getName() + " failed", ex);
					collectedException.addToErrorMessages(PostUtils.getKeyForPost(post), new UnspecifiedErrorMessage(ex));
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
		final CrudableContent<T, GenericParam> manager = this.getFittingDatabaseManager(post);
		final String oldIntraHash = post.getResource().getIntraHash();

		if (Role.SYNC.equals(this.loginUser.getRole())) {
			validateGroupsForSynchronization(post);
		}
		this.validateGroups(post.getUser(), post.getGroups(), session);

		PostUtils.limitedUserModification(post, this.loginUser);

		/*
		 * change group IDs to spam group IDs
		 */
		if (post.getUser().equals(this.loginUser)) {
			PostUtils.setGroupIds(post, this.loginUser);
		} else {
			final String postUserName = post.getUser().getName();
			final User groupUserDetails = this.userDBManager.getUserDetails(postUserName, session);
			PostUtils.setGroupIds(post, groupUserDetails);
		}

		/*
		 * XXX: this is a "hack" and will be replaced any time If the operation
		 * is UPDATE_URLS then create/delete the url right here and return the
		 * intra hash.
		 */
		if (PostUpdateOperation.UPDATE_URLS_ADD.equals(operation)) {
			log.debug("Adding URL in updatePost()/DBLogic.java");
			final BibTexExtra resourceExtra = ((BibTex) post.getResource()).getExtraUrls().get(0);

			/*
			 * TODO: here we extract the bibtex extra attributes to build a new
			 * bibtexextra object in the manager/param
			 */
			this.bibTexExtraDBManager.createURL(oldIntraHash, this.loginUser.getName(), resourceExtra.getUrl().toExternalForm(), resourceExtra.getText(), session);
			return oldIntraHash;
		} else if (PostUpdateOperation.UPDATE_URLS_DELETE.equals(operation)) {
			log.debug("Deleting URL in updatePost()/DBLogic.java");
			final BibTexExtra resourceExtra = ((BibTex) post.getResource()).getExtraUrls().get(0);
			this.bibTexExtraDBManager.deleteURL(oldIntraHash, this.loginUser.getName(), resourceExtra.getUrl(), session);

			return oldIntraHash;
		}

		/*
		 * update post
		 *
		 * if we don't get an exception here, we assume the resource has been
		 * successfully updated
		 */
		manager.updatePost(post, oldIntraHash, this.loginUser, operation, session);

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
	public int updateTags(final User user, final List<Tag> tagsToReplace, final List<Tag> replacementTags, final boolean updateRelations) {
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(this.loginUser, user.getName());

		final DBSession session = this.openSession();
		try {
			if (updateRelations) {
				if (tagsToReplace.size() != 1 || replacementTags.size() != 1) {
					throw new ValidationException("tag relations can only be updated, when exactly one tag is exchanged by exactly one other tag.");
				}

				this.tagRelationsDBManager.updateTagRelations(user, tagsToReplace.get(0), replacementTags.get(0), session);

			}

			/*
			 * finally delegate to tagDBManager
			 */
			return this.tagDBManager.updateTags(user, tagsToReplace, replacementTags, session);

		} finally {
			session.close();
		}
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
		this.permissionDBManager.ensureAdminAccess(this.loginUser);

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
		 * only logged in users can update user settings.
		 */
		final String username = user.getName();
		if (!UserUpdateOperation.ACTIVATE.equals(operation)) {
			this.ensureLoggedIn();

			/*
			 * group admins can change settings of their group
			 */
			final Group group = this.getGroupDetails(username, false);
			if (GroupUtils.isValidGroup(group)) {
				this.permissionDBManager.ensureIsAdminOrHasGroupRoleOrHigher(this.loginUser, group.getName(), GroupRole.ADMINISTRATOR);
			} else {

				/*
				 * only admins can change settings of /other/ users
				 */
				this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, username);
			}
		}
		final DBSession session = this.openSession();

		try {
			switch (operation) {
			case UPDATE_PASSWORD:
				return this.userDBManager.updatePasswordForUser(user, session);
			case DELETE_OPENID:
				this.userDBManager.deleteOpenIDUser(username, session);
				return username;
			case UPDATE_SETTINGS:
				return this.userDBManager.updateUserSettingsForUser(user, session);
			case UPDATE_API:
				this.userDBManager.updateApiKeyForUser(user, session);
				break;
			case UPDATE_CORE:
				return this.userDBManager.updateUserProfile(user, session);
			case UPDATE_LIMITED_USER:
				return this.userDBManager.updateLimitedUser(user, session);
			case ACTIVATE:
				return this.userDBManager.activateUser(user, session);
			case UPDATE_SPAMMER_STATUS:
				/*
				 * only admins are allowed to change spammer settings
				 */
				log.debug("Start update this framework");
				this.permissionDBManager.ensureAdminAccess(this.loginUser);
				/*
				 * open session and update spammer settings
				 */
				final String mode = this.adminDBManager.getClassifierSettings(ClassifierSettings.TESTING, session);
				log.debug("User prediction: " + user.getPrediction());
				return this.adminDBManager.flagSpammer(user, this.getAuthenticatedUser().getName(), mode, session);
			case UPDATE_ALL:
				return this.storeUser(user, true);
			default:
				throw new UnsupportedOperationException(operation + " not supported.");
			}
		} finally {
			session.close();
		}
		return null;
	}

	/**
	 * TODO: extract the method to create and update user
	 *
	 * Adds/updates a user in the database.
	 */
	private String storeUser(final User user, final boolean update) {
		final DBSession session = this.openSession();

		try {
			final User existingUser = this.userDBManager.getUserDetails(user.getName(), session);
			if (update) {
				/*
				 * update the user
				 */
				if (!present(existingUser.getName())) {
					/*
					 * error: user name does not exist
					 */
					throw new ValidationException("user " + user.getName() + " does not exist");
				}

				return this.userDBManager.updateUser(user, session);
			}

			final List<User> pendingUserList = this.userDBManager.getPendingUserByUsername(user.getName(), 0, Integer.MAX_VALUE, session);
			/*
			 * create a new user
			 */
			if (present(existingUser.getName()) || present(pendingUserList)) {
				/*
				 * error: user name already exists
				 */
				throw new ValidationException("user " + user.getName() + " already exists");
			}
			return this.userDBManager.createUser(user, session);
		} finally {
			/*
			 * TODO: check, if rollback is handled correctly!
			 */
			session.close();
		}
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
	public List<Author> getAuthors(final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final FilterEntity filter, final int start, final int end, final String search) {
		/*
		 * FIXME: implement a chain or something similar
		 */
		final DBSession session = this.openSession();

		try {
			if (GroupingEntity.ALL.equals(grouping)) {
				return this.authorDBManager.getAuthors(session);
			}

			throw new UnsupportedOperationException("Currently only ALL authors can be listed.");
		} finally {
			session.close();
		}
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
		this.ensureLoggedIn();
		final String userName = document.getUserName();

		/*
		 * users can only modify their own documents
		 */
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		final DBSession session = this.openSession();
		try {
			if (resourceHash != null) {
				/*
				 * document shall be attached to a post
				 */
				Post<BibTex> post = null;
				try {
					post = this.publicationDBManager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				} catch (final ResourceMovedException ex) {
					// ignore
				} catch (final ObjectNotFoundException ex) {
					// ignore
				}
				if (post != null) {
					/*
					 * post really exists!
					 */
					final boolean existingDoc = this.docDBManager.checkForExistingDocuments(userName, resourceHash, document.getFileName(), session);
					if (existingDoc) {
						/*
						 * the post has already a file with that name attached
						 * ...
						 * FIXME: is this really required?
						 */
						this.docDBManager.updateDocument(post.getContentId(), document.getFileHash(), document.getFileName(), document.getDate(), userName, document.getMd5hash(), session);

					} else {
						// add document
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
		this.ensureLoggedIn();

		final String lowerCaseUserName = userName.toLowerCase();
		this.permissionDBManager.ensureWriteAccess(this.loginUser, lowerCaseUserName);

		final DBSession session = this.openSession();

		try {
			return this.docDBManager.getDocument(lowerCaseUserName, fileHash, session);
		} finally {
			session.close();
		}
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
		this.ensureLoggedIn();

		final String lowerCaseUserName = userName.toLowerCase();

		final DBSession session = this.openSession();
		try {
			if (present(resourceHash)) {
				/*
				 * we just forward this task to getPostDetails from the
				 * BibTeXDatabaseManager and extract the documents.
				 */
				Post<BibTex> post = null;
				try {
					// FIXME: remove strange getpostdetails method
					post = this.publicationDBManager.getPostDetails(this.loginUser.getName(), resourceHash, lowerCaseUserName, UserUtils.getListOfGroupIDs(this.loginUser), true, session);
				} catch (final ResourceMovedException ex) {
					// ignore
				} catch (final ObjectNotFoundException ex) {
					// ignore
				}

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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDocuments(java.lang.String)
	 */
	@Override
	public List<Document> getDocuments(String userName) {
		this.ensureLoggedIn();

		final String lowerCaseUserName = userName.toLowerCase();
		this.permissionDBManager.ensureWriteAccess(this.loginUser, lowerCaseUserName);

		final DBSession session = this.openSession();

		try {
			return this.docDBManager.getLayoutDocuments(lowerCaseUserName, session);
		} finally {
			session.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDocumentStatistics(org.bibsonomy.common.enums.GroupingEntity, java.lang.String, org.bibsonomy.common.enums.FilterEntity, java.util.Set, java.util.Date, java.util.Date)
	 */
	@Override
	public Statistics getDocumentStatistics(final GroupingEntity groupingEntity, final String grouping, final Set<Filter> filters, final Date startDate, final Date endDate) {
		this.ensureLoggedIn();
		this.permissionDBManager.ensureAdminAccess(this.loginUser); // TOOD: currently only for admins
		final DBSession session = this.openSession();

		try {
			this.handleAdminFilters(filters);

			final StatisticsParam param = LogicInterfaceHelper.buildParam(StatisticsParam.class, null, groupingEntity, grouping, null, null, null, -1, -1, startDate, endDate, null, filters, this.loginUser);
			return this.statisticsDBManager.getDocumentStatistics(param, session);
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return 0 (cause we also return empty
			// list when a query timeout exception is thrown)
			return new Statistics(0);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#renameDocument(org.bibsonomy
	 * .model.Document, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateDocument(final String userName, final String resourceHash, final String documentName, final Document document) {
		/*
		 * users can only modify their own documents
		 */
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		final DBSession session = this.openSession();
		try {
			final String newName = document.getFileName();
			if (resourceHash != null) {
				/*
				 * the document belongs to a post --> check if the user owns the
				 * post
				 */
				Post<BibTex> post = null;
				try {
					post = this.publicationDBManager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				} catch (final ResourceMovedException ex) {
					// ignore
				} catch (final ObjectNotFoundException ex) {
					// ignore
				}
				if (post != null) {
					/*
					 * the given resource hash belongs to a post of the user ->
					 * rename the corresponding document to the new name
					 */
					final Document existingDocument = this.docDBManager.getDocumentForPost(userName, resourceHash, documentName, session);
					if (present(existingDocument)) {
						this.docDBManager.updateDocument(post.getContentId().intValue(), existingDocument.getFileHash(), newName, existingDocument.getDate(),
								userName, existingDocument.getMd5hash(), session);
					}
				} else {
					throw new ValidationException("Could not find a post with hash '" + resourceHash + "'.");
				}
			} else {
				throw new ValidationException("update document without resourceHash is not possible");
			}
			log.debug("renamed document " + documentName + " from user " + userName + "to " + newName);
		} finally {
			session.close();
		}
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
		this.ensureLoggedIn();

		final String userName = document.getUserName();
		/*
		 * users can only modify their own documents
		 */
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		final DBSession session = this.openSession();
		try {
			if (resourceHash != null) {
				/*
				 * the document belongs to a post --> check if the user owns the
				 * post
				 */
				Post<BibTex> post = null;
				try {
					post = this.publicationDBManager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
				} catch (final ResourceMovedException ex) {
					// ignore
				} catch (final ObjectNotFoundException ex) {
					// ignore
				}
				if (post != null) {
					/*
					 * the given resource hash belongs to a post of the user ->
					 * delete the corresponding document
					 */
					final Document existingDocument = this.docDBManager.getDocumentForPost(userName, resourceHash, document.getFileName(), session);
					if (present(existingDocument)) {
						this.docDBManager.deleteDocument(post.getContentId(), existingDocument, session);
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
		final DBSession session = this.openSession();
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
		final DBSession session = this.openSession();
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
		this.ensureLoggedIn();
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = this.openSession();
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
	public Statistics getPostStatistics(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final Set<Filter> filters, final Order order, final Date startDate, final Date endDate, final int start, final int end) {
		final DBSession session = this.openSession();

		try {
			this.handleAdminFilters(filters);

			final StatisticsParam param = LogicInterfaceHelper.buildParam(StatisticsParam.class, resourceType, grouping, groupingName, tags, hash, order, start, end, startDate, endDate, search, filters, this.loginUser);
			if (resourceType == GoldStandardPublication.class || resourceType == BibTex.class || resourceType == Bookmark.class || resourceType == Resource.class) {
				param.setContentTypeByClass(resourceType);
				return this.statisticsDBManager.getPostStatistics(param, session);
			}

			throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return 0 (cause we also return empty
			// list when a query timeout exception is thrown)
			return new Statistics(0);
		} finally {
			session.close();
		}
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
		final DBSession session = this.openSession();
		try {
			final TagRelationParam param = LogicInterfaceHelper.buildParam(TagRelationParam.class, resourceType, grouping, groupingName, tags, null, null, start, end, null, null, null, null, this.loginUser);
			param.setConceptStatus(status);
			return this.tagRelationsDBManager.getConcepts(param, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @return a concept, i.e. a tag with its assigned subtags
	 *
	 *         in both queries getConceptForUser and getGlobalConceptByName
	 *         the case of parameter conceptName is ignored
	 *
	 * @see org.bibsonomy.model.logic.LogicInterface#getConceptDetails(java.lang.
	 *      String, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public Tag getConceptDetails(final String conceptName, final GroupingEntity grouping, final String groupingName) {
		final DBSession session = this.openSession();
		try {
			if (GroupingEntity.USER.equals(grouping) || GroupingEntity.GROUP.equals(grouping) && present(groupingName)) {
				return this.tagRelationsDBManager.getConceptForUser(conceptName, groupingName, session);
			} else if (GroupingEntity.ALL.equals(grouping)) {
				return this.tagRelationsDBManager.getGlobalConceptByName(conceptName, session);
			}

			throw new RuntimeException("Can't handle request");
		} finally {
			session.close();
		}
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
		if (GroupingEntity.USER.equals(grouping)) {
			this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, groupingName);
			return this.storeConcept(concept, grouping, groupingName, false);
		}
		throw new UnsupportedOperationException("Currently, tag relations can only be created for users.");
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
		if (GroupingEntity.USER.equals(grouping)) {
			this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, groupingName);

			final DBSession session = this.openSession();
			try {
				this.tagRelationsDBManager.deleteConcept(concept, groupingName, session);
			} finally {
				session.close();
			}
			return;
		}
		throw new UnsupportedOperationException("Currently, tag relations can only be deleted for users.");
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
		if (GroupingEntity.USER.equals(grouping)) {
			this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, groupingName);

			final DBSession session = this.openSession();
			try {
				this.tagRelationsDBManager.deleteRelation(upper, lower, groupingName, session);
				return;
			} finally {
				session.close();
			}
		}
		throw new UnsupportedOperationException("Currently, tag relations can only be created for users.");
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
		if (!GroupingEntity.USER.equals(grouping)) {
			throw new UnsupportedOperationException("Currently only user's can have concepts.");
		}

		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, groupingName);

		final DBSession session = this.openSession();
		// now switch the operation and call the right method in the
		// taglRelationsDBManager or DBLogic
		try {
			switch (operation) {
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
		final DBSession session = this.openSession();
		try {
			if (update) {
				this.tagRelationsDBManager.insertRelations(concept, groupingName, session);
			} else {
				this.deleteConcept(concept.getName(), grouping, groupingName);
				this.tagRelationsDBManager.insertRelations(concept, groupingName, session);
			}
			return concept.getName();
		} finally {
			session.close();
		}
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
	public List<User> getUsers(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final UserRelation relation, final String search, final int start, final int end) {
		// assemble param object
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, resourceType, grouping, groupingName, tags, hash, order, start, end, null, null, search, null, this.loginUser);
		param.setUserRelation(relation);

		// check start/end values
		if (GroupingEntity.ALL.equals(grouping)) {
			this.permissionDBManager.checkStartEnd(this.loginUser, grouping, start, end, "users");
		}

		final DBSession session = this.openSession();
		try {
			// start chain
			return this.userDBManager.getUsers(param, session);
		} finally {
			session.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUserStatistics()
	 */
	@Override
	public Statistics getUserStatistics(final GroupingEntity grouping, final Set<Filter> filters, final Classifier classifier, final SpamStatus status, final Date startDate, final Date endDate) {
		final DBSession session = this.openSession();
		try {
			return this.statisticsDBManager.getUserStatistics(grouping, startDate, filters, classifier, status, session);
		} finally {
			session.close();
		}
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
		final DBSession session = this.openSession();
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
		final DBSession session = this.openSession();
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
		final DBSession session = this.openSession();
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
	 * org.bibsonomy.model.logic.LogicInterface#getClassifierHistory(java.lang
	 * .String)
	 */
	@Override
	public List<User> getClassifierHistory(final String userName) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = this.openSession();
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
	public List<User> getClassifierComparison(final int interval, final int limit) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = this.openSession();
		try {
			return this.adminDBManager.getClassifierComparison(interval, limit, session);
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
	public String getOpenIDUser(final String openID) {
		final DBSession session = this.openSession();
		try {
			return this.userDBManager.getOpenIDUser(openID, session);
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
		final DBSession session = this.openSession();
		try {
			return this.userDBManager.getUsernameByLdapUser(userId, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getUsernameByRemoteUserId(org
	 * .bibsonomy.model.user.remote.RemoteUserId)
	 */
	@Override
	public String getUsernameByRemoteUserId(final RemoteUserId remoteUserId) {
		final DBSession session = this.openSession();
		try {
			return this.userDBManager.getUsernameByRemoteUser(remoteUserId, session);
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
	public int getTagStatistics(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String regex, final ConceptStatus status, final Set<Filter> filters, final Date startDate, final Date endDate, final int start, final int end) {
		final DBSession session = this.openSession();
		try {
			final StatisticsParam param = LogicInterfaceHelper.buildParam(StatisticsParam.class, resourceType, grouping, groupingName, tags, null, null, start, end, startDate, endDate, null, filters, this.loginUser);
			if (present(resourceType)) {
				param.setContentTypeByClass(resourceType);
			}

			param.setConceptStatus(status);
			return this.statisticsDBManager.getTagStatistics(param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * We create a UserRelation of the form (sourceUser, targetUser)\in relation
	 * This Method only works for the FOLLOWER_OF and the OF_FRIEND relation
	 * Other relation will result in an UnsupportedRelationException
	 *
	 * TODO: the "tag" parameter is currently ignored by this function. As soon
	 * as tagged relationships are needed, please implement the handling of
	 * the "tag" parameter from here on (mainly in the UserDBManager)
	 *
	 * @see org.bibsonomy.model.logic.LogicInterface#insertUserRelationship()
	 */
	@Override
	public void createUserRelationship(final String sourceUser, final String targetUser, final UserRelation relation, final String tag) {
		this.ensureLoggedIn();
		/*
		 * relationships can only be created by the logged-in user or admins
		 */
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, sourceUser);

		final DBSession session = this.openSession();
		/*
		 * finally try to create relationship
		 */
		try {
			/*
			 * check if relationship may be created (e.g. some special users
			 * like 'dblp' are disallowed)
			 */
			this.permissionDBManager.checkUserRelationship(this.loginUser, this.userDBManager.getUserDetails(targetUser, session), relation, tag);
			this.userDBManager.createUserRelation(sourceUser, targetUser, relation, tag, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#getUserRelationship(java.lang
	 * .String, org.bibsonomy.common.enums.UserRelation)
	 */
	@Override
	public List<User> getUserRelationship(final String sourceUser, final UserRelation relation, final String tag) {
		this.ensureLoggedIn();
		// TODO: ask Robert about this method
		// this.permissionDBManager.checkUserRelationship(sourceUser,
		// targetUser, relation);
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, sourceUser);

		final DBSession session = this.openSession();
		try {
			// get all users that are in relation with sourceUser
			return this.userDBManager.getUserRelation(sourceUser, relation, tag, session);
		} finally {
			// unsupported relations will cause an UnsupportedRelationException
			session.close();
		}
	}

	/*
	 * We delete a UserRelation of the form (sourceUser, targetUser)\in relation
	 * This Method only works for the FOLLOWER_OF and the OF_FRIEND relation
	 * Other relation will result in an UnsupportedRelationException FIXME: use
	 * Strings (usernames) instead of users
	 *
	 * TODO: the "tag" parameter is currently ignored by this function. As soon
	 * as tagged relationships are needed, please implement the handling of
	 * the "tag" parameter from here on (mainly in the UserDBManager)
	 *
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteUserRelationship()
	 */
	@Override
	public void deleteUserRelationship(final String sourceUser, final String targetUser, final UserRelation relation, final String tag) {
		this.ensureLoggedIn();
		// ask Robert about this method
		// this.permissionDBManager.checkUserRelationship(sourceUser,
		// targetUser, relation);
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, sourceUser);

		final DBSession session = this.openSession();
		try {
			this.userDBManager.deleteUserRelation(sourceUser, targetUser, relation, tag, session);
		} finally {
			// unsupported Relations will cause an UnsupportedRelationException
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.model.logic.LogicInterface#createClipboardItems()
	 */
	@Override
	public int createClipboardItems(final List<Post<? extends Resource>> posts) {
		this.ensureLoggedIn();

		final DBSession session = this.openSession();
		try {
			for (final Post<? extends Resource> post : posts) {
				if (post.getResource() instanceof Bookmark) {
					throw new UnsupportedResourceTypeException("Bookmarks can't be stored in the clipboard");
				}
				/*
				 * get the complete post from the database
				 */
				final String intraHash = post.getResource().getIntraHash();
				final String postUserName = post.getUser().getName();
				final Post<BibTex> copy = this.publicationDBManager.getPostDetails(this.loginUser.getName(), intraHash, postUserName, UserUtils.getListOfGroupIDs(this.loginUser), session);

				/*
				 * post might be null, because a) it does not exist b) user may
				 * not access it
				 */
				if (copy == null) {
					/*
					 * TODO: exception handling?!
					 */
					throw new ValidationException("Post with hash " + intraHash + " of user " + postUserName + " not found!");
				}

				/*
				 * insert the post from the user's clipboard
				 */
				this.clipboardDBManager.createItem(this.loginUser.getName(), copy.getContentId(), session);
			}

			// get actual clipboard size
			return this.clipboardDBManager.getNumberOfClipboardEntries(this.loginUser.getName(), session);
		} catch (final Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteClipboardItems()
	 */
	@Override
	public int deleteClipboardItems(final List<Post<? extends Resource>> posts, final boolean clearClipboard) {
		this.ensureLoggedIn();

		final DBSession session = this.openSession();

		try {
			// decide which delete function will be called
			if (clearClipboard) {
				// clear all in clipboard
				this.clipboardDBManager.deleteAllItems(this.loginUser.getName(), session);
			} else {
				// delete specific post
				for (final Post<? extends Resource> post : posts) {
					if (post.getResource() instanceof Bookmark) {
						throw new UnsupportedResourceTypeException("Bookmarks can't be stored in the clipboard");
					}
					/*
					 * get the content_id from the database
					 */
					final Integer contentIdOfPost = this.publicationDBManager.getContentIdForPost(post.getResource().getIntraHash(), post.getUser().getName(), session);
					if (!present(contentIdOfPost)) {
						throw new ValidationException("Post not found. Can't remove post from clipboard.");
					}
					/*
					 * delete the post from the user's clipboard
					 */
					this.clipboardDBManager.deleteItem(this.loginUser.getName(), contentIdOfPost, session);
				}
			}

			// get actual clipboardsize
			return this.clipboardDBManager.getNumberOfClipboardEntries(this.loginUser.getName(), session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.LogicInterface#deleteInboxMessages(java.util
	 * .List, boolean)
	 */
	@Override
	public int deleteInboxMessages(final List<Post<? extends Resource>> posts, final boolean clearInbox) {
		/*
		 * check permissions
		 */
		this.ensureLoggedIn();
		/*
		 * delete one message from the inbox
		 */
		final DBSession session = this.openSession();
		try {
			if (clearInbox) {
				this.inboxDBManager.deleteAllInboxMessages(this.loginUser.getName(), session);
			} else {
				for (final Post<? extends Resource> post : posts) {
					final String sender = post.getUser().getName();
					final String receiver = this.loginUser.getName();
					final String resourceHash = post.getResource().getIntraHash();
					if (!present(receiver) || !present(resourceHash)) {
						/*
						 * FIXME: proper exception message!
						 */
						throw new ValidationException("You are not authorized to perform the requested operation");
					}
					this.inboxDBManager.deleteInboxMessage(sender, receiver, resourceHash, session);
				}
			}
			return this.inboxDBManager.getNumInboxMessages(this.loginUser.getName(), session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.GoldStandardPostLogicInterface#createRelation
	 * (java.lang.String, java.util.Set)
	 */
	@Override
	public void createRelations(final String postHash, final Set<String> references, final GoldStandardRelation relation) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser); // only
		// admins
		// can
		// create
		// references

		final DBSession session = this.openSession();
		try {
			this.goldStandardPublicationDBManager.addRelationsToPost(this.loginUser.getName(), postHash, references, relation, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.GoldStandardPostLogicInterface#deleteReferences
	 * (java.lang.String, java.util.Set)
	 */
	@Override
	public void deleteRelations(final String postHash, final Set<String> references, final GoldStandardRelation relation) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser); // only
		// admins
		// can
		// delete
		// references

		final DBSession session = this.openSession();
		try {
			this.goldStandardPublicationDBManager.removeRelationsFromPost(this.loginUser.getName(), postHash, references, relation, session);
		} finally {
			session.close();
		}
	}

	/**
	 * This method creates a new wiki for a user given by its username.
	 *
	 * @param userName the user for whom this wiki is to be created.
	 * @param wiki the wiki for userName.
	 */
	@Override
	public void createWiki(final String userName, final Wiki wiki) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);

		final DBSession session = this.openSession();
		try {
			this.wikiDBManager.createWiki(userName, wiki, session);
		} finally {
			session.close();
		}
	}

	/**
	 * Retrieves a wiki from the database.
	 *
	 * @see org.bibsonomy.model.logic.LogicInterface#getWiki(java.lang.String,
	 *      java.util.Date)
	 * @param userName the user for whom the wiki is to be retrieved.
	 * @param date - if <code>null</code>, the latest version of the wiki is
	 *        returned. Otherwise, the latest version before <code>date</code>.
	 * @return the current wiki for userName, latest before date or an empty
	 *         wiki if the
	 *         logged in user isn't allowed to access userName's wiki.
	 */
	@Override
	public Wiki getWiki(final String userName, final Date date) {
		final DBSession session = this.openSession();

		try {
			final User requUser = this.getUserDetails(userName);
			/*
			 * We return an empty wiki for users who are not allowed to access
			 * this wiki.
			 */
			if (!this.permissionDBManager.isAllowedToAccessUsersProfile(requUser, this.loginUser, session)) {
				return new Wiki();
			}

			if (!present(date)) {
				return this.wikiDBManager.getCurrentWiki(userName, session);
			}

			/*
			 * TODO: remove this comment when the time is right!
			 * this will never happen to get called because right now
			 * (29.04.2013)
			 * this method is only called with date = null.
			 */
			return this.wikiDBManager.getPreviousWiki(userName, date, session);
		} finally {
			session.close();
		}
	}

	/**
	 * This method will not be used yet, still it has to come here because of
	 * inheritance issues. It isn't called from anywhere anyway, yet.
	 *
	 * This method will retrieve old versions of a user's wiki for reversing
	 * actions or changes in the wiki.
	 *
	 * @param userName the name of the requesting user
	 * @return a list of dates where the wiki of userName has been changed.
	 */
	@Override
	public List<Date> getWikiVersions(final String userName) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, userName);

		final DBSession session = this.openSession();
		try {
			return this.wikiDBManager.getWikiVersions(userName, session);
		} finally {
			session.close();
		}
	}

	/**
	 * updates the current wiki with the new one.
	 */
	@Override
	public void updateWiki(final String userName, final Wiki wiki) {
		if (!this.permissionDBManager.isAdminOrSelf(this.loginUser, userName)) {
			// if we are here then the user is not the logged in one which means it is a group user
			if (!this.permissionDBManager.isAdminOrHasGroupRoleOrHigher(this.loginUser, userName, GroupRole.MODERATOR)) {
				throw new AccessDeniedException();
			}
		}

		final DBSession session = this.openSession();

		try {
			final Wiki currentWiki = this.wikiDBManager.getCurrentWiki(userName, session);

			/*
			 * Check if the wiki exists
			 */
			if (currentWiki != null) {

				/*
				 * Check if the text has changed compared to the
				 * current version in the database.
				 *
				 * If currentWikiText is null, we just interpret this
				 * as a missing wiki (shouldn't happen that much anymore)
				 * and set the contents to an empty string.
				 */
				String currentWikiText = currentWiki.getWikiText();
				if (currentWikiText == null) {
					currentWikiText = "";
				}

				/*
				 * If we find differences, update the database.
				 */
				if (!currentWikiText.equals(wiki.getWikiText())) {
					this.wikiDBManager.updateWiki(userName, wiki, session);
					this.wikiDBManager.logWiki(userName, currentWiki, session);
				}

				/*
				 * a wiki does not exist, at least there is nothing in the
				 * database.
				 * This should never happen after the 2.0.35 release, because
				 * we will create a wiki for each new user. All the old users
				 * should
				 * have been updated as well then.
				 */
			} else {
				this.createWiki(userName, wiki);
			}
		} finally {
			session.close();
		}
	}

	@Override
	public void createExtendedField(final Class<? extends Resource> resourceType, final String userName, final String intraHash, final String key, final String value) {
		final DBSession session = this.openSession();

		try {
			if (BibTex.class == resourceType) {
				this.publicationDBManager.createExtendedField(userName, intraHash, key, value, session);
			} else {
				throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
			}
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteExtendedField(final Class<? extends Resource> resourceType, final String userName, final String intraHash, final String key, final String value) {
		final DBSession session = this.openSession();

		try {
			if (BibTex.class == resourceType) {
				if (!present(key)) {
					this.publicationDBManager.deleteAllExtendedFieldsData(userName, intraHash, session);
				} else {
					if (!present(value)) {
						this.publicationDBManager.deleteExtendedFieldsByKey(userName, intraHash, key, session);
					} else {
						this.publicationDBManager.deleteExtendedFieldByKeyValue(userName, intraHash, key, value, session);
					}
				}
			} else {
				throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
			}
		} finally {
			session.close();
		}
	}

	@Override
	public Map<String, List<String>> getExtendedFields(final Class<? extends Resource> resourceType, final String userName, final String intraHash, final String key) {
		final DBSession session = this.openSession();

		try {
			if (BibTex.class == resourceType) {
				return this.publicationDBManager.getExtendedFields(userName, intraHash, key, session);
			}

			throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.ReviewLogicInterface#getReviews(java.lang.String
	 * )
	 */
	@Override
	public List<DiscussionItem> getDiscussionSpace(final String interHash) {
		final DBSession session = this.openSession();
		try {
			return this.discussionDatabaseManager.getDiscussionSpace(this.loginUser, interHash, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.DiscussionLogicInterface#createDiscussionItem
	 * (java.lang.String, java.lang.String, org.bibsonomy.model.DiscussionItem)
	 */
	@Override
	public void createDiscussionItem(final String interHash, final String username, final DiscussionItem discussionItem) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, username);

		final DBSession session = this.openSession();
		session.beginTransaction();
		/*
		 * TODO: Only checking should be done, GoldstandardCreation is the job
		 * of the calling Controller
		 */
		try {
			// verify that there exists a gold standard
			final Post<? extends Resource> goldStandardPost = this.getPostDetails(interHash, GoldStandardPostLogicInterface.GOLD_STANDARD_USER_NAME);
			if (!present(goldStandardPost)) {
				throw new ObjectNotFoundException(interHash);
			}
			/*
			 * create the discussion item
			 */
			discussionItem.setResourceType(goldStandardPost.getResource().getClass());
			final User commentUser = this.userDBManager.getUserDetails(username, session);
			discussionItem.setUser(commentUser);
			this.createDiscussionItem(interHash, discussionItem, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
			session.close();
		}
	}

	private void prepareDiscussionItem(final User commentUser, final Set<Group> groups, final DBSession session) {
		this.validateGroups(commentUser, groups, session);

		// transfer to spammer group id's if neccessary
		GroupUtils.prepareGroups(groups, commentUser.isSpammer());
	}

	private <D extends DiscussionItem> void createDiscussionItem(final String interHash, final D discussionItem, final DBSession session) {
		this.prepareDiscussionItem(discussionItem.getUser(), discussionItem.getGroups(), session);
		this.getCommentDatabaseManager(discussionItem).createDiscussionItemForResource(interHash, discussionItem, session);
	}

	@SuppressWarnings("unchecked")
	private <D extends DiscussionItem> DiscussionItemDatabaseManager<D> getCommentDatabaseManager(final DiscussionItem discussionItem) {
		return (DiscussionItemDatabaseManager<D>) this.allDiscussionManagers.get(discussionItem.getClass());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.DiscussionLogicInterface#updateDiscussionItem
	 * (java.lang.String, java.lang.String, org.bibsonomy.model.DiscussionItem)
	 */
	@Override
	public void updateDiscussionItem(final String username, final String interHash, final DiscussionItem discussionItem) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, username);

		final DBSession session = this.openSession();
		try {
			final User commentUser = this.userDBManager.getUserDetails(username, session);
			discussionItem.setUser(commentUser);

			this.updateDiscussionItemForUser(interHash, discussionItem, session);
		} finally {
			session.close();
		}
	}

	private <D extends DiscussionItem> void updateDiscussionItemForUser(final String interHash, final D discussionItem, final DBSession session) {
		this.prepareDiscussionItem(discussionItem.getUser(), discussionItem.getGroups(), session);
		this.getCommentDatabaseManager(discussionItem).updateDiscussionItemForResource(interHash, discussionItem.getHash(), discussionItem, session);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.bibsonomy.model.logic.DiscussionLogicInterface#deleteDiscussionItem
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteDiscussionItem(final String username, final String interHash, final String commentHash) {
		this.permissionDBManager.ensureIsAdminOrSelf(this.loginUser, username);

		final DBSession session = this.openSession();
		try {
			final User user = this.userDBManager.getUserDetails(username, session);
			for (final DiscussionItemDatabaseManager<? extends DiscussionItem> discussionItemManager : this.allDiscussionManagers.values()) {
				if (discussionItemManager.deleteDiscussionItemForResource(interHash, user, commentHash, session)) {
					return;
				}
			}
		} finally {
			session.close();
		}
	}

	@Override
	public List<PostMetaData> getPostMetaData(final HashID hashType, final String resourceHash, final String userName, final String metaDataPluginKey) {
		final DBSession session = this.openSession();
		try {
			return this.publicationDBManager.getPostMetaData(hashType, resourceHash, userName, metaDataPluginKey, session);
		} finally {
			session.close();
		}
	}

	@Deprecated
	@Override
	public List<Tag> getTagRelation(final int start, final int end, final TagRelation relation, final List<String> tagNames) {
		// TODO Auto-generated method stub
		return null;
	}

	private void handleAdminFilters(final Set<Filter> filters) {
		/*
		 * if filter is set to spam posts admins can see public spam!
		 */
		if (ValidationUtils.safeContains(filters, FilterEntity.ADMIN_SPAM_POSTS)) {
			this.permissionDBManager.ensureAdminAccess(this.loginUser);
			// add public spam group to the groups of the loggedin users
			this.loginUser.addGroup(new Group(GroupID.PUBLIC_SPAM));
		}
	}


	@Override
	public PersonSuggestionQueryBuilder getPersonSuggestion(final String queryString) {
		return new PersonSuggestionQueryBuilder(queryString) {
			@Override
			public List<ResourcePersonRelation> doIt() {
				return DBLogic.this.personDBManager.getPersonSuggestion(this);
			}
		};
	}

	@Override
	public List<Post<BibTex>> getPublicationSuggestion(final String queryString) {
		final PublicationSuggestionQueryBuilder options = new PublicationSuggestionQueryBuilder(queryString).withNonEntityPersons(true);
		return this.publicationDBManager.getPublicationSuggestion(options);
	}

	@Override
	public void addResourceRelation(final ResourcePersonRelation resourcePersonRelation) throws ResourcePersonAlreadyAssignedException {
		this.ensureLoggedInAndNoSpammer();
		ValidationUtils.assertNotNull(resourcePersonRelation.getPerson());
		ValidationUtils.assertNotNull(resourcePersonRelation.getPerson().getPersonId());
		ValidationUtils.assertNotNull(resourcePersonRelation.getRelationType());

		final List<ResourcePersonRelation> existingRelations = this.getResourceRelations() //
				.byInterhash(resourcePersonRelation.getPost().getResource().getInterHash()) //
				.byRelationType(resourcePersonRelation.getRelationType())//
				.byAuthorIndex(Integer.valueOf(resourcePersonRelation.getPersonIndex())) //
				.getIt();
		if (existingRelations.size() > 0 ) {
			final ResourcePersonRelation existingRelation = existingRelations.get(0);
			throw new ResourcePersonAlreadyAssignedException(existingRelation);
		}

		resourcePersonRelation.setChangedBy(this.loginUser.getName());
		resourcePersonRelation.setChangedAt(new Date());
		final DBSession session = this.openSession();
		try {
			this.personDBManager.addResourceRelation(resourcePersonRelation, session);
		} finally {
			session.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removePersonRelation(java.lang.String, java.lang.String, org.bibsonomy.model.Person, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public void removeResourceRelation(final int resourceRelationId) {
		this.ensureLoggedInAndNoSpammer();
		final DBSession session = this.openSession();
		try {
			this.personDBManager.removeResourceRelation(resourceRelationId, this.loginUser.getName(), session);
		} finally {
			session.close();
		}
	}

	/**
	 * Updates the given person
	 * @param person		person object containing the new values
	 * @param operation		the desired update operation
	 */
	public void updatePerson(final Person person, final PersonUpdateOperation operation) {
		this.ensureLoggedInAndNoSpammer();
		
		if (!present(person.getPersonId())) {
			throw new ValidationException("Invalid person ID given.");
		}

		final DBSession session = this.openSession();
			
		try {
			
			// is the person claimed?
			if (person.getUser() != null) {
				if (!person.getUser().equals(this.loginUser.getName())) {
					throw new AccessDeniedException();
				}
				if (present(person.getPersonId())) {
					final Person personOld = this.personDBManager.getPersonById(person.getPersonId(), session);
					if (personOld == null) {
						throw new NoSuchElementException("person " + person.getPersonId());
					}
					if (personOld.getUser() != null && !personOld.getUser().equals(this.loginUser.getName())) {
						throw new AccessDeniedException();
					}
				}
			}
			
			// check for email, homepage - can yonly be edited if the editr claimed the person
			if (operation.equals(PersonUpdateOperation.UPDATE_EMAIL) || operation.equals(PersonUpdateOperation.UPDATE_HOMEPAGE)) {
				if (person.getUser() == null) {
					throw new AccessDeniedException();
				}
			}
			
			person.setChangeDate(new Date());
			person.setChangedBy(this.loginUser.getName());

			switch (operation) {
				case UPDATE_ORCID: 
					this.personDBManager.updateOrcid(person, session);
					break;
				case UPDATE_ACADEMIC_DEGREE:
					this.personDBManager.updateAcademicDegree(person, session);
					break;
				case UPDATE_NAMES:
					this.updatePersonNames(person, session);
					break;
				case UPDATE_COLLEGE:
					this.personDBManager.updateCollege(person, session);
					break;
				case UPDATE_EMAIL:
					this.personDBManager.updateEmail(person, session);
					break;
				case UPDATE_HOMEPAGE:
					this.personDBManager.updateHomepage(person, session);
					break;
				case UPDATE_ALL:
					this.personDBManager.updatePerson(person, session);
					this.updatePersonNames(person, session);
					break;
				default:
					throw new UnsupportedOperationException("The requested method is not yet implemented.");
			}
			
		} finally {
			session.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#createOrUpdatePerson(org.bibsonomy.model.Person)
	 * 
	 * FIXME TODO integrate into maincreateOrUpdatePerson 
	 */
	@Override
	public void createOrUpdatePerson(final Person person) {
		this.ensureLoggedInAndNoSpammer();
		final DBSession session = this.openSession();
		try {
			this.createOrUpdatePerson(person, session);
		} finally {
			session.close();
		}
	}

	private void createOrUpdatePerson(final Person person, final DBSession session) {
		this.ensureLoggedInAndNoSpammer();
		if (person.getUser() != null) {
			if (!person.getUser().equals(this.loginUser.getName())) {
				throw new AccessDeniedException();
			}
			if (present(person.getPersonId())) {
				final Person personOld = this.personDBManager.getPersonById(person.getPersonId(), session);
				if (personOld == null) {
					throw new NoSuchElementException("person " + person.getPersonId());
				}
				if (personOld.getUser() != null && personOld.getUser().equals(this.loginUser.getName()) == false) {
					throw new AccessDeniedException();
				}
			}
		}
		person.setChangeDate(new Date());
		person.setChangedBy(this.loginUser.getName());

		if (present(person.getPersonId())) {
			this.personDBManager.updatePerson(person, session);
		} else {
			this.personDBManager.createPerson(person, session);
		}
		this.updatePersonNames(person, session);
	}

	private void updatePersonNames(final Person person, final DBSession session) {
		this.ensureLoggedIn();
		if (!present(person.getNames())) {
			return;
		}
		setMainNameIfNoneSet(person);

		session.beginTransaction();
		try {
			final List<PersonName> oldNames = this.personDBManager.getPersonNames(person.getPersonId(), session);

			final Map<PersonName, PersonName> oldNamesMap = buildIdentityNamesMapFromNames(oldNames);
			final Map<PersonName, PersonName> newNamesMap = buildIdentityNamesMapFromNames(person.getNames());
			for (final PersonName oldName : oldNames) {
				final PersonName newName = newNamesMap.get(oldName);
				if (newName != null) {
					if (!newName.equalsWithDetails(oldName)) {
						newName.setChangedAt(new Date());
						newName.setChangedBy(this.loginUser.getName());
						newName.setPersonNameChangeId(oldName.getPersonNameChangeId());
						this.personDBManager.updatePersonName(newName, session);
					}
				} else {
					this.personDBManager.removePersonName(oldName.getPersonNameChangeId(), this.loginUser.getName(), session);
				}
			}
			for (final PersonName newName : person.getNames()) {
				final PersonName oldName = oldNamesMap.get(newName);
				if (oldName == null) {
					newName.setChangedAt(new Date());
					newName.setChangedBy(this.loginUser.getName());
					this.personDBManager.createPersonName(newName, session);
				}
			}
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	private static Map<PersonName, PersonName> buildIdentityNamesMapFromNames(final List<PersonName> names) {
		final Map<PersonName,PersonName> namesMap = new HashMap<>();
		for (final PersonName name : names) {
			namesMap.put(name, name);
		}
		return namesMap;
	}

	private static void setMainNameIfNoneSet(final Person person) {
		boolean mainNameFound = false;
		for (final PersonName name : person.getNames()) {
			if (name.isMain() == true) {
				if (mainNameFound == true) {
					name.setMain(false);
				} else {
					mainNameFound = true;
				}
			}
		}
		if (mainNameFound == false) {
			person.getNames().get(0).setMain(true);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonById(int)
	 */
	@Override
	public Person getPersonById(final PersonIdType idType, final String id) {
		final DBSession session = this.openSession();
		try {
			if (PersonIdType.PERSON_ID == idType) {
				return this.personDBManager.getPersonById(id, session);
			} else if (PersonIdType.DNB_ID == idType) {
				return this.personDBManager.getPersonByDnbId(id, session);
				// } else if (PersonIdType.ORCID == idType) {
				//	TODO: implement
			} else if (PersonIdType.USER == idType) {
				return this.personDBManager.getPersonByUser(id, session);
			} else {
				throw new UnsupportedOperationException("person cannot be found by it type " + idType);
			}
		} finally {
			session.close();
		}
	}
	
	/**
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonByUser(String)
	 */
	public Person getPersonByUser(final String userName) {
		final DBSession session = this.openSession();
		
		try {
			if (present(userName)) {				
				return this.personDBManager.getPersonByUser(userName, session);
			}
			return null;
		} finally {
			session.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removePersonName(int)
	 */
	@Override
	public void removePersonName(final Integer personChangeId) {
		this.ensureLoggedInAndNoSpammer();
		final DBSession session = this.openSession();
		try {
			this.personDBManager.removePersonName(personChangeId, this.loginUser.getName(), session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param byInterHash
	 * @param resourcePersonRelationsWithPosts
	 */
	private static void addToMapIfNotPresent(final Map<String, ResourcePersonRelation> byInterHash, final List<ResourcePersonRelation> resourcePersonRelationsWithPosts) {
		for (final ResourcePersonRelation rpr : resourcePersonRelationsWithPosts) {
			final String interhash = rpr.getPost().getResource().getInterHash();
			if (byInterHash.containsKey(interhash)) {
				continue;
			}
			byInterHash.put(interhash,rpr);
		}
	}

	@Override
	public void createPersonName(final PersonName personName) {
		this.ensureLoggedInAndNoSpammer();
		final DBSession session = this.openSession();
		try {
			this.personDBManager.createPersonName(personName, session);
		} finally {
			session.close();
		}
	}

	@Override
	public void linkUser(final String personId) {
		this.ensureLoggedInAndNoSpammer();
		final DBSession session = this.openSession();
		try {
			this.personDBManager.unlinkUser(this.getAuthenticatedUser().getName(), session);
			final Person person = this.personDBManager.getPersonById(personId, session);
			person.setUser(this.getAuthenticatedUser().getName());
			this.createOrUpdatePerson(person, session);
		} finally {
			session.close();
		}

	}

	@Override
	public void unlinkUser(final String username) {
		this.ensureLoggedInAndNoSpammer();
		final DBSession session = this.openSession();
		try {
			this.personDBManager.unlinkUser(username, session);
		} finally {
			session.close();
		}
	}

	@Override
	public ResourcePersonRelationQueryBuilder getResourceRelations() {
		return new ResourcePersonRelationQueryBuilder() {
			@Override
			public List<ResourcePersonRelation> getIt() {
				final List<ResourcePersonRelation> rVal = this.query();
				if (rVal != null) {
					this.postProcess(rVal);
					return rVal;
				}
				throw new UnsupportedOperationException(this.toString());
			}

			private List<ResourcePersonRelation> query() {
				final DBSession session = DBLogic.this.openSession();
				try {
					if (!this.isWithPosts() && this.isWithPersonsOfPosts()) {
						throw new IllegalArgumentException("need to fetch posts to retrieve persons of posts");
					}
					if (present(this.getInterhash())) {
						if (!this.isWithPosts() && !present(this.getAuthorIndex()) && !present(this.getPersonId()) && !present(this.getRelationType())) {
							return DBLogic.this.personDBManager.getResourcePersonRelationsWithPersonsByInterhash(this.getInterhash(), session);
						} else if (present(this.getAuthorIndex()) && present(this.getRelationType()) && !this.isWithPosts() && !this.isWithPersons() && !present(this.getPersonId())) {
							return DBLogic.this.personDBManager.getResourcePersonRelations(this.getInterhash(), this.getAuthorIndex(), this.getRelationType(), session);
						}
					} else if (present(this.getPersonId()) && !this.isWithPersons() && !present(this.getAuthorIndex()) && !present(this.getRelationType())) {
						final List<ResourcePersonRelation> rVal = DBLogic.this.personDBManager.getResourcePersonRelationsWithPosts(this.getPersonId(), DBLogic.this.loginUser, BibTex.class, session);
						for (final ResourcePersonRelation rpr : rVal) {
							SystemTagsExtractor.handleHiddenSystemTags(rpr.getPost(), DBLogic.this.loginUser.getName());
						}
						if (this.isWithPersonsOfPosts()) {
							for (final ResourcePersonRelation resourcePersonRelation : rVal) {
								final String interHash = resourcePersonRelation.getPost().getResource().getInterHash();
								final List<ResourcePersonRelation> relsOfPub = DBLogic.this.getResourceRelations().byInterhash(interHash).withPersons(true).getIt();
								resourcePersonRelation.getPost().setResourcePersonRelations(relsOfPub);
							}
						}
						return rVal;
					}
					return null;
				} finally {
					session.close();
				}
			}

			private void postProcess(final List<ResourcePersonRelation> rVal) {
				if (this.isGroupByInterhash()) {
					final Map<String, ResourcePersonRelation> byInterHash = new HashMap<>();
					addToMapIfNotPresent(byInterHash, rVal);
					rVal.clear();
					rVal.addAll(byInterHash.values());
				}
				if (this.getOrder() == ResourcePersonRelationQueryBuilder.Order.publicationYear) {
					Collections.sort(rVal, new Comparator<ResourcePersonRelation>() {
						@Override
						public int compare(final ResourcePersonRelation o1, final ResourcePersonRelation o2) {
							try {
								final int year1 = Integer.parseInt(o1.getPost().getResource().getYear().trim());
								final int year2 = Integer.parseInt(o2.getPost().getResource().getYear().trim());
								if (year1 != year2) {
									return year2 - year1;
								}
							} catch (final Exception e) {
								log.warn(e);
							}
							return System.identityHashCode(o1) - System.identityHashCode(o2);
						}
					});
				} else if (this.getOrder() != null) {
					throw new UnsupportedOperationException();
				}
			}
		};
	}


	/**
	 * 
	 * @param personID
	 * @return a list of all matches for a person
	 */
	@Override
	public List<PersonMatch> getPersonMatches(String personID) {
		final DBSession session = this.openSession();
		if (present(this.loginUser.getName())){
			return this.personDBManager.getMatchesForFilterWithUserName(session, personID, this.loginUser.getName());
		}
		return this.personDBManager.getMatchesFor(session, personID);
	}

	/**
	 * increases the deny counter of a match and denys it after a threshold is reached
	 * 
	 * @param match
	 * @return
	 */
	@Override
	public void denieMerge(PersonMatch match) {
		final DBSession session = this.openSession();
		if (present(this.loginUser.getName())) {
			this.personDBManager.denyMatch(match, session, this.loginUser.getName());
		}
	}
	
	/**
	 * performs a merge that has no conflicts
	 * @param match
	 * @return
	 */
	@Override
	public boolean acceptMerge(PersonMatch match) {
		final DBSession session = this.openSession();
		if (present(this.loginUser.getName())) {
			return this.personDBManager.mergeSimilarPersons(match, this.loginUser.getName(), session);
		}
		return false;
	}
	
	/**
	 * 
	 * @param matchID
	 * @return the match with given matchID
	 */
	@Override
	public PersonMatch getPersonMatch(int matchID) {
		final DBSession session = this.openSession();
		return personDBManager.getMatch(matchID, session);
	}

	/**
	 * resolves conflicts and performs a merge
	 * @param formMatchId of the match to merge
	 * @param map of conflict fields with new values
	 * @return
	 */
	@Override
	public Boolean conflictMerge(int formMatchId, Map<String, String> map) {
		final DBSession session = this.openSession();
		if (present(this.loginUser.getName())) {
			return this.personDBManager.conflictMerge(session, formMatchId, map, this.loginUser.getName());
		}
		return false;
	}

	/**
	 * @param personId
	 * @return returns the updated personId, if the person was merged to an other person
	 */
	@Override
	public String getForwardId(final String personId) {
		final DBSession session = this.openSession();
		return this.personDBManager.getForwardId(personId, session);
	}

	@Override
	public Statistics getStatistics(final Query query) {
		try (final DBSession session = this.openSession()) {
			return this.getStatistics(query, session);
		}
	}

	private <Q extends Query> Statistics getStatistics(final Q query, final DBSession session) {
		// cast is safe
		final StatisticsProvider<Q> statisticsProvider = (StatisticsProvider<Q>) this.allStatisticDatabaseMangers.get(query.getClass());
		return statisticsProvider.getStatistics(query, session);
	}

	@Override
	public List<Project> getProjects(final ProjectQuery builder) {
		try (final DBSession session = this.openSession()) {
			return this.projectDatabaseManager.getProjects(builder, this.loginUser, session);
		}
	}

	@Override
	public Project getProjectDetails(final String projectId) {
		final boolean admin = this.permissionDBManager.isAdmin(this.loginUser);

		try (final DBSession session = this.openSession()) {
			return this.projectDatabaseManager.getProjectDetails(projectId, admin, session);
		}
	}

	@Override
	public JobResult createProject(final Project project) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		try (final DBSession session = this.openSession()) {
			return this.projectDatabaseManager.createProject(project, this.loginUser, session);
		}
	}

	@Override
	public JobResult updateProject(final String projectId, final Project project) {
		this.permissionDBManager.ensureAdminAccess(this.getAuthenticatedUser());
		try (final DBSession session = this.openSession()) {
			return this.projectDatabaseManager.updateProject(projectId, project, this.loginUser, session);
		}
	}

	@Override
	public JobResult deleteProject(String projectId) {
		this.permissionDBManager.ensureAdminAccess(this.getAuthenticatedUser());
		try (final DBSession session = this.openSession()) {
			return this.projectDatabaseManager.deleteProject(projectId, this.loginUser, session);
		}
	}

	/**
	 * @param projectDatabaseManager the projectDatabaseManager to set
	 */
	public void setProjectDatabaseManager(ProjectDatabaseManager projectDatabaseManager) {
		this.projectDatabaseManager = projectDatabaseManager;
	}

	/**
	 * @param dbSessionFactory the dbSessionFactory to set
	 */
	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	/**
	 * @param publicationReader the publicationReader to set
	 */
	public void setPublicationReader(BibTexReader publicationReader) {
		this.publicationReader = publicationReader;
	}

	/**
	 * @param loginUser the loginUser to set
	 */
	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
}