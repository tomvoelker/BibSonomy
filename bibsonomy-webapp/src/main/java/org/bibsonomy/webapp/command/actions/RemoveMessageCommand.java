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
	private String hash;
	private String user;
	private boolean clear;

	
	/**
	 * @return true if user wishes to delete all from his inbox
	 */
	public boolean isClear() {
		return this.clear;
	}

	/**
	 * @param clear
	 */
	public void setClear(boolean clear) {
		this.clear = clear;
	}

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
	public String getHash() {
		return this.hash;
	}

	/**
	 * @param hash
	 */
	public void setHash(final String hash) {
		this.hash = hash;
	}

	
	
}
