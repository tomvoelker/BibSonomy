package org.bibsonomy.database.managers.chain.bookmark;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to the bookmark chain.
 * 
 * TODO: the tests should have asserts
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class BookmarkChainTest extends AbstractChainTest {

	/**
	 * tests getBookmarkByConceptForUser
	 */
	@Test
	public void getBookmarkByConceptForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setRequestedUserName("hotho");
		this.bookmarkParam.setNumSimpleConcepts(3);
		this.bookmarkParam.setNumSimpleTags(0);
		this.bookmarkParam.setNumTransitiveConcepts(0);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkByFriends
	 */
	@Test
	public void getBookmarkByFriends() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkByHash
	 */
	@Test
	public void getBookmarkByHash() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkByHashForUser
	 */
	@Test
	public void getBookmarkByHashForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkByTagNames
	 */
	@Test
	public void getBookmarkByTagNames() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkByTagNamesAndUser
	 */
	@Test
	public void getBookmarkByTagNamesAndUser() {
		this.bookmarkParam.setUserName("grahl");
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setRequestedUserName("grahl");
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkForGroup
	 */
	@Test
	public void getBookmarkForGroup() {
		this.bookmarkParam.setGrouping(GroupingEntity.GROUP);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkForGroupAndTag
	 */
	@Test
	public void getBookmarkForGroupAndTag() {
		this.bookmarkParam.setGrouping(GroupingEntity.GROUP);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);

	}

	/**
	 * tests getBookmarkForHomePage
	 */
	@Test
	public void getBookmarkForHomePage() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkForUser
	 */
	@Test
	public void getBookmarkForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkOfFriendsByTags
	 */
	@Test
	public void getBookmarkOfFriendsByTags() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkOfFriendsByUser
	 */
	@Test
	public void getBookmarkOfFriendsByUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkPopular
	 */
	@Test
	public void getBookmarkPopular() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(Order.POPULAR);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkViewable
	 */
	@Test
	public void getBookmarkViewable() {
		this.bookmarkParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
	}

	/**
	 * tests getBookmarkSearch
	 *
	 * TODO: adapt to new test DB
	 */
	@Ignore
	public void getBookmarkSearch() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkParam.setSearch("\"www.ubuntuusers.de\"");
		final List<Post<Bookmark>> posts = this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession);
		assertEquals(4, posts.size());
	}
}