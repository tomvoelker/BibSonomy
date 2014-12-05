/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain.bookmark;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByResourceSearch;
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
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to the bookmark chain.
 * 
 * @author Miranda Grahl
 */
public class BookmarkChainTest extends AbstractDatabaseManagerTest {

	protected static Chain<List<Bookmark>, BookmarkParam> bookmarkChain;
	
	/**
	 * sets the chain
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setupChain() {
		bookmarkChain = (Chain<List<Bookmark>, BookmarkParam>) testDatabaseContext.getBean("bookmarkChain");
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
		assertEquals(GetResourcesByConceptForUser.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesByFriends.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesByHash.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
	}

	/**
	 * tests getBookmarkByHashForUser
	 */
	@Test
	public void getBookmarkByHashForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		assertEquals(GetResourcesByHashForUser.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
	}

	/**
	 * tests getBookmarkByTagNames
	 */
	@Test
	public void getBookmarkByTagNames() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		assertEquals(GetResourcesByTagNames.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesByTagNamesAndUser.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		
		assertEquals(GetResourcesByConceptForGroup.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesForGroup.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesForGroupAndTag.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesForHomepage.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesForUser.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
	}

	/**
	 * tests getBookmarkOfFriendsByTags
	 */
	@Test
	public void getBookmarkOfFriendsByTags() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		assertEquals(GetResourcesOfFriendsByTags.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesOfFriendsByUser.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesPopular.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
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
		assertEquals(GetResourcesViewable.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
	}

	/**
	 * tests getBookmarksSearch
	 */
	@Test
	public void getBookmarksSearch() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkParam.setSearch("\"www.ubuntuusers.de\"");
		assertEquals(GetBookmarksByResourceSearch.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
	}
	
	/**
	 * tests getBookmarksByFollowedUsers
	 */
	@Test
	public void getBookmarksByFollowedUsers() {
		this.bookmarkParam.setGrouping(GroupingEntity.FOLLOWER);
		this.bookmarkParam.addGroups(new ArrayList<Integer>(Arrays.asList(PUBLIC_GROUP_ID)));
		this.bookmarkParam.setUserName("testuser2");
		assertEquals(GetResourcesByFollowedUsers.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());		
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
		assertEquals(GetBookmarksByResourceSearch.class, bookmarkChain.getChainElement(this.bookmarkParam).getClass());
	}
	
	/**
	 * test if long tag queries are handled by the resource search
	 */
	@Test
	public void longTagQueries() {
		final BookmarkParam param = new BookmarkParam();
		for (int i = 0; i < PostLogicInterface.MAX_TAG_SIZE; i++) {
			param.addTagName("test" + i);
		}
		
		assertEquals(GetBookmarksByResourceSearch.class, bookmarkChain.getChainElement(param).getClass());
	}
}