package org.bibsonomy.database.params;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.RemoteUserId;

/**
 * used to store {@link RemoteUserId}s which do not themselves have a backlink to the user object
 * 
 * @author jensi
 * @version $Id$
 */
public class RemoteUserParam {
	private RemoteUserId remoteId;
	private User user;

	/**
	 * default constructor
	 */
	public RemoteUserParam() {
	}
	
	/**
	 * handy constructor
	 * @param user
	 * @param remoteId
	 */
	public RemoteUserParam(User user, RemoteUserId remoteId) {
		this.user = user;
		this.remoteId = remoteId;
	}

	/**
	 * @return the remoteId
	 */
	public RemoteUserId getRemoteId() {
		return this.remoteId;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param remoteId the remoteId to set
	 */
	public void setRemoteId(RemoteUserId remoteId) {
		this.remoteId = remoteId;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
