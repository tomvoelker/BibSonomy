package org.bibsonomy.database.params;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;

/**
 * 
 * 
 * @author MarcelM
 * @version $Id$
 */
public class SamlRemoteUserParam extends RemoteUserParam<SamlRemoteUserId>{
	
	public SamlRemoteUserParam(User user, RemoteUserId ruid) {
		super(user, (SamlRemoteUserId) ruid);
	}

	@Override
	public SamlRemoteUserId getRemoteId() {
		return this.remoteId;
	}
}
