package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

/** 
 * @author dzo
 * @version $Id$
 */
public class FOAFCommand extends BaseCommand {
	
	private String requestedUser;
	
	private User user;

	/**
	 * @param requestedUser the requestedUser to set
	 */
	public void setRequestedUser(final String requestedUser) {
		this.requestedUser = requestedUser;
	}

	/**
	 * @return the requestedUser
	 */
	public String getRequestedUser() {
		return requestedUser;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}	
	
}
