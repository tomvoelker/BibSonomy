package org.bibsonomy.database.params;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.OpenIdRemoteUserId;
import org.bibsonomy.model.user.remote.RemoteUserId;

/**
 * @author MarcelM
 * @version $Id$
 */
public class OpenIdRemoteUserParam extends RemoteUserParam<OpenIdRemoteUserId>{
	
	public OpenIdRemoteUserParam(User user, RemoteUserId ruid) {
		super(user, (OpenIdRemoteUserId) ruid);
	}

	@Override
	public OpenIdRemoteUserId getRemoteId() {
		return this.remoteId;
	}
}
