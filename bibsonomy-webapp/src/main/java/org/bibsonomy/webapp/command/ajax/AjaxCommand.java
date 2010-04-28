package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command for Ajax requests
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AjaxCommand extends BaseCommand {

	/** what this command shall do */
	protected String action; 	
	
	/** where to forward (optionally) */
	private String forward;
	
	/** the response string */
	protected String responseString;

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the forward
	 */
	public String getForward() {
		return this.forward;
	}

	/**
	 * @param forward the forward to set
	 */
	public void setForward(String forward) {
		this.forward = forward;
	}

	/**
	 * @return the responseString
	 */
	public String getResponseString() {
		return this.responseString;
	}

	/**
	 * @param responseString the responseString to set
	 */
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}
}