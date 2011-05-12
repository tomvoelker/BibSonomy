package org.bibsonomy.sync;

import java.util.Date;
import java.util.Properties;

/**
 * @author wla
 * @version $Id$
 */
public class SyncParam {

    private final String userName;
    private final int serviceId;
    private final int contentType;
    private final Date lastSyncDate;
    private final String status;
    private final Properties credentials;

    public SyncParam(String userName, int serviceId, int contentType, Date lastSyncDate, String status, Properties credentials) {
	this.userName = userName;
	this.serviceId = serviceId;
	this.contentType = contentType;
	this.lastSyncDate = lastSyncDate;
	this.status = status;
	this.credentials = credentials;
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
