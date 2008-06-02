package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Database Manager for permissions
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class PermissionDatabaseManager extends AbstractDatabaseManager {

	private final static PermissionDatabaseManager singleton = new PermissionDatabaseManager();
	private final GroupDatabaseManager groupDb;

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
	 * @param start
	 * @param end
	 * @param itemType
	 */
	public void checkStartEnd(final Integer start, final Integer end, final String itemType) {
		if (end > 1000) {
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
		ensureWriteAccess(loginUser, post.getUser().getName());
	}

	/**
	 * Check if the logged in user has write access to the given document.
	 * 
	 * @param document
	 * @param loginUser
	 */
	public void ensureWriteAccess(final Document document, final User loginUser) {
		// delegate write access check
		ensureWriteAccess(loginUser, document.getUserName());
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
		final List<Group> postGroups = post.getGroups();

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
	 * <li>if the logged-in user is a member of the requested group
	 * </ul>
	 * 
	 * @param loginUser - 
	 * 				the name of the logged-in user
	 * @param grouping -
	 * 				the requested grouping (GROUP or USER) 
	 * @param groupingName -
	 * 				the name of the requested user / group
	 * @param session -
	 *           	DB session
	 * @return <code>true</code> if the logged-in user is allowed to access the
	 *         documents of the requested user / group.
	 */
	public boolean isAllowedToAccessUsersOrGroupDocuments(final User loginUser, final GroupingEntity grouping, final String groupingName, final DBSession session) {
		if (grouping != null) {
			// user
			if (grouping.equals(GroupingEntity.USER)) {
				if (loginUser.getName() != null) {
					return loginUser.getName().equals(groupingName);
				}
			}
			// group
			if (grouping.equals(GroupingEntity.GROUP)) {
				// check group membership
				return UserUtils.getListOfGroupIDs(loginUser).contains( this.groupDb.getGroupIdByGroupName(groupingName, session) );
			}
		}
		return false;
	}

	/**
	 * Ensures that the user is an admin.
	 * 
	 * @param loginUser
	 */
	public void ensureAdminAccess(final User loginUser) {
		if (present(loginUser.getName()) == false || loginUser.getRole().equals(Role.ADMIN) == false) {
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
}