package org.bibsonomy.model.user.remote;

/**
 * @author jensi
 * @version $Id$
 */
public class OpenIdRemoteUserId extends SimpleRemoteUserId {
	private static final long serialVersionUID = 6136901954223962010L;

	/**
	 * default constructor
	 */
	public OpenIdRemoteUserId() {
		super();
	}

	/**
	 * handy constructor
	 * 
	 * @param remoteUserId
	 */
	public OpenIdRemoteUserId(String remoteUserId) {
		super(remoteUserId);
	}
}
