package org.bibsonomy.database.params;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.LdapRemoteUserId;
import org.bibsonomy.model.user.remote.RemoteUserId;

/**
 * @author MarcelM
 * @version $Id$
 */
public class LdapRemoteUserParam extends RemoteUserParam<LdapRemoteUserId>{
	
	public LdapRemoteUserParam(User user, RemoteUserId ruid) {
		super(user, (LdapRemoteUserId) ruid);
	}

	@Override
	public LdapRemoteUserId getRemoteId() {
		return this.remoteId;
	}
}
