package org.bibsonomy.common.enums;

/**
 * @author Robert Jäschke
 * @version $Id$
 */
public enum InetAddressStatus {
	
	/** The IP is blocked - write access is not allowed at all (neither registration nor posting). */
	WRITEBLOCKED(1),
	/** The IP can not be found in the status table - its status is unknown. */
	UNKNOWN(0);
	
	private static final InetAddressStatus[] map = new InetAddressStatus[]{UNKNOWN, WRITEBLOCKED};
	private final int status;
	
	private InetAddressStatus(final int status) {
		this.status = status;
	}
	
	/** Returns the numerical representation of this object.
	 * @return The numerical representation of the object.
	 */
	public int getInetAddressStatus() {
		return this.status;
	}
	
	/** Creates an instance of this class by its String representation.
	 * 
	 * @param inetAddressStatus - a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static InetAddressStatus getInetAddressStatus(final String inetAddressStatus) {
		if (inetAddressStatus == null) return UNKNOWN;
		return getInetAddressStatus(Integer.parseInt(inetAddressStatus));
	}
	
	/** Creates an instance of this class by its Integer representation.
	 * 
	 * @param inetAddressStatus - an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static InetAddressStatus getInetAddressStatus(final int inetAddressStatus) {
		return map[inetAddressStatus];
	}
}