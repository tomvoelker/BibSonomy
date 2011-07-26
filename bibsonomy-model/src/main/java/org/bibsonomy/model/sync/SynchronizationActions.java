package org.bibsonomy.model.sync;

/**
 * @author wla
 * @version $Id$
 */
public enum SynchronizationActions {
	/**
	 * server post must be updated
	 */
	UPDATE_SERVER,
	
	/**
	 * client post must be updated
	 */
    UPDATE_CLIENT,
    
    /**
     * post must be created on server
     */
    CREATE_SERVER,
    
    /**
     * post must be created on client
     */
    CREATE_CLIENT,
    
    /**
     * post must be deleted on server
     */
    DELETE_SERVER,
    
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
