package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;

/**
 * A {@link NameSpacedNameMapping} implementation that uses the bibsonomy {@link LogicInterface} as its backend.
 * @author jensi
 * @version $Id$
 */
public class LogicInterfaceUserNameMapping implements NameSpacedNameMapping<SamlRemoteUserId> {

	private LogicInterface logic;

	/**
	 * @return the {@link LogicInterface} implementation used to resolve names
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * @param logic the the {@link LogicInterface} implementation used to resolve names
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	@Override
	public String map(SamlRemoteUserId remoteId) {
		// TODO: change to new saml method
		return logic.getUsernameByLdapUserId(remoteId.getUserId());
	}

}
