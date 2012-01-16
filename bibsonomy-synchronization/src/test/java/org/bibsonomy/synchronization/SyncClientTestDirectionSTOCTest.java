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
 * @version $Id$
 */
public class SyncClientTestDirectionSTOCTest extends AbstractSynchronizationClientTest {

	private static final String RESULT_STRING = "created on client: 1, updated on client: 1, deleted on client: 1"; 

	private final String[] MODIFIED_PUBLICATION_KEYS = new String[]{
		"4841e7b5c7c23c613590fa4b79725498", // changed on client
		"4549ac62ae226657cd17d93dabfd6075", // changed on server
		"2ad021608b51b6f9e4a45933ca63ed9e", // created on server
		"3d6ec7b6695976eeec379dcc55ae9cb1",  // no changes
		"ce752b480d6ed1ef074f2f43a39f3f01" //deleted on client
	};
	
	private static final String[] MODIFIED_BOOKMARK_KEYS = new String[]{
		"9814aac6058e6db6c35ffe151f4c4c53", // changed on client
		"d9f4bd052fe19c2da43a8602de15896d", // changed on server
		"b89c5230f929a2c9af0c808b17fae120", // created on server
		"28d637eca8ef360612a238ac56900d54", // no changes
		"bf3a2a121bc7887c518e278ff34f984a" //deleted on client
	};
	
	private final static ConflictResolutionStrategy STRATEGY = ConflictResolutionStrategy.LAST_WINS;
	private final static SynchronizationDirection DIRECTION = SynchronizationDirection.SERVER_TO_CLIENT;

	/**
	 * tests server to client direction
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
			
			checkModifiedKeys(resourceType, serverPosts, "server");
			checkKeys(resourceType, clientPosts, "client");
		}
		
	}

}
