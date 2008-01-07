package org.bibsonomy.database.managers;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;

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
	 * @param loginUserName
	 */
	public void ensureWriteAccess(final Post<?> post, final String loginUserName) {
		// delegate write access check		
		ensureWriteAccess(loginUserName, post.getUser().getName());
	}
	
	/**
	 * @param loginUserName
	 * @param userName 
	 */
	public void ensureWriteAccess(final String loginUserName, final String userName){
		if (!loginUserName.toLowerCase().equals(userName.toLowerCase())){
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}
	
	/**
	 * @param document
	 * @param loginUserName
	 */
	public void ensureWriteAccess(final Document document, final String loginUserName) {
		// delegate write access check
		ensureWriteAccess(loginUserName, document.getUserName());
	}
	
	/** Ensures that the user is an admin.
	 * @param loginUserName
	 */
	public void ensureAdminAccess(final String loginUserName) {
		// TODO: implement.
	}

}