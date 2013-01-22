package org.bibsonomy.model.user.remote;

import java.io.Serializable;

/**
 * @author jensi
 * @version $Id$
 */
public interface RemoteUserId extends Serializable {
	/**
	 * @return short userId (without pre-/pos-fixes/namespaces etc.)
	 */
	public String getSimpleId();
	
	/**
	 * @return an object which represents the namespace of the remote userId (method+idP)
	 */
	public RemoteUserNameSpace getNameSpace();
}
