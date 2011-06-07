package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.sync.SynchronizationDatabaseManager;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static SynchronizationDatabaseManager syncDBManager;

	private final String syncUser1 = "Syncuser1";
	private URI testURI;
	private URI bibsonomyURI;
	
	@BeforeClass
	public static void setupManager() {
		syncDBManager = SynchronizationDatabaseManager.getInstance();
	}
	
	public SynchronizationDatabaseManagerTest() {
		try {
			this.testURI = new URI("http://www.test.de/");
			this.bibsonomyURI = new URI("http://www.bibsonomy.org/");
		} catch (URISyntaxException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	/**
	 * FIXME fix this test
	 * test for all access to the table `sync`
	 */
	@Ignore
	@Test
	public void testSyncService() {
		SyncService service = new SyncService();
		service.setService(testURI);
		Properties serverUser = new Properties();
		serverUser.setProperty("name", syncUser1);
		serverUser.setProperty("apiKey", "1546545646565");
		service.setServerUser(serverUser);

		syncDBManager.createSyncServerForUser(dbSession, syncUser1, testURI, serverUser);

		List<SyncService> services = syncDBManager.getSyncServerForUser(syncUser1, dbSession);
		assertTrue(services.contains(service));
		assertEquals(2, services.size());
		
		serverUser = new Properties();
		serverUser.setProperty("name", "syncUser2");
		serverUser.setProperty("apiKey", "jjkhjhjkhk");
		service.setServerUser(serverUser);
		syncDBManager.updateSyncServerForUser(dbSession, syncUser1, testURI, serverUser);
		
		services = syncDBManager.getSyncServerForUser(syncUser1, dbSession);
		assertTrue(services.contains(service));
		assertEquals(2, services.size());
		
		syncDBManager.deleteSyncServerForUser(dbSession, syncUser1, testURI);
		services = syncDBManager.getSyncServerForUser(syncUser1, dbSession);
		assertFalse(services.contains(service));
		assertEquals(1, services.size());
	}

	/**
	 *TODO fix this test
	 * Test for all "sync_data" queries
	 */
	@Ignore
	@Test
	public void testGetLastSyncData() {
		/*
		 * test getLastSynchronizationDate
		 */
		
		//TODO remove this
		int serviceId = 1;
		int contentType = 2;
		
		Class<? extends Resource> resourceType = BibTex.class;
		
		Date expectedDate = new Date(1296684000000L);
		Date date = syncDBManager.getLastSynchronizationDate(syncUser1, bibsonomyURI, resourceType, dbSession);
		assertEquals(expectedDate, date);

		/*
		 * check that no data in db
		 */
		SynchronizationData data = syncDBManager.getCurrentSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession);
		assertNull(data);

		/*
		 * insert new data in db
		 */
		date = new Date((new Date().getTime() % 1000) * 1000);
		syncDBManager.insertSyncronizationData(syncUser1, bibsonomyURI, resourceType, date, "undone", dbSession);

		/*
		 * check aded data
		 */
		data = syncDBManager.getCurrentSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession);
		assertNotNull(data);
		assertEquals(syncUser1, data.getUserName());
		assertEquals(date, data.getLastSyncDate());
		assertEquals("undone", data.getStatus());
		assertEquals(resourceType, data.getResourceType());
		assertEquals(bibsonomyURI, data.getService());

		/*
		 * set status of added data to done (simulate done synchronization)
		 */
		data.setStatus("done");
		syncDBManager.updateSyncData(dbSession, data);

		data = syncDBManager.getCurrentSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession);
		assertNull(data);

		List<SynchronizationData> dataList = syncDBManager.getSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession);
		assertEquals(2, dataList.size());

	}

}
