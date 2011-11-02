package org.bibsonomy.synchronization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationAction;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;

/**
 * @author wla
 * @version $Id$
 */
public class TwoStepSynchronizationClientTest extends AbstractSynchronizationClientTest {


	private static final String[] MODIFIED_BOOKMARK_KEYS = new String[]{
		"9814aac6058e6db6c35ffe151f4c4c53", // changed on client
		"d9f4bd052fe19c2da43a8602de15896d", // changed on server
		"60f6867a5c81143fc66cf6fe7a919d1d", // created on client
		"b89c5230f929a2c9af0c808b17fae120", // created on server
		"28d637eca8ef360612a238ac56900d54"  // no changes
	};

	private static final String[] MODIFIED_PUBLICATION_KEYS = new String[]{
			"4841e7b5c7c23c613590fa4b79725498", // changed on client
			"4549ac62ae226657cd17d93dabfd6075", // changed on server
			"4533fe874079584ea4700da84b4d13ae", // created on client
			"2ad021608b51b6f9e4a45933ca63ed9e", // created on server
			"3d6ec7b6695976eeec379dcc55ae9cb1"  // no changes
	};

	private static final Map<Class<? extends Resource>, String[]> KEYS = new HashMap<Class<? extends Resource>, String[]>(2);
	static {
		KEYS.put(Bookmark.class, MODIFIED_BOOKMARK_KEYS);
		KEYS.put(BibTex.class, MODIFIED_PUBLICATION_KEYS);
	}


	private final String RESULT_STRING = "created on client: 1, created on server: 1, updated on client: 1, updated on server: 1, deleted on client: 1, deleted on server: 1";

	@Override
	public void test() {
		final TwoStepSynchronizationClient sync = new TwoStepSynchronizationClient();
		
		try {
			sync.setOwnUri(new URI(SYNC_CLIENT_URI));
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		}

		/*
		 * check that synchronization is enabled
		 */
		final SyncService syncServer = clientLogic.getSyncServer(clientUser.getName()).get(0);
		URI syncServerUri = syncServer.getService();
		assertEquals(SYNC_SERVER_URI, syncServerUri.toString());

		/*
		 * get and check sync plan
		 */
		final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan = sync.getSyncPlan(clientLogic, syncServer);
		/*
		 * one plan for each resource type
		 */
		assertEquals(2, syncPlan.size());
		/*
		 * status should be "PLANNED"
		 */
		Date plannedDate = null;
		for (final Class<? extends Resource> resourceType : syncPlan.keySet()) {
			final SynchronizationData syncData = sync.getLastSyncData(syncServer, resourceType);
			assertEquals(SynchronizationStatus.PLANNED, syncData.getStatus());
			plannedDate = syncData.getLastSyncDate();
		}
		assertNotNull(plannedDate);
		/*
		 * ... we'll have to wait a second (resolution of lastSyncDate column)
		 */
		wait(1);
		
		/*
		 * we ask for the plan again (stupid, but user's could do this)
		 */
		final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan2 = sync.getSyncPlan(clientLogic, syncServer);
		/*
		 * one plan for each resource type
		 */
		assertEquals(2, syncPlan2.size());
		/*
		 * status should still be "PLANNED"
		 */
		for (final Class<? extends Resource> resourceType : syncPlan2.keySet()) {
			final SynchronizationData syncData = sync.getLastSyncData(syncServer, resourceType);
			/*
			 * should be a different date now
			 */
			assertTrue(plannedDate.before(syncData.getLastSyncDate()));
			assertEquals(SynchronizationStatus.PLANNED, syncData.getStatus());
		}
		
		
		for (final Entry<Class<? extends Resource>, List<SynchronizationPost>> entry : syncPlan2.entrySet()) {
			final Class<? extends Resource> resourceType = entry.getKey();
			final List<SynchronizationPost> resourceSyncPlan = entry.getValue();
			
			assertEquals(7, resourceSyncPlan.size());

			assertEquals(SynchronizationStatus.PLANNED, sync.getLastSyncData(syncServer, resourceType).getStatus());
			
			checkSyncPlan(resourceSyncPlan, resourceType);
			
			syncResources(sync, syncServerUri, resourceType, KEYS.get(resourceType), resourceSyncPlan);
			
			assertEquals(SynchronizationStatus.DONE, sync.getLastSyncData(syncServer, resourceType).getStatus());
		}
		

		/* *********************************************************************
		 * 
		 * next steps: add/delete/modify posts on client and server and then sync
		 * 
		 */

		/*
		 * change some posts on server
		 */
		changeLeftSyncAndCheck(sync, syncServer, "server", serverUser, serverLogic, "client", clientUser, clientLogic, "b89c5230f929a2c9af0c808b17fae120");
		/*
		 * FIXME: Since we have only a resolution of 1 second in MySQL, we must wait
		 * at least one second - otherwise we get a duplicate key exception when 
		 * inserting the sync data. 
		 */
		wait(1);
		/*
		 * change some posts on client
		 */
		changeLeftSyncAndCheck(sync, syncServer, "client", clientUser, clientLogic, "server", serverUser, serverLogic, "9814aac6058e6db6c35ffe151f4c4c53");
	}
	


	private void changeLeftSyncAndCheck(final TwoStepSynchronizationClient sync, final SyncService syncServer, final String leftHost, final User leftUser, final LogicInterface leftLogic, final String rightHost, final User rightUser, final LogicInterface rightLogic, final String deleteHash) {
		final Date now = new Date();
		final List<Post<?>> posts = new ArrayList<Post<?>>();
		/*
		 * add a post
		 */
		posts.add(createPost("added after sync on " + leftHost, DATE_FORMAT.format(now), DATE_FORMAT.format(now), leftUser, Bookmark.class));
		leftLogic.createPosts(posts);
		/*
		 * delete a post
		 */
		leftLogic.deletePosts(leftUser.getName(), Collections.singletonList(deleteHash));
		/*
		 * sync
		 */
		final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan = sync.getSyncPlan(clientLogic, syncServer);
		final Map<Class<? extends Resource>, SynchronizationData> syncData = sync.synchronize(clientLogic, syncServer, syncPlan);
		
		assertNotNull(syncData);
		assertTrue(syncData.containsKey(Bookmark.class));
		final SynchronizationData syncDataBookmark = syncData.get(Bookmark.class);
		assertEquals(SynchronizationStatus.DONE, syncDataBookmark.getStatus());
		assertEquals("created on " + rightHost + ": 1, deleted on " + rightHost + ": 1", syncDataBookmark.getInfo());
		/*
		 * check for posts on server
		 */
		final Map<String, SynchronizationPost> map = mapFromList(rightLogic.getSyncPosts(rightUser.getName(), Bookmark.class));
		assertTrue(map.containsKey(posts.get(0).getResource().getIntraHash()));
		assertFalse(map.containsKey(deleteHash));
	}
	
	/**
	 * Helper method to check the sync plan
	 * 
	 * Basically checks that order of posts in list is not changed.
	 * 
	 */
	private void checkSyncPlan(final List<SynchronizationPost> syncPlan, final Class<? extends Resource> resourceType) {
		int index = 0;
		assertEquals(SynchronizationAction.OK, syncPlan.get(index++).getAction());
		assertEquals(SynchronizationAction.DELETE_CLIENT, syncPlan.get(index++).getAction());
		assertEquals(SynchronizationAction.UPDATE_CLIENT, syncPlan.get(index++).getAction());
		assertEquals(SynchronizationAction.UPDATE_SERVER, syncPlan.get(index++).getAction());
		assertEquals(SynchronizationAction.CREATE_SERVER, syncPlan.get(index++).getAction());

		/*
		 * the remaining two posts were not in the client's list and thus come
		 * from the server's list which is a hashmap - thus we can't expect to 
		 * get them in a certain order
		 */
		final SynchronizationPost syncPost = syncPlan.get(index++);
		if (syncPost.getIntraHash().equals(KEYS.get(resourceType)[3])) {
			assertEquals(SynchronizationAction.CREATE_CLIENT, syncPost.getAction());
			assertEquals(SynchronizationAction.DELETE_SERVER, syncPlan.get(index++).getAction());
		} else {
			assertEquals(SynchronizationAction.DELETE_SERVER, syncPost.getAction());
			assertEquals(SynchronizationAction.CREATE_CLIENT, syncPlan.get(index++).getAction());
		}
		
	}

	/**
	 * Helper method to check synchronicity of client and server.
	 * 
	 * @param sync
	 * @param syncServer
	 * @param resourceType
	 * @param keys
	 */
	private void syncResources(final TwoStepSynchronizationClient sync, final URI syncServer, final Class<? extends Resource> resourceType, final String[] keys, final List<SynchronizationPost> syncPlan) {
		/*
		 * do sync
		 */
		final SynchronizationData data = sync.synchronizeResource(clientLogic, serverLogic, serverUser.getName(), resourceType, SynchronizationDirection.BOTH, syncPlan);
		
		assertNotNull("synchronization was not successful", data);
		assertEquals(SynchronizationStatus.DONE, data.getStatus());
		assertEquals(RESULT_STRING, data.getInfo());

		/*
		 * compare posts on client and server
		 */
		final Map<String, SynchronizationPost> serverPosts = mapFromList(serverLogic.getSyncPosts(serverUser.getName(), resourceType));
		final Map<String, SynchronizationPost> clientPosts = mapFromList(clientLogic.getSyncPosts(clientUser.getName(), resourceType));

		assertEquals(5, serverPosts.size());
		assertEquals(serverPosts.size(), clientPosts.size());

		for (final String key : keys) {
			assertTrue(serverPosts.containsKey(key));
			assertTrue(clientPosts.containsKey(key));
			assertTrue(key + " is not same", clientPosts.get(key).isSame(serverPosts.get(key)));
		}
	}

}
