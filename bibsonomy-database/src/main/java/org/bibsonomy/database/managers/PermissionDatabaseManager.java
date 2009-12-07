package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;


/**
 * Database Manager for permissions
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class PermissionDatabaseManager extends AbstractDatabaseManager {

	private final static PermissionDatabaseManager singleton = new PermissionDatabaseManager();
	private final GroupDatabaseManager groupDb;

	private static final Log log = LogFactory.getLog(PermissionDatabaseManager.class);

	private PermissionDatabaseManager() {
		this.groupDb = GroupDatabaseManager.getInstance();
	}

	/**
	 * @return PermissionDatabaseManager
	 */
	public static PermissionDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Checks whether the requested start- / end-values are OK
	 * 
	 * @param loginUser	
	 * @param start
	 * @param end
	 * @param itemType
	 */
	public void checkStartEnd(final User loginUser, final Integer start, final Integer end, final String itemType) {
		if (!Role.ADMIN.equals(loginUser.getRole()) && end > 1000) {
			throw new ValidationException("You are not authorized to retrieve more than the last 1000 " + itemType + " items.");
		}
	}

	/**
	 * Check if the logged in user has write access to the given post.
	 * 
	 * @param post
	 * @param loginUser
	 */
	public void ensureWriteAccess(final Post<? extends Resource> post, final User loginUser) {
		// delegate write access check
		this.ensureIsAdminOrSelf(loginUser, post.getUser().getName());
	}

	/**
	 * Throws an exception if the loginUser.getName and userName doesn't match.
	 * 
	 * @param loginUser
	 * @param userName
	 */
	public void ensureWriteAccess(final User loginUser, final String userName) {
		if (loginUser.getName() == null || !loginUser.getName().toLowerCase().equals(userName.toLowerCase())) {
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}

	/**
	 * This method checks, whether the user is allowed to access the posts
	 * documents. The user is allowed to access the documents,
	 * 
	 * 
	 * <ul>
	 * <li>if userName = post.userName</li> 
	 * <li>if the post is public and the posts user is together with the user
	 * in a group, which allows to share documents, or
	 * <li>if the post is viewable for a specific group, in which both users
	 * are and which allows to share documents.
	 * </ul>
	 * 
	 * TODO: eventually, we don't want to have the post as parameter, but only
	 * its groups?
	 * 
	 * @param userName -
	 *            the name of the user which wants to access the posts
	 *            documents.
	 * @param post -
	 *            the post which contains the documents the user wants to
	 *            access.
	 * @param session -
	 *            a DBSession.
	 * @return <code>true</code> if the user is allowed to access the
	 *         documents of the post.
	 */
	public boolean isAllowedToAccessPostsDocuments(final String userName, final Post<? extends Resource> post, final DBSession session) {
		final String postUserName = post.getUser().getName();
		/*
		 * if userName = postUserName, return true
		 */
		if ((userName != null && userName.equalsIgnoreCase(postUserName))) return true;
		/*
		 * else: check groups stuff ....
		 */
		final Collection<Group> postGroups = post.getGroups();

		// Get the groups in which both users are.
		final List<Group> commonGroups = this.groupDb.getCommonGroups(userName, postUserName, session);

		// Construct the public group.
		// TODO: this should better be done in a GroupFactory!
		// ----> what about GroupUtils?
		final Group publicGroup = new Group();
		publicGroup.setGroupId(GroupID.PUBLIC.getId());

		// Find a common group of both users, which allows to share documents.
		for (final Group group : commonGroups) {
			if (group.isSharedDocuments()) {
				// both users are in a group which allows to share documents
				if (postGroups.contains(publicGroup) || postGroups.contains(group)) {
					return true;
				}
			}
		}
		return false;
	}



	/**
	 * This method checks whether the logged-in user is allowed to see documents of 
	 * the requested user or a requested group. The user is allowed to access the documents,
	 *
	 * <ul>
	 * <li>if the logged-in user requests his own posts, i.e. loginUser = requestedUser
	 * <li>if the logged-in user is a member of the requested group AND the group allows shared documents.
	 * </ul>
	 * 
	 * @param loginUser - 
	 * 				the name of the logged-in user
	 * @param grouping -
	 * 				the requested grouping (GROUP or USER) 
	 * @param groupingName -
	 * 				the name of the requested user / group
	 * @param filter -
	 *              the requested filter entity
	 * @param session -
	 *           	DB session
	 * @return <code>true</code> if the logged-in user is allowed to access the
	 *         documents of the requested user / group.
	 */
	public boolean isAllowedToAccessUsersOrGroupDocuments(final User loginUser, final GroupingEntity grouping, final String groupingName, FilterEntity filter, final DBSession session) {
		boolean isAllowed = false;
		if (grouping != null) {
			// user
			if (grouping.equals(GroupingEntity.USER)) {
				if (loginUser.getName() != null) {
					isAllowed = loginUser.getName().equals(groupingName);
					if (!isAllowed && FilterEntity.JUST_PDF.equals(filter)) {
						throw new ValidationException("error.pdf_only_not_authorized_for_user");
					}
				}
			}
			// group
			if (grouping.equals(GroupingEntity.GROUP)) {
				final Group group = this.groupDb.getGroupByName(groupingName, session);
				/*
				 * check group membership and if the group allows shared documents
				 */
				isAllowed = group != null && UserUtils.getListOfGroupIDs(loginUser).contains(group.getGroupId()) && group.isSharedDocuments();
				if (!isAllowed && FilterEntity.JUST_PDF.equals(filter)) {
					throw new ValidationException("error.pdf_only_not_authorized_for_group");
				}
			}
		}
		return isAllowed;
	}

	/**
	 * Ensures that the user is member of given group.
	 * 
	 * @param userName 
	 * @param groupName 
	 * @param session 
	 */
	public void ensureMemberOfNonSpecialGroup(final String userName, final String groupName, DBSession session) {
		if( GroupID.isSpecialGroup(groupName))
			throw new ValidationException("Special groups not allowed for this system tag.");
		final Integer groupID = this.groupDb.getGroupIdByGroupNameAndUserName(groupName, userName, session);
		if( groupID==GroupID.INVALID.getId() )
			throw new ValidationException("You are not authorized to perform the requested operation.");
	}
	

	/**
	 * @param groupName
	 * @return if a group is a special group
	 */
	public boolean isSpecialGroup (final String groupName) {
		return GroupID.isSpecialGroup(groupName);
	}
	

	/**
	 * @param userName
	 * @param groupName
	 * @param session
	 * @return if the given user is a member of the specified group
	 */
	public boolean isMemberOfGroup(final String userName, final String groupName, DBSession session) {
		final Integer groupID = this.groupDb.getGroupIdByGroupNameAndUserName(groupName, userName, session);
		if( groupID==GroupID.INVALID.getId() ) {
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
		if (present(loginUser.getName()) == false || Role.ADMIN.equals(loginUser.getRole()) == false) {
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}

	/**
	 * Check maximum number of allowed tags per request
	 * 
	 * @param tags
	 * @return true if maximum size is exceeded, false otherwise
	 */
	public boolean exceedsMaxmimumSize(final List<String> tags) {
		return tags != null && tags.size() >= 10;
	}

	/**
	 * Check permissions to decide if filter can be set
	 * 
	 * @param loginUser
	 * 		- the user whose permissions need to be checked
	 * @param filter 
	 * 	    - the filter under question
	 * @return <code>true</code> if the logged-in user is allowed to set the specific 
	 * filter
	 */
	public boolean checkFilterPermissions(FilterEntity filter, User loginUser){

		if (filter == null) return false;

		switch (filter){
		case ADMIN_SPAM_POSTS:
			// Admin_SPAM_POSTS
			if (Role.ADMIN.equals(loginUser.getRole())){
				return true;
			} 
		}
		return false; 
	}

	/**
	 * Checks, if the given login user is either an admin, or the user requested
	 * by user name.
	 * 
	 * @param loginUser - the logged in user.
	 * @param userName - the name of the requested user.
	 * @return <code>true</code> if loginUser is an admin or userName.
	 */
	public boolean isAdminOrSelf(final User loginUser, final String userName) {
		return (
				(loginUser.getName() != null && loginUser.getName().equals(userName)) // loginUser = userName  
				||
				Role.ADMIN.equals(loginUser.getRole())                                // loginUser is admin
		);
	}
	
	/**
	 * if {@link #isAdminOrSelf(User, String)} returns false this method throws a validation exception
	 * @param loginUser
	 * @param userName
	 */
	public void ensureIsAdminOrSelf(final User loginUser, final String userName) {
		if (!this.isAdminOrSelf(loginUser, userName)) {
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}


	/**
	 * @FIXME WENN DIE RICHTIGEN GRUPPENADMINS EXISTIEREN MUSS DIESE FUNKTION GEÄNDERT WERDEN 
	 * @param loginUser
	 * @param group
	 * @return loginUser equals group.getName
	 */
	public boolean userIsGroupAdmin(User loginUser, Group group){
		/*
		 * user name == group name
		 */
		return loginUser.getName().equals(group.getName());
	}
	
	
	/**
	 * Checks if a user relationship between the logged-in user 
	 * and a requested user may be created.
	 * 
	 * @param loginUser - the logged-in user
	 * @param requestedUser - the requested user
	 * @param relation - the relation to be created
	 * @return true if everyhing is OK and the relationship may be created
	 * 
	 * 
	 */
	/*
	 * FIXME: Why do we need loginUser and relation?
	 */
	public boolean checkUserRelationship(User loginUser, User requestedUser, UserRelation relation) {
		if (!present(requestedUser)) {
			return false;
		}
		if ("dblp".equalsIgnoreCase(requestedUser.getName())) {
			throw new ValidationException("error.relationship_with_dblp");
		}
		return true;
	}
}