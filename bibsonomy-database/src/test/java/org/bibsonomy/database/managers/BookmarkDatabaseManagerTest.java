package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.DatabasePluginMock;
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
@Ignore
public class BookmarkDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * tests getBookmarkByTagNames
	 */
	@Test
	public void getBookmarkByTagNames() {
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkByTagNames(GroupID.PUBLIC, tagIndex, 10, 0, this.dbSession);
		assertEquals(3, posts.size());
		// more restriction
		tagIndex.add(new TagIndex("google", 2));
		posts = this.bookmarkDb.getBookmarkByTagNames(GroupID.PUBLIC, tagIndex, 10, 0, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getBookmarkByTagNamesCount
	 */
	@Test
	public void getBookmarkByTagNamesCount() {
		final ArrayList<String> tags = new ArrayList<String>();
		final ArrayList<Integer> groups = new ArrayList<Integer>();
		tags.add("suchmaschine");
		assertEquals(3, this.bookmarkDb.getBookmarkByTagNamesCount(tags, groups, this.dbSession));
		tags.add("google");
		assertEquals(1, this.bookmarkDb.getBookmarkByTagNamesCount(tags, groups, this.dbSession));
		tags.add("yahoo");
		assertEquals(0, this.bookmarkDb.getBookmarkByTagNamesCount(tags, groups, this.dbSession));
	}

	/**
	 * tests getBookmarkByTagNamesForUser
	 */
	@Test
	public void getBookmarkByTagNamesForUser() {
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		// get public posts of testuser1
		List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkByTagNamesForUser("testuser1", null, tagIndex, GroupID.PUBLIC.getId(), 10, 0, this.dbSession);
		assertEquals(1, posts.size());
		// get private post of testuser1
		posts = this.bookmarkDb.getBookmarkByTagNamesForUser("testuser1", null, tagIndex, GroupID.PRIVATE.getId(), 10, 0, this.dbSession);
		assertEquals(1, posts.size());
		// get public post of testuser1 (but groupId is now invalid)
		posts = this.bookmarkDb.getBookmarkByTagNamesForUser("testuser1", null, tagIndex, GroupID.INVALID.getId(), 10, 0, this.dbSession);
		assertEquals(1, posts.size());
		// get friends posts of testuers1 for testuser2
		final List<TagIndex> tagIndex2 = new ArrayList<TagIndex>();
		tagIndex2.add(new TagIndex("friends", 1));
		posts = this.bookmarkDb.getBookmarkByTagNamesForUser("testuser1", "testuser2", tagIndex2, GroupID.FRIENDS.getId(), 10, 0, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getBookmarkByConceptForUser
	 * 
	 * if userName == null, you don't need to set visibleGroupIDs
	 * otherwise you have to set visibleGroupIDs
	 */
	@Test
	public void getBookmarkByConceptForUser() {		
		final String loginUser = "testuser1";
		final String requestedUserName = "testuser1";
		List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		final List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkByConceptForUser(null, requestedUserName, visibleGroupIDs, tagIndex, 10, 0, this.dbSession);
		assertEquals(1, posts.size());
		// now get private posts
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getBookmarkByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex, 10, 0, this.dbSession);
		assertEquals(2, posts2.size());
	}

	/**
	 * tests getBookmarkByUserFriends
	 */
	@Test
	public void getBookmarkByUserFriends() {
		final String loginUser = "testuser2";
		List<Post<Bookmark>>  posts1 = this.bookmarkDb.getBookmarkByUserFriends(loginUser, 10, 0, this.dbSession);
		assertEquals(1, posts1.size());
	}

	/**
	 * tests getBookmarkForHomepage
	 */
	@Test
	public void getBookmarkForHomepage() {
		/*
		 * parameter limit is set to 10 in the (old) param object,
		 * but this query ignores this setting and returns always the 20 most recent bookmarks
		 */
		final List<Post<Bookmark>> posts1 = this.bookmarkDb.getBookmarkForHomepage(GroupID.PUBLIC, 10, this.dbSession);
		assertEquals(5, posts1.size());
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getBookmarkForHomepage(GroupID.PRIVATE, 10, this.dbSession);
		assertEquals(1, posts2.size());
		final List<Post<Bookmark>> posts3 = this.bookmarkDb.getBookmarkForHomepage(GroupID.FRIENDS, 10, this.dbSession);
		assertEquals(1, posts3.size());
	}

	/**
	 * tests getBookmarkPopular
	 */
	@Test
	public void getBookmarkPopular() {
		final List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkPopular(this.bookmarkParam, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getBookmarkByHash
	 */
	@Test
	public void getBookmarkByHash() {
		final String requBibtex = "b7aa3a91885e432c6c95bec0145c3968";
		final List<Post<Bookmark>> post = this.bookmarkDb.getBookmarkByHash(requBibtex, GroupID.FRIENDS, 10, 0, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBookmarkByHashCount
	 */
	@Test
	public void getBookmarkByHashCount() {
		Integer count = -1;
		final String requBibtex = "b7aa3a91885e432c6c95bec0145c3968";
		count = this.bookmarkDb.getBookmarkByHashCount(requBibtex, GroupID.FRIENDS, this.dbSession);
		assertTrue(count >= 0);
	}

	/**
	 * tests getBookmarkByHashForUser
	 */
	@Test
	public void getBookmarkByHashForUser() {
		// This bookmark is a private bookmark of testuser1
		final String requBibtex = "294a9e1d594297e7bb9da9e11229c5d7";
		String requestedUserName = "testuser2";
		final String userName = "testuser1";
		final ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>(0);
		final List<Post<Bookmark>> post = this.bookmarkDb.getBookmarkByHashForUser(userName, requBibtex, requestedUserName, visibleGroupIDs, this.dbSession, null);
		assertEquals(0, post.size());

		requestedUserName = "testuser1";
		final List<Post<Bookmark>> post2 = this.bookmarkDb.getBookmarkByHashForUser(userName, requBibtex, requestedUserName, visibleGroupIDs, this.dbSession, null);
		assertEquals(1, post2.size());
	}

	/**
	 * tests getBookmarkSearch
	 */
	@Test
	public void getBookmarkSearch() {	
		final String requestedUserName = "testuser1";
		final String search = "suchmaschine";
		final GroupID groupType = GroupID.PUBLIC;
		final List<Post<Bookmark>> post = this.bookmarkDb.getBookmarkSearch(groupType, search, requestedUserName, 10, 0, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBookmarkSearchCount
	 */
	@Test
	public void getBookmarkSearchCount() {
		final String requestedUserName = "testuser1";
		final String search = "suchmaschine";
		final GroupID groupType = GroupID.PUBLIC;
		Integer count = -1;
		count = this.bookmarkDb.getBookmarkSearchCount(groupType, search, requestedUserName, this.dbSession);
		assertTrue(count >= 0);
		count = -1;
		count = this.bookmarkDb.getBookmarkSearchCount(groupType, search, null, this.dbSession);
		assertTrue(count >= 0);
	}

	/**
	 * tests getBookmarkViewable
	 */
	@Test
	public void getBookmarkViewable() {
		final String userName = "testuser1";
		final List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkViewable(GroupID.PUBLIC.getId(), userName, 10, 0, this.dbSession);
		assertEquals(2, posts.size());
		final List<Post<Bookmark>> posts3 = this.bookmarkDb.getBookmarkViewable(GroupID.PRIVATE.getId(), userName, 10, 0, this.dbSession);
		assertEquals(1, posts3.size());
		// if groupId > 3, you don't need userName (chain manage the access control)
		int testgroup2Id = 4;
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getBookmarkViewable(testgroup2Id, "egal", 10, 0, this.dbSession);
		assertEquals(2, posts2.size());
	}

	/**
	 * tests getBookmarkForGroup
	 * 
	 * if userName == null, you dont need visibleGroupIDs
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
		String userName = "testuser1";
		final int groupId3 = 3;
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		final List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkForGroup(groupId3, visibleGroupIDs, userName, 10, 0, this.dbSession);
		assertEquals(8, posts.size());

		// get all posts of testuser1 (and testuser2) which are public or friends + private posts of testuser1
		final int groupId4 = 4;
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getBookmarkForGroup(groupId4, visibleGroupIDs, userName, 10, 0, this.dbSession);
		assertEquals(6, posts2.size());

		// get all posts of testuser2 (and testuser1) which are public or friends
		userName = "testuser2";
		final List<Post<Bookmark>> posts3 = this.bookmarkDb.getBookmarkForGroup(groupId3, visibleGroupIDs, userName, 10, 0, this.dbSession);
		assertEquals(5, posts3.size());

		// get all posts by testuser1, testuser2, which are public or friends
		final List<Post<Bookmark>> posts4 = this.bookmarkDb.getBookmarkForGroup(groupId4, visibleGroupIDs, userName, 10, 0, this.dbSession);
		assertEquals(3, posts4.size());
	}

	/**
	 * tests getBookmarkForGroupCount
	 * 
	 * groupId must be set
	 * visibleGroupIDs must be set
	 */
	@Test
	public void getBookmarkForGroupCount() {
		//approximated number of bookmarks, users own private/friends bookmarks are not included
		Integer count = -1;
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		count = this.bookmarkDb.getBookmarkForGroupCount(3, visibleGroupIDs, this.dbSession);
		assertEquals(4, count);

		// TODO: visibleGroupIDs don't need to set, why?
		count = this.bookmarkDb.getBookmarkForGroupCount(3, visibleGroupIDs, this.dbSession);
		assertEquals(4, count);
		// TODO: visibleGroupIDs don't need to set, why?
		count = this.bookmarkDb.getBookmarkForGroupCount(4, visibleGroupIDs, this.dbSession);
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
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkForGroupByTag(groupId, visibleGroupIDs, userName, tagIndex, this.dbSession);
		assertEquals(3, posts.size());
		
		posts = this.bookmarkDb.getBookmarkForGroupByTag(groupId, visibleGroupIDs, null, tagIndex, this.dbSession);
		assertEquals(2, posts.size());

		userName = "testuser2";
		posts = this.bookmarkDb.getBookmarkForGroupByTag(groupId, visibleGroupIDs, userName, tagIndex, this.dbSession);
		assertEquals(2, posts.size());
	}
	
	// TODO: Check the parameters
	/**
	 * tests getBookmarkForUser
	 *
	 * if userName == null or userName == requestedUserName, you dont need visibleGroupIDs
	 * otherwise:
	 * groupId must be set
	 * visibleGroupIDs must be set
	 * userName must be set
	 */
	@Test
	public void getBookmarkForUser() {
		final String requestedUserName = "testuser1";
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		// testuser1 has two public bookmarks
		final List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkForUser(null, requestedUserName, GroupID.PUBLIC.getId(), visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(2, posts.size());
		
		// testuser1 has one bookmarks for friends
		final List<Post<Bookmark>> posts1 = this.bookmarkDb.getBookmarkForUser(null, requestedUserName, GroupID.FRIENDS.getId(), visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(1, posts1.size());
		
		// testuser has two posts for group 4
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getBookmarkForUser(null, requestedUserName, 4, visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(2, posts2.size());
		
		// Invalid groupId => get all posts of testsuser1
		// When groupId = Invalid, you need userName
		String userName = "testuser1";
		final List<Post<Bookmark>> posts3 = this.bookmarkDb.getBookmarkForUser(userName, requestedUserName, GroupID.INVALID.getId(), visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(6, posts3.size());
		
		// Invalid groupId => testuser3 can only see public posts of testuser1
		// When groupId = Invalid, you need userName
		visibleGroupIDs.add(0);
		userName = "testuser3";
		final List<Post<Bookmark>> posts4 = this.bookmarkDb.getBookmarkForUser(userName, requestedUserName, GroupID.INVALID.getId(), visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(2, posts4.size());
	}

	/**
	 * tests getBookmarkForUserCount
	 */
	@Test
	//FIXME: NullPointerException
	public void getBookmarkForUserCount() {
		Integer count = -1;
		final String requestedUserName = "testuser1";
		String userName = "testuser3";
		count =  this.bookmarkDb.getBookmarkForUserCount(requestedUserName, userName, null, this.dbSession);	
		assertEquals(2, count);
		userName = "testuser2";
		count =  this.bookmarkDb.getBookmarkForUserCount(requestedUserName, userName, null, this.dbSession);	
		assertEquals(3, count);
		userName = "testuser1";
		count =  this.bookmarkDb.getBookmarkForUserCount(requestedUserName, userName, null, this.dbSession);	
		assertEquals(6, count);
	}

	/**
	 * tests deleteBookmark
	 */
	@Test
	public void deleteBookmark() {
		final String requBibtex = "108eca7b644e2c5e09853619bc416ed0";
		final List<Post<Bookmark>> post = this.bookmarkDb.getBookmarkByHash(requBibtex, GroupID.PUBLIC, 10, 0, this.dbSession);
		assertEquals(1, post.size());

		final String userName = "testuser1";
		final String resourceHash = "7eda282d1d604c702597600a06f8a6b0";
		final boolean delete = this.bookmarkDb.deletePost(userName, resourceHash, this.dbSession);
		assertTrue(!delete); // testuser1 cannot delete this posts, the owner is testuser2

		final String resourceHash2 = requBibtex;
		final boolean delete2 = this.bookmarkDb.deletePost(userName, resourceHash2, this.dbSession);
		assertTrue(delete2);

		final List<Post<Bookmark>> post2 = this.bookmarkDb.getBookmarkByHash(requBibtex, GroupID.PUBLIC, 10, 0, this.dbSession);
		assertEquals(0, post2.size());
	}

	/**
	 * tests storePost
	 */
	@Test
	public void storePost() {
		// ModelUtils
		final Post<Bookmark> post = new Post<Bookmark>();

		final Group group = new Group();
		group.setGroupId(GroupID.PUBLIC.getId());
		group.setDescription(null);
		group.setName("public");
		post.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName(ModelUtils.class.getName());
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName("testtag");
		post.getTags().add(tag);

		post.setContentId(null);
		post.setDescription("trallalla");
		post.setDate(new Date());

		final User user = new User();
		user.setName("testuser1");
		post.setUser(user);

		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0); // alternative for: setResourceDefaults(bookmark) in ModelUtils;
		bookmark.setTitle("test"); // does not change hash
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();

		post.setResource(bookmark);

		final Post<Bookmark> toInsert = post;

		try {
			this.bookmarkDb.storePost(toInsert.getUser().getName(), toInsert, null, true, this.dbSession);
			fail("Should throw a throwable");
		} catch (Throwable t) {
			assertTrue(t instanceof IllegalArgumentException);
		}
		try {
			this.bookmarkDb.storePost(toInsert.getUser().getName(), toInsert, "6f372faea7ff92eedf52f597090a6291", false, this.dbSession);
			fail("Should throw a throwable");
		} catch (Throwable t) {
			assertTrue(t instanceof IllegalArgumentException);
		}
		// no oldIntraHash and no update
		this.bookmarkDb.storePost(toInsert.getUser().getName(), toInsert, null, false, this.dbSession);
		final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, toInsert.getUser().getName(), GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { ModelUtils.class.getName(), "testtag" }), "", null, 0, 50, null, toInsert.getUser());
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), Integer.MAX_VALUE, null, new String[] { "resource", "tags", "user", "date" });
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), Integer.MAX_VALUE, null);

		// Duplicate post and check whether plugins are called
		this.resetParameters();
		// FIXME: this boilerplate code could be removed with a DI-framework (i.e. next three lines)
		final DatabasePluginMock plugin = new DatabasePluginMock();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);
		assertFalse(plugin.isOnBibTexUpdate());
		param.setHash("6f372faea7ff92eedf52f597090a6291");
		final Post<Bookmark> someBookmarkPost = this.bookmarkDb.getBookmarkByHash(param, this.dbSession).get(0);
		this.bookmarkDb.storePost(someBookmarkPost.getUser().getName(), someBookmarkPost, "6f372faea7ff92eedf52f597090a6291", true, this.dbSession);
		assertTrue(plugin.isOnBookmarkUpdate());
	}

	/**
	 * tests getContentIDForBookmark
	 */
	@Test
	public void getContentIDForBookmark() {
		final String requBibtex = "7eda282d1d604c702597600a06f8a6b0";
		final String userName = "testuser2";
		assertEquals(3, this.bookmarkDb.getContentIDForBookmark(requBibtex, userName, this.dbSession));
	}

	/**
	 * tests getPosts
	 */
	@Test
	//FIXME: RuntimeException: Can't handle request
	public void getPosts() {
		List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		tagIndex.add(new TagIndex("suchmaschine", 1));
		final BookmarkParam param = new BookmarkParam();
		param.setTagIndex(tagIndex);
		param.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		param.setGroupId(GroupID.PUBLIC.getId());
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(3, posts2.size());
	}

	/**
	 * tests getBookmarksByConceptForGroup
	 */
	@Test
	//FIXME: RuntimeException: Couldn't execute query
	public void getBookmarksByConceptForGroup() {
		final BookmarkParam param = new BookmarkParam();
		
		param.addSimpleConceptName("clustering");
		//param.addSimpleConceptName("software");		
		param.setUserName("hotho");
		param.setRequestedGroupName("kde");
		param.setGrouping(GroupingEntity.GROUP);
		param.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		
		final List<Post<Bookmark>> posts2 = this.bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(10, posts2.size());
	}	
}