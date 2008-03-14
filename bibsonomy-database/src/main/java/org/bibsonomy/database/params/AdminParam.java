package org.bibsonomy.database.params;

import java.net.InetAddress;
import java.util.Date;

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
	
	private String userName;
	
	private Integer spammer;
	
	private int oldGroupId;
	
	private int newGroupId;
	
	private Date updatedAt;
	
	private String updatedBy;
	
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
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getSpammer() {
		return this.spammer;
	}
	public void setSpammer(Integer spammer) {
		this.spammer = spammer;
	}
	public String getUpdatedBy() {
		return this.updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public int getOldGroupId() {
		return this.oldGroupId;
	}
	public void setOldGroupId(int oldGroupId) {
		this.oldGroupId = oldGroupId;
	}
	public int getNewGroupId() {
		return this.newGroupId;
	}
	public void setNewGroupId(int newGroupId) {
		this.newGroupId = newGroupId;
	}
	public Date getUpdatedAt() {
		return this.updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}	
}