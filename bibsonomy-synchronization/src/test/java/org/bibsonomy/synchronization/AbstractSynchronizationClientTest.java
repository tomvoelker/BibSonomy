/**
 * BibSonomy-Synchronization - Handles user synchronization between BibSonomy authorities
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.testutil.TestServerBuilder;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.TestUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mortbay.jetty.Server;

/**
 * @author wla
 */
public abstract class AbstractSynchronizationClientTest extends AbstractDatabaseManagerTest {

	private static final int PORT = 41253; // if you change the port here you must change it in the test sql script (database module) too
	protected static final String SYNC_SERVER_URI = "http://localhost:" + PORT + "/"; //default rest api test server url
	protected static final String SYNC_CLIENT_URI = "http://www.test.de/";
	protected static final String SERVER_USER_NAME = "syncServer";
	protected static final String SERVER_USER_APIKEY = "15cb586b630cc343cd60684807bf4785";
	protected static final String CLIENT_USER_NAME = "sync2";

	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("rawtypes")
	protected static final Class[] resourceTypes = new Class[] {Bookmark.class, BibTex.class};
	
	protected static final String[] BOOKMARK_KEYS = new String[]{
		"9814aac6058e6db6c35ffe151f4c4c53", // changed on client
		"d9f4bd052fe19c2da43a8602de15896d", // changed on server
		"60f6867a5c81143fc66cf6fe7a919d1d", // created on client
		"b89c5230f929a2c9af0c808b17fae120", // created on server
		"28d637eca8ef360612a238ac56900d54"  // no changes
	};

	protected static final String[] PUBLICATION_KEYS = new String[]{
		"4841e7b5c7c23c613590fa4b79725498", // changed on client
		"4549ac62ae226657cd17d93dabfd6075", // changed on server
		"4533fe874079584ea4700da84b4d13ae", // created on client
		"2ad021608b51b6f9e4a45933ca63ed9e", // created on server
		"3d6ec7b6695976eeec379dcc55ae9cb1"  // no changes
	};
	
	protected static void wait(final int seconds) {
		try {
			Thread.sleep(1000 * seconds);
		} catch (final InterruptedException ex) {
			// ignore
		}
	}
	
	private static Server restServer;

	@SuppressWarnings("javadoc")
	@BeforeClass
	public static void initRestServer() throws Exception {
		final DBLogicApiInterfaceFactory dbLogicFactory = new DBLogicApiInterfaceFactory();
		dbLogicFactory.setDbSessionFactory(dbSessionFactory);
		final TestServerBuilder buildServer = new TestServerBuilder(dbLogicFactory, PORT);
		restServer = buildServer.buildServer();
		restServer.start();
	}
	
	@SuppressWarnings("javadoc")
	@AfterClass
	public static void stop() throws Exception {
		if (present(restServer)) {
			restServer.stop();
		}
	}
	
	protected User serverUser;
	protected User clientUser;

	protected LogicInterface clientLogic;
	protected LogicInterface serverLogic;
	
	protected URI syncServer;
	protected SynchronizationClient sync;
	
	private String[] modifiedBookmarkKeys;
	private String[] modifiedPublicationKeys;
	
	@SuppressWarnings({ "unchecked", "javadoc" })
	@Before
	public void initialize() {
		/*
		 * create server user
		 */
		this.serverUser = new User();
		this.serverUser.setName(SERVER_USER_NAME);
		this.serverUser.setRole(Role.SYNC);
		this.serverUser.setGroups(new ArrayList<Group>());
		this.serverUser.getGroups().add(new Group("jbhj"));
		this.serverUser.setApiKey(SERVER_USER_APIKEY);

		/*
		 * create client user
		 */
		this.clientUser = new User();
		this.clientUser.setName(CLIENT_USER_NAME);
		this.clientUser.setRole(Role.SYNC);

		/*
		 * create the logic interfaces
		 */
		this.clientLogic = new SyncDBLogic(this.clientUser, dbSessionFactory);
		this.serverLogic = new SyncDBLogic(this.serverUser, dbSessionFactory);

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
			serverPosts.add(this.createPost("no changes", "2011-01-10 14:32:00", "2011-01-31 14:32:00", this.serverUser, clazz));

			// post 2 "deleted on server" is not in the server database

			// post 3 "deleted on client" created and modified before last
			// synchronization
			serverPosts.add(this.createPost("deleted on client", "2011-01-10 14:55:00", "2011-01-15 14:33:00", this.serverUser, clazz));

			// post 4 "changed on server" created before, changed after the last
			// synchronization
			serverPosts.add(this.createPost("changed on server", "2010-09-16 14:35:00", "2011-03-16 17:30:00", this.serverUser, clazz));

			// post 5 "changed on client" created and modified before last
			// synchronization
			serverPosts.add(this.createPost("changed on client", "2009-12-31 23:59:00", "2010-02-01 17:23:00", this.serverUser, clazz));

			// post 6 "created on server" created and modified after last
			// synchronization
			serverPosts.add(this.createPost("created on server", "2011-03-18 11:20:00", "2011-03-18 11:20:00", this.serverUser, clazz));

			// post 7 "created on client" is not in the server database*

			this.serverLogic.createPosts(serverPosts);

			/*
			 * create client posts
			 */
			final List<Post<?>> clientPosts = new ArrayList<Post<?>>();

			// post 1: "post without changes" is the same post as in database
			clientPosts.add(this.createPost("no changes", "2011-01-10 14:32:00", "2011-01-31 14:32:00", this.clientUser, clazz));

			// post 2: "post deleted on server" here created and modified before
			// last synchronization
			clientPosts.add(this.createPost("post deleted on server", "2009-11-02 12:20:00", "2009-11-02 12:23:00", this.clientUser, clazz));

			// post 3: "post deleted on client" is not in the client list

			// post 4: "post changed on server" same hashes and create date as
			// in database, but change date is before last synchronization
			clientPosts.add(this.createPost("changed on server", "2010-09-16 14:35:00", "2011-01-16 17:58:00", this.clientUser, clazz));

			// post 5: "post changed on client" same hashes and create date as
			// in database, but change date is after the last synchronization
			// date
			clientPosts.add(this.createPost("changed on client", "2009-12-31 23:59:00", "2011-03-25 10:59:00", this.clientUser, clazz));

			// post 6: "post created on server" is not in the client list

			// post 7: "post created on client" created and modified after last
			// synchronization
			clientPosts.add(this.createPost("created on client", "2011-03-18 14:13:00", "2011-03-18 14:13:00", this.clientUser, clazz));

			this.clientLogic.createPosts(clientPosts);
			
			this.sync = new SynchronizationClient();

			/*
			 * setup server and client
			 */
			this.syncServer = TestUtils.createURI(SYNC_SERVER_URI);
			this.sync.setOwnUri(TestUtils.createURI(SYNC_CLIENT_URI));

			/*
			 * check that synchronization is enabled
			 */
			assertEquals(SYNC_SERVER_URI, this.clientLogic.getSyncServiceSettings(this.clientUser.getName(), null, true).get(0).getService().toString());
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
	protected <T extends Resource> Post<T> createPost(String title, final String createDate, final String changeDate, final User user, final Class<T> resourceType) {
		final Post<T> post = ModelUtils.generatePost(resourceType);
		post.setUser(user);
		try {
			post.setChangeDate(DATE_FORMAT.parseDateTime(changeDate).toDate());
			post.setDate(DATE_FORMAT.parseDateTime(createDate).toDate());
			if (resourceType == Bookmark.class) {
				final Bookmark bookmark = (Bookmark) post.getResource();
				title = title.replace(" ", "-");
				bookmark.setUrl("http://www." + title + ".com");
			}
		} catch (final Exception ex) {
			// ignore
		}
		post.getResource().setTitle(title);
		return post;
	}
	
	private static class SyncDBLogic extends DBLogic {
		public SyncDBLogic(final User user, final DBSessionFactory dbSessionFactory) {
			super(user, dbSessionFactory, null);
		}
	}
	
	protected static Map<String, SynchronizationPost> mapFromList(final List<SynchronizationPost> syncPosts) {
		final Map<String, SynchronizationPost> map = new HashMap<String, SynchronizationPost>();
		for (final SynchronizationPost post : syncPosts) {
			map.put(post.getIntraHash(), post);
		}
		return map;
	}
	
	protected void checkKeys(final Class<? extends Resource> resourceType, final Map<String, SynchronizationPost> posts, final String serviceType) {
		final String[] keys;
		if (Bookmark.class.equals(resourceType)) {
			keys = BOOKMARK_KEYS;
		} else {
			keys = PUBLICATION_KEYS;
		}
		
		for (final String key : keys) {
			assertTrue("["+ resourceType.getSimpleName() + "] " + serviceType + " does not contain key: " + key, posts.containsKey(key));
		}
	}
	
	protected void checkModifiedKeys(final Class<? extends Resource> resourceType, final Map<String, SynchronizationPost> posts, final String serviceType) {
		final String[] clientKeys;
		if (Bookmark.class.equals(resourceType)) {
			clientKeys = this.modifiedBookmarkKeys;
		} else {
			clientKeys = this.modifiedPublicationKeys;
		}
		
		for (final String key : clientKeys) {
			assertTrue("["+ resourceType.getSimpleName() + "] " + serviceType + " does not contain key: " + key, posts.containsKey(key));
		}
	}

	protected void setModifiedBookmarkKeys(final String[] modifiedBookmarkKeys) {
		this.modifiedBookmarkKeys = modifiedBookmarkKeys;
	}
	
	protected void setModifiedPublicationKeys(final String[] modifiedPublicationKeys) {
		this.modifiedPublicationKeys = modifiedPublicationKeys;
	}

	/**
	 * @param strategy
	 * @param userCredentials
	 * @param direction 
	 * @return
	 */
	protected SyncService createServerService(final ConflictResolutionStrategy strategy, final Properties userCredentials, final SynchronizationDirection direction) {
		final SyncService service = new SyncService();
		service.setService(this.syncServer);
		service.setResourceType(Resource.class);
		service.setDirection(direction);
		service.setStrategy(strategy);
		service.setServerUser(userCredentials);
		return service;
	}
	
}
