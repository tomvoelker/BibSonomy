package org.bibsonomy.database.managers.chain.bookmark;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByConceptForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByFollowedUsers;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByFriends;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHash;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHashForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNames;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroup;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroupAndTag;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForHomePage;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksOfFriendsByTags;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksOfFriendsByUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksPopular;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksSearch;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksViewable;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to the bookmark chain.
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
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksByConceptForUser.class, this.chainStatus.getChainElement().getClass());
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
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkParam.setRequestedGroupName(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksByFriends.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkByHash
	 */
	@Test
	public void getBookmarkByHash() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksByHash.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkByHashForUser
	 */
	@Test
	public void getBookmarkByHashForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksByHashForUser.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkByTagNames
	 */
	@Test
	public void getBookmarkByTagNames() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksByTagNames.class, this.chainStatus.getChainElement().getClass());
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
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksByTagNamesAndUser.class, this.chainStatus.getChainElement().getClass());
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
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksForGroup.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkForGroupAndTag
	 */
	@Test
	public void getBookmarkForGroupAndTag() {
		this.bookmarkParam.setGrouping(GroupingEntity.GROUP);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksForGroupAndTag.class, this.chainStatus.getChainElement().getClass());

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
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksForHomePage.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkForUser
	 */
	@Test
	public void getBookmarksForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkParam.setGroupId(GroupID.INVALID.getId());
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksForUser.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkOfFriendsByTags
	 */
	@Test
	public void getBookmarkOfFriendsByTags() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksOfFriendsByTags.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarksOfFriendsByUser
	 */
	@Test
	public void getBookmarksOfFriendsByUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		final List<Post<Bookmark>> bookmarks = this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		System.out.println(bookmarks);
		assertEquals(GetBookmarksOfFriendsByUser.class, this.chainStatus.getChainElement().getClass());
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
		this.bookmarkParam.setDays(0);
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksPopular.class, this.chainStatus.getChainElement().getClass());
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
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksViewable.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarksSearch
	 */
	@Ignore
	public void getBookmarksSearch() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkParam.setSearch("\"www.ubuntuusers.de\"");
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksSearch.class, this.chainStatus.getChainElement().getClass());
	}
	
	/**
	 * tests getBookmarkByFollowedUsers
	 */
	public void getBookmarkByFollowedUsers() {
		this.bookmarkParam.setGrouping(GroupingEntity.FOLLOWER);
		this.bookmarkParam.addGroups(new ArrayList<Integer>(0));
		this.bookmarkParam.setUserName("testuser2");
		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
		assertEquals(GetBookmarksByFollowedUsers.class, this.chainStatus.getChainElement().getClass());		
	}
	
	
}