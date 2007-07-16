package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.DatabasePluginMock;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

/**
 * Tests related to BibTex.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getBookmarkByTagNames() {
		List<Post<Bookmark>> posts = this.bookmarkDb.getBookmarkByTagNames(this.bookmarkParam, this.dbSession);
		assertEquals(10, posts.size());
	}

	@Test
	public void getBookmarkByTagNamesForUser() {
		this.bookmarkDb.getBookmarkByTagNamesForUser(this.bookmarkParam, this.dbSession);
		this.resetParameters();
		this.bookmarkParam.setGroupId(GroupID.INVALID.getId());
		this.bookmarkDb.getBookmarkByTagNamesForUser(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkByConceptForUser() {
		this.bookmarkDb.getBookmarkByConceptForUser(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkByUserFriends() {
		this.bookmarkDb.getBookmarkByUserFriends(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkForHomepage() {
		this.bookmarkDb.getBookmarkForHomepage(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkPopular() {
		this.bookmarkDb.getBookmarkPopular(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkByHash() {
		this.bookmarkDb.getBookmarkByHash(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkByHashCount() {
		Integer count = -1;
		count = this.bookmarkDb.getBookmarkByHashCount(this.bookmarkParam, this.dbSession);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkByHashForUser() {
		this.bookmarkDb.getBookmarkByHashForUser(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkSearch() {
		this.bookmarkParam.setSearch("test");
		this.bookmarkDb.getBookmarkSearch(this.bookmarkParam, this.dbSession);
		this.bookmarkParam.setUserName(null);
		this.bookmarkDb.getBookmarkSearch(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkSearchCount() {
		this.bookmarkParam.setSearch("test");
		Integer count = -1;
		count = this.bookmarkDb.getBookmarkSearchCount(this.bookmarkParam, this.dbSession);
		assertTrue(count >= 0);

		this.bookmarkParam.setUserName(null);
		count = -1;
		count = this.bookmarkDb.getBookmarkSearchCount(this.bookmarkParam, this.dbSession);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkViewable() {
		this.bookmarkDb.getBookmarkViewable(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkForGroup() {
		this.bookmarkDb.getBookmarkForGroup(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkForGroupCount() {
		Integer count = -1;
		count = this.bookmarkDb.getBookmarkForGroupCount(this.bookmarkParam, this.dbSession);
		assertTrue(count >= 0);
	}

	@Test
	public void getBookmarkForGroupByTag() {
		this.bookmarkDb.getBookmarkForGroupByTag(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkForUser() {
		this.bookmarkDb.getBookmarkForUser(this.bookmarkParam, this.dbSession);
		this.resetParameters();
		this.bookmarkParam.setGroupId(GroupID.INVALID.getId());
		this.bookmarkDb.getBookmarkForUser(this.bookmarkParam, this.dbSession);
	}

	@Test
	public void getBookmarkForUserCount() {
		this.bookmarkDb.getBookmarkForUserCount(this.bookmarkParam, this.dbSession);
		this.resetParameters();
		this.bookmarkParam.setGroupId(GroupID.INVALID.getId());
		this.bookmarkDb.getBookmarkForUserCount(this.bookmarkParam, this.dbSession);
	}

	/**
	 * Test for setting bookmarks of a user in database regarding different
	 * statements
	 */
	
	@Test
	public void insertBookmarkPost() {
		final Post<Bookmark> toInsert = ModelUtils.generatePost(Bookmark.class);
		toInsert.setContentId(Integer.MAX_VALUE);
		this.bookmarkDb.insertBookmarkPost(toInsert, this.dbSession);
	}
	
	@Test
	public void deleteBookmark() {
		this.bookmarkDb.deletePost(this.bookmarkParam.getRequestedUserName(), this.bookmarkParam.getHash(), this.dbSession);
	}

	@Test
	public void storePost() {
		final Post<Bookmark> toInsert = ModelUtils.generatePost(Bookmark.class);

		try {
			this.bookmarkDb.storePost(toInsert.getUser().getName(), toInsert, null, true, this.dbSession);
			fail("Should throw a throwable");
		} catch (Throwable t) {
			assertTrue(t instanceof IllegalArgumentException);
		}
		try {
			this.bookmarkDb.storePost(toInsert.getUser().getName(), toInsert, "06aef6e5439298f27dc5aee82c4293d6", false, this.dbSession);
			fail("Should throw a throwable");
		} catch (Throwable t) {
			assertTrue(t instanceof IllegalArgumentException);
		}

		this.bookmarkDb.storePost(toInsert.getUser().getName(), toInsert, null, false, this.dbSession);
		final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, toInsert.getUser().getName(), GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { ModelUtils.class.getName(), "hurz" }), "", null, 0, 50);
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), new String[] { "resource", "tags" });
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), "");

		// Duplicate post and check whether plugins are called
		this.resetParameters();
		// FIXME: this boilerplate code could be removed with a DI-framework (i.e. next three lines)
		final DatabasePluginMock plugin = new DatabasePluginMock();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);
		assertFalse(plugin.isOnBibTexUpdate());
		param.setHash("e636edf2736cfc61897bf21039ffea1b");
		final Post<Bookmark> someBookmarkPost = this.bookmarkDb.getBookmarkByHash(param, this.dbSession).get(0);
		this.bookmarkDb.storePost(someBookmarkPost.getUser().getName(), someBookmarkPost, "e636edf2736cfc61897bf21039ffea1b", true, this.dbSession);
		assertTrue(plugin.isOnBookmarkUpdate());
	}

	@Test
	public void insertBookmarkLog() {
		// FIXME
		// this.db.getBookmark().insertBookmarkLog(this.bookmarkParam);
	}

	@Test
	public void insertBookmarkHash() {
		this.bookmarkParam.setHash("1234567890");
		this.bookmarkParam.setUrl("www.hallo.de");
		this.bookmarkDb.insertBookmarkHash(this.bookmarkParam,this.dbSession);
	}

	@Test
	public void updateBookmarkHash() {
		 this.bookmarkDb.updateBookmarkHash(this.bookmarkParam,this.dbSession);
	}

	@Test
	public void updateBookmarkLog() {
		// FIXME
		// this.db.getBookmark().updateBookmarkLog(this.bookmarkParam);
	}

	@Test
	public void deleteBookmarkByContentId() {
		// FIXME
		// this.db.getBookmark().deleteBookmarkByContentId(this.bookmarkParam);
	}

	@Test
	public void getNewContentID() {
		// FIXME
		// this.db.getBookmark().getNewContentID(this.bookmarkParam);
	}

	// FIXME: either db or single-result querytype is wrong @Test
	public void getContentIDForBookmark() {
		this.bookmarkParam.setHash("5d2a36f3df07d2b03839faf6e05ec719");
		this.bookmarkParam.setUserName("jaeschke");
		assertEquals(2648964, this.bookmarkDb.getContentIDForBookmark(this.bookmarkParam, this.dbSession));
	}

	@Test
	public void getPosts() {
		this.bookmarkParam.setHash("");
		final List<Post<Bookmark>> posts = this.bookmarkDb.getPosts(this.bookmarkParam, this.dbSession);
		assertEquals(this.bookmarkParam.getLimit(), posts.size());
	}
}