package org.bibsonomy.lucene.index;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.CommonModelUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author fei
 * @version $Id$
 */
public class LuceneUpdateManagerTest extends AbstractDatabaseManagerTest {
	private static final Log log = LogFactory.getLog(LuceneUpdateManagerTest.class);
	
	
	private static final String LUCENE_MAGIC_AUTHOR = "luceneAuthor";
	private static final String LUCENE_MAGIC_TAG    = "luceneTag";
	private static final String LUCENE_MAGIC_EDITOR = "luceneEditor";
	private static final String LUCENE_MAGIC_TITLE  = "luceneTitle";
	
	/** time offset between concurrent postings [ms] */
	private static final long CONCURRENCY_OFFSET    = 5;
	
	private static BookmarkDatabaseManager bookmarkDb;
	private static BibTexDatabaseManager publicationDb;
	private static AdminDatabaseManager adminDb;
	
	private static LuceneResourceManager<BibTex> luceneBibTexUpdater;
	private static LuceneResourceManager<Bookmark> luceneBookmarkUpdater;
	private static LuceneResourceSearch<BibTex> bibtexSearcher;
	private static LuceneResourceSearch<Bookmark> bookmarkSearcher;

	/** search terms for each relevant publication search field */
	String[] bibtexSearchTerms = {
			LUCENE_MAGIC_TITLE,
			LUCENE_MAGIC_TAG,
			LUCENE_MAGIC_AUTHOR,
			LUCENE_MAGIC_EDITOR
			};

	/** search terms for each relevant publication search field */
	String[] bookmarkSearchTerms = {
			LUCENE_MAGIC_TITLE,
			};

	/**
	 * Initializes the test database.
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void initDatabaseManager() {		
		bookmarkDb = BookmarkDatabaseManager.getInstance();	
		publicationDb = BibTexDatabaseManager.getInstance();
		adminDb = AdminDatabaseManager.getInstance();
		
		luceneBibTexUpdater = (LuceneResourceManager<BibTex>) LuceneSpringContextWrapper.getBeanFactory().getBean("lucenePublicationManager");
		luceneBookmarkUpdater = (LuceneResourceManager<Bookmark>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneBookmarkManager");
		
		bibtexSearcher = luceneBibTexUpdater.getSearcher();
		bookmarkSearcher = luceneBookmarkUpdater.getSearcher();
	}
	
	@Before
	public void setUpLucene() {
		// generate index
		try {
			generateIndex();
			
			luceneBibTexUpdater.resetIndexReader();
			luceneBookmarkUpdater.resetIndexReader();
		} catch (final Exception e) {
			log.error("Error creating lucene index.", e);
		}
		
	}

	/**
	 * tests visibility of private posts
	 */
	@Test
	public void privatePosts() {
		// set up data structures
		final Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add(GroupID.PUBLIC.name());
		allowedGroups.add(GroupID.PRIVATE.name());

		//--------------------------------------------------------------------
		// TEST 1: insert private post into test database and search for it
		//         as different user
		//--------------------------------------------------------------------
		// store test post in database
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin());
		final Post<BibTex> toInsert = generateBibTexDatabaseManagerTestPost(GroupID.PRIVATE);
		
		publicationDb.createPost(toInsert, this.dbSession);

		// update index
		updateResourceIndices();
		
		// search for all relevant fields
		for( final String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			final ResultList<Post<BibTex>> resultList = 
				bibtexSearcher.getPosts(toInsert.getUser().getName(), toInsert.getUser().getName()+"noIse", null, allowedGroups, term, null, null, null, null, null, null, 10, 0);
			
			assertEquals(0, resultList.size());
		}
		//--------------------------------------------------------------------
		// TEST 2: search for the same post as the owner 
		//--------------------------------------------------------------------
		// search for all relevant fields
		for( final String term : bibtexSearchTerms ) {
			log.error("[PrivatePost] Searching for " + term);
			final ResultList<Post<BibTex>> resultList = 
				bibtexSearcher.getPosts(toInsert.getUser().getName(), null, null, allowedGroups, term, null, null, null, null, null, null, 1000, 0);
			
			assertEquals(1, resultList.size());
		}
		
		// delete post
		publicationDb.deletePost(toInsert.getUser().getName(), toInsert.getResource().getIntraHash(), this.dbSession);
	}
	
	/**
	 * tests asynchronous update of the lucene index
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	@Test
	// @Ignore
	public void updateIndices() throws IOException, ClassNotFoundException, SQLException {
		// set up data structures
		final Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add("public");

		//--------------------------------------------------------------------
		// TEST 1: insert special post into test database and search for it
		//--------------------------------------------------------------------
		// store test post in database
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin());
		Post<BibTex> bibtexPost = generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		
		publicationDb.createPost(bibtexPost, this.dbSession);

		// update index
		this.updateResourceIndices();
		
		// search for all relevant fields
		for( final String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			final ResultList<Post<BibTex>> resultList = 
				bibtexSearcher.getPosts(bibtexPost.getUser().getName(), null, null, allowedGroups, term, null, null, null, null, null, null, 10, 0);
			
			assertEquals(1, resultList.size());
		}

		//--------------------------------------------------------------------
		// TEST 2: remove post, update the index and search again
		//--------------------------------------------------------------------
		// remove test post in database
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.Logging());
		publicationDb.deletePost(bibtexPost.getUser().getName(), bibtexPost.getResource().getIntraHash(), this.dbSession);
		// FIXME: the updater looks at the tas table to get the newest date, which is
		//        1815 after deleting the post - so we add another post
		final Post<BibTex> workaroundInsert = generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		publicationDb.createPost(workaroundInsert, this.dbSession);
		
		// update index
		updateResourceIndices();
		
		// search again
		for( final String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			final ResultList<Post<BibTex>> resultList = 
				bibtexSearcher.getPosts(bibtexPost.getUser().getName(), null, null, allowedGroups, term, null, null, null, null, null, null, 1000, 0);
			
			for( final Post<BibTex> post : resultList ) {
				log.info("Got post: " + post.getDate()+ "("+post.getResource().getTitle()+")");
			}
			
			assertEquals(true, resultList.size()>= 1);
		}
		
		//--------------------------------------------------------------------
		// TEST 3: add bibtex and bookmark post, update the index and search again
		//         we set the date almost to the previous one to simulate
		//         concurrency
		//--------------------------------------------------------------------
		final Post<Bookmark> bookmarkPost = generateBookmarkDatabaseManagerTestPost();
		bookmarkPost.setDate(new Date(workaroundInsert.getDate().getTime()+CONCURRENCY_OFFSET));

		bibtexPost = generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		bibtexPost.setDate(new Date(workaroundInsert.getDate().getTime()+CONCURRENCY_OFFSET));
		
		bookmarkDb.createPost(bookmarkPost, this.dbSession);
		publicationDb.createPost(bibtexPost, this.dbSession);
		
		// update index
		updateResourceIndices();
		
		// search for bibtex posts
		for( final String term : bibtexSearchTerms ) {
			log.debug("Searching for " + term);
			final ResultList<Post<BibTex>> bibtexList = 
				bibtexSearcher.getPosts(bibtexPost.getUser().getName(), null, null, allowedGroups, term, null, null, null, null, null, null, 1000, 0);
			assertEquals(2, bibtexList.size());
		}
		// search for bookmark posts
		for( final String term : bookmarkSearchTerms ) {
			log.debug("Searching for " + term);
			final ResultList<Post<Bookmark>> bookmarkList = 
				bookmarkSearcher.getPosts(bookmarkPost.getUser().getName(), null, null, allowedGroups, term, null, null, null, null, null, null, 1000, 0);
			assertEquals(1, bookmarkList.size());
		}
	}

	protected void updateResourceIndices() {
		for (int i = 0; i < luceneBibTexUpdater.getResourceIndeces().size(); i++) {
			luceneBibTexUpdater.updateAndReloadIndex();
			luceneBookmarkUpdater.updateAndReloadIndex();
		}
	}
	
	/**
	 * tests handling of spam posts
	 */
	@Test
	// @Ignore // TODO: fails on hudson
	public void spamPosts() {
		// set up data structures
		final Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add("public");
		allowedGroups.add("testgroup1");
		List<Post<BibTex>> bibResultList;
		List<Post<Bookmark>> bmResultList;

		List<Post<BibTex>> bibRefList;
		final List<Post<Bookmark>> bmRefList;

		// create testuser
		final String userName = "testuser1";
		final User user = new User(userName);
		
		// flag user as spammer
		user.setPrediction(1);
		user.setSpammer(true);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		
		updateResourceIndices();
		
		// search
		bibResultList = bibtexSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1, 0);
		assertEquals(0, bibResultList.size());
		bmResultList  = bookmarkSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1, 0);
		assertEquals(0, bmResultList.size());

		waitForDB();
		
		user.setPrediction(0);
		user.setSpammer(false);
		user.setAlgorithm("admin");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		
		updateResourceIndices();
		
		// search
		final int groupId = -1;
		final List<Integer> groups = new ArrayList<Integer>();
		for( int i=0; i<10; i++ ) 
			groups.add(i);
		
		bibResultList = bibtexSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1000, 0);
		bibRefList    = publicationDb.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, groups, null, 1000, 0, null, this.dbSession);
		assertEquals(bibRefList.size() - 1, bibResultList.size()); // db list contains one interhash duplicate!

		// FIXME: this test is broken - we only get public posts from the db logic
		/*
		bmResultList  = bookmarkSearcher.searchPosts(null, userName, null, userName, allowedGroups, 1000, 0);
		bmRefList     = bookmarkDb.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, groups, null, 1000, 0, null, this.dbSession);
		assertEquals(bmRefList.size(), bmResultList.size());
		*/
		
		
		// test multiple flagging/unflagging operations
		waitForDB();
		user.setPrediction(1);
		user.setSpammer(true);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		waitForDB();
		user.setPrediction(0);
		user.setSpammer(false);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		waitForDB();
		user.setPrediction(1);
		user.setSpammer(true);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		
		updateResourceIndices();
		
		// search
		bibResultList = bibtexSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1000, 0);
		assertEquals(0, bibResultList.size());
		bmResultList  = bookmarkSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1000, 0);
		assertEquals(0, bmResultList.size());
		
		// test multiple flagging/unflagging operations
		waitForDB();
		user.setPrediction(0);
		user.setSpammer(false);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		waitForDB();
		user.setPrediction(1);
		user.setSpammer(true);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		waitForDB();
		user.setPrediction(0);
		user.setSpammer(false);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		updateResourceIndices();

		// search
		bibResultList = bibtexSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1000, 0);
		bibRefList    = publicationDb.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, groups, null, 1000, 0, null, this.dbSession);
		assertEquals(bibRefList.size() - 1, bibResultList.size()); // db list contains one interhash duplicate
}

	private void waitForDB() {
		// FIXME: we get an SQL duplicate key violation exception, if we don't wait....
		try {
			Thread.sleep(2000);
		} catch (final InterruptedException e) {
			log.error("Error while going to sleep... Probably spam flagging will fail!", e);
		}
	}

	/**
	 * tests some internel index functions
	 */
	@Test
	public void searchTest() {
		// set up data structures
		final Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add("public");
		allowedGroups.add("testgroup1");
		allowedGroups.add("testgroup2");
		allowedGroups.add("testgroup3");

		//--------------------------------------------------------------------
		// TEST 1: insert special post into test database and search for it
		//--------------------------------------------------------------------
		// store test post in database
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin());
		final Post<BibTex> bibtexPost = generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		final String bibTitle = "luceneTitle1";
		bibtexPost.getResource().setTitle(bibTitle);
		
		publicationDb.createPost(bibtexPost, this.dbSession);

		final Post<Bookmark> bookmarkPost = generateBookmarkDatabaseManagerTestPost();
		final String bmTitle = "BrandNewluceneTitle2";
		bookmarkPost.getUser().setName("brandNewLuceneName");
		bookmarkPost.getResource().setTitle(bmTitle);
		bookmarkPost.getResource().recalculateHashes();
		
		bookmarkDb.createPost(bookmarkPost, this.dbSession);

		// update index
		updateResourceIndices();
		
		// search for publications
		ResultList<Post<BibTex>> bibResultList = 
			bibtexSearcher.getPosts(bibtexPost.getUser().getName(), null, null, allowedGroups, bibTitle, null, null, null, null, null, null, 1, 0);
        
		assertEquals(1, bibResultList.size());

		bibResultList = 
			bibtexSearcher.getPosts(bibtexPost.getUser().getName(), null, null, allowedGroups, bibTitle+"2", null, null, null, null, null, null, 1, 0);
		assertEquals(0, bibResultList.size());
		
		// search for bookmarks
		ResultList<Post<Bookmark>> bmResultList = 
			bookmarkSearcher.getPosts(bookmarkPost.getUser().getName(), null, null, allowedGroups, bmTitle, null, null, null, null, null, null, 1, 0);
		assertEquals(1, bmResultList.size());

		bmResultList = 
			bookmarkSearcher.getPosts(bookmarkPost.getUser().getName(), null, null, allowedGroups, bmTitle+"2", null, null, null, null, null, null, 1, 0);
		assertEquals(0, bmResultList.size());
		
		//------------------------------------------------------------------------
		// author search
		//------------------------------------------------------------------------
		// String group,  String search, String requestedUserName, String requestedGroupName, 
		// String year, String firstYear, String lastYear, List<String> tagList) {

		bibResultList = 
			bibtexSearcher.getPosts(null, null, null, allowedGroups, null, null, "luceneAuthor", null, null, null, null, 1000, 0);

		bibResultList = 
			bibtexSearcher.getPosts(null, null, null, allowedGroups, null, null, "luceneAuthor", null, "1980", null, null, 1000, 0);

		bibResultList = 
			bibtexSearcher.getPosts(null, null, null, allowedGroups, null, null, "luceneAuthor", null, "1980", "2000", null, 1000, 0);

		bibResultList = 
			bibtexSearcher.getPosts(null, null, null, allowedGroups, null, null, "luceneAuthor", null, null, "2000", null, 1000, 0);

		//------------------------------------------------------------------------
		// tag cloud
		//------------------------------------------------------------------------
		// List<Tag> authorTags = bibtexSearcher.getTagsByAuthor(GroupID.PUBLIC.name(), "luceneAuthor", null, null, null, null, null, null, 1000);
														      
	}
	
	/**
	 * tests locking of the resource searcher
	 */
	@Test
	public void concurrencyAccess() {
		// FIXME: implement me
	}
	
	/**
	 * generates lucene index for the test database
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	private static void generateIndex() throws IOException, ClassNotFoundException, SQLException {
		luceneBibTexUpdater.generateIndex(false);
		luceneBookmarkUpdater.generateIndex(false);
	}
	
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private static Post<BibTex> generateBibTexDatabaseManagerTestPost(final GroupID groupID) {
		final Post<BibTex> post = new Post<BibTex>();
		post.setContentId(null);
		post.setDescription("luceneTestPost");
		post.setDate(new Date(System.currentTimeMillis()));
		
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		post.setUser(user);
		
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
		
		final BibTex publication = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(publication);
		publication.setCount(0);		
		publication.setEntrytype("inproceedings");
		publication.setAuthor("MegaMan and Lucene GigaWoman "+LUCENE_MAGIC_AUTHOR);
		publication.setEditor("Peter Silie "+LUCENE_MAGIC_EDITOR);
		publication.setTitle("title "+ (Math.round(Math.random()*Integer.MAX_VALUE))+" "+LUCENE_MAGIC_TITLE);
		publication.setYear("test year");
		publication.setJournal("test journal");
		publication.setBooktitle("test booktitle");
		publication.setVolume("test volume");
		publication.setNumber("test number");
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
	private static Post<Bookmark> generateBookmarkDatabaseManagerTestPost() {
		
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
		
		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0);
		bookmark.setTitle("test" + (Math.round(Math.random() * Integer.MAX_VALUE)) + " " + LUCENE_MAGIC_TITLE);
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		
		post.setResource(bookmark);
		return post;
	}
}
