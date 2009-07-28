package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.webapp.command.AjaxCommand;
import org.bibsonomy.webapp.util.RequestLogic;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class BasketManagerCommand extends AjaxCommand{
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
