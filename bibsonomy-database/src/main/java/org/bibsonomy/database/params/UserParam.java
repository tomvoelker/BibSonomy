package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;

/**
 * Parameters that are specific to users.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
public class UserParam extends GenericParam {

	/**
	 * a user
	 */
	private User user;
	
	/**
	 * a user relation
	 */
	private UserRelation userRelation;

	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * set user relation
	 * 
	 * @param userRelation
	 */
	public void setUserRelation(UserRelation userRelation) {
		this.userRelation = userRelation;
	}

	/**
	 * get user relation
	 * 
	 * @return the user relation
	 */
	public UserRelation getUserRelation() {
		return userRelation;
	}
}