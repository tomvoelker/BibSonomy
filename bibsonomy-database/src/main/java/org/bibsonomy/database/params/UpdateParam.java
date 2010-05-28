package org.bibsonomy.database.params;

import org.bibsonomy.model.User;

/**
 * @author philipp
 * @version $Id$
 * @param <T>
 */
public class UpdateParam<T> extends LoggingParam<T> {

	private User user;
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
