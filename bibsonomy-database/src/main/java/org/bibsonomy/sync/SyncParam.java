package org.bibsonomy.sync;

import java.util.Date;

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

    public SyncParam(String userName, int serviceId, int contentType, Date lastSyncDate, String status) {
	this.userName = userName;
	this.serviceId = serviceId;
	this.contentType = contentType;
	this.lastSyncDate = lastSyncDate;
	this.status = status;
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
}
