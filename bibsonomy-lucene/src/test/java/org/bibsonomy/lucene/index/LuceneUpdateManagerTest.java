package org.bibsonomy.lucene.index;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.lucene.database.LuceneBibTexLogic;
import org.bibsonomy.lucene.database.LuceneBookmarkLogic;
import org.bibsonomy.lucene.index.analyzer.SpringPerFieldAnalyzerWrapper;
import org.bibsonomy.lucene.search.LuceneSearchBibTex;
import org.bibsonomy.lucene.search.LuceneSearchBookmarks;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneBibTexConverter;
import org.bibsonomy.lucene.util.LuceneBookmarkConverter;
import org.bibsonomy.lucene.util.LuceneResourceConverter;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.testutil.CommonModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LuceneUpdateManagerTest extends AbstractDatabaseManagerTest {
	private static final Logger log       = Logger.getLogger(LuceneUpdateManagerTest.class);
	private static final Log legacylog = LogFactory.getLog(LuceneUpdateManagerTest.class);
	private static final String LUCENE_MAGIC_AUTHOR = "luceneAuthor";
	private static final String LUCENE_MAGIC_TAG    = "luceneTag";
	private static final String LUCENE_MAGIC_EDITOR = "luceneEditor";
	private static final String LUCENE_MAGIC_TITLE  = "luceneTitle";
	
	/** time offset between concurrent postings [ms] */
	private static final long CONCURRENCY_OFFSET    = 5;
	
	private static BookmarkDatabaseManager bookmarkDb;
	private static BibTexDatabaseManager bibTexDb;
	private static AdminDatabaseManager adminDb;
	
	private LuceneResourceManager<BibTex> luceneBibTexUpdater;
	private LuceneResourceManager<Bookmark> luceneBookmarkUpdater;
	
	private LuceneResourceIndex<BibTex> luceneBibTexIndex;
	private LuceneResourceIndex<Bookmark> luceneBookmarkIndex;

	/** search terms for each relevant bibtex search field */
	String[] bibtexSearchTerms = {
			LUCENE_MAGIC_TITLE,
			LUCENE_MAGIC_TAG,
			LUCENE_MAGIC_AUTHOR,
			LUCENE_MAGIC_EDITOR
			};

	/** search terms for each relevant bibtex search field */
	String[] bookmarkSearchTerms = {
			LUCENE_MAGIC_TITLE,
			};

	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabaseManager() {
		bookmarkDb = BookmarkDatabaseManager.getInstance();	
		bibTexDb = BibTexDatabaseManager.getInstance();
		adminDb = AdminDatabaseManager.getInstance();
	}
	
	@Before
	public void setUpLucene() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		// generate index
		try {
			generateIndex();
			// initialize data structures
			init();
			this.luceneBibTexUpdater.resetIndexReader();
			this.luceneBookmarkUpdater.resetIndexReader();
		} catch (final Exception e) {
			log.error("Error creating lucene index.", e);
		}
		
	}
	
	@Override
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
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
		final Post<BibTex> toInsert = this.generateBibTexDatabaseManagerTestPost(GroupID.PRIVATE);
		
		bibTexDb.createPost(toInsert, this.dbSession);

		// update index
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}

		// prepare searcher
		final ResourceSearch<BibTex> bibtexSearcher = LuceneSearchBibTex.getInstance();

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
		bibTexDb.deletePost(toInsert.getUser().getName(), toInsert.getResource().getIntraHash(), this.dbSession);
	}
	
	/**
	 * tests asynchronous update of the lucene index
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	@Test
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
		Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		
		bibTexDb.createPost(bibtexPost, this.dbSession);

		// update index
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}

		// prepare searcher
		final ResourceSearch<BibTex> bibtexSearcher = LuceneSearchBibTex.getInstance();
		final ResourceSearch<Bookmark> bookmarkSearcher = LuceneSearchBookmarks.getInstance();

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
		bibTexDb.deletePost(bibtexPost.getUser().getName(), bibtexPost.getResource().getIntraHash(), this.dbSession);
		// FIXME: the updater looks at the tas table to get the newest date, which is
		//        1815 after deleting the post - so we add another post
		final Post<BibTex> workaroundInsert = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		bibTexDb.createPost(workaroundInsert, this.dbSession);
		
		// update index
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}
		
		// search again
		for( final String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			final ResultList<Post<BibTex>> resultList = 
				bibtexSearcher.getPosts(bibtexPost.getUser().getName(), null, null, allowedGroups, term, null, null, null, null, null, null, 1000, 0);
			
			assertEquals(1, resultList.size());
		}
		
		//--------------------------------------------------------------------
		// TEST 3: add bibtex and bookmark post, update the index and search again
		//         we set the date almost to the previous one to simulate
		//         concurrency
		//--------------------------------------------------------------------
		final Post<Bookmark> bookmarkPost = this.generateBookmarkDatabaseManagerTestPost();
		bookmarkPost.setDate(new Date(workaroundInsert.getDate().getTime()+CONCURRENCY_OFFSET));

		bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		bibtexPost.setDate(new Date(workaroundInsert.getDate().getTime()+CONCURRENCY_OFFSET));
		
		bookmarkDb.createPost(bookmarkPost, this.dbSession);
		bibTexDb.createPost(bibtexPost, this.dbSession);
		
		// update index
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}
		
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
	
	/**
	 * tests handling of spam posts
	 */
	@Test
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
		// prepare searcher
		final ResourceSearch<BibTex> bibtexSearcher = LuceneSearchBibTex.getInstance();
		final ResourceSearch<Bookmark> bookmarkSearcher = LuceneSearchBookmarks.getInstance();
		
		// flag user as spammer
		user.setPrediction(1);
		user.setSpammer(true);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}
		
		// search
		bibResultList = bibtexSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1, 0);
		assertEquals(0, bibResultList.size());
		bmResultList  = bookmarkSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1, 0);
		assertEquals(0, bmResultList.size());

		waitForDB();
		
		user.setPrediction(0);
		user.setSpammer(false);
		user.setAlgorithm("luceneTest");
		adminDb.flagSpammer(user, "luceneAdmin", this.dbSession);
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}
		
		// search
		final int groupId = -1;
		final List<Integer> groups = new ArrayList<Integer>();
		for( int i=0; i<10; i++ ) 
			groups.add(i);
		
		bibResultList = bibtexSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1000, 0);
		bibRefList    = bibTexDb.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, groups, null, 1000, 0, null, this.dbSession);
		assertEquals(bibRefList.size(), bibResultList.size());

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
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}
		
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
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}

		// search
		bibResultList = bibtexSearcher.getPosts(userName, null, null, allowedGroups, userName, null, null, null, null, null, null, 1000, 0);
		bibRefList    = bibTexDb.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, groups, null, 1000, 0, null, this.dbSession);
		assertEquals(bibRefList.size(), bibResultList.size());
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
		final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		final String bibTitle = "luceneTitle1";
		bibtexPost.getResource().setTitle(bibTitle);
		
		bibTexDb.createPost(bibtexPost, this.dbSession);

		final Post<Bookmark> bookmarkPost = this.generateBookmarkDatabaseManagerTestPost();
		final String bmTitle = "BrandNewluceneTitle2";
		bookmarkPost.getUser().setName("brandNewLuceneName");
		bookmarkPost.getResource().setTitle(bmTitle);
		bookmarkPost.getResource().recalculateHashes();
		
		bookmarkDb.createPost(bookmarkPost, this.dbSession);

		// update index
		for( int i=0; i<LuceneBase.getRedundantCnt(); i++ ) {
			this.luceneBibTexUpdater.updateAndReloadIndex();
			this.luceneBookmarkUpdater.updateAndReloadIndex();
		}

		// prepare searcher
		final ResourceSearch<BibTex> bibtexSearcher = LuceneSearchBibTex.getInstance();
		final ResourceSearch<Bookmark> bookmarkSearcher = LuceneSearchBookmarks.getInstance();

		// search for bibtex
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

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * generates lucene index for the test database
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	private void generateIndex() throws IOException, ClassNotFoundException, SQLException {
		// FIXME: configure this via spring
		final LuceneGenerateResourceIndex<BibTex> bibTexIndexer = 
			new LuceneGenerateBibTexIndex(); 
		final LuceneGenerateResourceIndex<Bookmark> bookmarkIndexer = 
			new LuceneGenerateBookmarkIndex();
		
		bibTexIndexer.createEmptyIndex();
		bookmarkIndexer.createEmptyIndex();
		bibTexIndexer.shutdown();
		bookmarkIndexer.shutdown();
		
		bibTexIndexer.setLogic(LuceneBibTexLogic.getInstance());
		bibTexIndexer.setAnalyzer(SpringPerFieldAnalyzerWrapper.getInstance());
		bookmarkIndexer.setLogic(LuceneBookmarkLogic.getInstance());
		bookmarkIndexer.setAnalyzer(SpringPerFieldAnalyzerWrapper.getInstance());
		
		
		LuceneResourceConverter<BibTex> bibTexConverter;
		LuceneResourceConverter<Bookmark> bookmarkConverter;
		Map<String,Map<String,Object>> postPropertyMap;

		postPropertyMap = (Map<String, Map<String, Object>>) LuceneSpringContextWrapper.getBeanFactory().getBean("bibTexPropertyMap");
		bibTexConverter = new LuceneBibTexConverter();
		bibTexConverter.setPostPropertyMap(postPropertyMap);
		
		postPropertyMap = (Map<String, Map<String, Object>>) LuceneSpringContextWrapper.getBeanFactory().getBean("bookmarkPropertyMap");
		bookmarkConverter = new LuceneBookmarkConverter();
		bookmarkConverter.setPostPropertyMap(postPropertyMap);
		
		bibTexIndexer.setResourceConverter(bibTexConverter);
		bookmarkIndexer.setResourceConverter(bookmarkConverter);
		
		
		bibTexIndexer.generateIndex();
		bookmarkIndexer.generateIndex();
		bibTexIndexer.shutdown();
		bookmarkIndexer.shutdown();
	}
	
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
		final BibTex resource;

		
		final BibTex bibtex = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(bibtex);
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
	
	private void init() {
		/*
		// create db logic
		this.luceneBibTexLogic   = LuceneBibTexLogic.getInstance();
		this.luceneBookmarkLogic = LuceneBookmarkLogic.getInstance();
		HashMap<Class<? extends Resource>, LuceneDBInterface<? extends Resource>> logicMap = 
			new HashMap<Class<? extends Resource>, LuceneDBInterface<? extends Resource>>();
		logicMap.put(Bookmark.class, luceneBookmarkLogic);
		logicMap.put(BibTex.class, luceneBibTexLogic);
		
		// create indices
		this.luceneBibTexIndex = LuceneBibTexIndex.getInstance();
		this.luceneBookmarkIndex = LuceneBookmarkIndex.getInstance();
		HashMap<Class<? extends Resource>, LuceneResourceIndex<? extends Resource>> indexMap = 
			new HashMap<Class<? extends Resource>, LuceneResourceIndex<? extends Resource>>(); 
		indexMap.put(Bookmark.class, this.luceneBookmarkIndex);
		indexMap.put(BibTex.class, this.luceneBibTexIndex);
		
		// create searcher
		this.luceneBibTexSearch = LuceneSearchBibTex.getInstance();
		this.luceneBookmarkSearch = LuceneSearchBookmarks.getInstance();
		HashMap<Class<? extends Resource>, LuceneSearch<? extends Resource>> searchMap = 
			new HashMap<Class<? extends Resource>, LuceneSearch<? extends Resource>>(); 
		searchMap.put(Bookmark.class, luceneBookmarkSearch);
		searchMap.put(BibTex.class, luceneBibTexSearch);

		this.luceneUpdater = new LuceneUpdateManager();
		this.luceneUpdater.setLogicMap(logicMap);
		this.luceneUpdater.setIndexMap(indexMap);
		this.luceneUpdater.setSearchMap(searchMap);
		*/
		this.luceneBibTexIndex     = new LuceneBibTexIndex(0);
		this.luceneBookmarkIndex   = new LuceneBookmarkIndex(0);
		this.luceneBibTexIndex.reset();
		this.luceneBookmarkIndex.reset();
		
		this.luceneBibTexUpdater   = LuceneBibTexManager.getInstance();
		this.luceneBookmarkUpdater = LuceneBookmarkManager.getInstance();
	}
}

