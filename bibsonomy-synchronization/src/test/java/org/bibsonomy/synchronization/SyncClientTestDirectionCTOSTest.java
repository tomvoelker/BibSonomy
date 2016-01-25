/**
 * BibSonomy-Synchronization - Handles user synchronization between BibSonomy authorities
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.synchronization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.junit.Test;

/**
 * @author wla
 */
public class SyncClientTestDirectionCTOSTest extends AbstractSynchronizationClientTest {
	private final String RESULT_STRING = "created on server: 1, updated on server: 1, deleted on server: 1";
	
	private final String[] MODIFIED_PUBLICATION_KEYS = new String[]{
		"4841e7b5c7c23c613590fa4b79725498", // changed on client
		"4549ac62ae226657cd17d93dabfd6075", // changed on server
		"4533fe874079584ea4700da84b4d13ae", // created on client
		"3d6ec7b6695976eeec379dcc55ae9cb1", // no changes
		"983514bda43910e1bf783554fb80e512"  // deleted on server
	};
	
	private final String[] MODIFIED_BOOKMARK_KEYS = new String[]{
		"9814aac6058e6db6c35ffe151f4c4c53", // changed on client
		"d9f4bd052fe19c2da43a8602de15896d", // changed on server
		"60f6867a5c81143fc66cf6fe7a919d1d", // created on client
		"28d637eca8ef360612a238ac56900d54", // no changes
		"5c89e301af8266532cc45ef5a324a037"  // deleted on server
	};
	
	private static final ConflictResolutionStrategy STRATEGY = ConflictResolutionStrategy.LAST_WINS;
	private static final SynchronizationDirection DIRECTION = SynchronizationDirection.CLIENT_TO_SERVER;
	

	/**
	 * tests sync client with client to server
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSync() {
		final Properties userCredentials = new Properties();
		userCredentials.setProperty("userName", SERVER_USER_NAME);
		userCredentials.setProperty("apiKey", serverUser.getApiKey());
		final SyncService service = createServerService(STRATEGY, userCredentials, DIRECTION);
		
		clientLogic.updateSyncServer(clientLogic.getAuthenticatedUser().getName(), service);
		setModifiedBookmarkKeys(MODIFIED_BOOKMARK_KEYS);
		setModifiedPublicationKeys(MODIFIED_PUBLICATION_KEYS);
		
		final Map<Class<? extends Resource>, SynchronizationData> syncData = sync.synchronize(clientLogic, this.syncServer);
		
		for (final Class<? extends Resource> resourceType : resourceTypes) {
			assertTrue(syncData.containsKey(resourceType));
			
			final SynchronizationData data = syncData.get(resourceType);
			assertNotNull(data);
			
			assertEquals(RESULT_STRING, data.getInfo());		
			/*
			 * compare posts on client and server
			 */
			final Map<String, SynchronizationPost> serverPosts = mapFromList(serverLogic.getSyncPosts(serverUser.getName(), resourceType));
			final Map<String, SynchronizationPost> clientPosts = mapFromList(clientLogic.getSyncPosts(clientUser.getName(), resourceType));

			assertEquals(5, serverPosts.size());
			assertEquals(serverPosts.size(), clientPosts.size());
			
			checkModifiedKeys(resourceType, clientPosts, "client");
			checkKeys(resourceType, serverPosts, "server");
		}
	}

}
