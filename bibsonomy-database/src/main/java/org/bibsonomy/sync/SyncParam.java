package org.bibsonomy.sync;

import java.net.URI;
import java.util.Date;
import java.util.Properties;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.SynchronizationStatus;

/**
 * @author wla
 * @version $Id$
 */
public class SyncParam {

	/*
	 * FIXME: use SyncData as attribute
	 */
	
	private String userName;
	private URI service;
	private int serviceId;
	private int contentType;
	private Date lastSyncDate;
	private SynchronizationStatus status;
	private String info;
	private boolean server;
	private Properties credentials;
	
	public SyncParam() {
		
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the serviceId
	 */
	public URI getService() {
		return service;
	}

	/**
	 * @return the serviceId
	 */
	public int getServiceId() {
		return serviceId;
	}

	/**
	 * @return the contentType
	 */
	public int getContentType() {
		return contentType;
	}

	/**
	 * @return the lastSyncDate
	 */
	public Date getLastSyncDate() {
		return lastSyncDate;
	}

	/**
	 * @return the status
	 */
	public SynchronizationStatus getStatus() {
		return status;
	}

	/**
	 * @return the credentials
	 */
	public Properties getCredentials() {
		return credentials;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setService(URI service) {
		this.service = service;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public void setResourceType(final Class<? extends Resource> resourceType) {
		this.contentType = ConstantID.getContentTypeByClass(resourceType).getId();
	}
	
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public void setLastSyncDate(Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

	public void setStatus(SynchronizationStatus status) {
		this.status = status;
	}

	public void setCredentials(Properties credentials) {
		this.credentials = credentials;
	}

	public boolean getServer() {
		return this.server;
	}

	public void setServer(boolean server) {
		this.server = server;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
