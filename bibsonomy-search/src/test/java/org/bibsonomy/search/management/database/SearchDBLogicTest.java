/**
 * BibSonomy Search - Helper classes for search modules.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.management.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.bibsonomy.testutil.CommonModelUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for the {@link SearchDBLogic}
 * 
 * @author fei
 */
public class SearchDBLogicTest extends AbstractDatabaseManagerTest {

	private static final String SEARCH_MAGIC_AUTHOR = "searchAuthor";
	private static final String SEARCH_MAGIC_TAG = "searchTag";
	private static final String SEARCH_MAGIC_EDITOR = "searchEditor";
	private static final String SEARCH_MAGIC_TITLE = "searchTitle";

	/** constant for querying for all posts which have been deleted since the last index update */
	private static final long QUERY_TIME_OFFSET_MS = 60 * 1000;

	private static BookmarkDatabaseManager bookmarkDb;
	private static BibTexDatabaseManager publicationDatabaseManager;

	/** bookmark database interface */
	private static SearchDBLogic<Bookmark> searchBookmarkLogic;

	/** bibtex database interface */
	private static SearchDBLogic<BibTex> searchBibTexLogic;

	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabaseManager() {
		bookmarkDb = BookmarkDatabaseManager.getInstance();	
		publicationDatabaseManager = BibTexDatabaseManager.getInstance();
	}

	/**
	 * inits the search db logics
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpSearch() {
		searchBookmarkLogic = (SearchDBLogic<Bookmark>) SearchSpringContextWrapper.getBeanFactory().getBean("bookmarkSearchDBLogic");
		searchBibTexLogic = (SearchDBLogic<BibTex>) SearchSpringContextWrapper.getBeanFactory().getBean("publicationSearchDBLogic");
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

		List<SearchPost<BibTex>> posts = searchBibTexLogic.getPostsForUser(requestedUserName, 10, 0);
		List<Post<BibTex>> postsRef = publicationDatabaseManager.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());

		posts = searchBibTexLogic.getPostsForUser(requestedUserName, 10, 0);
		postsRef = publicationDatabaseManager.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());

		requestedUserName = "testuser2";
		posts = searchBibTexLogic.getPostsForUser(requestedUserName, 10, 0);
		postsRef = publicationDatabaseManager.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());
	}

	/**
	 * tests whether all newly added posts are retrieved
	 * @throws PersonListParserException 
	 */
	@Test
	public void retrieveRecordsFromDatabase() throws PersonListParserException {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new BibTexExtraPlugin());
		final List<Post<? extends Resource>> refPosts = new LinkedList<Post<? extends Resource>>();
		//--------------------------------------------------------------------
		// TEST 1: insert special posts into test database and search for it
		//--------------------------------------------------------------------
		final Integer lastTasId = searchBibTexLogic.getLastTasId();
		for (int i = 0; i < 5; i++) {
			// store test posts in database
			final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC, i);
			refPosts.add(bibtexPost);
			publicationDatabaseManager.createPost(bibtexPost, this.dbSession);
		}
		
		// retrieve posts
		final List<? extends Post<BibTex>> posts = searchBibTexLogic.getNewPosts(lastTasId.intValue(), Integer.MAX_VALUE, 0);

		assertEquals(refPosts.size(), posts.size());

		final Map<String,Boolean> testMap = new HashMap<String, Boolean>(); 
		for (final Post<? extends Resource> post : posts) {
			testMap.put(post.getResource().getTitle(), Boolean.TRUE);
		}
		for (final Post<? extends Resource> post : refPosts) {
			assertNotNull(testMap.get(post.getResource().getTitle()));
		}
	}
	
	/**
	 * tests {@link SearchDBLogic#getNewPosts(int, int, int)}'s limit and offset
	 * feature
	 * 
	 * @throws PersonListParserException
	 */
	@Test
	public void testLimitOffset() throws PersonListParserException {
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(new BibTexExtraPlugin());
		final List<Post<? extends Resource>> refPosts = new LinkedList<Post<? extends Resource>>();
		//--------------------------------------------------------------------
		// TEST 1: insert special posts into test database and search for it
		//--------------------------------------------------------------------
		final Integer lastTasId = searchBibTexLogic.getLastTasId();
		for (int i = 0; i < 5; i++) {
			// store test posts in database
			final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC, i);
			refPosts.add(bibtexPost);
			publicationDatabaseManager.createPost(bibtexPost, this.dbSession);
		}
		
		final List<? extends Post<BibTex>> posts = searchBibTexLogic.getNewPosts(lastTasId.intValue(), 10, 4);
		assertEquals(1, posts.size());
		
		final Post<BibTex> post = posts.get(0);
		assertEquals(getTitleForId(0), post.getResource().getTitle());
		assertEquals(3, post.getTags().size());
	}

	/**
	 * tests whether all posts whithin a given time range are retrieved
	 * 
	 * FIXME: fails too often - please fix! 
	 * @throws PersonListParserException 
	 */
	@Test
	public void getContentIdsToDelete() throws PersonListParserException {
		final List<Post<? extends Resource>> refPosts = new LinkedList<Post<? extends Resource>>();

		//--------------------------------------------------------------------
		// TEST 1: insert and delete special posts into test database and search for it
		//--------------------------------------------------------------------
		// start time - we ignore milliseconds
		final long start    = System.currentTimeMillis();
		final long fromDate = start - (start % 1000);

		for (int i = 0; i < 5; i++) {
			// store test posts in database
			final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC, i);
			refPosts.add(bibtexPost);
			publicationDatabaseManager.createPost(bibtexPost, this.dbSession);
			// delete test post
			publicationDatabaseManager.deletePost(bibtexPost.getUser().getName(), bibtexPost.getResource().getIntraHash(), this.dbSession);
		}
		// retrieve posts
		final List<Integer> posts = searchBibTexLogic.getContentIdsToDelete(new Date(fromDate-QUERY_TIME_OFFSET_MS));

		assertTrue(refPosts.size() <= posts.size());
	}

	/**
	 * test whether newest post's date is detected
	 * @throws PersonListParserException 
	 */
	@Test
	public void getNewestRecordDateFromTas() throws PersonListParserException {
		//--------------------------------------------------------------------
		// TEST 1: insert special post into test database and search for it
		//--------------------------------------------------------------------
		// store test post in database
		final Post<BibTex> bibtexPost = this.generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC, 100);
		publicationDatabaseManager.createPost(bibtexPost, this.dbSession);

		Date postDate = searchBibTexLogic.getNewestRecordDateFromTas();
		// compare modulo milliseconds 
		assertEquals(bibtexPost.getDate().getTime() - (bibtexPost.getDate().getTime() % 100000), postDate.getTime()-(postDate.getTime() % 100000));

		final Post<Bookmark> bookmarkPost = this.generateBookmarkDatabaseManagerTestPost();
		bookmarkDb.createPost(bookmarkPost, this.dbSession);

		postDate = searchBookmarkLogic.getNewestRecordDateFromTas();
		assertEquals(bookmarkPost.getDate().getTime() - (bookmarkPost.getDate().getTime() % 100000), postDate.getTime()-(postDate.getTime() % 100000));
	}

	/**
	 * tests confluence of search's and bibsonomy's database post queries 
	 */
	@Test
	public void getBookmarkUserPosts() {
		// get all public posts for the testuser
		String requestedUserName = "testuser1";
		final int groupId = -1;
		final List<Integer> groups = new ArrayList<Integer>();

		List<SearchPost<Bookmark>> posts;
		List<Post<Bookmark>> postsRef;

		posts = searchBookmarkLogic.getPostsForUser(requestedUserName, 10, 0);
		postsRef = bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, null, 10, 0, null, this.dbSession);
		assertEquals(postsRef.size(), posts.size());

		requestedUserName = "testuser2";
		posts = searchBookmarkLogic.getPostsForUser(requestedUserName, 10, 0);
		postsRef = bookmarkDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, groupId, groups, null, null, 10, 0, null, this.dbSession);  
		assertEquals(postsRef.size(), posts.size());
	}

	/**
	 * tests confluence of search's and bibsonomy's database post queries 
	 */
	@Test
	public void getBookmarkNewPosts() {
		// FIXME: implement a test
	}

	/**
	 * tests confluence of search's and bibsonomy's database post queries 
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
	 * @param i 
	 * @throws PersonListParserException 
	 */
	private static Post<BibTex> generateBibTexDatabaseManagerTestPost(final GroupID groupID, int i) throws PersonListParserException {
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
		tag.setName(SEARCH_MAGIC_TAG);
		post.getTags().add(tag);

		post.setContentId(null); // will be set in storePost()
		post.setDescription("searchTestPost");
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
		publication.setAuthor(PersonNameUtils.discoverPersonNames("MegaMan and Lucene GigaWoman " + SEARCH_MAGIC_AUTHOR));
		publication.setEditor(PersonNameUtils.discoverPersonNames("Peter Silie " + SEARCH_MAGIC_EDITOR));
		
		// TODO: remove random
		
		publication.setTitle(getTitleForId(i));
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
	 * @param i
	 * @return
	 */
	private static String getTitleForId(int id) {
		return "title "+ String.valueOf(id) + " " + SEARCH_MAGIC_TITLE;
	}

	/**
	 * generate a Bookmark Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private Post<Bookmark> generateBookmarkDatabaseManagerTestPost() {
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
		bookmark.setTitle("test" + (Math.round(Math.random() * Integer.MAX_VALUE)) + " " + SEARCH_MAGIC_TITLE);
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		resource = bookmark;

		post.setResource(resource);
		return post;
	}
}