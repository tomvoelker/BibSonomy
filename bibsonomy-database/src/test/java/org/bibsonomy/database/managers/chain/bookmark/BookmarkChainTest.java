package org.bibsonomy.database.managers.chain.bookmark;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByResourceSearch;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByConceptByTag;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByConceptForGroup;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByConceptForUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByFollowedUsers;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByFriends;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByHash;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByHashForUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByTagNames;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForGroup;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForGroupAndTag;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForHomepage;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesOfFriendsByTags;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesOfFriendsByUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesPopular;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesViewable;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to the bookmark chain.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class BookmarkChainTest extends AbstractChainTest {

	protected static BookmarkChain bookmarkChain;
	
	/**
	 * sets the chain
	 */
	@BeforeClass
	public static void setupChain() {
		bookmarkChain = new BookmarkChain();
	}
	
	private BookmarkParam bookmarkParam;
	
	/**
	 * creates a new bookmark param
	 */
	@Before
	public void createParam() {
		this.bookmarkParam = ParamUtils.getDefaultBookmarkParam();
	}
	
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByConceptForUser.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByFriends.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByHash.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkByHashForUser
	 */
	@Test
	public void getBookmarkByHashForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByHashForUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkByTagNames
	 */
	@Test
	public void getBookmarkByTagNames() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByTagNames.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByTagNamesAndUser.class, chainStatus.getChainElement().getClass());
	}
	
	/**
	 * tests getBookmarksByConceptForGroup
	 */
	@Test
	public void getBookmarksByConceptForGroup() {
		this.bookmarkParam.setGrouping(GroupingEntity.GROUP);
		this.bookmarkParam.setRequestedGroupName("testgroup1");
		this.bookmarkParam.setNumSimpleConcepts(3);
		this.bookmarkParam.setNumSimpleTags(0);
		this.bookmarkParam.setNumTransitiveConcepts(0);
		this.bookmarkParam.setHash(null);
		
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByConceptForGroup.class, chainStatus.getChainElement().getClass());
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
		this.bookmarkParam.setRequestedUserName(null);
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesForGroup.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkForGroupAndTag
	 */
	@Test
	public void getBookmarkForGroupAndTag() {
		this.bookmarkParam.setGrouping(GroupingEntity.GROUP);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setRequestedUserName(null);
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesForGroupAndTag.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesForHomepage.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesForUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarkOfFriendsByTags
	 */
	@Test
	public void getBookmarkOfFriendsByTags() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesOfFriendsByTags.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesOfFriendsByUser.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesPopular.class, chainStatus.getChainElement().getClass());
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
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesViewable.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBookmarksSearch
	 */
	@Test
	public void getBookmarksSearch() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkParam.setSearch("\"www.ubuntuusers.de\"");
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetBookmarksByResourceSearch.class, chainStatus.getChainElement().getClass());
	}
	
	/**
	 * tests getBookmarksByFollowedUsers
	 */
	@Test
	public void getBookmarksByFollowedUsers() {
		this.bookmarkParam.setGrouping(GroupingEntity.FOLLOWER);
		this.bookmarkParam.addGroups(new ArrayList<Integer>(Arrays.asList(PUBLIC_GROUP_ID)));
		this.bookmarkParam.setUserName("testuser2");
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByFollowedUsers.class, chainStatus.getChainElement().getClass());		
	}
	
	/**
	 * tests getBookmarksByConceptTag
	 */
	@Test
	public void getBookmarksByConceptTag() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setNumSimpleConcepts(3);
		this.bookmarkParam.setNumSimpleTags(0);
		this.bookmarkParam.setNumTransitiveConcepts(0);
		
		bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, chainStatus);
		assertEquals(GetResourcesByConceptByTag.class, chainStatus.getChainElement().getClass());
	}
	
}