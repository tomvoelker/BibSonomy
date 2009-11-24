package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * This Commands information: which Message is to be removed from the inbox 
 * @author sdo
 * @version $Id$
 */
public class RemoveMessageCommand extends BaseCommand implements Serializable {
	private static final long serialVersionUID = -6623936347565283765L;
	private String requestedResourceHash;
	private String user;

	/**
	 * @return String
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * @param user
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	/**
	 * @return String
	 */
	public String getRequestedResourceHash() {
		return this.requestedResourceHash;
	}

	/**
	 * @param requestedResourceHash
	 */
	public void setRequestedResourceHash(final String requestedResourceHash) {
		this.requestedResourceHash = requestedResourceHash;
	}

	
	
}
