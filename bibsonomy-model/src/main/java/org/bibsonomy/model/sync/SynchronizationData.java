package org.bibsonomy.model.sync;

import java.util.Date;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationData {

	private int serviceId;
	private String userName;
	private int contentType;
	private Date lastSyncDate;
	private String status;
	/**
	 * @return the serviceId
	 */
	public int getServiceId() {
		return this.serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the contentType
	 */
	public int getContentType() {
		return this.contentType;
	}
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	/**
	 * @return the lastSyncDate
	 */
	public Date getLastSyncDate() {
		return this.lastSyncDate;
	}
	/**
	 * @param lastSyncDate the lastSyncDate to set
	 */
	public void setLastSyncDate(Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
