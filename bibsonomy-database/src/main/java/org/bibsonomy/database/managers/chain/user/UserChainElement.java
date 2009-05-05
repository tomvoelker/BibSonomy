package org.bibsonomy.database.managers.chain.user;

import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

/**
 * Chain element for user chain
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class UserChainElement extends ChainElement<User, UserParam> {

	protected final UserDatabaseManager userDB;
	protected final GroupDatabaseManager groupDB;
	
	/**
	 * Constructs a chain element
	 */
	public UserChainElement() {
		this.userDB = UserDatabaseManager.getInstance();
		this.groupDB = GroupDatabaseManager.getInstance();
	}
}