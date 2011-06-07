package org.bibsonomy.model.sync;

import java.net.URI;
import java.util.Date;

import org.bibsonomy.model.Resource;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationData {

	private URI service;
	private String userName;
	private Class<? extends Resource> resourceType;
	private Date lastSyncDate;
	private String status;
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
	public URI getService() {
		return this.service;
	}
	public void setService(URI service) {
		this.service = service;
	}
	public Class<? extends Resource> getResourceType() {
		return this.resourceType;
	}
	public void setResourceType(Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}
	
}
