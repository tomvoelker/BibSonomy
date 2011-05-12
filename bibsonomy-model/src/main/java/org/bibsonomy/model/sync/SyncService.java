package org.bibsonomy.model.sync;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** 
 * @author wla
 * @version $Id$
 */
public class SyncService {
	
	private Properties serverUser;
	private int serviceId;
	private String serviceName;
	private final Map<Integer, Date> lastSyncDates;
	
	/**
	 * Constructor
	 */
	public SyncService() {
		lastSyncDates = new HashMap<Integer, Date>();
		//this.serviceId = serviceId;
	}
	
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
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @return the lastSyncDates
	 */
	public Map<Integer, Date> getLastSyncDates() {
		return lastSyncDates;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SyncService) {
			SyncService test = (SyncService) obj;
			return this.getServiceId() == test.getServiceId();
		} 
		return super.equals(obj);
	}

	
}
