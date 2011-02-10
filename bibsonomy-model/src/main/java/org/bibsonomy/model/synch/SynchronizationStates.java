package org.bibsonomy.model.synch;

/**
 * @author wla
 * @version $Id$
 */
public enum SynchronizationStates {
	/**
	 * server post must be updated
	 */
	UPDATE,
	
	/**
	 * client post must be updated
	 */
    UPDATE_CLIENT,
    
    /**
     * post must be created on server
     */
    CREATE,
    
    /**
     * post must be created on client
     */
    CREATE_CLIENT,
    
    /**
     * post must be deleted on server
     */
    DELETE,
    
    /**
     * post must be deleted on client
     */
    DELETE_CLIENT,
    
    /**
     * nothing to do
     */
    OK,
    
    /**
     * conflict, ask user
     */
    ASK,
    
    /**
     * something else... possible error, 
     */
    UNDEFINED;
}
