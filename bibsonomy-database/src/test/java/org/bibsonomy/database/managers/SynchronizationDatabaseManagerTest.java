/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.text.ParseException;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wla
 */
public class SynchronizationDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static SynchronizationDatabaseManager syncDBManager;
	private static URI testURI;
	private static URI bibsonomyURI;
	private static URI deviceURI;
	private static URI serverURI;
	private static Properties credentialsSyncUser1;
	private static Class<Bookmark> resourceType;
	private static SynchronizationDirection direction;
	private static ConflictResolutionStrategy strategy;
	
	private final static String syncUser1 = "syncuser1";

	@BeforeClass
	public static void setupManager() {
		syncDBManager = SynchronizationDatabaseManager.getInstance();
		
		testURI = TestUtils.createURI("http://www.test.de/");
		bibsonomyURI = TestUtils.createURI("http://www.bibsonomy.org/");
		deviceURI = TestUtils.createURI("client://android/123456789012?device=NexusOne");
		serverURI = TestUtils.createURI("http://www.biblicious.org/");

		credentialsSyncUser1 = new Properties();
		credentialsSyncUser1.setProperty("name", syncUser1);
		credentialsSyncUser1.setProperty("apiKey", "1546545646565");
		
		resourceType = Bookmark.class;
		direction = SynchronizationDirection.SERVER_TO_CLIENT;
		strategy = ConflictResolutionStrategy.SERVER_WINS;
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
		syncDBManager.updateSyncData(syncUser1, deviceURI, Resource.class, lastSyncData.getLastSyncDate(), running, "", null, this.dbSession);
		
		final SynchronizationData lastSyncDataAfterUpdate = syncDBManager.getLastSyncData(syncUser1, deviceURI, Resource.class, null, this.dbSession);
		assertEquals(running, lastSyncDataAfterUpdate.getStatus());
	}
	
	/**
	 * test getSyncServices() statement
	 */
	@Test
	public void testGetSyncClients() {
		final List<SyncService> syncClients = syncDBManager.getSyncServiceSettings(syncUser1, null, false, this.dbSession);
		assertEquals(2, syncClients.size());
	}	
	
	/**
	 * test getAutoSyncServer() statement
	 */
	@Test
	public void testgetAutoSyncServer() {

		// update sync-service testURI for SyncUser1 with auto-sync settings
		SyncService autoSyncService = syncDBManager.getSyncServiceDetails(testURI, this.dbSession);
		assertEquals(testURI, autoSyncService.getService());
		autoSyncService.setServerUser(credentialsSyncUser1);
		autoSyncService.setInitialAutoSync(false);
		autoSyncService.setAutosync(true);
		autoSyncService.setDirection(direction);
		autoSyncService.setStrategy(strategy);
		autoSyncService.setResourceType(resourceType);
		syncDBManager.createSyncServerForUser(syncUser1, autoSyncService, this.dbSession);

		// retrieve  auto-sync service from db
		final List<SyncService> autoSyncServers = syncDBManager.getAutoSyncServer(this.dbSession);
		assertEquals(1, autoSyncServers.size());
		assertEquals(syncUser1, autoSyncServers.get(0).getUserName());
	}

	/**
	 * test for all access to the table `sync`
	 */
	@Test
	public void testSyncService() {		

		
		final SyncService service = new SyncService();
		service.setService(testURI);
		service.setServerUser(credentialsSyncUser1);
		service.setResourceType(resourceType);
		service.setDirection(direction);
		service.setStrategy(strategy);

		syncDBManager.createSyncServerForUser(syncUser1, service, dbSession);

		List<SyncService> services = syncDBManager.getSyncServiceSettings(syncUser1, null, true, dbSession);
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
		
		services = syncDBManager.getSyncServiceSettings(syncUser1, null, true, dbSession);
		assertTrue(services.contains(service));
		assertEquals(1, services.size());
		final SyncService syncService2 = services.get(0);
		assertEquals(resourceType2, syncService2.getResourceType());
		assertEquals(direction, syncService2.getDirection());
		assertEquals(strategy2, syncService2.getStrategy());
		assertEquals(credentialsSyncUser2, syncService2.getServerUser());
		
		syncDBManager.deleteSyncServerForUser(syncUser1, testURI, dbSession);
		services = syncDBManager.getSyncServiceSettings(syncUser1, null, true, dbSession);
		assertFalse(services.contains(service));
		assertEquals(0, services.size());
		
		List<SyncService> syncServers = syncDBManager.getSyncServiceSettings(null, null, true, dbSession);
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
		final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:SS");
		final Date expected = fmt.parseDateTime("2011-02-02 23:00:00").toDate();
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
		syncDBManager.updateSyncData(syncUser1, bibsonomyURI, resourceType, data.getLastSyncDate(), SynchronizationStatus.DONE, "", null, dbSession);

		/*
		 * check that no synchronization is running 
		 */
		assertNull(syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, SynchronizationStatus.RUNNING, dbSession));

		final SynchronizationData data2 = syncDBManager.getLastSyncData(syncUser1, bibsonomyURI, resourceType, null, dbSession);
		assertEquals(SynchronizationStatus.DONE, data2.getStatus());

	}

}
