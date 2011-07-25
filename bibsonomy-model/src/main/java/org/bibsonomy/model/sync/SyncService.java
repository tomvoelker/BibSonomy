package org.bibsonomy.model.sync;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.bibsonomy.model.Resource;

/** 
 * @author wla
 * @version $Id$
 */
public class SyncService {
	
	private Properties serverUser; // FIXME: rename to "userCredentials" or "user" or "credententials"
	private URI service; // FIXME: rename to "uri" or "serviceUri"
	private Map <String, SynchronizationData> lastSyncData;
	private Class<? extends Resource> resourceType;
	private SynchronizationDirection direction;
	
	/**
	 * @return the clientUser
	 */
	public Properties getServerUser() {
		return this.serverUser;
	}
	/**
	 * @param serverUser the clientUser to set
	 */
	public void setServerUser(Properties serverUser) {
		this.serverUser = serverUser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SyncService) {
			final SyncService test = (SyncService) obj;
			return this.getService().equals(test.getService());
		} 
		return super.equals(obj);
	}

	/**
	 * @return the service
	 */
	public URI getService() {
		return this.service;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setService(URI service) {
		this.service = service;
	}
	
	/**
	 * @return the lastSyncData
	 */
	public Map <String, SynchronizationData> getLastSyncData() {
		return lastSyncData;
	}
	
	/**
	 * 
	 * @param lastSyncData the lastSyncData to set
	 */
	public void setLastSyncData(Map<String, SynchronizationData> lastSyncData) {
		this.lastSyncData = lastSyncData;
	}
	
	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}
	/**
	 * @return the resourceType
	 */
	public Class<? extends Resource> getResourceType() {
		return resourceType;
	}
	/**
	 * @param direction the direction to set
	 */
	public void setDirection(SynchronizationDirection direction) {
		this.direction = direction;
	}
	/**
	 * @return the direction
	 */
	public SynchronizationDirection getDirection() {
		return direction;
	}
	@Override
	public String toString() {
		return service.toString();
	}
}
