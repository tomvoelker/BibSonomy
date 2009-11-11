package org.bibsonomy.lucene.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.bibsonomy.util.ExceptionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class LuceneDBLogicTest extends AbstractDatabaseManagerTest {
	private static final int POST_TIME_TO_WAIT = 1537;

	private static final Log log = LogFactory.getLog(LuceneDBLogicTest.class);
	
	private static final String LUCENE_MAGIC_AUTHOR = "luceneAuthor";
	private static final String LUCENE_MAGIC_TAG    = "luceneTag";
	private static final String LUCENE_MAGIC_EDITOR = "luceneEditor";
	private static final String LUCENE_MAGIC_TITLE  = "luceneTitle";

	/** username for test queries */
	private static final String TEST_USERNAME = "testuser1";
	
	/** bookmark database interface */
	LuceneDBInterface<Bookmark> luceneBookmarkLogic;

	/** bibtex database interface */
	LuceneDBInterface<BibTex> luceneBibTexLogic;

	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabase() {
		new TestDatabaseLoader().load();
	}

	@Before
	public void setUp() {
		super.setUp();
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		this.luceneBookmarkLogic = LuceneBookmarkLogic.getInstance();
		this.luceneBibTexLogic   = LuceneBibTexLogic.getInstance();
	}
	
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}
	

	/**
	 * tests confluence of lucene's and bibsonomy's database post queries 
	 */
	@Test
	public void getBibtexUserPosts() {
		// get all public posts for the testuser
		String requestedUserName = "testuser1";
		int groupId = 3;
		final List<Integer> groups = new ArrayList<Integer>();
		
		List<Post<BibTex>> posts    = this.luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		List<Post<BibTex>> postsRef = this.bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());
		
		groupId = 0;
		posts    = this.luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());
		
		groupId = -1;
		posts    = this.luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
//		assertEquals(postsRef.size(), posts.size()); // FIXME: user gets all his posts
		
		requestedUserName = "testuser2";
		groupId = 1;
		posts    = this.luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());
		
		requestedUserName = "testuser2";
		groupId = 2;
		posts    = this.luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());

		//List<Post<Bookmark>> bookmarkPosts = this.dbLogic.getBookmarkForUser(TEST_USERNAME, TEST_USERNAME, GroupID.PUBLIC.getId(), visibleGroupIDs, Integer.MAX_VALUE, 0);

	}
	
	/**
	 * tests whether all posts whithin a given time range are retrieved
	 */
	@Test
	public void retrieveRecordsFromDatabase() {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtra());
		//--------------------------------------------------------------------
		// TEST 1: insert special posts into test database and search for it
		//--------------------------------------------------------------------
		// start time - we ignore milliseconds
		long start = System.currentTimeMillis();
		Date fromDate = new Date(start- start%1000);
		try {
			Thread.sleep(POST_TIME_TO_WAIT);
		} catch (InterruptedException e) {
			log.error("Thread interrupted", e);
		}
		List<Post<? extends Resource>> refPosts = new LinkedList<Post<? extends Resource>>();
		
		for( int i=0; i<5; i++ ) {
			// store test posts in database
			Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
			refPosts.add(bibtexPost);
			this.bibTexDb.storePost(bibtexPost.getUser().getName(), bibtexPost, null, false, this.dbSession);
			try {
				Thread.sleep(POST_TIME_TO_WAIT);
			} catch (InterruptedException e) {
				log.error("Thread interrupted", e);
			}
		}

		// end time - we ignore milliseconds
		long end = System.currentTimeMillis();
		Date toDate = new Date(end - end%1000 + 1000);
		
		// retrieve posts
		List<HashMap<String, Object>> posts = luceneBibTexLogic.getPostsForTimeRange(fromDate, toDate);

		assertEquals(refPosts.size(), posts.size());
		HashMap<String, Boolean> testMap = new HashMap<String, Boolean>();
		for( HashMap<String, Object> post : posts ) {
			String title = (String)post.get("title");
			testMap.put(title, true);
		}
		for( Post<? extends Resource> post : refPosts ) {
			assertNotNull(testMap.get(post.getResource().getTitle()));
		}
	}
	
	/**
	 * tests whether all posts whithin a given time range are retrieved
	 */
	@Test
	public void getContentIdsToDelete() {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtra());
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.Logging());
		
		//--------------------------------------------------------------------
		// TEST 1: insert and delete special posts into test database and search for it
		//--------------------------------------------------------------------
		// start time - we ignore milliseconds
		long start = System.currentTimeMillis();
		Date fromDate = new Date(start- start%1000);
		List<Post<? extends Resource>> refPosts = new LinkedList<Post<? extends Resource>>();
		
		for( int i=0; i<5; i++ ) {
			// store test posts in database
			Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
			refPosts.add(bibtexPost);
			this.bibTexDb.storePost(bibtexPost.getUser().getName(), bibtexPost, null, false, this.dbSession);
			try {
				Thread.sleep(POST_TIME_TO_WAIT);
			} catch (InterruptedException e) {
				log.error("Thread interrupted", e);
			}
			// delete test post
			this.bibTexDb.deletePost(bibtexPost.getUser().getName(), bibtexPost.getResource().getIntraHash(), this.dbSession);
		}

		// end time - we ignore milliseconds
		long end = System.currentTimeMillis();
		Date toDate = new Date(end - end%1000);
		
		// retrieve posts
		List<Integer> posts = luceneBibTexLogic.getContentIdsToDelete(fromDate, toDate);

		assertEquals(refPosts.size(), posts.size());
	}
	
	/**
	 * test whether newest post's date is detected
	 */
	@Test
	public void getNewestRecordDateFromTas() {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtra());

		//--------------------------------------------------------------------
		// TEST 1: insert special post into test database and search for it
		//--------------------------------------------------------------------
		// store test post in database
		Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		this.bibTexDb.storePost(bibtexPost.getUser().getName(), bibtexPost, null, false, this.dbSession);
		
		Date postDate = this.luceneBibTexLogic.getNewestRecordDateFromTas();
		// compare modulo milliseconds 
		assertEquals(bibtexPost.getDate().getTime()-bibtexPost.getDate().getTime()%5000, postDate.getTime()-postDate.getTime()%5000);

		Post<Bookmark> bookmarkPost = this.generateBookmarkDatabaseManagerTestPost();
		this.bookmarkDb.storePost(bookmarkPost.getUser().getName(), bookmarkPost, null, false, this.dbSession);
		
		postDate = this.luceneBookmarkLogic.getNewestRecordDateFromTas();
		assertEquals(bookmarkPost.getDate().getTime()-bookmarkPost.getDate().getTime()%5000, postDate.getTime()-postDate.getTime()%5000);
	}
	
	/**
	 * tests confluence of lucene's and bibsonomy's database post queries 
	 */
	@Test
	public void getBookmarkUserPosts() {
		// get all public posts for the testuser
		String requestedUserName = "testuser1";
		int groupId = 3;
		List<Integer> groups = new ArrayList<Integer>();
		
		List<Post<Bookmark>> posts    = this.luceneBookmarkLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		List<Post<Bookmark>> postsRef = this.bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);  
		assertEquals(postsRef.size(), posts.size());
		
		groupId = 0;
		posts    = this.luceneBookmarkLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);  
		assertEquals(postsRef.size(), posts.size());
		
		// FIXME: why does this result differ???
		/*
		groupId = -1;
		posts    = this.luceneBookmarkLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0, this.dbSession);  
		assertEquals(postsRef.size(), posts.size());
		*/
		
		requestedUserName = "testuser2";
		groupId = 1;
		posts    = this.luceneBookmarkLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);  
		assertEquals(postsRef.size(), posts.size());
		
		requestedUserName = "testuser2";
		groupId = 2;
		posts    = this.luceneBookmarkLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = this.bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);  
		assertEquals(postsRef.size(), posts.size());


	}
	
	

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private Post <BibTex> generateBibTexDatabaseManagerTestPost(GroupID groupID) {
		
		final Post<BibTex> post = new Post<BibTex>();

		final Group group = new Group(groupID);
	
		post.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName(LUCENE_MAGIC_TAG);
		post.getTags().add(tag);

		post.setContentId(null); // will be set in storePost()
		post.setDescription("luceneTestPost");
		post.setDate(new Date(System.currentTimeMillis()));
		final User user = new User();
		setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		post.setUser(user);
		final BibTex resource;

		
		final BibTex bibtex = new BibTex();
		this.setBeanPropertiesOn(bibtex);
		bibtex.setCount(0);		
		bibtex.setEntrytype("inproceedings");
		bibtex.setAuthor("MegaMan and Lucene GigaWoman "+LUCENE_MAGIC_AUTHOR);
		bibtex.setEditor("Peter Silie "+LUCENE_MAGIC_EDITOR);
		bibtex.setTitle("bibtex insertpost test");
		resource = bibtex;
		
		String title, year, journal, booktitle, volume, number = null;
		title = "title "+ (Math.round(Math.random()*Integer.MAX_VALUE))+" "+LUCENE_MAGIC_TITLE;
		year = "test year";
		journal = "test journal";
		booktitle = "test booktitle";
		volume = "test volume";
		number = "test number";
		bibtex.setTitle(title);
		bibtex.setYear(year);
		bibtex.setJournal(journal);
		bibtex.setBooktitle(booktitle);
		bibtex.setVolume(volume);
		bibtex.setNumber(number);
		bibtex.setScraperId(-1);
		bibtex.setType("2");
		bibtex.recalculateHashes();
		post.setResource(resource);
		return post;
	}
	
	/**
	 * generate a Bookmark Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private Post <Bookmark> generateBookmarkDatabaseManagerTestPost() {
		
		final Post<Bookmark> post = new Post<Bookmark>();

		final Group group = new Group();
		group.setDescription(null);
		group.setName("public");
		group.setGroupId(GroupID.PUBLIC.getId());
		post.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		post.getTags().add(tag);

		post.setContentId(null); // will be set in storePost()
		post.setDescription("Some description");
		post.setDate(new Date());
		final User user = new User();
		setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		post.setUser(user);
		final Bookmark resource;

		
		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0);
		//bookmark.setIntraHash("e44a7a8fac3a70901329214fcc1525aa");
		bookmark.setTitle("test"+(Math.round(Math.random()*Integer.MAX_VALUE))+" "+LUCENE_MAGIC_TITLE);
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		resource = bookmark;
		
		post.setResource(resource);
		return post;
	}
	
	/**
	 * Calls every setter on an object and fills it wiht dummy values.
	 */
	private void setBeanPropertiesOn(final Object obj) {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				try {
					final Method setter = d.getWriteMethod();
					final Method getter = d.getReadMethod();
					if ((setter != null) && (getter != null)) {
						setter.invoke(obj, new Object[] { getDummyValue(d.getPropertyType(), d.getName()) });
					}
				} catch (final Exception ex) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (final IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + obj.getClass().getName() + "'");
		}
	}
	
	/**
	 * Returns dummy values for some primitive types and classes
	 */
	private static Object getDummyValue(final Class<?> type, final String name) {
		if (String.class == type) {
			return "test-" + name;
		}
		if ((int.class == type) || (Integer.class == type)) {
			return Math.abs(name.hashCode());
		}
		if ((boolean.class == type) || (Boolean.class == type)) {
			return (name.hashCode() % 2 == 0);
		}
		if (URL.class == type) {
			try {
				return new URL("http://www.bibsonomy.org/test/" + name);
			} catch (final MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
		}
		if (Privlevel.class == type) {
			return Privlevel.MEMBERS;
		}
		log.debug("no dummy value for type '" + type.getName() + "'");
		return null;
	}
}

