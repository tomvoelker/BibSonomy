package org.bibsonomy.sync;

import java.util.Date;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.sync.SynchronizationData;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManager extends AbstractDatabaseManager {
    private static final SynchronizationDatabaseManager singelton = new SynchronizationDatabaseManager();
    
    /**
     * 
     * @return SynchronizationDatabaseManager
     */
    public static SynchronizationDatabaseManager getInstance() {
	return singelton;
    }
    
    private SynchronizationDatabaseManager() {
    }
    
    /**
     * updates given syncronization data in db
     * @param query
     * @param session
     * @param data
     */
    public void updateSyncData(final String query, final DBSession session, final SynchronizationData data) {
	SyncParam param = new SyncParam(data.getUserName(), data.getServiceId(), data.getContentType(), data.getLastSyncDate(), data.getStatus());
	session.update(query, param);
    }

    
    public Date getLastSynchronizationDate(final String userName, final int serviceId, final int contentType, final DBSession session) {
	return this.queryForObject("getLastSyncDate", new SyncParam(userName, serviceId, contentType, null, null), Date.class, session);
    }
    
    public Date getCurrentSyncDate(final String userName,
	    final int serviceId, final int contentType, final DBSession session) {

	return this.queryForObject("getCurrentSyncDate", new SyncParam(userName,
		serviceId, contentType, null, "undone"), Date.class, session);
    }
    
    public void insertInitialSynchronization (String userName, int serviceId, int contentType, final DBSession session) {
	SyncParam param = new SyncParam(userName, serviceId, contentType, new Date(), "undone");
	session.insert("insertSync", param);
    }
    
    public SynchronizationData getCurrentSynchronization (final String userName, final int serviceId, final int contentType, final DBSession session) {
	SyncParam param = new SyncParam(userName, serviceId, contentType, null, null);
	return queryForObject("getCurrentSyncData", param, SynchronizationData.class, session);
    }
}
