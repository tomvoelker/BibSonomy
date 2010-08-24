package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command for remote authentication.
 *
 * @author Dominik Benz
 * @version $Id$
 */
public class RemoteAuthCommand extends BaseCommand {
	
	/** the authentication URL (including the auth key) to the remote host */
	private String authUrl;
	
	/**	the URL to the remote host requesting authentication */
	private String reqUrl;
		
	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public String getReqUrl() {
		return reqUrl;
	}
	
}
