package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationClients;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.sync.SynchronizationDatabaseManager;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManagerTest extends
	AbstractDatabaseManagerTest {
    
    private static SynchronizationDatabaseManager syncDBManager;
    
    private final String syncUser1 = "Syncuser1";

    @BeforeClass
    public static void setupManager() {
	syncDBManager = SynchronizationDatabaseManager.getInstance();
    }
    
    /**
     * test for insertSyncServicesForUser and getSyncServicesForUser
     */
    @Test
    public void testSyncService() {
	SyncService service = new SyncService();
	service.setServiceId(0);
	service.setServiceName(SynchronizationClients.LOCAL.toString());
	Properties serverUser = new Properties();
	serverUser.setProperty("name", syncUser1);
	serverUser.setProperty("apiKey", "1546545646565");
	service.setServerUser(serverUser);
	
	syncDBManager.insertSyncServiceForUser(dbSession, syncUser1, 0, serverUser);
	
	List<SyncService> services = syncDBManager.getSyncServicesForUser(syncUser1, dbSession);
	assertTrue(services.contains(service));
	assertEquals(2, services.size());
    }
    
    /**
     * Test for all "sync_data" queries
     */
    @Test
    public void testGetLastSyncData() {
	/*
	 * test getLastSynchronizationDate
	 */
	int serviceId = 1;
	int contentType = 2;
	Date expectedDate = new Date(1296684000000L);
	Date date = syncDBManager.getLastSynchronizationDate(syncUser1, serviceId, contentType, dbSession);
	assertEquals(expectedDate, date);
	
	/*
	 * check that no data in db
	 */
	SynchronizationData data = syncDBManager.getCurrentSynchronizationData(syncUser1, serviceId, contentType, dbSession);
	assertNull(data);
	
	/*
	 * insert new data in db
	 */
	date = new Date((new Date().getTime() % 1000) * 1000);
	syncDBManager.insertSyncronizationData(syncUser1, serviceId, contentType, date, "undone", dbSession);
	
	/*
	 * check aded data
	 */
	data = syncDBManager.getCurrentSynchronizationData(syncUser1, serviceId, contentType, dbSession);
	assertNotNull(data);
	assertEquals(syncUser1, data.getUserName());
	assertEquals(date, data.getLastSyncDate());
	assertEquals("undone", data.getStatus());
	assertEquals(contentType, data.getContentType());
	assertEquals(serviceId, data.getServiceId());
	
	/*
	 * set status of added data to done (simulate done synchronization)
	 */
	data.setStatus("done");
	syncDBManager.updateSyncData(dbSession, data);
	
	data = syncDBManager.getCurrentSynchronizationData(syncUser1, serviceId, contentType, dbSession);
	assertNull(data);
	
	List<SynchronizationData> dataList = syncDBManager.getSynchronizationData(syncUser1, serviceId, contentType, dbSession);
	assertEquals(2, dataList.size());
	
    }
    
    
}
