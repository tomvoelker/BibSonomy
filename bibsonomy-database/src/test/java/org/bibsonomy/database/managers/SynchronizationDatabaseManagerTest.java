package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static SynchronizationDatabaseManager syncDBManager;

	private final String syncUser1 = "syncuser1";
	private final URI testURI;
	private final URI bibsonomyURI;
	
	@BeforeClass
	public static void setupManager() {
		syncDBManager = SynchronizationDatabaseManager.getInstance();
	}
	
	public SynchronizationDatabaseManagerTest() throws URISyntaxException {
		this.testURI = new URI("http://www.test.de/");
		this.bibsonomyURI = new URI("http://www.bibsonomy.org/");
	}

	/**
	 * test for all access to the table `sync`
	 */
	@Test
	public void testSyncService() {
		final SyncService service = new SyncService();
		service.setService(testURI);
		
		final Properties credentialsSyncUser1 = new Properties();
		credentialsSyncUser1.setProperty("name", syncUser1);
		credentialsSyncUser1.setProperty("apiKey", "1546545646565");
		service.setServerUser(credentialsSyncUser1);

		syncDBManager.createSyncServerForUser(dbSession, syncUser1, testURI, Bookmark.class, credentialsSyncUser1, SynchronizationDirection.SERVER_TO_CLIENT, ConflictResolutionStrategy.SERVER_WINS);

		List<SyncService> services = syncDBManager.getSyncServersForUser(syncUser1, dbSession);
		assertTrue(services.contains(service));
		assertEquals(1, services.size());
		final SyncService syncService = services.get(0);
		assertEquals(Bookmark.class, syncService.getResourceType());
		assertEquals(SynchronizationDirection.SERVER_TO_CLIENT, syncService.getDirection());
		assertEquals(ConflictResolutionStrategy.SERVER_WINS, syncService.getStrategy());
		
		final Properties credentialsSyncUser2 = new Properties();
		credentialsSyncUser2.setProperty("name", "syncUser2");
		credentialsSyncUser2.setProperty("apiKey", "jjkhjhjkhk");
		service.setServerUser(credentialsSyncUser2);
		syncDBManager.updateSyncServerForUser(dbSession, syncUser1, testURI, BibTex.class, credentialsSyncUser2, SynchronizationDirection.SERVER_TO_CLIENT, ConflictResolutionStrategy.LAST_WINS);
		
		services = syncDBManager.getSyncServersForUser(syncUser1, dbSession);
		assertTrue(services.contains(service));
		assertEquals(1, services.size());
		final SyncService syncService2 = services.get(0);
		assertEquals(BibTex.class, syncService2.getResourceType());
		assertEquals(SynchronizationDirection.SERVER_TO_CLIENT, syncService2.getDirection());
		
		syncDBManager.deleteSyncServerForUser(dbSession, syncUser1, testURI);
		services = syncDBManager.getSyncServersForUser(syncUser1, dbSession);
		assertFalse(services.contains(service));
		assertEquals(0, services.size());
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
		assertEquals(syncUser1, data.getUserName());
		assertEquals(date, data.getLastSyncDate());
		assertEquals(SynchronizationStatus.RUNNING, data.getStatus());
		assertEquals(resourceType, data.getResourceType());
		assertEquals(bibsonomyURI, data.getService());

		/*
		 * set status of added data to done (simulate successful synchronization)
		 */
		syncDBManager.updateSyncData(dbSession, syncUser1, bibsonomyURI, resourceType, data.getLastSyncDate(), SynchronizationStatus.DONE, "");

		/*
		 * check that no synchronization is running 
		 */
		assertNull(syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, SynchronizationStatus.RUNNING, dbSession));

		final SynchronizationData data2 = syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, null, dbSession);
		assertEquals(SynchronizationStatus.DONE, data2.getStatus());

	}

}
