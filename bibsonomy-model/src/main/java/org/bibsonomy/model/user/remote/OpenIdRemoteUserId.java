package org.bibsonomy.model.user.remote;

/**
 * @author jensi
 * @version $Id$
 */
public class OpenIdRemoteUserId extends SimpleRemoteUserId {
	private static final long serialVersionUID = 6136901954223962010L;

	/**
	 * default constructor
	 * do not use - currently RemoteUSer Stuff is only working for SAML
	 */
	protected OpenIdRemoteUserId() {
		super();
	}

	/**
	 * handy constructor
	 * do not use - currently RemoteUSer Stuff is only working for SAML
	 * 
	 * @param remoteUserId
	 */
	protected OpenIdRemoteUserId(String remoteUserId) {
		super(remoteUserId);
	}
}
