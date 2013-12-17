package org.bibsonomy.database.params;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;

/**
 * used to store {@link SamlRemoteUserId}s which do not themselves have a backlink to the user object
 * 
 * @author jensi
 */
public class SamlUserParam {
	
	protected SamlRemoteUserId samlRemoteUserId;
	protected User user;

	/**
	 * default constructor
	 */
	public SamlUserParam() {
	}

	/**
	 * handy constructor
	 * @param user
	 * @param samlRemoteUserId
	 */
	public SamlUserParam(User user, SamlRemoteUserId samlRemoteUserId) {
		this.user = user;
		this.samlRemoteUserId = samlRemoteUserId;
	}

	public SamlRemoteUserId getSamlRemoteUserId() {
		return this.samlRemoteUserId;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param samlRemoteUserId the remoteId to set
	 */
	public void setSamlRemoteUserId(final SamlRemoteUserId samlRemoteUserId) {
		this.samlRemoteUserId = samlRemoteUserId;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
