package org.bibsonomy.sync;

import java.net.URI;
import java.util.Date;
import java.util.Properties;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationStatus;

/**
 * @author wla
 * @version $Id$
 */
public class SyncParam {

	private String userName;
	private URI service;
	private int serviceId;
	private Date lastSyncDate;
	private SynchronizationStatus status;
	private SynchronizationDirection direction;
	private ConflictResolutionStrategy strategy;
	private Class<? extends Resource> resourceType;
	private String info;
	private boolean server;
	private Properties credentials;
	private String sslDn;
	private URI secureAPI;

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

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public void setService(final URI service) {
		this.service = service;
	}

	public void setServiceId(final int serviceId) {
		this.serviceId = serviceId;
	}

	public void setLastSyncDate(final Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

	public void setStatus(final SynchronizationStatus status) {
		this.status = status;
	}

	public void setCredentials(final Properties credentials) {
		this.credentials = credentials;
	}

	public boolean getServer() {
		return this.server;
	}

	public void setServer(final boolean server) {
		this.server = server;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(final String info) {
		this.info = info;
	}

	public Class<? extends Resource> getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(final Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}

	public SynchronizationDirection getDirection() {
		return this.direction;
	}

	public void setDirection(final SynchronizationDirection direction) {
		this.direction = direction;
	}

	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(final ConflictResolutionStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * @return the strategy
	 */
	public ConflictResolutionStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @return the ssl_dn
	 */
	public String getSslDn() {
		return sslDn;
	}

	/**
	 * @param sslDn the ssl_dn to set
	 */
	public void setSslDn(final String sslDn) {
		this.sslDn = sslDn;
	}

	/**
	 * @return the secureApi
	 */
	public URI getSecureAPI() {
		return secureAPI;
	}

	/**
	 * @param secureAPI the secureApi to set
	 */
	public void setSecureAPI(final URI secureAPI) {
		this.secureAPI = secureAPI;
	}
}
