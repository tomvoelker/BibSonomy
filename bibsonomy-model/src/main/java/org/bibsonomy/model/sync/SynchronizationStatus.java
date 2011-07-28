package org.bibsonomy.model.sync;

/**
 * The different states a synchronization can be in. 
 * 
 * @author rja
 * @version $Id$
 */
public enum SynchronizationStatus {
	/*
	 * NOTE: column is a varchar(8), so please use short names
	 */
	/**
	 * A synchronization plan was requested. 
	 */
	PLANNED("planned"),
	/**
	 * A client is currently working on the plan = synchronizing.
	 */
	RUNNING("running"),
	/**
	 * Synchronization is complete. 
	 */
	DONE("done"),
	/**
	 * An error during sync occurred. 
	 */
	ERROR("error");
	
	
	private String status;

	private SynchronizationStatus(final String status) {
		this.status = status;
	}
	
	/**
	 * @return The string representation for the synchronization status.
	 */
	public String getSynchronizationStatus() {
		return status;
	}
}
