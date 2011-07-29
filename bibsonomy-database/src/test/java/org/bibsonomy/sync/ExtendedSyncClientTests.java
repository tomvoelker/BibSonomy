package org.bibsonomy.sync;

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

import org.bibsonomy.database.DBLogic;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;

/**
 * @author wla
 * @version $Id$
 */
public class ExtendedSyncClientTests extends AbstractSynchronizationClientTest {

	@Override
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
		changeLeftSyncAndCheck(sync, syncServer, "server", serverUser, serverLogic, "client", clientUser, clientLogic, "b89c5230f929a2c9af0c808b17fae120");

		wait(1);
		/*
		 * change some posts on client
		 */
		changeLeftSyncAndCheck(sync, syncServer, "client", clientUser, clientLogic, "server", serverUser, serverLogic, "9814aac6058e6db6c35ffe151f4c4c53");
		
		
	}
		
	private void changeLeftSyncAndCheck(final SynchronizationClient sync, final URI syncServer, final String leftHost, final User leftUser, final DBLogic leftLogic, final String rightHost, final User rightUser, final DBLogic rightLogic, final String deleteHash) {
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
