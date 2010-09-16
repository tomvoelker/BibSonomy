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
	
	/** path for the remote application to forward to after logging in */
	private String forwardPath;
	
	/** the IP for which the auth-key is valid */
	private String ip;
	
	/** the nr. of minutes this authKey is valid */
	private int validPeriod;
	
	/** a (more or less) secret key which will be needed to decrypt the authKey */
	private String s;
			
	// ----------------------------------------------------------
	// getter - setter
	// ---------------------------------------------------------

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
	
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getValidPeriod() {
		return this.validPeriod;
	}

	public void setValidPeriod(int validPeriod) {
		this.validPeriod = validPeriod;
	}

	public void setForwardPath(String forwardPath) {
		this.forwardPath = forwardPath;
	}

	public String getForwardPath() {
		return forwardPath;
	}	

	public void setS(String s) {
		this.s = s;
	}

	public String getS() {
		return s;	
	
}
