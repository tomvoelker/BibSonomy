package org.bibsonomy.synchronization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class ExtendedSyncClientTest extends AbstractSynchronizationClientTest {

	/**
	 * test the sync client
	 */
	@Test
	public void test() {
		/*
		 * normal synchronization, tested by other tests 
		 */
		sync.synchronize(clientLogic, this.syncServer);
		
		/*
		 * make some changes and test again 
		 */
		/*
		 * FIXME: Since we have only a resolution of 1 second in MySQL, we must wait
		 * at least one second - otherwise we get a duplicate key exception when 
		 * inserting the sync data. 
		 */
		wait(1);
		
		/*
		 * change some posts on server
		 */
		changeLeftSyncAndCheck(syncServer, "server", serverUser, serverLogic, "client", clientUser, clientLogic, "b89c5230f929a2c9af0c808b17fae120");

		wait(1);
		/*
		 * change some posts on client
		 */
		changeLeftSyncAndCheck(syncServer, "client", clientUser, clientLogic, "server", serverUser, serverLogic, "9814aac6058e6db6c35ffe151f4c4c53");
		
		/*
		 * test different strategies
		 */
		Map<Class<? extends Resource>, SynchronizationData> data;
		updateServer(ConflictResolutionStrategy.FIRST_WINS);
		makeConflict(clientLogic, serverLogic, 0);
		data = sync.synchronize(clientLogic, this.syncServer);
		assertEquals("updated on server: 1", data.get(BibTex.class).getInfo());
		
		updateServer(ConflictResolutionStrategy.SERVER_WINS);
		makeConflict(clientLogic, serverLogic, 1);
		data = sync.synchronize(clientLogic, this.syncServer);
		assertEquals("updated on client: 1", data.get(BibTex.class).getInfo());
		makeConflict(serverLogic, clientLogic, 2);
		data = sync.synchronize(clientLogic, this.syncServer);
		assertEquals("updated on client: 1", data.get(BibTex.class).getInfo());
		
		updateServer(ConflictResolutionStrategy.CLIENT_WINS);
		makeConflict(clientLogic, serverLogic, 3);
		data = sync.synchronize(clientLogic, this.syncServer);
		assertEquals("updated on server: 1", data.get(BibTex.class).getInfo());
		makeConflict(serverLogic, clientLogic, 4);
		data = sync.synchronize(clientLogic, this.syncServer);
		assertEquals("updated on server: 1", data.get(BibTex.class).getInfo());
		
		/*
		 * Test post with changed hash
		 */
		final String clientUserName = clientLogic.getAuthenticatedUser().getName();
		Post<? extends Resource> post = clientLogic.getPostDetails(BOOKMARK_KEYS[1], clientUserName);
		Bookmark book = (Bookmark)post.getResource();
		book.setUrl("http://www.changed-hash.com");
		book.setTitle("changed-hash");
		wait(1);
		post.setChangeDate(new Date());
		clientLogic.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_ALL);
		/*
		 * new hash: 6ca4e7931a99a90d3157fdb7318507fd
		 */
		wait(1);
		data = sync.synchronize(clientLogic, syncServer);
		assertEquals("created on server: 1, deleted on server: 1", data.get(Bookmark.class).getInfo());
		
		/*
		 * and counterpart
		 */
		wait(1);
		post = serverLogic.getPostDetails("6ca4e7931a99a90d3157fdb7318507fd", SERVER_USER_NAME);
		book = (Bookmark)post.getResource();
		book.setTitle("changed-again");
		book.setUrl("http://www.changed-again.com");
		post.setChangeDate(new Date());
		serverLogic.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_ALL);
		/*
		 * new hash: b33ad42e584f8bc3d73ad18332a62b26
		 */
		wait(1);
		data = sync.synchronize(clientLogic, syncServer);
		assertEquals("created on client: 1, deleted on client: 1", data.get(Bookmark.class).getInfo());
	}
	
	private void makeConflict (final LogicInterface earlier, final LogicInterface later, final int pos) {
		wait(1);
		Date date = new Date();
		Post<? extends Resource> post = earlier.getPostDetails(PUBLICATION_KEYS[pos], earlier.getAuthenticatedUser().getName());
		post.setChangeDate(date);
		earlier.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_ALL);
		wait(1);
		date = new Date();
		post = later.getPostDetails(PUBLICATION_KEYS[pos], later.getAuthenticatedUser().getName());
		post.setChangeDate(date);
		later.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_ALL);
	}
	
	private void updateServer(final ConflictResolutionStrategy strategy) {
		final Properties userCredentials = new Properties();
		userCredentials.setProperty("userName", SERVER_USER_NAME);
		userCredentials.setProperty("apiKey", serverUser.getApiKey());
		
		final SyncService service = createServerService(strategy, userCredentials, SynchronizationDirection.BOTH);
		
		clientLogic.updateSyncServer(clientLogic.getAuthenticatedUser().getName(), service);
	}

	private void changeLeftSyncAndCheck(final URI syncServer, final String leftHost, final User leftUser, final LogicInterface leftLogic, final String rightHost, final User rightUser, final LogicInterface rightLogic, final String deleteHash) {
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
		final Map<Class<? extends Resource>, SynchronizationData> syncData = sync.synchronize(clientLogic, syncServer);
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

}
