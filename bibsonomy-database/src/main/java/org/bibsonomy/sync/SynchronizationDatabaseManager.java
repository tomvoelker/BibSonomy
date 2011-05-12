package org.bibsonomy.sync;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManager extends AbstractDatabaseManager {
    private static final SynchronizationDatabaseManager singelton = new SynchronizationDatabaseManager();
    
    /**
     * Singleton 
     * @return SynchronizationDatabaseManager
     */
    public static SynchronizationDatabaseManager getInstance() {
	return singelton;
    }
    
    private SynchronizationDatabaseManager() {
    }
    
    /**
     * Updates given synchronization data in db. This method will be used from SynchronizationServer!
     * @param session database session
     * @param data SynchronizationData
     */
    public void updateSyncData(final DBSession session, final SynchronizationData data) {
	SyncParam param = new SyncParam(data.getUserName(), data.getServiceId(), data.getContentType(), data.getLastSyncDate(), data.getStatus(), null);
	session.update("updateSyncStatus", param);
    }

    /**
     * Insert new synchronization SERVER into db.
     * @param session
     * @param userName
     * @param serviceId
     * @param credentials
     */
    public void insertSyncServiceForUser(final DBSession session, final String userName, final int serviceId, final Properties credentials) {
	session.insert("insertSyncServiceForUser", new SyncParam(userName, serviceId, 0, null, null, credentials));
    }
    
    /**
     * @param userName
     * @param serviceId
     * @param contentType
     * @param session
     * @return last synchronization date for given user, content type and service 
     */
    public Date getLastSynchronizationDate(final String userName, final int serviceId, final int contentType, final DBSession session) {
	return this.queryForObject("getLastSyncDateForUserForServiceForContent", new SyncParam(userName, serviceId, contentType, null, null, null), Date.class, session);
    }
    
    /**
     * Inserts synchronization data with GIVEN status into db. 
     * @param userName
     * @param serviceId
     * @param contentType
     * @param lastSyncDate
     * @param status
     * @param session
     */
    public void insertSyncronizationData (String userName, int serviceId, int contentType, final Date lastSyncDate, final String status, final DBSession session) {
	SyncParam param = new SyncParam(userName, serviceId, contentType, lastSyncDate, status, null);
	session.insert("insertSync", param);
    }
    
    /**
     * 
     * @param userName
     * @param serviceId
     * @param contentType
     * @param session
     * @return returns last synchronization data for given user, service and content with status="undone"
     */
    public SynchronizationData getCurrentSynchronizationData (final String userName, final int serviceId, final int contentType, final DBSession session) {
	SyncParam param = new SyncParam(userName, serviceId, contentType, null, null, null);
	return queryForObject("getCurrentSyncData", param, SynchronizationData.class, session);
    }
    
    /**
     * 
     * @param userName
     * @param serviceId
     * @param contentType
     * @param session
     * @return list of synchronizationData for given user, service and contentType
     */
    public List<SynchronizationData> getSynchronizationData (final String userName, final int serviceId, final int contentType, final DBSession session) {
	SyncParam param = new SyncParam(userName, serviceId, contentType, null, null, null);
	return this.queryForList("getSyncData", param, SynchronizationData.class, session);
    }
    
    /**
     * 
     * @param userName
     * @param session
     * @return all synchronization server for user
     */
    @SuppressWarnings("unchecked")
    public List<SyncService> getSyncServicesForUser(final String userName, final DBSession session) {
	SyncParam param = new SyncParam(userName, 0, 0, null, null, null);
	return queryForList("getSyncServicesForUser", param, session);
    }
}
