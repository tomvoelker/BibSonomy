/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.UserUtils;

/**
 * Database Manager for permissions
 * 
 * @author Dominik Benz
 */
public class PermissionDatabaseManager extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(PermissionDatabaseManager.class);
	
	private final static PermissionDatabaseManager singleton = new PermissionDatabaseManager();

	/**
	 * @return PermissionDatabaseManager
	 */
	public static PermissionDatabaseManager getInstance() {
		return singleton;
	}

	private final GroupDatabaseManager groupDb;
	private final GeneralDatabaseManager generalDb;

	private PermissionDatabaseManager() {
		this.groupDb = GroupDatabaseManager.getInstance();
		this.generalDb = GeneralDatabaseManager.getInstance();
	}

	/**
	 * Checks whether the requested start- / end-values are OK
	 * 
	 * @param loginUser
	 * @param start
	 *            TODO: unused
	 * @param end
	 * @param itemType
	 */
	public void checkStartEnd(final User loginUser, final int start, final int end, final String itemType) {
		if (!this.isAdmin(loginUser) && (end - start > PostLogicInterface.MAX_QUERY_SIZE)) {
			throw new AccessDeniedException("You are not authorized to retrieve more than " + PostLogicInterface.MAX_QUERY_SIZE + " " + itemType + " items at a time.");
		}
	}

	/**
	 * Check if the logged in user has write access to the given post.
	 * 
	 * @param post
	 * @param loginUser
	 */
	public void ensureWriteAccess(final Post<? extends Resource> post, final User loginUser) {
		if (post.getResource() instanceof GoldStandard<?>) {
			// only regular users (no spammers) may modify or create Community
			// Posts
			if (loginUser.isSpammer()) {
				throw new AccessDeniedException("You are not authorized to modify this post.");
			}
		} else {
			// delegate write access check
			this.ensureIsAdminOrSelf(loginUser, post.getUser().getName());
		}
	}

	/**
	 * Throws an exception if the loginUser.getName and userName doesn't match.
	 * 
	 * @param loginUser
	 * @param userName
	 */
	public void ensureWriteAccess(final User loginUser, final String userName) {
		if ((loginUser.getName() == null) || !loginUser.getName().toLowerCase().equals(userName.toLowerCase())) {
			throw new AccessDeniedException();
		}
	}

	/**
	 * This method checks, whether the user is allowed to access the posts
	 * documents. The user is allowed to access the documents,
	 * 
	 * 
	 * <ul>
	 * <li>if userName = post.userName</li>
	 * <li>if the post is public and the posts user is together with the user in
	 * a group, which allows to share documents, or
	 * <li>if the post is viewable for a specific group, in which both users are
	 * and which allows to share documents.
	 * </ul>
	 * 
	 * TODO: eventually, we don't want to have the post as parameter, but only
	 * its groups?
	 * 
	 * @param userName
	 *            - the name of the user which wants to access the posts
	 *            documents.
	 * @param post
	 *            - the post which contains the documents the user wants to
	 *            access.
	 * @param session
	 *            - a DBSession.
	 * @return <code>true</code> if the user is allowed to access the documents
	 *         of the post.
	 */
	public boolean isAllowedToAccessPostsDocuments(final String userName, final Post<? extends Resource> post, final DBSession session) {
		final String postUserName = post.getUser().getName();
		/*
		 * if userName = postUserName, return true
		 */
		if (((userName != null) && userName.equalsIgnoreCase(postUserName))) {
			return true;
		}
		/*
		 * else: check groups stuff ....
		 */
		final Collection<Group> postGroups = post.getGroups();

		/*
		 * Get the groups in which both users are. It is important 
		 * to have postUserName as the second user, we will get 
		 * his userSharedDocuments value in the group !
		 */
		final List<Group> commonGroups = this.groupDb.getCommonGroups(userName, postUserName, session);

		/*
		 * Construct the public group.
		 */
		final Group publicGroup = GroupUtils.getPublicGroup();

		/*
		 * Find a common group of both users, which allows to share documents.
		 */
		for (final Group group : commonGroups) {
			if (group.isSharedDocuments()) {
				// both users are in a group which allows to share documents
				if (postGroups.contains(publicGroup) || postGroups.contains(group)) {
					// check if postUserName allows to share documents
					final GroupMembership memberShip = group.getGroupMembershipForUser(postUserName);
					if (memberShip.isUserSharedDocuments()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * This method checks whether the logged-in user is allowed to see documents
	 * of the requested user or a requested group. The user is allowed to access
	 * the documents,
	 * 
	 * <ul>
	 * <li>if the logged-in user requests his own posts, i.e. loginUser =
	 * requestedUser
	 * <li>if the logged-in user is a member of the requested group AND the
	 * group allows shared documents.
	 * </ul>
	 * 
	 * @param loginUser
	 *            - the name of the logged-in user
	 * @param grouping
	 *            - the requested grouping (GROUP or USER)
	 * @param groupingName
	 *            - the name of the requested user / group
	 * @param filter
	 *            - the requested filter entity
	 * @param session
	 *            - DB session
	 * @return <code>true</code> if the logged-in user is allowed to access the
	 *         documents of the requested user / group.
	 */
	public boolean isAllowedToAccessUsersOrGroupDocuments(final User loginUser, final GroupingEntity grouping, final String groupingName, final FilterEntity filter, final DBSession session) {
		if (grouping != null) {
			switch (grouping) {
			case USER:
				final String loggedinUserName = loginUser.getName();
				if (loggedinUserName != null) {
					if (loggedinUserName.equals(groupingName)) {
						return true;
					}
				}
				
				final List<Group> commonGroups = this.groupDb.getCommonGroups(loginUser.getName(), groupingName, session);
				/*
				 * Find a common group of both users, which allows to share documents.
				 */
				for (final Group group : commonGroups) {
					if (group.isSharedDocuments()) {
						final GroupMembership memberShip = group.getGroupMembershipForUser(groupingName);
						if (memberShip.isUserSharedDocuments()) {
							return true;
						}
					}
				}
				return false;
			case GROUP:
				final Group group = this.groupDb.getGroupByName(groupingName, session);
				/*
				 * check group membership and if the group allows shared
				 * documents
				 */
				return (group != null) && UserUtils.getListOfGroupIDs(loginUser).contains(group.getGroupId()) && group.isSharedDocuments();
			default:
				log.debug("grouping '" + grouping + "' not supported");
				break;
			}
		}
		return false;
	}

	/**
	 * checks if the loginUser is allowed to access the profile of user
	 * 
	 * @param user
	 * @param loginUser
	 * @param session
	 * @return <code>true</code> iff loginUser is allowed to access users
	 *         profiles
	 */
	public boolean isAllowedToAccessUsersProfile(final User user, final User loginUser, final DBSession session) {
		if (!present(user)) {
			return false;
		}

		/*
		 * check if user is self or admin
		 */
		if (this.isAdminOrSelf(loginUser, user.getName())) {
			return true;
		}

		/*
		 * get privacy level of user from database and respect it
		 */
		ProfilePrivlevel privacyLevel = ProfilePrivlevel.PRIVATE; // private is
																	// default
																	// setting

		/*
		 * if the settings weren't loaded yet, load the profile privacy setting
		 * now
		 */
		if (!present(user.getSettings()) || !present(user.getSettings().getProfilePrivlevel())) {
			final ProfilePrivlevel result = this.queryForObject("getProfilePrivlevel", user, ProfilePrivlevel.class, session);

			if (present(result)) {
				privacyLevel = result;
			}
		} else {
			privacyLevel = user.getSettings().getProfilePrivlevel();
		}

		switch (privacyLevel) {
		case PUBLIC:
			return true;
		case PRIVATE:
			return false;
		case FRIENDS:
			return this.generalDb.isFriendOf(loginUser.getName(), user.getName(), session);
		}

		return false;
	}

	/**
	 * Ensures that the user is member of given group.
	 * 
	 * @param userName
	 * @param groupName
	 * @param session
	 */
	public void ensureMemberOfNonSpecialGroup(final String userName, final String groupName, final DBSession session) {
		if (GroupID.isSpecialGroup(groupName)) {
			throw new ValidationException("Special groups not allowed for this system tag.");
		}
		final Integer groupID = this.groupDb.getGroupIdByGroupNameAndUserName(groupName, userName, session);
		if (groupID == GroupID.INVALID.getId()) {
			throw new AccessDeniedException();
		}
	}

	/**
	 * @param groupName
	 * @return if a group is a special group
	 */
	public boolean isSpecialGroup(final String groupName) {
		return GroupID.isSpecialGroup(groupName);
	}

	/**
	 * @param userName
	 * @param groupName
	 * @param session
	 * @return if the given user is a member of the specified group
	 */
	public boolean isMemberOfGroup(final String userName, final String groupName, final DBSession session) {
		final Integer groupID = this.groupDb.getGroupIdByGroupNameAndUserName(groupName, userName, session);
		if (groupID == GroupID.INVALID.getId()) {
			return false;
		}
		return true;
	}

	/**
	 * Ensures that the user is an admin.
	 * 
	 * @param loginUser
	 */
	public void ensureAdminAccess(final User loginUser) {
		if (!present(loginUser.getName()) || !this.isAdmin(loginUser)) {
			throw new AccessDeniedException();
		}
	}

	/**
	 * Check whether the ResourceSearch (Lucene Index) should be used for the
	 * amount of tags in the query
	 * 
	 * @param i
	 * @return true if maximum size is exceeded, false otherwise
	 */
	public boolean useResourceSearchForTagQuery(final int i) {
		return i >= PostLogicInterface.MAX_TAG_SIZE;
	}
	
	/**
	 * Checks, if the given login user is either an admin, or the user requested
	 * by user name.
	 * 
	 * @param loginUser
	 *            - the logged in user.
	 * @param userName
	 *            - the name of the requested user.
	 * @return <code>true</code> if loginUser is an admin or userName.
	 */
	public boolean isAdminOrSelf(final User loginUser, final String userName) {
		return ((present(loginUser.getName()) && loginUser.getName().equals(userName)) // loginUser
																						// =
																						// userName
		|| this.isAdmin(loginUser) // loginUser is admin
		);
	}
	
	/**
	 * @param loginUser
	 * @param userName
	 * @return <code>true</code> if loginUser is an admin or userName or at least an admin of the group
	 */
	public boolean isAdminOrGroupAdminOrSelf(User loginUser, String userName) {
		return isGroupAdmin(loginUser, userName) || isAdminOrSelf(loginUser, userName);
	}
	
	/**
	 * Checks, if the given login user is either an admin, or the user requested
	 * by user name or at least a moderator of the group if userName is a group
	 * 
	 * @param loginUser
	 * @param userName
	 * @return <code>true</code> if loginUser is an admin or userName or at least a moderator of the group
	 */
	public boolean isAdminOrGroupModeratorOrSelf(final User loginUser, final String userName) {
		return isAdminOrGroupAdminOrSelf(loginUser, userName) || isGroupModerator(loginUser, userName);
	}

	/**
	 * @param loginUser
	 * @param userName
	 * @return
	 */
	private boolean isGroupModerator(User loginUser, String userName) {
		return this.userHasGroupRole(loginUser, userName, GroupRole.MODERATOR);
	}

	/**
	 * @param loginUser
	 * @param userName
	 * @return
	 */
	private boolean isGroupAdmin(User loginUser, String userName) {
		return this.userHasGroupRole(loginUser, userName, GroupRole.ADMINISTRATOR);
	}

	/**
	 * Checks if the given user is an admin.
	 * 
	 * @param loginUser
	 * @return <code>true</code> iff user is admin
	 */
	public boolean isAdmin(final User loginUser) {
		return Role.ADMIN.equals(loginUser.getRole());
	}

	/**
	 * if {@link #isAdminOrSelf(User, String)} returns false this method throws
	 * a validation exception
	 * 
	 * @param loginUser
	 * @param userName
	 */
	public void ensureIsAdminOrSelf(final User loginUser, final String userName) {
		if (!this.isAdminOrSelf(loginUser, userName)) {
			throw new AccessDeniedException();
		}
	}
	
	/**
	 * @param loginUser
	 * @param username
	 */
	public void ensureIsAdminOrGroupAdminOrSelf(User loginUser, String username) {
		if (!this.isAdminOrGroupAdminOrSelf(loginUser, username)) {
			throw new AccessDeniedException();
		}
	}

	/**
	 * Checks if a user has the specified role for the given group.
	 * 
	 * @param loginUser
	 * @param groupName
	 * @param role 
	 * @return if role equals the users role
	 */
	public boolean userHasGroupRole(final User loginUser, final String groupName, GroupRole role) {
		for (final Group g : loginUser.getGroups()) {
			if (g.getName().equals(groupName)) {
				final GroupMembership membership = g.getGroupMembershipForUser(loginUser.getName());
				if (role.equals(membership.getGroupRole())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * FIXME: Why do we need relation?
	 * 
	 * Checks if a user relationship between the logged-in user and a requested
	 * user may be created.
	 * 
	 * @param loginUser
	 *            - the logged-in user
	 * @param relation
	 *            - the relation to be created
	 * @param tag
	 *            TODO
	 * @param targetUser
	 *            - the target user
	 * @return true if everything is OK and the relationship may be created
	 * 
	 * 
	 */
	public boolean checkUserRelationship(final User loginUser, final User targetUser, final UserRelation relation, final String tag) {
		/*
		 * when we add an internal relation, the target user must exist (and
		 * some special users like 'dblp' are not allowed)
		 */
		if (relation.isInternal()) {
			if (!present(targetUser.getName())) {
				throw new ValidationException("error.relationship_with_nonexisting_user");
			}
			if (UserUtils.isDBLPUser(targetUser)) {
				throw new ValidationException("error.relationship_with_dblp");
			}
			if (loginUser.isSpammer()) {
				throw new ValidationException("error.relationship_from_spammer");
			}
		}
		return true;
	}

}