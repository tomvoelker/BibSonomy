package org.bibsonomy.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.sync.SynchronizationClient;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationClientTest extends AbstractDatabaseManagerTest {
	private static final String SYNC_SERVER_URI = "http://www.test.de/";
	private static final String SERVER_USER_NAME = "syncServer";
	private static final String CLIENT_USER_NAME = "sync2";

	private User serverUser;
	private User clientUser;

	private DBLogic clientLogic;
	private DBLogic serverLogic;

	private static final String[] BOOKMARK_KEYS = new String[]{
		"9814aac6058e6db6c35ffe151f4c4c53", // changed on client
		"d9f4bd052fe19c2da43a8602de15896d", // changed on server
		"60f6867a5c81143fc66cf6fe7a919d1d", // created on client
		"b89c5230f929a2c9af0c808b17fae120", // created on server
		"28d637eca8ef360612a238ac56900d54"  // no changes
	};

	final String[] PUBLICATION_KEYS = new String[]{
			"4841e7b5c7c23c613590fa4b79725498", // changed on client
			"4549ac62ae226657cd17d93dabfd6075", // changed on server
			"4533fe874079584ea4700da84b4d13ae", // created on client
			"2ad021608b51b6f9e4a45933ca63ed9e", // created on server
			"3d6ec7b6695976eeec379dcc55ae9cb1"  // no changes
	};

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final String RESULT_STRING = "created on client: 1, created on server: 1, updated on client: 1, updated on server: 1, deleted on client: 1, deleted on server: 1";

	@SuppressWarnings("unchecked")
	@Before
	public void initialize() {
		/*
		 * create server user
		 */
		serverUser = new User();
		serverUser.setName(SERVER_USER_NAME);
		serverUser.setRole(Role.SYNC);
		serverUser.setGroups(new ArrayList<Group>());
		serverUser.getGroups().add(new Group("jbhj"));

		/*
		 * create client user
		 */
		clientUser = new User();
		clientUser.setName(CLIENT_USER_NAME);
		clientUser.setRole(Role.SYNC);

		/*
		 * create the logic interfaces
		 */
		clientLogic = new DBLogic(clientUser, new IbatisDBSessionFactory());
		serverLogic = new DBLogic(serverUser, new IbatisDBSessionFactory());

		/*
		 * iterate over all resource types
		 */
		final Class<? extends Resource> resourceTypes[] = new Class[]{BibTex.class, Bookmark.class};

		for (final Class<? extends Resource> clazz : resourceTypes) {
			/*
			 * create server posts
			 */
			final List<Post<?>> serverPosts = new ArrayList<Post<?>>();

			// post 1 "no changes" created and modified before last
			// synchronization
			serverPosts.add(createPost("no changes", "2011-01-10 14:32:00", "2011-01-31 14:32:00", serverUser, clazz));

			// post 2 "deleted on server" is not in the server database

			// post 3 "deleted on client" created and modified before last
			// synchronization
			serverPosts.add(createPost("deleted on client", "2011-01-10 14:55:00", "2011-01-15 14:33:00", serverUser, clazz));

			// post 4 "changed on server" created before, changed after the last
			// synchronization
			serverPosts.add(createPost("changed on server", "2010-09-16 14:35:00", "2011-03-16 17:30:00", serverUser, clazz));

			// post 5 "changed on client" created and modified before last
			// synchronization
			serverPosts.add(createPost("changed on client", "2009-12-31 23:59:00", "2010-02-01 17:23:00", serverUser, clazz));

			// post 6 "created on server" created and modified after last
			// synchronization
			serverPosts.add(createPost("created on server", "2011-03-18 11:20:00", "2011-03-18 11:20:00", serverUser, clazz));

			// post 7 "created on client" is not in the server database*

			serverLogic.createPosts(serverPosts);

			/*
			 * create client posts
			 */
			final List<Post<?>> clientPosts = new ArrayList<Post<?>>();

			// post 1: "post without changes" is the same post as in database
			clientPosts.add(createPost("no changes", "2011-01-10 14:32:00", "2011-01-31 14:32:00", clientUser, clazz));

			// post 2: "post deleted on server" here created and modified before
			// last synchronization
			clientPosts.add(createPost("post deleted on server", "2009-11-02 12:20:00", "2009-11-02 12:23:00", clientUser, clazz));

			// post 3: "post deleted on client" is not in the client list

			// post 4: "post changed on server" same hashes and create date as
			// in database, but change date is before last synchronization
			clientPosts.add(createPost("changed on server", "2010-09-16 14:35:00", "2011-01-16 17:58:00", clientUser, clazz));

			// post 5: "post changed on client" same hashes and create date as
			// in database, but change date is after the last synchronization
			// date
			clientPosts.add(createPost("changed on client", "2009-12-31 23:59:00", "2011-03-25 10:59:00", clientUser, clazz));

			// post 6: "post created on server" is not in the client list

			// post 7: "post created on client" created and modified after last
			// synchronization
			clientPosts.add(createPost("created on client", "2011-03-18 14:13:00", "2011-03-18 14:13:00", clientUser, clazz));

			clientLogic.createPosts(clientPosts);
		}
	}


	@Test
	public void testSynchronization() throws URISyntaxException {
		final SynchronizationClient sync = new SynchronizationClient(new IbatisDBSessionFactory());

		/*
		 * setup server
		 */
		final URI syncServer = new URI(SYNC_SERVER_URI);

		/*
		 * setup synchronization client
		 */
		sync.setOwnUri(new URI(SYNC_SERVER_URI));

		/*
		 * check that synchronization is enabled
		 */
		assertEquals(SYNC_SERVER_URI, clientLogic.getSyncServer(clientUser.getName()).get(0).getService().toString());

		/*
		 * sync + check publications and bookmarks
		 */
		syncResources(sync, syncServer, BibTex.class, PUBLICATION_KEYS);
		syncResources(sync, syncServer, Bookmark.class, BOOKMARK_KEYS);

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
		final SynchronizationData syncData = sync.synchronize(clientLogic, syncServer, Bookmark.class);
		assertNotNull(syncData);
		assertEquals(SynchronizationStatus.DONE, syncData.getStatus());
		System.out.println(syncData.getInfo());
		assertEquals("created on " + rightHost + ": 1, deleted on " + rightHost + ": 1", syncData.getInfo());
		/*
		 * check for posts on server
		 */
		final Map<String, SynchronizationPost> map = mapFromList(rightLogic.getSyncPosts(rightUser.getName(), Bookmark.class));
		assertTrue(map.containsKey(posts.get(0).getResource().getIntraHash()));
		assertFalse(map.containsKey(deleteHash));
	}
	
	private static Map<String, SynchronizationPost> mapFromList(final List<SynchronizationPost> syncPosts) {
		final Map<String, SynchronizationPost> map = new HashMap<String, SynchronizationPost>();
		for (final SynchronizationPost post : syncPosts) {
			map.put(post.getIntraHash(), post);
		}
		return map;
	}
	


	/**
	 * Helper method to check synchronicity of client and server.
	 * 
	 * @param sync
	 * @param syncServer
	 * @param resourceType
	 * @param keys
	 */
	private void syncResources(final SynchronizationClient sync, final URI syncServer, final Class<? extends Resource> resourceType, final String[] keys) {
		final SynchronizationData data = sync.synchronize(clientLogic, syncServer, resourceType);
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

	/**
	 * helper method to create posts of the given type
	 * 
	 * @param <T>
	 * @param title
	 * @param createDate
	 * @param changeDate
	 * @param user
	 * @param resourceType
	 * @return
	 */
	private <T extends Resource> Post<T> createPost(String title, String createDate, String changeDate, User user, Class<T> resourceType) {
		final Post<T> post = ModelUtils.generatePost(resourceType);
		post.setUser(user);
		try {
			post.setChangeDate(DATE_FORMAT.parse(changeDate));
			post.setDate(DATE_FORMAT.parse(createDate));
			if (resourceType == Bookmark.class) {
				final Bookmark bookmark = (Bookmark)post.getResource();
				title = title.replace(" ", "-");
				bookmark.setUrl("http://www." + title + ".com");
			}
		} catch (ParseException ex) {
			// ignore
		}
		post.getResource().setTitle(title);
		return post;
	}
	
	private static void wait(final int seconds) {
		try {
			Thread.sleep(1000 * seconds);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
