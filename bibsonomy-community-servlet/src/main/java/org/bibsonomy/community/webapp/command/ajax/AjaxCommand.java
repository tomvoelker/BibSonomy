package org.bibsonomy.community.webapp.command.ajax;


/**
 * Command for Ajax requests
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AjaxCommand {

	/** what this command shall do */
	protected String action; 	
	
	/** the response string */
	protected String responseString;
	
	public String getAction() {
		return this.action;
	}
	public void setAction(String action) {
		this.action = action;
	}	
	public String getResponseString() {
		return this.responseString;
	}
	public void setResponseString(String response) {
		this.responseString = response;
	}	
}