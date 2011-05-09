package org.bibsonomy.database.managers;

import static org.bibsonomy.testutil.Assert.assertTagsByName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.CommonModelUtils;
import org.bibsonomy.testutil.DBTestUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * tests related to bookmarks.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Anton Wilhelm
 * 
 * @version $Id$
 */
public class BookmarkDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final String TESTUSER1_NAME = "testuser1";
	private static final String TESTUSER2_NAME = "testuser2";
	
	private static BookmarkDatabaseManager bookmarkDb;
	
	/**
	 * sets up the bookmark db manager
	 */
	@BeforeClass
	public static void setupManager() {
		bookmarkDb = BookmarkDatabaseManager.getInstance();
	}
	

	/**
	 * tests getBookmarkByTagNames
	 */
	@Test
	public void getBookmarkByTagNames() {
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("suchmaschine");
		List<Post<Bookmark>> posts = bookmarkDb.getPostsByTagNames(PUBLIC_GROUP_ID, tagIndex, null, 10, 0, this.dbSession);
		assertEquals(3, posts.size());
		
		// more restriction
		DBTestUtils.addToTagIndex(tagIndex, "google");
		posts = bookmarkDb.getPostsByTagNames(PUBLIC_GROUP_ID, tagIndex, null, 10, 0, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getBookmarkByTagNamesCount
	 */
	@Test
	public void getBookmarkByTagNamesCount() {
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("suchmaschine");							
		assertEquals(6, bookmarkDb.getPostsByTagNamesCount(tagIndex, PUBLIC_GROUP_ID, this.dbSession));
		
		DBTestUtils.addToTagIndex(tagIndex, "google");
		assertEquals(8, bookmarkDb.getPostsByTagNamesCount(tagIndex, PUBLIC_GROUP_ID, this.dbSession));
		
		DBTestUtils.addToTagIndex(tagIndex, "yahoo");
		assertEquals(0, bookmarkDb.getPostsByTagNamesCount(tagIndex, PUBLIC_GROUP_ID, this.dbSession));
	}

	/**
	 * tests getBookmarkByTagNamesForUser
	 * 
	 * visibleGroupIDs must only be set, if ( groupId == -1 && userName != null ) && userName != requestedUserName
	 * tagIndex must be set
	 * requestedUserName must be set
	 */
	@Test
	public void getBookmarkByTagNamesForUser() {
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("suchmaschine");
		List<Post<Bookmark>> posts;
		
		// get public posts of testuser1
		posts = bookmarkDb.getPostsByTagNamesForUser(null, TESTUSER1_NAME, tagIndex, PUBLIC_GROUP_ID, visibleGroupIDs, 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		// get private post of testuser1
		posts = bookmarkDb.getPostsByTagNamesForUser(null, TESTUSER1_NAME, tagIndex, PRIVATE_GROUP_ID, visibleGroupIDs, 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		// get public post of testuser1 (but groupId is now invalid)
		posts = bookmarkDb.getPostsByTagNamesForUser(null, TESTUSER1_NAME, tagIndex, INVALID_GROUP_ID, visibleGroupIDs, 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		// get friends posts of testuers1 for testuser2
		final List<TagIndex> tagIndex2 = DBTestUtils.getTagIndex("friends");
		posts = bookmarkDb.getPostsByTagNamesForUser(TESTUSER2_NAME, TESTUSER1_NAME, tagIndex2, FRIENDS_GROUP_ID, visibleGroupIDs, 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getBookmarkByConceptForUser
	 * 
	 * if userName == null || userName == requestedUserName, you don't need to add a group to visibleGroupIDs
	 * otherwise you have to set visibleGroupIDs
	 * requestedUserName must be set
	 * tagIndex must be set
	 */
	@Test
	public void getBookmarkByConceptForUser() {
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("suchmaschine");
		
		final List<Integer> visibleGroupIDs = Collections.singletonList(PUBLIC_GROUP_ID);
		final List<Post<Bookmark>> posts = bookmarkDb.getPostsByConceptForUser(null, TESTUSER1_NAME, visibleGroupIDs, tagIndex, false, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		// now get private posts
		final List<Post<Bookmark>> posts2 = bookmarkDb.getPostsByConceptForUser(TESTUSER1_NAME, TESTUSER1_NAME, visibleGroupIDs, tagIndex, false, 10, 0, null, this.dbSession);
		assertEquals(2, posts2.size());
	}

	/**
	 * tests getPostsFromInbox
	 */
	@Test
	public void getBookmarkFromInbox() {
		List<Post<Bookmark>> postsFromInbox = bookmarkDb.getPostsFromInbox("testuser2", 10, 0, this.dbSession);
		assertEquals(3, postsFromInbox.size());
		
		postsFromInbox = bookmarkDb.getPostsFromInbox("testuser1", 10, 0, this.dbSession);
		assertEquals(0, postsFromInbox.size());
	}
	
	/**
	 * tests getPostsFromInboxByHash
	 */
	@Test
	public void getBookmarkFromInboxByHash() {
		List<Post<Bookmark>> postsFromInboxByHash = bookmarkDb.getPostsFromInboxByHash("testuser2", "6f372faea7ff92eedf52f597090a6291", this.dbSession);
		assertEquals(1, postsFromInboxByHash.size());
		
		postsFromInboxByHash = bookmarkDb.getPostsFromInboxByHash("testuser1", "6f372faea7ff92eedf52f597090a6291", this.dbSession);
		assertEquals(0, postsFromInboxByHash.size());
	}
	
	/**
	 * tests getBookmarkByUserFriends
	 * 
	 * userName must be set
	 */
	@Test
	public void getBookmarkByUserFriends() {
		final List<Post<Bookmark>>  posts1 = bookmarkDb.getPostsByUserFriends(TESTUSER2_NAME, HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, posts1.size());
	}

	/**
	 * tests getBookmarkForHomepage
	 * 
	 * groupType must be set
	 */
	@Test
	public void getBookmarkForHomepage() {
		final List<Post<Bookmark>> posts = bookmarkDb.getPostsForHomepage(null, 10, 0, null, this.dbSession);
		assertEquals(5, posts.size());
	}

	/**
	 * tests getBookmarkPopular
	 */
	@Test
	public void getBookmarkPopular() {
		final List<Post<Bookmark>> posts = bookmarkDb.getPostsPopular(0, 30, 0, HashID.INTER_HASH, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getBookmarkByHash
	 * 
	 * hash (requBibtex) must be set
	 * groupType must be set
	 */
	@Test
	public void getBookmarkByHash() {
		String requBookmark = "b7aa3a91885e432c6c95bec0145c3968";
		List<Post<Bookmark>> post = bookmarkDb.getPostsByHash(requBookmark, HashID.INTRA_HASH, FRIENDS_GROUP_ID, 10, 0, this.dbSession);
		assertEquals(1, post.size());
		
		// this should test which bookmark will be received (there are two in equal hashes in the test database one with public group one with friend group) 
		requBookmark = "85ab919107e4cc79b345e996b3c0b097";
		post = bookmarkDb.getPostsByHash(requBookmark, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession);
		assertEquals(1, post.size());
		
		requBookmark = "85ab919107e4cc79b345e996b3c0b097";
		post = bookmarkDb.getPostsByHash(requBookmark, HashID.INTRA_HASH, FRIENDS_GROUP_ID, 10, 0, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBookmarkByHashCount
	 * 
	 * hash (requBibtex) must be set
	 * groupType must be set
	 */
	@Test
	public void getBookmarkByHashCount() {
		final String requHash = "b7aa3a91885e432c6c95bec0145c3968";
		int count = bookmarkDb.getPostsByHashCount(requHash, HashID.INTRA_HASH, this.dbSession);
		assertTrue(count >= 0);
	}

	/**
	 * tests getBookmarkByHashForUser
	 * 
	 * hash (requBibtex) must be set
	 * requestedUserName must be set
	 * if userName == null || userName == requestedUserName, you don't need to add a group to visibleGroupIDs 
	 * 
	 */
	@Test
	public void getBookmarkByHashForUser() {
		// This bookmark is a private bookmark of testuser1
		final String requBibtex = "294a9e1d594297e7bb9da9e11229c5d7";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		List<Post<Bookmark>> post = bookmarkDb.getPostsByHashForUser(null, requBibtex, TESTUSER2_NAME, visibleGroupIDs, null, this.dbSession);
		assertEquals(0, post.size());
		
		post = bookmarkDb.getPostsByHashForUser(TESTUSER1_NAME, requBibtex, TESTUSER1_NAME, visibleGroupIDs, null, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBookmarkViewable
	 * 
	 * groupId must be set 
	 * userName must only be set, when groupId > 3
	 */
	@Test
	public void getBookmarkViewable() {
		List<Post<Bookmark>> posts = bookmarkDb.getPostsViewable(null, TESTUSER1_NAME, PUBLIC_GROUP_ID, HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		posts = bookmarkDb.getPostsViewable(null, TESTUSER1_NAME, PRIVATE_GROUP_ID, HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		posts = bookmarkDb.getPostsViewable(null, TESTUSER1_NAME, FRIENDS_GROUP_ID, HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		// if groupId > 2, you don't need userName (chain manage the access control)
		posts = bookmarkDb.getPostsViewable(null, "egal", TESTGROUP2_ID, HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());
	}

	/**
	 * tests getBookmarkForGroup
	 * 
	 * if userName == null, you don't need visibleGroupIDs
	 * otherwise:
	 * groupId must be set
	 * visibleGroupIDs must be set
	 * userName must be set
	 */
	@Test
	public void getBookmarkForGroup() {
		/*
		 * testuser1 & testuser2 are members of group 3 
		 * testuser1 is also member of group 4
		 */
		// get all posts of testuser1 (and testuser2) which are public or friends + private posts of testuser1
		final List<Integer> visibleGroupIDsUser2 = new LinkedList<Integer>();
		visibleGroupIDsUser2.add(PUBLIC_GROUP_ID);
		visibleGroupIDsUser2.add(TESTGROUP1_ID); // both users are members of group 3
		
		final List<Integer> visibleGroupIDsUser1 = new LinkedList<Integer>(visibleGroupIDsUser2);
		visibleGroupIDsUser1.add(TESTGROUP2_ID); // user 1 is also member of group 4
		
		final List<Post<Bookmark>> posts = bookmarkDb.getPostsForGroup(TESTGROUP1_ID, visibleGroupIDsUser1, TESTUSER1_NAME, HashID.INTRA_HASH, null, 20, 0, null, this.dbSession);
		assertEquals(10, posts.size());

		// get all posts of testuser1 (and testuser2) which are public or friends + private posts of testuser1
		final List<Post<Bookmark>> posts2 = bookmarkDb.getPostsForGroup(TESTGROUP2_ID, visibleGroupIDsUser1, TESTUSER1_NAME, HashID.INTRA_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(8, posts2.size());

		// get all posts of testuser2 (and testuser1) which are public or friends or group posts of testuser1
		final List<Post<Bookmark>> posts3 = bookmarkDb.getPostsForGroup(TESTGROUP1_ID, visibleGroupIDsUser2, TESTUSER2_NAME, HashID.INTRA_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(7, posts3.size());

		// get all posts by testuser1, testuser2, which are public or friends no group posts
		final List<Post<Bookmark>> posts4 = bookmarkDb.getPostsForGroup(TESTGROUP2_ID, visibleGroupIDsUser2, TESTUSER2_NAME, HashID.INTRA_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(5, posts4.size());
	}

	/**
	 * tests getBookmarkForGroupCount
	 * 
	 * groupId must be set
	 * you don't need to add a group to visibleGroupIDs, because you have no userName
	 * 
	 * visibleGroupIDs && userName && (userName != requestedUserName) optional
	 */
	@Test
	public void getBookmarkForGroupCount() {
		//approximated number of bookmarks, users own private/friends bookmarks are not included
		final String requestedUserName = "";
		final String loginUserName = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		final int count1 = bookmarkDb.getPostsForGroupCount(requestedUserName, loginUserName, TESTGROUP1_ID, visibleGroupIDs, this.dbSession);
		assertEquals(3, count1);
		final int count3 = bookmarkDb.getPostsForGroupCount(requestedUserName, loginUserName, TESTGROUP2_ID, visibleGroupIDs, this.dbSession);
		assertEquals(1, count3);
	}

	/**
	 * tests getBookmarkForGroupByTag
	 * 
	 * userName can be set
	 * groupId must be set
	 * visibleGroupIDs must be set
	 * tagIndex must be set
	 */
	@Test
	public void getBookmarkForGroupByTag() {
		String userName = TESTUSER1_NAME;
		final List<Integer> visibleGroupIDs = Collections.singletonList(PUBLIC_GROUP_ID);
		List<TagIndex> tagIndex = DBTestUtils.getTagIndex("suchmaschine");
		List<Post<Bookmark>> posts = bookmarkDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, userName, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(3, posts.size());
		
		posts = bookmarkDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, null, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());

		userName = "testuser2";
		posts = bookmarkDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, userName, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());
	}
	
	/**
	 * tests getBookmarkForUser
	 *
	 * visibleGroupIDs must only be set, if ( groupId == -1 && userName != null ) && userName != requestedUserName
	 * groupId must be set
	 * userName must be set
	 */
	@Test
	public void getBookmarkForUser() {
		final String requestedUserName = TESTUSER1_NAME;
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		// testuser1 has two public bookmarks
		final List<Post<Bookmark>> posts = bookmarkDb.getPostsForUser(null, requestedUserName, HashID.INTRA_HASH, PUBLIC_GROUP_ID, visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		
		/*
		 * testuser1 has one bookmark for friends
		 */
		final List<Post<Bookmark>> posts1 = bookmarkDb.getPostsForUser(null, requestedUserName, HashID.INTRA_HASH, FRIENDS_GROUP_ID, visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(1, posts1.size());
		
		// testuser has two posts for group 4
		final List<Post<Bookmark>> posts2 = bookmarkDb.getPostsForUser(null, requestedUserName, HashID.INTRA_HASH, 4, visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts2.size());
		
		// invalid groupId => get all posts of testsuser1
		// when groupId = invalid, you need userName otherwise (userName == null) the groupId would be 0
		// testuser1 may see all has own posts
		final List<Post<Bookmark>> posts3 = bookmarkDb.getPostsForUser(TESTUSER1_NAME, requestedUserName, HashID.INTRA_HASH, GroupID.INVALID.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(8, posts3.size());
		
		// invalid groupId => testuser23 (which is no friend of testuser1) can only see public posts of testuser1
		visibleGroupIDs.add(PUBLIC_GROUP_ID);
		final List<Post<Bookmark>> posts4 = bookmarkDb.getPostsForUser("testuser23", requestedUserName, HashID.INTRA_HASH, GroupID.INVALID.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(1, posts4.size());
		
		// invalid groupId => testuser3 (which is a friend of testuser1!) can see public+friends posts of testuser1
		final List<Post<Bookmark>> posts5 = bookmarkDb.getPostsForUser("testuser3", requestedUserName, HashID.INTRA_HASH, GroupID.INVALID.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		// public + friends
		assertEquals(1 + 1, posts5.size());
	}

	/**
	 * tests getBookmarkForUserCount
	 * 
	 * visibleGroupIDs must only be set, if ( groupId == -1 && userName != null ) && userName != requestedUserName
	 * groupId must be set
	 * userName must be set
	 */
	@Test
	public void getBookmarkForUserCount() {
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		final int count1 =  bookmarkDb.getPostsForUserCount(TESTUSER1_NAME, "", PUBLIC_GROUP_ID, visibleGroupIDs, this.dbSession);	
		assertEquals(1, count1);
		
		final int count2 =  bookmarkDb.getPostsForUserCount(TESTUSER2_NAME, "", PUBLIC_GROUP_ID, visibleGroupIDs, this.dbSession);	
		assertEquals(2, count2);
		
		final int count3 =  bookmarkDb.getPostsForUserCount("testuser3", "", PUBLIC_GROUP_ID, visibleGroupIDs, this.dbSession);	
		assertEquals(1, count3);
	}
	
	private Post <Bookmark> generateBookmarkDatabaseManagerTestPost() {
		final Post<Bookmark> post = new Post<Bookmark>();

		final Group group = new Group();
		group.setDescription(null);
		group.setName("public");
		group.setGroupId(0);
		post.getGroups().add(group);
		
		ModelUtils.addToTagSet(post.getTags(), "tag1", "tag2");

		post.setContentId(null); // will be set in createPost or updatePost
		post.setDescription("Some description");
		post.setDate(new Date());
		post.setChangeDate(new Date());
		
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName(TESTUSER1_NAME);
		user.setRole(Role.NOBODY);
		post.setUser(user);
		
		final Bookmark resource = new Bookmark();
		resource.setCount(0);
		resource.setTitle("test");
		resource.setUrl("http://www.testurl.orgg");
		resource.recalculateHashes();
		post.setResource(resource);
		
		return post;
	}
	
	/**
	 * tests storePostWrongUsage
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updatePostWrongUsage() {
		final Post<Bookmark> toInsert = this.generateBookmarkDatabaseManagerTestPost();
		bookmarkDb.updatePost(toInsert, null, null, this.dbSession);
	}
	
	/**
	 * tests storePost
	 */
	@Test
	public void createPost() {
		final Post<Bookmark> toInsert = this.generateBookmarkDatabaseManagerTestPost();
		toInsert.getResource().recalculateHashes();

		// no oldIntraHash and no update
		bookmarkDb.createPost(toInsert, this.dbSession);
		final String userName = toInsert.getUser().getName();
		final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, GroupingEntity.USER, userName, Arrays.asList(new String[] { "tag1", "tag2" }), "", null, 0, 50, null, null, toInsert.getUser());
		final List<Post<Bookmark>> posts = bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), Integer.MAX_VALUE, null, new String[] { "resource", "tags", "user", "date", "changeDate" });
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), Integer.MAX_VALUE, null);

		// Duplicate post and check whether plugins are called
		assertFalse(this.pluginMock.isOnBibTexUpdate());
		
		assertTrue(bookmarkDb.deletePost(userName, toInsert.getResource().getIntraHash(), this.dbSession));
	}
	
	@Test
	public void updateBookmark() {
		final String hash = "7eda282d1d604c702597600a06f8a6b0";
		final Post<Bookmark> someBookmarkPost = bookmarkDb.getPostsByHash(hash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession).get(0);
		bookmarkDb.updatePost(someBookmarkPost, hash, null, this.dbSession);
		assertTrue(this.pluginMock.isOnBookmarkUpdate());
	}

	/**
	 * tests getContentIDForBookmark
	 * 
	 * hash (requBibtex) must be set
	 * userName must be set
	 */
	@Test
	public void getContentIDForBookmark() {
		assertEquals(6, bookmarkDb.getContentIdForPost("20592a292e53843965c1bb42bfd51876", TESTUSER2_NAME, this.dbSession));
	}
	
	/**
	 * tests deleteBookmark
	 */
	@Test
	public void deleteBookmark() {
		final String intraHash = "108eca7b644e2c5e09853619bc416ed0";
		final List<Post<Bookmark>> post = bookmarkDb.getPostsByHash(intraHash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession);
		assertEquals(1, post.size());
		
		final boolean delete = bookmarkDb.deletePost(TESTUSER2_NAME, intraHash, this.dbSession);
		assertFalse(delete); // testuser2 cannot delete this posts, the owner is testuser2

		// now try it with testuser1
		final boolean delete2 = bookmarkDb.deletePost(TESTUSER1_NAME, intraHash, this.dbSession);
		assertTrue(delete2);

		final List<Post<Bookmark>> post2 = bookmarkDb.getPostsByHash(intraHash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession);
		assertEquals(0, post2.size());
		
		// recreate the deleted post
		bookmarkDb.createPost(post.get(0), this.dbSession);
	}

	/**
	 * tests getBookmarksByConceptForGroup
	 * 
	 * visibleGroupIDs must only be set, if ( groupId == -1 && userName != null ) && userName != requestedUserName
	 * requestedUserName must be set
	 * tagIndex must be set
	 */
	@Test
	public void getBookmarksByConceptForGroup() {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName("testuser1");
		//param.setRequestedUserName("testuser1");
		param.setGroupId(-1);
		param.setGrouping(GroupingEntity.GROUP);
		param.addSimpleConceptName("apple");
		param.setRequestedGroupName("testgroup2");
		param.setGroups(Arrays.asList(0, 4));
		
		
		final List<Post<Bookmark>> posts = bookmarkDb.getPosts(param, this.dbSession);
		
		assertEquals(1, posts.size());
	}
	
	/**
	 * tests {@link PostDatabaseManager#getPostsByFollowedUsers(String, List, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	@Ignore
	public void getBookmarkByFollowedUsers() {
		/*
		 * testuser 1 follows testuser 2 and 3, who have 3 bookmark posts
		 */
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(PUBLIC_GROUP_ID);
		visibleGroupIDs.add(PRIVATE_GROUP_ID);
		visibleGroupIDs.add(FRIENDS_GROUP_ID);
		
		final List<Post<Bookmark>> posts = bookmarkDb.getPostsByFollowedUsers(TESTUSER1_NAME, visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(3, posts.size());
		
		// TODO: when updateBookmark is called first order has changed!
		assertEquals(TESTUSER2_NAME, posts.get(0).getUser().getName());
		assertEquals("20592a292e53843965c1bb42bfd51876", posts.get(1).getResource().getIntraHash());		
		assertEquals("testuser3", posts.get(1).getUser().getName());
		assertEquals("965a65fdc161e354f3828050390e2b06", posts.get(2).getResource().getIntraHash());
		assertEquals(TESTUSER2_NAME, posts.get(2).getUser().getName());
		assertEquals("7eda282d1d604c702597600a06f8a6b0", posts.get(0).getResource().getIntraHash());
	}
	
	/**
	 * We want to update a post's tags only.
	 * 
	 * We use the bookmark post from testuser1 with content_id 1.
	 * 
	 * Old tags: testtag
	 * New tags: google yahoo
	 * 
	 * Groups: 3,4,5
	 */
	@Test
	public void testUpdatePostTagsOnly() {
		/*
		 * the id of the post we're testing
		 */
		final String userName = "testuser1";
		final String intraHash = "6f372faea7ff92eedf52f597090a6291";
		/*
		 * get original post for later comparison
		 */
		final Post<Bookmark> oldPost = bookmarkDb.getPostDetails(userName, intraHash, userName, Collections.singletonList(0), this.dbSession);
		
		/*
		 * OK, normally this should be tested elsewhere, but here
		 * we check, if the post contains all information it should,
		 * in particular all of its three groups
		 */
		assertEquals(3, oldPost.getGroups().size());
		assertTagsByName(oldPost.getTags(), ModelUtils.getTagSet("testtag"));
		
		/*
		 * We set only the tags, the user name, and the resource's hash.
		 * That should be sufficient to identify the original post and
		 * update its tags.   
		 */
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setUser(new User(userName));
		final Bookmark bookmark = new Bookmark();
		bookmark.setIntraHash(intraHash);
		post.setResource(bookmark);
		final Set<Tag> tags = ModelUtils.getTagSet("google_test", "yahoo_test");
		post.setTags(tags);
		bookmarkDb.updatePost(post, intraHash, PostUpdateOperation.UPDATE_TAGS, this.dbSession);
		
		final Post<Bookmark> newPost = bookmarkDb.getPostDetails(userName, intraHash, userName, Collections.singletonList(0), this.dbSession);
		final Set<Tag> dbTags = newPost.getTags();
		assertTagsByName(tags, dbTags);
		
		/*
		 * a tag-only update should never change the content id!
		 */
		assertEquals(oldPost.getContentId(), newPost.getContentId());
		/*
		 * nor the date
		 */
		assertEquals(oldPost.getDate(), newPost.getDate());
		/*
		 * nor the groups
		 */
		assertEquals(oldPost.getGroups(), newPost.getGroups());
	}
	
	
	/**
	 * We want to completely update a post
	 * 
	 * We use the bookmark post from testuser1 with content_id 1.
	 * 
	 * 
	 */
	@Test
	public void testUpdatePost() {
		/*
		 * the id of the post we're testing
		 */
		final String userName = "testuser1";
		final String oldIntraHash = "2574200000e4bb79b100e406b777a044";
		/*
		 * get original post for later comparison
		 */
		final Post<Bookmark> oldPost = bookmarkDb.getPostDetails(userName, oldIntraHash, userName, Collections.singletonList(0), this.dbSession);
	
		/*
		 * We set only the tags, the user name, and the resource's hash.
		 * That should be sufficient to identify the original post and
		 * update its tags.   
		 */
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setUser(new User(userName));
		post.setGroups(Collections.singleton(GroupUtils.getPublicGroup()));
		post.setDate(new Date());
		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle("New Title");
		bookmark.setUrl("http://www.example.com/");
		bookmark.recalculateHashes();
		final String newIntraHash = bookmark.getIntraHash();
		post.setResource(bookmark);
		// add some tags that do not interfere with other tests (especially with tags in tag-relations)
		final Set<Tag> tags = ModelUtils.getTagSet("google_test", "yahoo_test");
		post.setTags(tags);
		bookmarkDb.updatePost(post, oldIntraHash, PostUpdateOperation.UPDATE_ALL, this.dbSession);
		
		final Post<Bookmark> newPost = bookmarkDb.getPostDetails(userName, newIntraHash, userName, Collections.singletonList(0), this.dbSession);
		final Set<Tag> dbTags = newPost.getTags();
		
		assertTagsByName(tags, dbTags);
		/*
		 * a complete update MUST change the content id!
		 */
		assertFalse(oldPost.getContentId().equals(newPost.getContentId()));
		/*
		 * and the change date!
		 */
		assertFalse(oldPost.getChangeDate().equals(newPost.getChangeDate()));
		/*
                 * but not the date!
                 */
		assertEquals(oldPost.getDate(), newPost.getDate());
		/*
		 * the hashes and so on also should have changed
		 */
		final Bookmark newBookmark = newPost.getResource();
		assertFalse(oldPost.getResource().getIntraHash().equals(newBookmark.getIntraHash()));
		/*
		 * the new URL and title ...
		 */
		assertEquals("New Title", newBookmark.getTitle());
		assertEquals("http://www.example.com/", newBookmark.getUrl());
	}
	
}