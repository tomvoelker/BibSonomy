package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

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
    
    @Test
    public void testGetLastSyncDate() {
	int serviceId = 1;
	int contentType = 1;
	Date date = syncDBManager.getLastSynchronizationDate(syncUser1, serviceId, contentType, dbSession);	
	assertNotNull(date);
	
	date = syncDBManager.getCurrentSyncDate(syncUser1, serviceId, contentType, dbSession);
	assertNull(date);
	
	date = new Date();
	syncDBManager.insertInitialSynchronization(syncUser1, serviceId, contentType, dbSession);
	
	Date currentSyncDate = syncDBManager.getCurrentSyncDate(syncUser1, serviceId, contentType, dbSession);
	assertNotNull(currentSyncDate);
	
	long time = date.getTime();
	time -= time % 1000;
	assertEquals(time, currentSyncDate.getTime());
	
	date = syncDBManager.getLastSynchronizationDate(syncUser1, serviceId, contentType, dbSession);
	assertEquals(time, date.getTime());
	
    }
    
}
