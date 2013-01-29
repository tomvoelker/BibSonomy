package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.user.remote.SamlRemoteUserId;

/**
 * @author jensi
 * @version $Id$
 */
public class SamlUserIDRegistrationCommand extends UserIDRegistrationCommand {
	private static final long serialVersionUID = -2989822342885077454L;

	private SamlRemoteUserId samlId = new SamlRemoteUserId();

	/**
	 * @return the samlId
	 */
	public SamlRemoteUserId getSamlId() {
		return this.samlId;
	}

	/**
	 * @param samlId the samlId to set
	 */
	public void setSamlId(SamlRemoteUserId samlId) {
		this.samlId = samlId;
	}
}
