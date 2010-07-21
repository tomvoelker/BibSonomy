package org.bibsonomy.database.systemtags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.database.managers.AbstractDBLogicBase;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.systemstags.SystemTagFactory;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemtagsTest extends AbstractDBLogicBase {
	
	private static UserDatabaseManager userDb;
	private static InboxDatabaseManager inboxDb;
	private static GroupDatabaseManager groupDb;
	private static BookmarkDatabaseManager bookmarkDb;
	private static BibTexDatabaseManager bibTexDb;
	
	/**
	 * inits managers
	 */
	@BeforeClass
	public static void setupManagers() {
		userDb = UserDatabaseManager.getInstance();
		groupDb = GroupDatabaseManager.getInstance();
		inboxDb = InboxDatabaseManager.getInstance();
		bookmarkDb = BookmarkDatabaseManager.getInstance();
		bibTexDb = BibTexDatabaseManager.getInstance();
	}


	/**
	 * Test Functionality of the SystemTagFactory
	 */
	@Test
	public void testSystemTagFactory() {
		// test initialization of systemTag collections (in constructor of SystemTagFactory)
		SystemTagFactory sysTagFactory = SystemTagFactory.getInstance();
		assertNotNull(sysTagFactory.getExecutableSystemTag("for:foogroup"));
		assertTrue(sysTagFactory.isExecutableSystemTag("send:sdo"));
		assertTrue(sysTagFactory.isSearchSystemTag("sys:author:sdo"));
		assertTrue(sysTagFactory.isSearchSystemTag("sys:entrytype:article"));
		assertFalse(sysTagFactory.isExecutableSystemTag("sys:author:sdo"));
		assertFalse(sysTagFactory.isExecutableSystemTag("send"));		
	}
	

	
	/**
	 * Test Search SystemTags
	 */
	
	@Test
	public void testAuthor() {
		String systemtag = "sys:author:greatAuthor";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("greatAuthor", param.getAuthor());
	}

	@Test
	public void testBibtexKey() {
		String systemtag = "sys:bibtexkey:123456";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("123456", param.getBibtexKey());
	}

	@Test
	public void testDays() {
		String systemtag = "sys:days:13";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals(13, param.getDays());
	}
	
	/**
	 * Tests most queries which should be useable with the entrytype system tag.
	 */
	@Test
	public void testEntryType(){
		BibTexParam param = null;
		List<Post<BibTex>> posts = null;
		
		/*
		 * tests the GetBibtexForUser query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:Article"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(0, posts.size());
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexByKey query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:Book"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setBibtexKey("test bibtexKey");
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(0, posts.size());		
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setBibtexKey("test bibtexKey");
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexByTagNamesAndUser query
		 */
		List<String> tags = new ArrayList<String>();
		tags.add("sys:entrytype:Book");
		tags.add("testbibtex");
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", tags, "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(1);
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(0, posts.size());		
		
		tags = new ArrayList<String>();
		tags.add("sys:entrytype:test entrytype");
		tags.add("testbibtex");
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", tags , "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(1);
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexByConceptForUser query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:Book"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(1);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(0);
		param.addSimpleConceptName("testbibtex");
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(0, posts.size());
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(1);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(0);
		param.addSimpleConceptName("testbibtex");
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexForHomePage query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:Book"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(0, posts.size());
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = bibTexDb.getPosts(param, this.dbSession);
		
		assertEquals(2, posts.size());
	}
	
	@Test
	public void testGroup() {
		String systemtag = "sys:group:someGroup";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("someGroup", param.getRequestedGroupName());
		assertEquals(GroupingEntity.GROUP, param.getGrouping());
	}

	@Test
	public void testTitle() {
		String systemtag1 = "sys:title:word1";
		String systemtag2 = "sys:title:word2";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag1, systemtag2 }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("word1 word2", param.getTitle());
	}

	@Test
	public void testUser() {
		String systemtag = "sys:user:Me";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("Me", param.getRequestedUserName());
		assertEquals(GroupingEntity.USER, param.getGrouping());
	}
	
	@Test
	public void testYear() {
		String systemTag = "sys:Year:1999";
        BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemTag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("1999", param.getYear());
		systemTag = "sys:year:2000-2010";
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemTag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("2000-2010", param.getFirstYear());
		assertEquals("2000-2010", param.getLastYear());
		systemTag = "sys:year:2000-";
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemTag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("2000", param.getFirstYear());
		systemTag = "sys:year:-2010";
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemTag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals("2010", param.getLastYear());
		// Test if systemTag was added to param's systemTag Collection
		assertNotNull(param.getSystemTags().get("year"));
	}
	
	/**
	 * Test Executable SystemTags
	 */
	@Test
	public void testForGroupTag() {
		// create users
		User testUser1 = createTestUser("forgroupuser1");
		User testUser2 = createTestUser("forgroupuser2");
		
		// create groups 
		createTestUser("forgroup1");
		createTestUser("forgroup2");
		Group testGroup1 = createTestGroup("forgroup1");
		Group testGroup2 = createTestGroup("forgroup2");
		
		// add users to groups
		groupDb.addUserToGroup("forgroup1", "forgroupuser1", this.dbSession);
		groupDb.addUserToGroup("forgroup1", "forgroupuser2", this.dbSession);
		groupDb.addUserToGroup("forgroup2", "forgroupuser2", this.dbSession);
		
		// update users
		testUser1.setGroups(groupDb.getGroupsForUser(testUser1.getName(), this.dbSession));
		testUser2.setGroups(groupDb.getGroupsForUser(testUser2.getName(), this.dbSession));

		// create posts
		Set<Tag> tags1 = ModelUtils.getTagSet("for:forgroup1");
		Set<Tag> tags2 = ModelUtils.getTagSet("for:forgroup1", "for:forgroup2");
		
		List<Post<?>> posts1 = new LinkedList<Post<?>>();
		List<Post<?>> posts2 = new LinkedList<Post<?>>();
		List<Post<?>> posts3 = new LinkedList<Post<?>>();
		posts1.add(createTestBookmarkPost(testUser1, tags1));
		posts2.add(createTestBookmarkPost(testUser2, tags2));
		posts3.add(createTestBookmarkPost(testUser1, tags2));
		//change posts3 to avoid douplicates 
		posts3.get(0).getResource().setTitle("some other title");
		// store posts
		DBLogicUserInterfaceFactory logicFactory = new DBLogicUserInterfaceFactory(); 
		logicFactory.setDbSessionFactory(getDbSessionFactory());
		LogicInterface logic1 = logicFactory.getLogicAccess(testUser1.getName(), "password");
		LogicInterface logic2 = logicFactory.getLogicAccess(testUser2.getName(), "password");
		
		// Scenario: 
		//    forgroupuser1 is member of forgroup1
		//    forgroupuser2 is member of forgroup1 and forgroup2
		// 
		//    post1 one contains tags 'for:forgroup1'
		//    post2 one contains tags 'for:forgroup1' and 'for:forgroup2'
		//    post3 one contains tags 'for:forgroup1' and 'for:forgroup2'
		//
		//    post1 is owned by forgroupuser1
		//    post2 is owned by forgroupuser2
		//    post3 is owned by forgroupuser1
		//
		//    logic1 is forgroupuser1's instance
		//    logic2 is forgroupuser2's instance
		
		// forgroupuser1 gives post1 to forgroup1
		logic1.createPosts(posts1);
		List<?> retVal = lookupGroupPost(posts1.get(0), logic1, testGroup1.getName());
		assertEquals(1, retVal.size());
		retVal = lookupGroupPost(posts1.get(0), logic1, testGroup2.getName());
		assertEquals(0, retVal.size());
		
		// forgroupuser2 gives post1 and post2 to forgroup1
		logic2.createPosts(posts2);
		retVal = lookupGroupPost(posts2.get(0), logic2, testGroup2.getName());
		assertEquals(1, retVal.size());
		retVal = lookupGroupPost(posts2.get(0), logic2, testGroup2.getName());
		assertEquals(1, retVal.size());
		
		// forgroupuser1 gives post3 to forgroup2 -- we expect an error
		try {
			logic1.createPosts(posts3);
			fail("User was not allowed to write post");
		} catch (DatabaseException ex){
			// ignore
		}
		
		// forgroupuser1 gives post2 to forgroup1 and forgroup2 -- we expect an error
		try {
			logic1.createPosts(posts2);
			fail("User was not allowed to write post");
		} catch (AccessDeniedException ve){
			// ignore
		}
	}
	
	
	@Test
	public void testForFriendTag(){
		/*
		 * Create 2 users
		 */
		User testUser1 = createTestUser("senderUser");
		User testUser2 = createTestUser("receiverUser");
		// make a logic for each user
		DBLogicUserInterfaceFactory logicFactory = new DBLogicUserInterfaceFactory();
		logicFactory.setDbSessionFactory(getDbSessionFactory());
		LogicInterface user1Logic = logicFactory.getLogicAccess(testUser1.getName(), "password");
		LogicInterface user2Logic = logicFactory.getLogicAccess(testUser2.getName(), "password");
		// user 2 adds user 1 as a friend => user 1 can now send posts to user 2
		// however user 2 can not send posts to user 1
		testUser2.addFriend(testUser1);
		user2Logic.createUserRelationship(testUser2.getName(), testUser1.getName(), UserRelation.OF_FRIEND);
		
		/*
		 *  User 2 tries to send a post to user1: We assume failure
		 */
		/*Set<Tag> tags = ModelUtils.getTagSet("send:" + testUser1.getName());
		Post<? extends Resource> post = createTestBookmarkPost(testUser2, tags);
		List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);
		try {
			user2Logic.createPosts(posts);
			fail("User2 was not allowed to send a post to user1");
		} catch (DatabaseException de) {
			// one errorMessage should be present, caused by the unallowed Tag
			assertEquals(1, de.getErrorMessages(post.getResource().getIntraHash()).size());
			ErrorMessage em = de.getErrorMessages(post.getResource().getIntraHash()).get(0);
			assertTrue(SystemTagErrorMessage.class.isAssignableFrom(em.getClass()));
		}*/

		/*
		 * User2 tries to send a post to himself: We assume failure
		 */
		/*final Tag tag = tags.iterator().next();
		tag.setName("send:"+testUser2.getName());
		try {
			user2Logic.createPosts(posts);
			fail("User2 was not allowed to send a post to himself");
		} catch (DatabaseException de) {
			// one errorMessage should be present, caused by the unallowed Tag
			assertEquals(1, de.getErrorMessages(post.getResource().getIntraHash()).size());
			ErrorMessage em = de.getErrorMessages(post.getResource().getIntraHash()).get(0);
			assertTrue(SystemTagErrorMessage.class.isAssignableFrom(em.getClass()));
		}*/

		/*
		 * User1 tries to send a post to user2: Since he is user2s friend we assume success
		 */
		Set<Tag> tags = ModelUtils.getTagSet("foo", "send:"+testUser2.getName());
		
		Post<Bookmark> bookmark= this.createTestBookmarkPost(testUser1, tags);
		List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(bookmark);
		
		tags = ModelUtils.getTagSet("bar", "send:"+testUser2.getName());
		
		Post<BibTex> publication = this.createTestPublicationPost(testUser1, tags);
		posts.add(publication);
		user1Logic.createPosts(posts);
		// user 2 should now have 2 posts in his inbox, 1 bookmark and 1 bibtex
		assertEquals(2, inboxDb.getNumInboxMessages(testUser2.getName(), dbSession));
		assertEquals(1, user2Logic.getPostStatistics(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		assertEquals(1, user2Logic.getPostStatistics(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		// get posts from inbox and count
		assertEquals(1, user2Logic.getPosts(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null).size());
		assertEquals(1, user2Logic.getPosts(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null).size());

		/*
		 * User1 now changes (and finally deletes) his posts, We expect NO changes in the inbox
		 */
		/*
		 * User1 now changes his bookmark post without changing the hash
		 */
		bookmark.getResource().setTitle("a new title");
		posts = new LinkedList<Post<?>>();
		posts.add(bookmark);
		user1Logic.updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
		// change only a tag
		bookmark.addTag("fooBookmark");
		user1Logic.updatePosts(posts, PostUpdateOperation.UPDATE_TAGS);
		// there should now still be only one bookmarkPost in the inbox
		assertEquals(1, user2Logic.getPostStatistics(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		// the bookmarkPost from the inbox should look exactly like the original post
		List<Post<Bookmark>> inboxBookmarks = user2Logic.getPosts(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null);
		assertEquals(inboxBookmarks.get(0).getResource().getTitle(), "test");
		// the bookmarkPost from the inbox should still have only 2 tags (foo and from:senderUser)
		assertEquals(2, inboxBookmarks.get(0).getTags().size());

		/*
		 * User1 now changes his bookmark post changing the hash
		 */
		bookmark.getResource().setUrl("http://testurl2.orgg");
		user1Logic.updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
		// there should now still be only one bookmarkPost in the inbox
		assertEquals(1, user2Logic.getPostStatistics(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		// the bookmarkPost from the inbox should look exactly like the original post
		inboxBookmarks = user2Logic.getPosts(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null);
		assertEquals(inboxBookmarks.get(0).getResource().getTitle(), "test");
		assertEquals(2, inboxBookmarks.get(0).getTags().size());
		assertEquals(inboxBookmarks.get(0).getResource().getUrl(), "http://www.testurl.orgg");

		/*
		 * User1 now deletes his bookmarPost
		 */
		List<String> intraHashes =new ArrayList<String>();
		intraHashes.add(bookmark.getResource().getHash());
		user1Logic.deletePosts(testUser1.getName(), intraHashes);
		// there should now still be only one bookmarkPost in the inbox
		assertEquals(1, user2Logic.getPostStatistics(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		// the bookmarkPost from the inbox should look exactly like the original post
		inboxBookmarks = user2Logic.getPosts(Bookmark.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null);
		assertEquals(inboxBookmarks.get(0).getResource().getTitle(), "test");
		assertEquals(2, inboxBookmarks.get(0).getTags().size());
		assertEquals(inboxBookmarks.get(0).getResource().getUrl(), "http://www.testurl.orgg");
		
		
		/*
		 * User1 now changes his publication post without changing the hash
		 */
		publication.getResource().setChapter("chapter1");
		posts = new LinkedList<Post<?>>();
		posts.add(publication);
		user1Logic.updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
		// change only a tag
		publication.addTag("barBibTex");
		user1Logic.updatePosts(posts, PostUpdateOperation.UPDATE_TAGS);
		assertEquals(1, user2Logic.getPostStatistics(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		// the inboxPost should still have no chapter, just as the original testPost
		List<Post<BibTex>> inboxPublications = user2Logic.getPosts(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null);
		assertEquals(inboxPublications.get(0).getResource().getChapter(), null);
		// the bookmarkPost from the inbox should still have only 2 tags (bar and from:senderUser)
		assertEquals(2, inboxPublications.get(0).getTags().size());
		
		/*
		 * User1 now changes his publication post changing the hash
		 */
		publication.getResource().setAuthor("Famous Author");
		user1Logic.updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
		// there should now still be only one publicationPost in the inbox
		assertEquals(1, user2Logic.getPostStatistics(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		// the inboxPost should still have the same author as the original post
		inboxPublications = user2Logic.getPosts(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null);
		assertEquals(2, inboxPublications.get(0).getTags().size());
		assertEquals(inboxPublications.get(0).getResource().getAuthor(), "Lonely Writer");
		assertEquals(inboxPublications.get(0).getResource().getChapter(), null);
		
		/*
		 * User1 now deletes his publicationPost
		 */
		intraHashes =new ArrayList<String>();
		intraHashes.add(publication.getResource().getIntraHash());
		user1Logic.deletePosts(testUser1.getName(), intraHashes);
		// there should now still be only one publicationPost in the inbox
		assertEquals(1, user2Logic.getPostStatistics(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 0, null, null));
		// the inboxPost should still have the same author as the original post
		inboxPublications = user2Logic.getPosts(BibTex.class, GroupingEntity.INBOX, testUser2.getName(), null, null, null, null, 0, 10, null);
		assertEquals(2, inboxPublications.get(0).getTags().size());
		assertEquals(inboxPublications.get(0).getResource().getAuthor(), "Lonely Writer");
		assertEquals(inboxPublications.get(0).getResource().getChapter(), null);
		
		/*
		 * User2 now clears his Inbox
		 */
		user2Logic.deleteInboxMessages(null, true);
	}
	

	
	/*
	 * create a testBookmark for a given user and with given TAgs
	 */
	private Post<Bookmark> createTestBookmarkPost(final User user, final Set<Tag> tags) {
		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0);
		bookmark.setTitle("test");
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		return createTestPost(bookmark, user, tags);
	}

	
	/*
	 * create a testPublication for a given user and with given Tags
	 */
	private Post<BibTex> createTestPublicationPost(final User user, final Set<Tag> tags) {
		final BibTex publication = new BibTex();
		publication.setCount(0);
		publication.setAbstract("The abstract of a testPost");
		publication.setAuthor("Lonely Writer");
		publication.setBibtexKey("test");
		publication.setEntrytype("article");
		publication.setEditor("Edith Editor");
		publication.setTitle("test");
		return createTestPost(publication, user, tags);
	}
	
	private <T extends Resource> Post<T> createTestPost(final T resource, final User user, final Set<Tag> tags) {
		// generate post
		final Post<T> post = new Post<T>();
		final Group group = new Group();

		group.setDescription(null);
		group.setName("public");
		group.setGroupId(GroupID.PUBLIC.getId());
		post.getGroups().add(group);

		post.getTags().addAll(tags);
		
		post.setContentId(null); // will be set in storePost()
		post.setDescription("Some description");
		post.setDate(new Date());
		post.setUser(user);

		post.setResource(resource);

		return post;
	}
	
	/**
	 * Get test user for given name.
	 * 
	 * @param name
	 * @return
	 */
	private User createTestUser( String name ) {
		// lookup
		User user = userDb.getUserDetails(name, this.dbSession);
		if( user.getName()!=null ) {
			final List<Post<Bookmark>> bookmarks = 
				bookmarkDb.getPostsForUser(null, name, HashID.INTRA_HASH, GroupID.INVALID.getId(), new ArrayList<Integer>(), null, Integer.MAX_VALUE, 0, null, this.dbSession);
			for( Post<Bookmark> post : bookmarks ) {
				bookmarkDb.deletePost(name, post.getResource().getIntraHash(), this.dbSession);
			}
			final List<Post<BibTex>> publications = 
				bibTexDb.getPostsForUser(null, name, HashID.INTRA_HASH, GroupID.INVALID.getId(), new ArrayList<Integer>(), null, Integer.MAX_VALUE, 0, null, this.dbSession);
			for( Post<BibTex> post : publications) {
				bibTexDb.deletePost(name, post.getResource().getIntraHash(), this.dbSession);
			}
			
		} else {
			user = new User(name);
			user.setRealname("New Testuser");
			user.setEmail("new-testuser@bibsonomy.org");
			user.setHomepage(ParamUtils.EXAMPLE_URL);
			user.setPassword("password");
			user.setApiKey("00000000000000000000000000000000");
			user.getSettings().setDefaultLanguage("zv");
			user.setSpammer(false);
			user.setRole(Role.DEFAULT);
			user.setToClassify(1);
			user.setAlgorithm(null);
			userDb.createUser(user, this.dbSession);			
			userDb.activateUser(user, this.dbSession);
		}
		return user;
	}
	
	/**
	 * Get test group for given name.
	 * 
	 * @param name
	 * @return
	 */
	private Group createTestGroup( String name ) {
		Group group = groupDb.getGroupByName(name, this.dbSession);
		if( group!=null ) {
			groupDb.deleteGroup(name, this.dbSession);
		}
		group = new Group();
		group.setName(name);
		groupDb.createGroup(group, this.dbSession);

		return group;
	}

	/**
	 * Lookup given post for given group.
	 * 
	 * @param <T>
	 * @param post
	 * @param logic
	 * @param groupName
	 * @return
	 */
	private <T extends Resource> List<Post<T>> lookupGroupPost(Post<T> post, LogicInterface logic, String groupName ) {
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		List<String> tags = new LinkedList<String>();
		// FIXME: why does GetPostsForGroup chain element not allow hash-selection?
		List<Post<T>> groupPosts = logic.getPosts(
				(Class<T>)post.getResource().getClass(), groupingEntity, groupName, tags, 
				post.getResource().getIntraHash(), null, null, 0, Integer.MAX_VALUE, "");
		return groupPosts;
	}
	
	
	/**
	 * Some old tests, should probably be deleted since the tested functions are no longer in use
	 */
	@Test
	@Ignore
	public void testAttribute() {
		String groupingTag = "sys:grouping";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { groupingTag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals(GroupingEntity.USER, param.getGrouping());
	}

	@Test
	@Ignore
	public void testFormat() {
		String systemtag = "sys:date:12:03:1983";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals(GroupingEntity.FRIEND, param.getGrouping());
	}

	@Test
	@Ignore
	public void testFormatFalse() {
		String systemtag = "sys:date:12:03:3";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		assertEquals(GroupingEntity.USER, param.getGrouping());
	}


}