package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.sync.SynchronizationDatabaseManager;
import org.bibsonomy.testutil.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static SynchronizationDatabaseManager syncDBManager;
	private static URI testURI;
	private static URI bibsonomyURI;
	private static URI deviceURI;

	private final String syncUser1 = "syncuser1";
	
	
	@BeforeClass
	public static void setupManager() {
		syncDBManager = SynchronizationDatabaseManager.getInstance();
		
		testURI = TestUtils.createURI("http://www.test.de/");
		bibsonomyURI = TestUtils.createURI("http://www.bibsonomy.org/");
		deviceURI = TestUtils.createURI("client://android/123456789012?device=NexusOne");
	}
	
	@Test
	public void testDevice() {
		final SynchronizationStatus status = SynchronizationStatus.DONE;
		final Date date = new Date();
		syncDBManager.insertSynchronizationData(syncUser1, deviceURI, Resource.class, date, status, this.dbSession);
		
		final SynchronizationData lastSyncData = syncDBManager.getLastSyncData(syncUser1, deviceURI, Resource.class, status, this.dbSession);
		
		assertNotNull(lastSyncData);
		assertEquals(status, lastSyncData.getStatus());
		assertEquals("NexusOne", lastSyncData.getDeviceInfo());
		
		final SynchronizationStatus running = SynchronizationStatus.RUNNING;
		syncDBManager.updateSyncData(syncUser1, deviceURI, Resource.class, lastSyncData.getLastSyncDate(), running, "", this.dbSession);
		
		final SynchronizationData lastSyncDataAfterUpdate = syncDBManager.getLastSyncData(syncUser1, deviceURI, Resource.class, null, this.dbSession);
		assertEquals(running, lastSyncDataAfterUpdate.getStatus());
	}
	
	@Test
	public void testGetSyncClients() {
		final List<SyncService> syncClients = syncDBManager.getSyncServices(syncUser1, null, false, this.dbSession);
		syncClients.size();
	}

	/**
	 * test for all access to the table `sync`
	 */
	@Test
	public void testSyncService() {		
		final Properties credentialsSyncUser1 = new Properties();
		credentialsSyncUser1.setProperty("name", syncUser1);
		credentialsSyncUser1.setProperty("apiKey", "1546545646565");
		final Class<Bookmark> resourceType = Bookmark.class;
		final SynchronizationDirection direction = SynchronizationDirection.SERVER_TO_CLIENT;
		final ConflictResolutionStrategy strategy = ConflictResolutionStrategy.SERVER_WINS;
		
		final SyncService service = new SyncService();
		service.setService(testURI);
		service.setServerUser(credentialsSyncUser1);
		service.setResourceType(resourceType);
		service.setDirection(direction);
		service.setStrategy(strategy);

		syncDBManager.createSyncServerForUser(syncUser1, service, dbSession);

		List<SyncService> services = syncDBManager.getSyncServices(syncUser1, null, true, dbSession);
		assertEquals(1, services.size());
		assertTrue(services.contains(service));
		final SyncService syncService = services.get(0);
		assertEquals(resourceType, syncService.getResourceType());
		assertEquals(direction, syncService.getDirection());
		assertEquals(strategy, syncService.getStrategy());
		
		final ConflictResolutionStrategy strategy2 = ConflictResolutionStrategy.LAST_WINS;
		final Class<BibTex> resourceType2 = BibTex.class;
		final Properties credentialsSyncUser2 = new Properties();
		credentialsSyncUser2.setProperty("name", "syncUser2");
		credentialsSyncUser2.setProperty("apiKey", "jjkhjhjkhk");
		
		service.setServerUser(credentialsSyncUser2);
		service.setStrategy(strategy2);
		service.setResourceType(resourceType2);
		syncDBManager.updateSyncServerForUser(syncUser1, service, dbSession);
		
		services = syncDBManager.getSyncServices(syncUser1, null, true, dbSession);
		assertTrue(services.contains(service));
		assertEquals(1, services.size());
		final SyncService syncService2 = services.get(0);
		assertEquals(resourceType2, syncService2.getResourceType());
		assertEquals(direction, syncService2.getDirection());
		assertEquals(strategy2, syncService2.getStrategy());
		assertEquals(credentialsSyncUser2, syncService2.getServerUser());
		
		syncDBManager.deleteSyncServerForUser(syncUser1, testURI, dbSession);
		services = syncDBManager.getSyncServices(syncUser1, null, true, dbSession);
		assertFalse(services.contains(service));
		assertEquals(0, services.size());
		
		List<SyncService> syncServers = syncDBManager.getSyncServices(null, null, true, dbSession);
		assertEquals(1, syncServers.size());
	}

	/**
	 * Test for all "sync_data" queries
	 * @throws ParseException 
	 */
	@Test
	public void testGetLastSyncData() throws ParseException {
		/*
		 * get last successful sync date
		 */
		final Class<? extends Resource> resourceType = BibTex.class;
		final Date expected = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse("2011-02-02 23:00:00");
		assertEquals(expected, syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, null, dbSession).getLastSyncDate());

		/*
		 * check that no synchronization is running 
		 */
		assertNull(syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, SynchronizationStatus.RUNNING, dbSession));

		/*
		 * insert new data in db
		 * We round the date to seconds, because the used MySQL column has such
		 * a low resolution.
		 */
		final Date date = new Date((new Date().getTime() / 1000) * 1000);
		syncDBManager.insertSynchronizationData(syncUser1, bibsonomyURI, resourceType, date, SynchronizationStatus.RUNNING, dbSession);

		/*
		 * check added data
		 */
		final SynchronizationData data = syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, null, dbSession);
		assertNotNull(data);
		assertEquals(date, data.getLastSyncDate());
		assertEquals(SynchronizationStatus.RUNNING, data.getStatus());
		assertEquals(resourceType, data.getResourceType());
		assertEquals(bibsonomyURI, data.getService());

		/*
		 * set status of added data to done (simulate successful synchronization)
		 */
		syncDBManager.updateSyncData(syncUser1, bibsonomyURI, resourceType, data.getLastSyncDate(), SynchronizationStatus.DONE, "", dbSession);

		/*
		 * check that no synchronization is running 
		 */
		assertNull(syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, SynchronizationStatus.RUNNING, dbSession));

		final SynchronizationData data2 = syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, null, dbSession);
		assertEquals(SynchronizationStatus.DONE, data2.getStatus());

	}

}
