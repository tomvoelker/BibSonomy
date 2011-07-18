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
	UNKNOWN("unknown"),
	RUNNING("running"),
	DONE("done"),
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
