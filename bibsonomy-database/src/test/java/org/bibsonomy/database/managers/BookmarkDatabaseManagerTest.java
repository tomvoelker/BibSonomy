package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.CommonModelUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to Bookmarks.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Anton Wilhelm
 * @version $Id$
 */
public class BookmarkDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static final Log log = LogFactory.getLog(BibTexDatabaseManagerTest.class);

	/**
	 * tests getBookmarkByTagNames
	 */
	@Test
	public void getBookmarkByTagNames() {
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		List<Post<Bookmark>> posts = this.bookmarkDb.getPostsByTagNames(GroupID.PUBLIC.getId(), tagIndex, null, 10, 0, this.dbSession);
		assertEquals(3, posts.size());
		// more restriction
		tagIndex.add(new TagIndex("google", 2));
		posts = this.bookmarkDb.getPostsByTagNames(GroupID.PUBLIC.getId(), tagIndex, null, 10, 0, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getBookmarkByTagNamesCount
	 */
	@Test
	public void getBookmarkByTagNamesCount() {
		final int groupId = GroupID.PUBLIC.getId();	
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();		
		final TagIndex t1 = new TagIndex("suchmaschine",1);	
		final TagIndex t2 = new TagIndex("google",2);	
		final TagIndex t3 = new TagIndex("yahoo",3);	
				
		tagIndex.add(t1);			
		assertEquals(6, this.bookmarkDb.getPostsByTagNamesCount(tagIndex, groupId, this.dbSession));
		tagIndex.add(t2);
		assertEquals(8, this.bookmarkDb.getPostsByTagNamesCount(tagIndex, groupId, this.dbSession));
		tagIndex.add(t3);
		assertEquals(0, this.bookmarkDb.getPostsByTagNamesCount(tagIndex, groupId, this.dbSession));
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
		final String requestedUserName = "testuser1";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		List<Post<Bookmark>> posts;
		
		// get public posts of testuser1
		posts = this.bookmarkDb.getPostsByTagNamesForUser(null, requestedUserName, tagIndex, GroupID.PUBLIC.getId(), visibleGroupIDs, 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		// get private post of testuser1
		posts = this.bookmarkDb.getPostsByTagNamesForUser(null, requestedUserName, tagIndex, GroupID.PRIVATE.getId(), visibleGroupIDs, 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		// get public post of testuser1 (but groupId is now invalid)
		posts = this.bookmarkDb.getPostsByTagNamesForUser(null, requestedUserName, tagIndex, GroupID.INVALID.getId(), visibleGroupIDs, 10, 0, null, null, this.dbSession);
		for (Post<Bookmark> post : posts) {
			System.out.println(post.getUser().getName() + "(" + post.getGroups() + ") : " + post.getResource().getIntraHash() + " " + post.getTags());
		}
		assertEquals(1, posts.size());
		// get friends posts of testuers1 for testuser2
		final List<TagIndex> tagIndex2 = new ArrayList<TagIndex>();
		tagIndex2.add(new TagIndex("friends", 1));
		posts = this.bookmarkDb.getPostsByTagNamesForUser("testuser2", "testuser1", tagIndex2, GroupID.FRIENDS.getId(), visibleGroupIDs, 10, 0, null, null, this.dbSession);
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
		final String loginUser = "testuser1";
		final String requestedUserName = "testuser1";
		List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPostsByConceptForUser(null, requestedUserName, visibleGroupIDs, tagIndex, false, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		// now get private posts
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getPostsByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex, false, 10, 0, null, this.dbSession);
		assertEquals(2, posts2.size());
	}

	/**
	 * tests getBookmarkByUserFriends
	 * 
	 * userName must be set
	 */
	@Test
	public void getBookmarkByUserFriends() {
		final String loginUser = "testuser2";
		final List<Post<Bookmark>>  posts1 = this.bookmarkDb.getPostsByUserFriends(loginUser, HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, posts1.size());
	}

	/**
	 * tests getBookmarkForHomepage
	 * 
	 * groupType must be set
	 */
	// FIXME: test is only successfully when running alone
	@Ignore
	@Test
	public void getBookmarkForHomepage() {
		/*
		 * parameter limit is set to 10 in the (old) param object,
		 * but this query ignores this setting and returns always the 20 most recent bookmarks
		 */
		final List<Post<Bookmark>> posts1 = this.bookmarkDb.getPostsForHomepage(null, 10, 0, null, this.dbSession);
		assertEquals(5, posts1.size());
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getPostsForHomepage(null, 10, 0, null, this.dbSession);
		assertEquals(1, posts2.size());
		final List<Post<Bookmark>> posts3 = this.bookmarkDb.getPostsForHomepage(null, 10, 0, null, this.dbSession);
		assertEquals(1, posts3.size());
	}

	/**
	 * tests getBookmarkPopular
	 */
	@Test
	public void getBookmarkPopular() {
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPostsPopular(0, 30, 0, HashID.INTER_HASH, this.dbSession);
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
		List<Post<Bookmark>> post = this.bookmarkDb.getPostsByHash(requBookmark, HashID.INTRA_HASH, GroupID.FRIENDS.getId(), 10, 0, this.dbSession);
		assertEquals(1, post.size());
		
		// this should test which bookmark will be received (there are two in equal hashes in the test database one with public group one with friend group) 
		requBookmark = "85ab919107e4cc79b345e996b3c0b097";
		post = this.bookmarkDb.getPostsByHash(requBookmark, HashID.INTRA_HASH, GroupID.PUBLIC.getId(), 10, 0, this.dbSession);
		assertEquals(1, post.size());
		
		requBookmark = "85ab919107e4cc79b345e996b3c0b097";
		post = this.bookmarkDb.getPostsByHash(requBookmark, HashID.INTRA_HASH, GroupID.FRIENDS.getId(), 10, 0, this.dbSession);
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
		Integer count = -1;
		final String requHash = "b7aa3a91885e432c6c95bec0145c3968";
		count = this.bookmarkDb.getPostsByHashCount(requHash, HashID.INTRA_HASH, this.dbSession);
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
		String userName = "testuser1";
		String requestedUserName = "testuser2";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		List<Post<Bookmark>> post = this.bookmarkDb.getPostsByHashForUser(null, requBibtex, requestedUserName, visibleGroupIDs, null, this.dbSession);
		assertEquals(0, post.size());

		requestedUserName = "testuser1";
		post = this.bookmarkDb.getPostsByHashForUser(userName, requBibtex, requestedUserName, visibleGroupIDs, null, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBookmarkSearch
	 * 
	 * groupType must be set
	 * search must be set
	 * requestedUserName can be set
	 */
	@Test
	public void getBookmarkSearch() {	
		final String requestedUserName = "testuser1";
		final String search = "suchmaschine";
		final int groupId = GroupID.PUBLIC.getId();
		List<Post<Bookmark>> post = this.bookmarkDb.getPostsSearch(groupId, search, requestedUserName, 10, 0, this.dbSession);
		assertEquals(1, post.size());
		// you don't need requestedUserName
		post = this.bookmarkDb.getPostsSearch(groupId, search, null, 10, 0, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBookmarkSearchCount
	 * 
	 * groupType must be set
	 * search must be set
	 * requestedUserName can be set
	 */
	@Test
	public void getBookmarkSearchCount() {
		final String requestedUserName = "testuser1";
		final String search = "suchmaschine";
		final int groupId = GroupID.PUBLIC.getId();
		Integer count = -1;
		count = this.bookmarkDb.getPostsSearchCount(groupId, search, requestedUserName, this.dbSession);
		assertEquals(1, count);
		count = -1;
		count = this.bookmarkDb.getPostsSearchCount(groupId, search, null, this.dbSession);
		assertEquals(1, count);
	}
	
	/**
	 * tests getBookmarkSearchForGroup
	 * 
	 * groupId must be set
	 * userName must be set
	 * search must be set
	 */
	@Test
	public void getBookmarkSearchForGroup() {
		final int limit = 5;
		final int offset = 0;
		String userName = "testuser1";
		int groupId = GroupID.PUBLIC.getId();
		String search = "suchmaschine";
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);

		List<Post<Bookmark>> posts = this.bookmarkDb.getPostsSearch(groupId, search, userName, limit, offset, this.dbSession);
		assertEquals(1, posts.size());
		
	}

	/**
	 * tests getBookmarkViewable
	 * 
	 * groupId must be set 
	 * userName must only be set, when groupId > 3
	 */
	// FIXME: test is only successfully when running alone
	@Ignore
	@Test
	public void getBookmarkViewable() {
		final String userName = "testuser1";
		List<Post<Bookmark>> posts = this.bookmarkDb.getPostsViewable(null, userName, GroupID.PUBLIC.getId(), HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());
		posts = this.bookmarkDb.getPostsViewable(null, userName, GroupID.PRIVATE.getId(), HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		posts = this.bookmarkDb.getPostsViewable(null, userName, GroupID.FRIENDS.getId(), HashID.INTRA_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		// if groupId > 3, you don't need userName (chain manage the access control)
		int testgroup2Id = 4;
		posts = this.bookmarkDb.getPostsViewable(null, "egal", testgroup2Id, HashID.INTRA_HASH, 10, 0, null, this.dbSession);
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
	// FIXME: test is only successfully when running alone
	@Ignore
	@Test
	public void getBookmarkForGroup() {
		/*
		 * testuser1 & testuser2 are members of group 3 
		 * testuser1 is also member of group 4
		 */
		// get all posts of testuser1 (and testuser2) which are public or friends + private posts of testuser1
		String userName = "testuser1";
		final int groupId3 = 3;
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPostsForGroup(groupId3, visibleGroupIDs, userName, HashID.INTRA_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(8, posts.size());

		// get all posts of testuser1 (and testuser2) which are public or friends + private posts of testuser1
		final int groupId4 = 4;
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getPostsForGroup(groupId4, visibleGroupIDs, userName, HashID.INTRA_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(6, posts2.size());

		// get all posts of testuser2 (and testuser1) which are public or friends
		userName = "testuser2";
		final List<Post<Bookmark>> posts3 = this.bookmarkDb.getPostsForGroup(groupId3, visibleGroupIDs, userName, HashID.INTRA_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(5, posts3.size());

		// get all posts by testuser1, testuser2, which are public or friends
		final List<Post<Bookmark>> posts4 = this.bookmarkDb.getPostsForGroup(groupId4, visibleGroupIDs, userName, HashID.INTRA_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(3, posts4.size());
	}

	/**
	 * tests getBookmarkForGroupCount
	 * 
	 * groupId must be set
	 * you don't need to add a group to visibleGroupIDs, because you have no userName
	 * 
	 * visibleGroupIDs && userName && (userName != requestedUserName) optional
	 */
	// FIXME: test is only successfully when running alone
	@Ignore
	@Test
	public void getBookmarkForGroupCount() {
		//approximated number of bookmarks, users own private/friends bookmarks are not included
		Integer count = -1;
		final String requestedUserName = "";
		final String loginUserName = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		count = this.bookmarkDb.getPostsForGroupCount(requestedUserName, loginUserName, 3, visibleGroupIDs, this.dbSession);
		assertEquals(4, count);
		count = this.bookmarkDb.getPostsForGroupCount(requestedUserName, loginUserName, 3, visibleGroupIDs, this.dbSession);
		assertEquals(4, count);
		count = this.bookmarkDb.getPostsForGroupCount(requestedUserName, loginUserName, 4, visibleGroupIDs, this.dbSession);
		assertEquals(2, count);
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
		final int groupId = 3;
		String userName = "testuser1";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		List<Post<Bookmark>> posts = this.bookmarkDb.getPostsForGroupByTag(groupId, visibleGroupIDs, userName, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(3, posts.size());
		
		posts = this.bookmarkDb.getPostsForGroupByTag(groupId, visibleGroupIDs, null, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());

		userName = "testuser2";
		posts = this.bookmarkDb.getPostsForGroupByTag(groupId, visibleGroupIDs, userName, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());
	}
	
	/**
	 * tests getBookmarkForUser
	 *
	 * visibleGroupIDs must only be set, if ( groupId == -1 && userName != null ) && userName != requestedUserName
	 * groupId must be set
	 * userName must be set
	 */
	// FIXME: test is only successful when running alone
	@Ignore
	@Test
	public void getBookmarkForUser() {
		
		final String requestedUserName = "testuser1";
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		// testuser1 has two public bookmarks
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPostsForUser(null, requestedUserName, HashID.INTRA_HASH, GroupID.PUBLIC.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());
		
		/*
		 * testuser1 has one bookmark for friends
		 */
		final List<Post<Bookmark>> posts1 = this.bookmarkDb.getPostsForUser(null, requestedUserName, HashID.INTRA_HASH, GroupID.FRIENDS.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(1, posts1.size());
		
		// testuser has two posts for group 4
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getPostsForUser(null, requestedUserName, HashID.INTRA_HASH, 4, visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts2.size());
		
		// invalid groupId => get all posts of testsuser1
		// when groupId = invalid, you need userName otherwise (userName == null) the groupId would be 0
		// testuser1 may see all hos own posts
		final List<Post<Bookmark>> posts3 = this.bookmarkDb.getPostsForUser("testuser1", requestedUserName, HashID.INTRA_HASH, GroupID.INVALID.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(6, posts3.size());
		
		// invalid groupId => testuser23 (which is no friend of testuser1) can only see public posts of testuser1
		visibleGroupIDs.add(0);
		final List<Post<Bookmark>> posts4 = this.bookmarkDb.getPostsForUser("testuser23", requestedUserName, HashID.INTRA_HASH, GroupID.INVALID.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(2, posts4.size());
		
		// invalid groupId => testuser3 (which is a friend of testuser1!) can see public+friends posts of testuser1
		visibleGroupIDs.add(0);
		final List<Post<Bookmark>> posts5 = this.bookmarkDb.getPostsForUser("testuser3", requestedUserName, HashID.INTRA_HASH, GroupID.INVALID.getId(), visibleGroupIDs, null, 10, 0, null, this.dbSession);
		assertEquals(3, posts5.size());
	}

	/**
	 * tests getBookmarkForUserCount
	 * 
	 * visibleGroupIDs must only be set, if ( groupId == -1 && userName != null ) && userName != requestedUserName
	 * groupId must be set
	 * userName must be set
	 */
	// FIXME: test is only successfully when running alone
	@Ignore
	@Test
	public void getBookmarkForUserCount() {
		Integer count = -1;
		int groupId = 0;
		String requestedUserName = "testuser1";
		String loginUserName = "";
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		count =  this.bookmarkDb.getPostsForUserCount(requestedUserName, loginUserName, groupId, visibleGroupIDs, this.dbSession);	
		assertEquals(2, count);
		requestedUserName = "testuser2";
		count =  this.bookmarkDb.getPostsForUserCount(requestedUserName, loginUserName, groupId, visibleGroupIDs, this.dbSession);	
		assertEquals(2, count);
		requestedUserName = "testuser3";
		count =  this.bookmarkDb.getPostsForUserCount(requestedUserName, loginUserName, groupId, visibleGroupIDs, this.dbSession);	
		assertEquals(1, count);
	}
	
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
		bookmark.setTitle("test");
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		resource = bookmark;
		
		post.setResource(resource);
		return post;
	}
	
	/**
	 * tests storePostWrongUsage
	 */
	@Test(expected = IllegalArgumentException.class)
	public void storePostWrongUsage() {
		final Post<Bookmark> toInsert = this.generateBookmarkDatabaseManagerTestPost();

		this.bookmarkDb.updatePost(toInsert, null, null, this.dbSession);
	}
	
	/**
	 * tests storePost
	 */
	@Test
	public void createPost() {
		final Post<Bookmark> toInsert = generateBookmarkDatabaseManagerTestPost();
		toInsert.getResource().recalculateHashes();

		// no oldIntraHash and no update
		this.bookmarkDb.createPost(toInsert, this.dbSession);
		final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { "tag1", "tag2" }), "", null, 0, 50, null, null, toInsert.getUser());
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), Integer.MAX_VALUE, null, new String[] { "resource", "tags", "user", "date" });
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), Integer.MAX_VALUE, null);

		// Duplicate post and check whether plugins are called
		this.resetParameters();
		assertFalse(this.pluginMock.isOnBibTexUpdate());
		this.pluginMock.reset();
		
		final String hash = "37f7645843eece1b46ae5202b6b489d8";
		param.setHash(hash);
		final Post<Bookmark> someBookmarkPost = this.bookmarkDb.getPostsByHash(hash, HashID.INTRA_HASH, GroupID.PUBLIC.getId(), 10, 0, this.dbSession).get(0);
		this.bookmarkDb.updatePost(someBookmarkPost, hash, null, this.dbSession);
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
		final String requBibtex = "7eda282d1d604c702597600a06f8a6b0";
		final String userName = "testuser2";
		assertEquals(3, this.bookmarkDb.getContentIdForPost(requBibtex, userName, this.dbSession));
	}
	
	/**
	 * tests deleteBookmark
	 */
	@Test
	// TODO: should delete the bookmarks that are inserted in storePost
	public void deleteBookmark() {
		final String intraHash = "37f7645843eece1b46ae5202b6b489d8";
		final List<Post<Bookmark>> post = this.bookmarkDb.getPostsByHash(intraHash, HashID.INTRA_HASH, GroupID.PUBLIC.getId(), 10, 0, this.dbSession);
		assertEquals(1, post.size());

		String userName = "testuser2";
		final boolean delete = this.bookmarkDb.deletePost(userName, intraHash, this.dbSession);
		assertTrue(!delete); // testuser1 cannot delete this posts, the owner is testuser2

		userName = "testuser1";
		final boolean delete2 = this.bookmarkDb.deletePost(userName, intraHash, this.dbSession);
		assertTrue(delete2);

		final List<Post<Bookmark>> post2 = this.bookmarkDb.getPostsByHash(intraHash, HashID.INTRA_HASH, GroupID.PUBLIC.getId(), 10, 0, this.dbSession);
		assertEquals(0, post2.size());
	}

	/**
	 * tests getPosts
	 */
	@Ignore //FIXME: RuntimeException: Can't handle request
	@Test
	public void getPosts() {
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		final BookmarkParam param = new BookmarkParam();
		param.setTagIndex(tagIndex);
		param.setGroupId(GroupID.PUBLIC.getId());
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(3, posts.size());
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
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		param.addSimpleConceptName("suchmaschine");
		param.setRequestedGroupName("testuser1");
		param.setGroups(visibleGroupIDs);
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(3, posts.size());
	}
	
	@Test
	public void getBookmarkByFollowedUsers() {
		/*
		 * testuser 1 follows testuser 2 and 3, who have 3 bookmark posts
		 */
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		visibleGroupIDs.add(1);
		visibleGroupIDs.add(2);
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPostsByFollowedUsers("testuser1", visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(3, posts.size());
		assertEquals("testuser2", posts.get(0).getUser().getName());
		assertEquals("20592a292e53843965c1bb42bfd51876", posts.get(0).getResource().getIntraHash());		
		assertEquals("testuser3", posts.get(1).getUser().getName());
		assertEquals("965a65fdc161e354f3828050390e2b06", posts.get(1).getResource().getIntraHash());
		assertEquals("testuser2", posts.get(2).getUser().getName());
		assertEquals("7eda282d1d604c702597600a06f8a6b0", posts.get(2).getResource().getIntraHash());
	}
}