package org.bibsonomy.database.params;

import java.net.InetAddress;

import org.bibsonomy.common.enums.InetAddressStatus;

/** Holds parameters for admin specific things (e.g. blocking an IP, marking a spammer).
 * 
 * @author rja
 * @version $Id$
 */
public class AdminParam {

	
	/** An inetAddress whose status should be get/set/deleted. */
	private InetAddress inetAddress = null;
	/** Status of the corresponding inetAddress */
	private InetAddressStatus inetAddressStatus;
	
	public InetAddress getInetAddress() {
		return this.inetAddress;
	}
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}
	public InetAddressStatus getInetAddressStatus() {
		return this.inetAddressStatus;
	}
	public void setInetAddressStatus(InetAddressStatus inetAddressStatus) {
		this.inetAddressStatus = inetAddressStatus;
	}
	
}
