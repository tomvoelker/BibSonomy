package org.bibsonomy.database.managers;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * Database Manager for permissions
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class PermissionDatabaseManager extends AbstractDatabaseManager {

	private final static PermissionDatabaseManager singleton = new PermissionDatabaseManager();

	private PermissionDatabaseManager() {
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
	
	/** Ensures that the user is an admin.
	 * @param loginUser
	 */
	public void ensureAdminAccess(final User loginUser) {
		// TODO: implement.
	}

}