package org.bibsonomy.model.user.remote;

/**
 * @author jensi
 * @version $Id$
 */
public class LdapRemoteUserId extends SimpleRemoteUserId {
	private static final long serialVersionUID = 991296232335349674L;

	/**
	 * default constructor
	 */
	public LdapRemoteUserId() {
		super();
	}

	/**
	 * handy constructor
	 * 
	 * @param remoteUserId
	 */
	public LdapRemoteUserId(String remoteUserId) {
		super(remoteUserId);
	}
}
