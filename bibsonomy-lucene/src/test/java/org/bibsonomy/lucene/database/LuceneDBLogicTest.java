package org.bibsonomy.lucene.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.CommonModelUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneDBLogicTest extends AbstractDatabaseManagerTest {

	private static final String LUCENE_MAGIC_AUTHOR = "luceneAuthor";
	private static final String LUCENE_MAGIC_TAG    = "luceneTag";
	private static final String LUCENE_MAGIC_EDITOR = "luceneEditor";
	private static final String LUCENE_MAGIC_TITLE  = "luceneTitle";

	/** constant for querying for all posts which have been deleted since the last index update */
	private static final long QUERY_TIME_OFFSET_MS = 30*1000;

	private static BookmarkDatabaseManager bookmarkDb;
	private static BibTexDatabaseManager bibTexDb;

	/** bookmark database interface */
	private static LuceneDBInterface<Bookmark> luceneBookmarkLogic;

	/** bibtex database interface */
	private static LuceneDBInterface<BibTex> luceneBibTexLogic;

	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabaseManager() {
		bookmarkDb = BookmarkDatabaseManager.getInstance();	
		bibTexDb = BibTexDatabaseManager.getInstance();
	}

	@BeforeClass
	public static void setUpLucene() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		luceneBookmarkLogic = LuceneBookmarkLogic.getInstance();
		luceneBibTexLogic   = LuceneBibTexLogic.getInstance();
	}

	@AfterClass
	public static void unbindJNDI() {
		JNDITestDatabaseBinder.unbind();
	}

	/**
	 * tests confluence of lucene's and bibsonomy's database post queries 
	 */
	@Test
	public void getBibtexUserPosts() {
		// get all public posts for the testuser
		String requestedUserName = "testuser1";
		final int groupId = -1;
		final List<Integer> groups = new ArrayList<Integer>();

		List<LucenePost<BibTex>> posts    = luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		List<Post<BibTex>> postsRef = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());

		posts    = luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size()); 

		requestedUserName = "testuser2";
		posts    = luceneBibTexLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());
	}

	/**
	 * tests whether all newly added posts are retrieved
	 */
	@Test
	public void retrieveRecordsFromDatabase() {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin());
		final List<Post<? extends Resource>> refPosts = new LinkedList<Post<? extends Resource>>();
		//--------------------------------------------------------------------
		// TEST 1: insert special posts into test database and search for it
		//--------------------------------------------------------------------
		final Integer lastTasId = luceneBibTexLogic.getLastTasId();
		for( int i=0; i<5; i++ ) {
			// store test posts in database
			final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
			refPosts.add(bibtexPost);
			bibTexDb.createPost(bibtexPost, this.dbSession);
		}

		// retrieve posts
		final List<? extends Post<BibTex>> posts = luceneBibTexLogic.getNewPosts(lastTasId);

		assertEquals(refPosts.size(), posts.size());

		final Map<String,Boolean> testMap = new HashMap<String, Boolean>(); 
		for( final Post<? extends Resource> post : posts ) {
			testMap.put(post.getResource().getTitle(), true);
		}
		for( final Post<? extends Resource> post : refPosts ) {
			assertNotNull(testMap.get(post.getResource().getTitle()));
		}
	}

	/**
	 * tests whether all posts whithin a given time range are retrieved
	 */
	@Test
	public void getContentIdsToDelete() {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin());
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.Logging());
		final List<Post<? extends Resource>> refPosts = new LinkedList<Post<? extends Resource>>();

		//--------------------------------------------------------------------
		// TEST 1: insert and delete special posts into test database and search for it
		//--------------------------------------------------------------------
		// start time - we ignore milliseconds
		final long start    = System.currentTimeMillis();
		final long fromDate = start- start%1000;

		for( int i=0; i<5; i++ ) {
			// store test posts in database
			final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
			refPosts.add(bibtexPost);
			bibTexDb.createPost(bibtexPost, this.dbSession);
			// delete test post
			bibTexDb.deletePost(bibtexPost.getUser().getName(), bibtexPost.getResource().getIntraHash(), this.dbSession);
		}
		// retrieve posts
		final List<Integer> posts = luceneBibTexLogic.getContentIdsToDelete(new Date(fromDate-QUERY_TIME_OFFSET_MS));

		assertEquals(true, refPosts.size()<=posts.size());
	}

	/**
	 * test whether newest post's date is detected
	 */
	@Test
	@Ignore // ignored test, as it inherently fails on slow machines
	public void getNewestRecordDateFromTas() {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin());

		//--------------------------------------------------------------------
		// TEST 1: insert special post into test database and search for it
		//--------------------------------------------------------------------
		// store test post in database
		final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		bibTexDb.createPost(bibtexPost, this.dbSession);

		Date postDate = luceneBibTexLogic.getNewestRecordDateFromTas();
		// compare modulo milliseconds 
		assertEquals(bibtexPost.getDate().getTime()-bibtexPost.getDate().getTime()%100000, postDate.getTime()-postDate.getTime()%100000);

		final Post<Bookmark> bookmarkPost = this.generateBookmarkDatabaseManagerTestPost();
		bookmarkDb.createPost(bookmarkPost, this.dbSession);

		postDate = luceneBookmarkLogic.getNewestRecordDateFromTas();
		assertEquals(bookmarkPost.getDate().getTime()-bookmarkPost.getDate().getTime()%100000, postDate.getTime()-postDate.getTime()%100000);
	}

	/**
	 * tests confluence of lucene's and bibsonomy's database post queries 
	 */
	@Test
	public void getBookmarkUserPosts() {
		// get all public posts for the testuser
		String requestedUserName = "testuser1";
		final int groupId = -1;
		final List<Integer> groups = new ArrayList<Integer>();

		List<LucenePost<Bookmark>> posts;    
		List<Post<Bookmark>> postsRef;

		posts    = luceneBookmarkLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());

		requestedUserName = "testuser2";
		posts    = luceneBookmarkLogic.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, 10, 0);
		postsRef = bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, 10, 0, null, this.dbSession);  
		assertEquals(postsRef.size(), posts.size());
	}

	/**
	 * tests confluence of lucene's and bibsonomy's database post queries 
	 */
	@Test
	public void getBookmarkNewPosts() {
		// FIXME: implement a test
	}

	/**
	 * tests confluence of lucene's and bibsonomy's database post queries 
	 */
	@Test
	public void getBibTexNewPosts() {
		// FIXME: implement a test
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private Post <BibTex> generateBibTexDatabaseManagerTestPost(final GroupID groupID) {

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
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		post.setUser(user);


		final BibTex publication = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(publication);
		publication.setCount(0);		
		publication.setEntrytype("inproceedings");
		publication.setAuthor("MegaMan and Lucene GigaWoman "+LUCENE_MAGIC_AUTHOR);
		publication.setEditor("Peter Silie "+LUCENE_MAGIC_EDITOR);
		publication.setTitle("bibtex insertpost test");

		String title, year, journal, booktitle, volume, number = null;
		title = "title "+ (Math.round(Math.random()*Integer.MAX_VALUE))+" "+LUCENE_MAGIC_TITLE;
		year = "test year";
		journal = "test journal";
		booktitle = "test booktitle";
		volume = "test volume";
		number = "test number";
		publication.setTitle(title);
		publication.setYear(year);
		publication.setJournal(journal);
		publication.setBooktitle(booktitle);
		publication.setVolume(volume);
		publication.setNumber(number);
		publication.setScraperId(-1);
		publication.setType("2");
		publication.recalculateHashes();
		post.setResource(publication);
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
		CommonModelUtils.setBeanPropertiesOn(user);
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
}