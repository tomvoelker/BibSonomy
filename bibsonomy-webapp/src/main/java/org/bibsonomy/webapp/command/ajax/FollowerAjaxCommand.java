package org.bibsonomy.webapp.command.ajax;


/**
 * @author Christian Kramer
 * @version $Id$
 */
public class FollowerAjaxCommand extends AjaxCommand {
	private String requestedUserName;

	/**
	 * 
	 * @return requested username
	 */
	public String getRequestedUserName() {
		return this.requestedUserName;
	}

	/**
	 * 
	 * @param userName
	 */
	public void setRequestedUserName(String userName) {
		this.requestedUserName = userName;
	}
}
