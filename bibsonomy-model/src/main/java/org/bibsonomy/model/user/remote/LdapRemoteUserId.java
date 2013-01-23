package org.bibsonomy.model.user.remote;

/**
 * @author jensi
 * @version $Id$
 */
public class LdapRemoteUserId extends SimpleRemoteUserId {
	private static final long serialVersionUID = 991296232335349674L;

	/**
	 * default constructor
	 * do not use - currently RemoteUSer Stuff is only working for SAML
	 */
	protected LdapRemoteUserId() {
		super();
	}

	/**
	 * handy constructor
	 * 
	 * @param remoteUserId
	 */
	protected LdapRemoteUserId(String remoteUserId) {
		super(remoteUserId);
	}
}
