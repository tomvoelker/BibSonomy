package org.bibsonomy.model.sync;

/**
 * @author wla
 * @version $Id$
 */
public enum ConflictResolutionStrategy {
    /**
     * client changes will be applied to server account
     */
	CLIENT_WINS,
    
	/**
	 * server changes will be applied to client account
	 */
	SERVER_WINS,
	
	/**
	 * latest changes will be applied to another account
	 */
    LAST_WINS,
    
    /**
     * the first changes will be applied to another account
     */
    FIRST_WINS,
    
    /**
     * user can select, which changes will be applied
     */
    ASK_USER;
}
