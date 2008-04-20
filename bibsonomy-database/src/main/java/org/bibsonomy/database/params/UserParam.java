package org.bibsonomy.database.params;

import org.bibsonomy.model.User;

/**
 * Parameters that are specific to users.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
public class UserParam extends GenericParam {

	private User user;

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
}