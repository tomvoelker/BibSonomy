package org.bibsonomy.webapp.command;

/**
 * Command for Ajax requests
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AjaxCommand extends BaseCommand {

	/** what this command shall do */
	private String action; 
	
	/** user for which we want to add a group or mark as spammer */
	private String userName; 
	
	/** the response string */
	private String response;
	
	public String getAction() {
		return this.action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getResponse() {
		return this.response;
	}
	public void setResponse(String response) {
		this.response = response;
	}		
}