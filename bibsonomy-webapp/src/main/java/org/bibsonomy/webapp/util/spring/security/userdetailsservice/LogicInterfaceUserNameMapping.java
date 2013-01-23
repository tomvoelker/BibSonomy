package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import org.bibsonomy.model.logic.LogicInterface;

/**
 * A {@link NameSpacedNameMapping} implementation that uses the bibsonomy {@link LogicInterface} as its backend.
 * @author jensi
 * @version $Id$
 */
public class LogicInterfaceUserNameMapping implements NameSpacedNameMapping {

	private LogicInterface logic;
	
	@Override
	public String mapName(String nameSpace, String name) {
		// TODO: support several idPs? maybe also treat ldap and openid as namespaces
		final String systemName = logic.getUsernameByLdapUserId(name);
		return systemName;
	}

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

}
