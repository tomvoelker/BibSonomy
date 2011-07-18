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
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
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

		syncDBManager.createSyncServerForUser(dbSession, testURI, syncUser1, credentialsSyncUser1);

		List<SyncService> services = syncDBManager.getSyncServersForUser(syncUser1, dbSession);
		assertTrue(services.contains(service));
		assertEquals(1, services.size());
		
		final Properties credentialsSyncUser2 = new Properties();
		credentialsSyncUser2.setProperty("name", "syncUser2");
		credentialsSyncUser2.setProperty("apiKey", "jjkhjhjkhk");
		service.setServerUser(credentialsSyncUser2);
		syncDBManager.updateSyncServerForUser(dbSession, syncUser1, testURI, credentialsSyncUser2);
		
		services = syncDBManager.getSyncServersForUser(syncUser1, dbSession);
		assertTrue(services.contains(service));
		assertEquals(1, services.size());
		
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
		assertEquals(expected, syncDBManager.getLastSynchronizationDate(syncUser1, bibsonomyURI, resourceType, dbSession));

		/*
		 * check that unsuccessful data in db
		 */
		assertNull(syncDBManager.getCurrentSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession));

		/*
		 * insert new data in db
		 * We round the date to seconds, because the used MySQL column has such
		 * a low resolution.
		 */
		final Date date = new Date((new Date().getTime() / 1000) * 1000);
		syncDBManager.insertSyncronizationData(syncUser1, bibsonomyURI, resourceType, date, "undone", dbSession);

		/*
		 * check added data
		 */
		final SynchronizationData data = syncDBManager.getCurrentSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession);
		assertNotNull(data);
		assertEquals(syncUser1, data.getUserName());
		assertEquals(date, data.getLastSyncDate());
		assertEquals("undone", data.getStatus());
		assertEquals(resourceType, data.getResourceType());
		assertEquals(bibsonomyURI, data.getService());

		/*
		 * set status of added data to done (simulate successful synchronization)
		 */
		syncDBManager.updateSyncStatus(dbSession, data, "done");

		assertNull(syncDBManager.getCurrentSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession));

		final List<SynchronizationData> dataList = syncDBManager.getSynchronizationData(syncUser1, bibsonomyURI, resourceType, dbSession);
		assertEquals(2, dataList.size());

	}

}
