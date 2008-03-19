package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

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
	 * Check if the logged in user has write access to the given Post
	 * 
	 * @param post
	 * @param loginUser
	 */
	public void ensureWriteAccess(final Post<?> post, final User loginUser) {
		// delegate write access check		
		ensureWriteAccess(loginUser, post.getUser().getName());
	}
	
	/**
	 * @param loginUser
	 * @param userName 
	 */
	public void ensureWriteAccess(final User loginUser, final String userName){
		if (loginUser.getName() == null || !loginUser.getName().toLowerCase().equals(userName.toLowerCase())){
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}
	
	/**
	 * @param document
	 * @param loginUser
	 */
	public void ensureWriteAccess(final Document document, final User loginUser) {
		// delegate write access check
		ensureWriteAccess(loginUser, document.getUserName());
	}
	
	/** This method checks, whether the user is allowed to access the posts documents.
	 * The user is allowed to access the documents, 
	 * 
	 * <ul>
	 * <li>if the post is public and the posts 
	 * user is together with the user in a group, which allows to share documents, or 
	 * <li>if the post is viewable for a specific group, in which both users are and 
	 * which allows to share documents.
	 * </ul>  
	 * 
	 * TODO: eventually, we don't want to have the post as parameter, but only
	 * its groups?
	 * 
	 * @param userName - the name of the user which wants to access the posts documents.
	 * @param post - the post which contains the documents the user wants to access.
	 * @param session - a DBSession.
	 * @return <code>true</code> if the user is allowed to access the documents of the post.
	 */
	public boolean isAllowedToAccessPostsDocuments(final String userName, final Post<? extends Resource> post, final DBSession session) {
		final String postUserName = post.getUser().getName();
		final List<Group> postGroups = post.getGroups();
		/*
		 * Get the groups in which both users are.
		 */
		final List<Group> commonGroups = groupDb.getCommonGroups(userName, postUserName, session);
		/*
		 * Construct the public group. TODO: this should better be done in a GroupFactory!
		 */
		final Group publicGroup = new Group();
		publicGroup.setGroupId(GroupID.PUBLIC.getId());
		/*
		 * Find a common group of both users, which allows to share documents. 
		 */
		for (final Group group: commonGroups) {
			if (group.isSharedDocuments()) {
				/*
				 * both users are in a group which allows to share documents
				 */
				if (postGroups.contains(publicGroup) || postGroups.contains(group)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/** Ensures that the user is an admin.
	 * @param loginUser
	 */
	public void ensureAdminAccess(final User loginUser) {
		if (loginUser.getName() == null || !loginUser.getRole().equals(Role.ADMIN)) { 
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}
	
	/**
	 * Check maximum number of allowed tags per request
	 * 
	 * @param tags
	 * @return true if maximum size is exceeded, false otherwise
	 */
	public Boolean exceedsMaxmimumSize (List<String> tags) {
		return tags != null && tags.size() > 10;
	}

}