package org.bibsonomy.model.sync;

/**
 * @author wla
 * @version $Id$
 */
public enum SynchronizationDirection {
	/*
	 * NOTE: column is a varchar(4), so please use short names
	 */
	/**
	 * both directions 
	 */
	BOTH("both"),
	
	/**
	 * only server changes will be applied to client 
	 */
	SERVER_TO_CLIENT("stoc"),
	
	/**
	 * only client changes will be applied to server
	 */
	CLIENT_TO_SERVER("ctos");
	
	
	private String direction;

	private SynchronizationDirection(final String direction) {
		this.direction = direction;
	}
	
	/**
	 * @return The string representation for the synchronization direction.
	 */
	public String getSynchronizationDirection() {
		return direction;
	}
	
	/**
	 * @param direction
	 * @return synchronization direction for given string.
	 */
	public static SynchronizationDirection getSynchronizationDirectionByString(String direction) {
		if("stoc".equals(direction)) {
			return SERVER_TO_CLIENT;
		} else if("ctos".equals(direction)) {
			return CLIENT_TO_SERVER;
		} else {
			return BOTH;
		}
	}
}
