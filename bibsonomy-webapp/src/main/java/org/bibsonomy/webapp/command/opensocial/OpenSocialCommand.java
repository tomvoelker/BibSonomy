package org.bibsonomy.webapp.command.opensocial;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author fei
 * @version $Id$
 */
public class OpenSocialCommand extends BaseCommand {
	/** Security token used for authentication */
	private String securityToken;

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public String getSecurityToken() {
		return securityToken;
	}
}
