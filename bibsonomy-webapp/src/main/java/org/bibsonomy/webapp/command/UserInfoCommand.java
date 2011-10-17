package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

/** 
 * @author dzo
 * @version $Id$
 */
public class UserInfoCommand extends BaseCommand {
	
	private String requestedUser;
	private String format;

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
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(final String format) {
		this.format = format;
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
