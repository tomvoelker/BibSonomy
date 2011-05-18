package org.bibsonomy.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.sync.SynchronizationClient;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationClientTest extends AbstractDatabaseManagerTest{

	private User serverUser;
	private User clientUser;

	private DBLogic clientLogic;
	private DBLogic serverLogic;
	private String serviceIdentifier;
	private String serverServiceIdentifier;

	@Before
	public void initialize() {

		/*
		 * create server user
		 */
		serverUser = new User();
		serverUser.setName("sync1");
		serverUser.setRole(Role.SYNC);

		/*
		 * create client user
		 */
		clientUser = new User();
		clientUser.setName("sync2");
		clientUser.setRole(Role.SYNC);

		/*
		 * create the logic interfaces
		 */
		clientLogic = new DBLogic(clientUser, new IbatisDBSessionFactory());
		serverLogic = new DBLogic(serverUser, new IbatisDBSessionFactory());


		/*
		 * create server posts
		 */
		Post<BibTex> post;

		// post 1 "no changes" created and modified before last synchronization
		List<Post<?>> postList = new ArrayList<Post<?>>();
		post = createPost("no changes", "2011-01-31 14:32:00", "2011-01-10 14:32:00", serverUser);
		postList.add(post);

		// post 2 "deleted on server" is not in the server database

		// post 3 "deleted on client" created and modified before last synchronization
		post = createPost("deleted on client", "2011-01-15 14:33:00", "2011-01-10 14:55:00", serverUser);
		postList.add(post);

		// post 4 "changed on server" created before, changed after the last scnchronization
		post = createPost("changed on server", "2011-03-16 17:30:00", "2010-09-16 14:35:00", serverUser);
		postList.add(post);

		// post 5 "changed on client" created and modified before last synchronization
		post = createPost("changed on client", "2010-02-01 17:23:00", "2009-12-31 23:59:00", serverUser);
		postList.add(post);

		// post 6 "created on server" created and modified after last synchronization
		post = createPost("created on server", "2011-03-18 11:20:00", "2011-03-18 11:20:00", serverUser);
		postList.add(post);

		// post 7 "created on client" is not in the server database*

		serverLogic.createPosts(postList);
		
		postList = new ArrayList<Post<?>>();
		/*
		 * create client posts
		 */

		//post 1: "post without changes" is the same post as in database
		post = createPost("no changes", "2011-01-31 14:32:00", "2011-01-10 14:32:00", clientUser);
		postList.add(post);

		//post 2: "post deleted on server" here created and modified before last synchronization
		post = createPost("post deleted on server", "2009-11-02 12:23:00", "2009-11-02 12:20:00", clientUser);
		postList.add(post);

		//post 3: "post deleted on client" is not in the client list

		//post 4: "post changed on server" same hashes and create date as in database, but change date is before last synchronization
		post = createPost("changed on server", "2011-01-16 17:58:00", "2010-09-16 14:35:00", clientUser);
		postList.add(post);

		//post 5: "post changed on client" same hashes and create date as in database, but change date is after the last synchronization date
		post = createPost("changed on client", "2011-03-25 10:59:00", "2009-12-31 23:59:00", clientUser);
		postList.add(post);

		//post 6: "post created on server" is not in the client list

		//post 7: "post created on client" created and modified after last synchronization
		post = createPost("created on client", "2011-03-18 14:13:00", "2011-03-18 14:13:00", clientUser);
		postList.add(post);

		clientLogic.createPosts(postList);

		/*
		 * TODO replace serviceIdentifier with correct identifier
		 */
		serviceIdentifier = "1";


	}

	private Post<BibTex> createPost(String title, String changeDate, String postingDate, User user) {
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		post.setUser(user);
		try {
			post.setChangeDate(format.parse(changeDate));
			post.setDate(format.parse(postingDate));
		} catch (ParseException ex) {
			// ignore
		}
		post.getResource().setTitle(title);
		return post;
	}

	@Test
	public void testSynchronization() {
		final SynchronizationClient synchronizer = new SynchronizationClient();

		assertTrue("synchronization wasn't successful", synchronizer.synchronize(serverUser, clientUser, serverLogic, clientLogic, serviceIdentifier, serverServiceIdentifier));

		final HashMap<String, SynchronizationPost> serverPosts = (HashMap<String, SynchronizationPost>) serverLogic.getSyncPostsMapForUser(serverUser.getName());
		final HashMap<String, SynchronizationPost> clientPosts = (HashMap<String, SynchronizationPost>) clientLogic.getSyncPostsMapForUser(clientUser.getName());

		final ArrayList<String> keys = new ArrayList<String>();
		keys.add("4841e7b5c7c23c613590fa4b79725498"); //changed on client
		keys.add("4549ac62ae226657cd17d93dabfd6075"); //changed on server
		keys.add("4533fe874079584ea4700da84b4d13ae"); //created on client
		keys.add("2ad021608b51b6f9e4a45933ca63ed9e"); //created on server
		keys.add("3d6ec7b6695976eeec379dcc55ae9cb1"); //no changes

		assertEquals(5, serverPosts.size());
		assertEquals(serverPosts.size(), clientPosts.size());

		System.out.println(serverPosts);
		System.out.println(clientPosts);

		for (final String key : keys) {
			System.out.println(key);
			assertTrue(serverPosts.containsKey(key));
			assertTrue(clientPosts.containsKey(key));
			assertTrue(key + " is not same", clientPosts.get(key).isSame(serverPosts.get(key)));
		}

		SynchronizationData syncData = serverLogic.getCurrentSynchronizationDataForUserForServiceForContent(serverUser.getName(), Integer.parseInt(serviceIdentifier), ConstantID.BIBTEX_CONTENT_TYPE.getId());
		assertTrue(syncData == null);

		syncData = serverLogic.getLastSynchronizationDataForUserForContentType(serverUser.getName(), Integer.parseInt(serviceIdentifier), ConstantID.BIBTEX_CONTENT_TYPE.getId());
		assertNotNull(syncData);

	}
}
