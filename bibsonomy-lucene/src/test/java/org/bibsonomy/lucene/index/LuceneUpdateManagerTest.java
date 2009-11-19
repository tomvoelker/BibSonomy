package org.bibsonomy.lucene.index;

import static org.junit.Assert.assertEquals;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.lucene.database.LuceneBibTexLogic;
import org.bibsonomy.lucene.database.LuceneBookmarkLogic;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.analyzer.SpringPerFieldAnalyzerWrapper;
import org.bibsonomy.lucene.search.delegate.LuceneDelegateBibTexSearch;
import org.bibsonomy.lucene.search.delegate.LuceneDelegateBookmarkSearch;
import org.bibsonomy.lucene.search.delegate.LuceneDelegateResourceSearch;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.bibsonomy.util.ExceptionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LuceneUpdateManagerTest extends AbstractDatabaseManagerTest {
	private static final Logger log       = Logger.getLogger(LuceneUpdateManagerTest.class);
	private static final org.apache.commons.logging.Log legacylog = LogFactory.getLog(LuceneUpdateManagerTest.class);
	private static final String LUCENE_MAGIC_AUTHOR = "luceneAuthor";
	private static final String LUCENE_MAGIC_TAG    = "luceneTag";
	private static final String LUCENE_MAGIC_EDITOR = "luceneEditor";
	private static final String LUCENE_MAGIC_TITLE  = "luceneTitle";
	
	/** time offset between concurrent postings [ms] */
	private static final long CONCURRENCY_OFFSET    = 5;
	
	private LuceneResourceManager<BibTex> luceneBibTexUpdater;
	private LuceneResourceManager<Bookmark> luceneBookmarkUpdater;
	
	private LuceneDBInterface<BibTex> luceneBibTexLogic;
	private LuceneDBInterface<Bookmark> luceneBookmarkLogic;
	private LuceneResourceIndex<BibTex> luceneBibTexIndex;
	private LuceneResourceIndex<Bookmark> luceneBookmarkIndex;
	private LuceneDelegateResourceSearch<BibTex> luceneBibTexSearch;
	private LuceneDelegateResourceSearch<Bookmark> luceneBookmarkSearch;

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
	public static void initDatabase() {
		new TestDatabaseLoader().load();
	}

	@Before
	public void setUp() {
		super.setUp();
		
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		// generate index
		try {
			generateIndex();
			// initialize data structures
			init();
			this.luceneBibTexUpdater.reloadIndex();
			this.luceneBookmarkUpdater.reloadIndex();
		} catch (Exception e) {
			log.error("Error creating lucene index.", e);
		}
		
	}
	
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
		Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add(GroupID.PUBLIC.name());
		allowedGroups.add(GroupID.PRIVATE.name());

		//--------------------------------------------------------------------
		// TEST 1: insert private post into test database and search for it
		//         as different user
		//--------------------------------------------------------------------
		// store test post in database
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtra());
		Post<BibTex> toInsert = this.generateBibTexDatabaseManagerTestPost(GroupID.PRIVATE);
		
		this.bibTexDb.createPost(toInsert, this.dbSession);

		// update index
		this.luceneBibTexUpdater.updateIndex();
		this.luceneBookmarkUpdater.updateIndex();
		this.luceneBibTexUpdater.reloadIndex();
		this.luceneBookmarkUpdater.reloadIndex();
		
		// prepare searcher
		ResourceSearch<BibTex> bibtexSearcher = LuceneDelegateBibTexSearch.getInstance();

		// search for all relevant fields
		for( String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			ResultList<Post<BibTex>> resultList = bibtexSearcher.searchPosts(null, term, toInsert.getUser().getName()+"noIse", toInsert.getUser().getName(), allowedGroups, 1, 0);
			
			assertEquals(0, resultList.size());
		}
		//--------------------------------------------------------------------
		// TEST 2: search for the same post as the owner 
		//--------------------------------------------------------------------
		// search for all relevant fields
		for( String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			ResultList<Post<BibTex>> resultList = bibtexSearcher.searchPosts(null, term, toInsert.getUser().getName(), toInsert.getUser().getName(), allowedGroups, 1000, 0);
			
			assertEquals(1, resultList.size());
		}
		
		// delete post
		this.bibTexDb.deletePost(toInsert.getUser().getName(), toInsert.getResource().getIntraHash(), this.dbSession);
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
		Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add("public");

		//--------------------------------------------------------------------
		// TEST 1: insert special post into test database and search for it
		//--------------------------------------------------------------------
		// store test post in database
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtra());
		Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		
		this.bibTexDb.createPost(bibtexPost, this.dbSession);

		// update index
		this.luceneBibTexUpdater.updateIndex();
		this.luceneBookmarkUpdater.updateIndex();
		this.luceneBibTexUpdater.reloadIndex();
		this.luceneBookmarkUpdater.reloadIndex();

		// prepare searcher
		ResourceSearch<BibTex> bibtexSearcher = LuceneDelegateBibTexSearch.getInstance();
		ResourceSearch<Bookmark> bookmarkSearcher = LuceneDelegateBookmarkSearch.getInstance();

		// search for all relevant fields
		for( String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			ResultList<Post<BibTex>> resultList = bibtexSearcher.searchPosts(null, term, null, bibtexPost.getUser().getName(), allowedGroups, 1, 0);
			
			assertEquals(1, resultList.size());
		}

		//--------------------------------------------------------------------
		// TEST 2: remove post, update the index and search again
		//--------------------------------------------------------------------
		// remove test post in database
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.Logging());
		this.bibTexDb.deletePost(bibtexPost.getUser().getName(), bibtexPost.getResource().getIntraHash(), this.dbSession);
		// FIXME: the updater looks at the tas table to get the newest date, which is
		//        1815 after deleting the post - so we add another post
		final Post<BibTex> workaroundInsert = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		this.bibTexDb.createPost(workaroundInsert, this.dbSession);
		
		// update index
		this.luceneBibTexUpdater.updateIndex();
		this.luceneBookmarkUpdater.updateIndex();
		this.luceneBibTexUpdater.reloadIndex();
		this.luceneBookmarkUpdater.reloadIndex();
		
		// search again
		for( String term : bibtexSearchTerms ) {
			log.info("Searching for " + term);
			ResultList<Post<BibTex>> resultList = bibtexSearcher.searchPosts(null, term, null, bibtexPost.getUser().getName(), allowedGroups, 1000, 0);
			
			assertEquals(1, resultList.size());
		}
		
		//--------------------------------------------------------------------
		// TEST 3: add bibtex and bookmark post, update the index and search again
		//         we set the date almost to the previous one to simulate
		//         concurrency
		//--------------------------------------------------------------------
		Post<Bookmark> bookmarkPost = this.generateBookmarkDatabaseManagerTestPost();
		bookmarkPost.setDate(new Date(workaroundInsert.getDate().getTime()+CONCURRENCY_OFFSET));

		bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		bibtexPost.setDate(new Date(workaroundInsert.getDate().getTime()+CONCURRENCY_OFFSET));
		
		this.bookmarkDb.createPost(bookmarkPost, this.dbSession);
		this.bibTexDb.createPost(bibtexPost, this.dbSession);
		
		// update index
		this.luceneBibTexUpdater.updateIndex();
		this.luceneBookmarkUpdater.updateIndex();
		this.luceneBibTexUpdater.reloadIndex();
		this.luceneBookmarkUpdater.reloadIndex();
		
		// search for bibtex posts
		for( String term : bibtexSearchTerms ) {
			log.debug("Searching for " + term);
			ResultList<Post<BibTex>> bibtexList = bibtexSearcher.searchPosts(null, term, null, bibtexPost.getUser().getName(), allowedGroups, 1000, 0);
			assertEquals(2, bibtexList.size());
		}
		// search for bookmark posts
		for( String term : bookmarkSearchTerms ) {
			log.debug("Searching for " + term);
			ResultList<Post<Bookmark>> bookmarkList = bookmarkSearcher.searchPosts(null, term, null, bookmarkPost.getUser().getName(), allowedGroups, 1000, 0);
			assertEquals(1, bookmarkList.size());
		}
	}
	
	/**
	 * tests handling of spam posts
	 */
	@Test
	public void spamPosts() {
		// set up data structures
		Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add("public");
		allowedGroups.add("testgroup1");
		List<Post<BibTex>> bibResultList;
		List<Post<Bookmark>> bmResultList;

		List<Post<BibTex>> bibRefList;
		List<Post<Bookmark>> bmRefList;

		// create testuser
		String userName = "testuser1";
		User user = new User(userName);
		// prepare searcher
		ResourceSearch<BibTex> bibtexSearcher = LuceneDelegateBibTexSearch.getInstance();
		ResourceSearch<Bookmark> bookmarkSearcher = LuceneDelegateBookmarkSearch.getInstance();
		
		// flag user as spammer
		user.setPrediction(1);
		this.luceneBibTexUpdater.flagSpammer(user);
		this.luceneBookmarkUpdater.flagSpammer(user);
		this.luceneBibTexUpdater.updateIndex();
		this.luceneBookmarkUpdater.updateIndex();
		this.luceneBibTexUpdater.reloadIndex();
		this.luceneBookmarkUpdater.reloadIndex();
		
		// search
		bibResultList = bibtexSearcher.searchPosts(null, userName, null, userName, allowedGroups, 1, 0);
		assertEquals(0, bibResultList.size());
		bmResultList  = bookmarkSearcher.searchPosts(null, userName, null, userName, allowedGroups, 1, 0);
		assertEquals(0, bmResultList.size());

		
		user.setPrediction(0);
		this.luceneBibTexUpdater.flagSpammer(user);
		this.luceneBookmarkUpdater.flagSpammer(user);
		this.luceneBibTexUpdater.updateIndex();
		this.luceneBookmarkUpdater.updateIndex();
		this.luceneBibTexUpdater.reloadIndex();
		this.luceneBookmarkUpdater.reloadIndex();
		
		// search
		int groupId = -1;
		final List<Integer> groups = new ArrayList<Integer>();
		for( int i=0; i<10; i++ ) 
			groups.add(i);
		
		bibResultList = bibtexSearcher.searchPosts(null, userName, null, userName, allowedGroups, 1000, 0);
		bibRefList    = this.bibTexDb.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, groups, null, 1000, 0, null, this.dbSession);
		assertEquals(bibRefList.size(), bibResultList.size());

		// FIXME: this test is broken - we only get public posts from the db logic
		/*
		bmResultList  = bookmarkSearcher.searchPosts(null, userName, null, userName, allowedGroups, 1000, 0);
		bmRefList     = this.bookmarkDb.getPostsForUser(userName, userName, HashID.INTER_HASH, groupId, groups, null, 1000, 0, null, this.dbSession);
		assertEquals(bmRefList.size(), bmResultList.size());
		*/
		
		// test update manager recovery
		User spamUser = new User("testuser1");
		spamUser.setPrediction(1);
		spamUser.setSpammer(true);
		spamUser.setAlgorithm("luceneTest");
		this.adminDb.flagSpammer(spamUser, "lucene", this.dbSession);
		
		this.luceneBibTexIndex.reset();
		this.luceneBookmarkIndex.reset();
		this.luceneBibTexUpdater.recovery();
		this.luceneBookmarkUpdater.recovery();
		
		assertEquals(true, this.luceneBibTexIndex.getUsersToFlag().contains("testuser1"));
		assertEquals(true, this.luceneBookmarkIndex.getUsersToFlag().contains("testuser1"));

		// FIXME: we get an SQL duplicate key violation exception, if we don't wait....
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.error("Error while going to sleep... Probably spam flagging will fail!", e);
		}
		
		spamUser.setPrediction(0);
		spamUser.setSpammer(false);
		spamUser.setAlgorithm("luceneTest");
		this.adminDb.flagSpammer(spamUser, "lucene", this.dbSession);
		
		this.luceneBibTexIndex.reset();
		this.luceneBookmarkIndex.reset();
		this.luceneBibTexUpdater.recovery();
		this.luceneBookmarkUpdater.recovery();
		
		assertEquals(false, this.luceneBibTexIndex.getUsersToFlag().contains("testuser1"));
		assertEquals(false, this.luceneBookmarkIndex.getUsersToFlag().contains("testuser1"));
}

	/**
	 * tests some internel index functions
	 */
	@Test
	public void searchTest() {
		// set up data structures
		Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add("public");
		allowedGroups.add("testgroup1");
		allowedGroups.add("testgroup2");
		allowedGroups.add("testgroup3");

		//--------------------------------------------------------------------
		// TEST 1: insert special post into test database and search for it
		//--------------------------------------------------------------------
		// store test post in database
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new org.bibsonomy.database.plugin.plugins.BibTexExtra());
		Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		String bibTitle = "luceneTitle1";
		bibtexPost.getResource().setTitle(bibTitle);
		
		this.bibTexDb.createPost(bibtexPost, this.dbSession);

		Post<Bookmark> bookmarkPost = this.generateBookmarkDatabaseManagerTestPost();
		String bmTitle = "BrandNewluceneTitle2";
		bookmarkPost.getUser().setName("brandNewLuceneName");
		bookmarkPost.getResource().setTitle(bmTitle);
		bookmarkPost.getResource().recalculateHashes();
		
		this.bookmarkDb.createPost(bookmarkPost, this.dbSession);

		// update index
		this.luceneBibTexUpdater.updateIndex();
		this.luceneBookmarkUpdater.updateIndex();
		this.luceneBibTexUpdater.reloadIndex();
		this.luceneBookmarkUpdater.reloadIndex();

		// prepare searcher
		ResourceSearch<BibTex> bibtexSearcher = LuceneDelegateBibTexSearch.getInstance();
		ResourceSearch<Bookmark> bookmarkSearcher = LuceneDelegateBookmarkSearch.getInstance();

		// search for bibtex
		ResultList<Post<BibTex>> bibResultList = bibtexSearcher.searchPosts(null, bibTitle, null, bibtexPost.getUser().getName(), allowedGroups, 1, 0);
		assertEquals(1, bibResultList.size());

		bibResultList = bibtexSearcher.searchPosts(null, bibTitle+"2", null, bibtexPost.getUser().getName(), allowedGroups, 1, 0);
		assertEquals(0, bibResultList.size());
		
		// search for bookmarks
		ResultList<Post<Bookmark>> bmResultList = bookmarkSearcher.searchPosts(null, bmTitle, null, bookmarkPost.getUser().getName(), allowedGroups, 10, 0);
		assertEquals(1, bmResultList.size());

		bmResultList = bookmarkSearcher.searchPosts(null, bmTitle+"2", null, bookmarkPost.getUser().getName(), allowedGroups, 10, 0);
		assertEquals(0, bmResultList.size());
		
		//------------------------------------------------------------------------
		// author search
		//------------------------------------------------------------------------
		// String group,  String search, String requestedUserName, String requestedGroupName, 
		// String year, String firstYear, String lastYear, List<String> tagList) {

		bibResultList = bibtexSearcher.searchAuthor(GroupID.PUBLIC.name(), "luceneAuthor", null, null, null, null, null, null, 1000, 0);

		bibResultList = bibtexSearcher.searchAuthor(GroupID.PRIVATE.name(), "luceneAuthor", null, null, null, null, null, null, 1000, 0);

		bibResultList = bibtexSearcher.searchAuthor(GroupID.PUBLIC.name(), "luceneAuthor", null, null, "1980", null, null, null, 1000, 0);

		bibResultList = bibtexSearcher.searchAuthor(GroupID.PUBLIC.name(), "luceneAuthor", null, null, null, "1980", null, null, 1000, 0);

		bibResultList = bibtexSearcher.searchAuthor(GroupID.PUBLIC.name(), "luceneAuthor", null, null, null, "1980", "2000", null, 1000, 0);

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
	private void generateIndex() throws IOException, ClassNotFoundException, SQLException {
		/*
		GenerateLuceneIndex indexer = new GenerateLuceneIndex(JNDITestDatabaseBinder.getLuceneProperties());
		indexer.setLogic(LuceneBibTexLogic.getInstance());
		indexer.generateIndex();
		*/
		
		// FIXME: configure this via spring
		LuceneGenerateResourceIndex<BibTex> bibTexIndexer = 
			new LuceneGenerateBibTexIndex(JNDITestDatabaseBinder.getLuceneProperties()); 
		LuceneGenerateResourceIndex<Bookmark> bookmarkIndexer = 
			new LuceneGenerateBookmarkIndex(JNDITestDatabaseBinder.getLuceneProperties());
		
		bibTexIndexer.setLogic(LuceneBibTexLogic.getInstance());
		bibTexIndexer.setAnalyzer(SpringPerFieldAnalyzerWrapper.getInstance());
		bookmarkIndexer.setLogic(LuceneBookmarkLogic.getInstance());
		bookmarkIndexer.setAnalyzer(SpringPerFieldAnalyzerWrapper.getInstance());
		
		bibTexIndexer.generateIndex();
		bookmarkIndexer.generateIndex();
	}
	
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
					ExceptionUtils.logErrorAndThrowRuntimeException(legacylog, ex, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (final IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(legacylog, ex, "could not introspect object of class '" + obj.getClass().getName() + "'");
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
		this.luceneBibTexIndex     = LuceneBibTexIndex.getInstance();
		this.luceneBookmarkIndex   = LuceneBookmarkIndex.getInstance();
		this.luceneBibTexIndex.reset();
		this.luceneBookmarkIndex.reset();
		
		this.luceneBibTexUpdater   = LuceneBibTexManager.getInstance();
		this.luceneBookmarkUpdater = LuceneBookmarkManager.getInstance();
	}
}

