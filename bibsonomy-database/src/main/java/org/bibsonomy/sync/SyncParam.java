package org.bibsonomy.sync;

import java.net.URI;

import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;

/**
 * @author wla
 * @version $Id$
 */
public class SyncParam {

	private SynchronizationData data;
	private SyncService syncService;

	private String userName;
	private URI service;
	private int serviceId;
	private boolean server;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
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
	 * @param service the service to set
	 */
	public void setService(final URI service) {
		this.service = service;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(final int serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the server
	 */
	public boolean isServer() {
		return this.server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(final boolean server) {
		this.server = server;
	}

	/**
	 * @return the data
	 */
	public SynchronizationData getData() {
		return this.data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(final SynchronizationData data) {
		this.data = data;
	}
	
	/**
	 * @return the syncService
	 */
	public SyncService getSyncService() {
		return this.syncService;
	}

	/**
	 * @param syncService the syncService to set
	 */
	public void setSyncService(final SyncService syncService) {
		this.syncService = syncService;
	}
}
