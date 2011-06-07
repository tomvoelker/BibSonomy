package org.bibsonomy.model.sync;

import java.net.URI;
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
	private URI service;
	private final Map<Integer, Date> lastSyncDates;
	private String userName;
	private String apiKey;
	
	//TODO remove this after implementation of syncpage
	private final Map<Integer, String> lastResults;
	
	/**
	 * Constructor
	 */
	public SyncService() {
		lastSyncDates = new HashMap<Integer, Date>();
		lastResults = new HashMap<Integer, String>();
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
			final SyncService test = (SyncService) obj;
			return this.getService().equals(test.getService());
		} 
		return super.equals(obj);
	}

	/**
	 * @return the lastResults
	 */
	public Map<Integer, String> getLastResults() {
		return lastResults;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	public URI getService() {
		return this.service;
	}

	public void setService(URI service) {
		this.service = service;
	}

	
}
