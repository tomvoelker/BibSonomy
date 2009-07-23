package org.bibsonomy.webapp.command;


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
	
	public String getForward() {
		return this.forward;
	}
	public void setForward(String forward) {
		this.forward = forward;
	}
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