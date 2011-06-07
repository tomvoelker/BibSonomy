package org.bibsonomy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Date;
import java.util.Properties;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.SynchronizationClients;

/**
 * @author wla
 * @version $Id$
 */
public class SyncParam {

	private final String userName;
	private final URI service;
	private final int serviceId;
	private final int contentType;
	private final Date lastSyncDate;
	private final String status;
	private final Properties credentials;

	public SyncParam(String userName, final URI service, Class<? extends Resource> resourceType, Date lastSyncDate, String status, Properties credentials) {
		this.userName = userName;
		this.service = service;
		if(present(resourceType)) {
			this.contentType = ConstantID.getContentTypeByClass(resourceType).getId();
		} else {
			contentType = 0;
		}
		this.lastSyncDate = lastSyncDate;
		this.status = status;
		this.credentials = credentials;
		this.serviceId = SynchronizationClients.getByUri(service).getId();
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
	public String getStatus() {
		return status;
	}

	/**
	 * @return the credentials
	 */
	public Properties getCredentials() {
		return credentials;
	}
}
