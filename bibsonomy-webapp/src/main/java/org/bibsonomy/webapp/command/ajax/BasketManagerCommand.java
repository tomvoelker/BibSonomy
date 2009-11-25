package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.webapp.command.AjaxCommand;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class BasketManagerCommand extends AjaxCommand{
	private String hash;
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
