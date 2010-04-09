package org.bibsonomy.database.managers.chain.bibtex;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByAuthor;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByAuthorAndTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByConceptByTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByConceptForGroup;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByConceptForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByFollowedUsers;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByFriends;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHash;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHashForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNames;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroup;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroupAndTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForHomePage;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexFromBasketForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexOfFriendsByTags;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexOfFriendsByUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexPopular;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexSearch;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexViewable;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to the BibTex chain.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexChainTest extends AbstractChainTest {
	
	protected static BibTexChain bibtexChain;
	
	/**
	 * sets up the chain
	 */
	@BeforeClass
	public static void setUpChain() {
		bibtexChain = new BibTexChain();
	}
	
	private BibTexParam bibtexParam;
	
	/**
	 * inits the param
	 */
	@Before
	public void initParam() {
		this.bibtexParam = ParamUtils.getDefaultBibTexParam();
	}

	/**
	 * tests getBibtexByConceptForUser
	 */
	@Test
	public void getBibtexByConceptForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(3);
		this.bibtexParam.setNumSimpleTags(0);
		this.bibtexParam.setNumTransitiveConcepts(0);
		
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexByConceptForUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByConceptForGroup
	 */
	@Test
	public void getBibtexByConceptForGroup() {
		this.bibtexParam.setGrouping(GroupingEntity.GROUP);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(3);
		this.bibtexParam.setNumSimpleTags(0);
		this.bibtexParam.setNumTransitiveConcepts(0);
		
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexByConceptForGroup.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibTexByConceptByTag
	 */
	@Test
	public void getBibTexByConceptByTag() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(3);
		this.bibtexParam.setNumSimpleTags(0);
		this.bibtexParam.setNumTransitiveConcepts(0);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibTexByConceptByTag.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByFriends
	 */
	@Test
	public void getBibtexByFriends() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setRequestedGroupName(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexByFriends.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByHash
	 */
	@Test
	public void getBibtexByHash() {
		BibTexParam param = new BibTexParam();
		param.setHash("I_am_a_hash");
		param.setBibtexKey(null);
		param.setGrouping(GroupingEntity.ALL);
		param.setRequestedUserName(null);
		param.setTagIndex(null);
		param.setOrder(null);
		param.setSearch(null);
		bibtexChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetBibtexByHash.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByHashForUser
	 */
	@Test
	public void getBibtexByHashForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexByHashForUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByTagNames
	 */
	@Test
	public void getBibtexByTagNames() {
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexByTagNames.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByTagNamesAndUser
	 */
	@Test
	public void getBibtexByTagNamesAndUser() {
		this.bibtexParam.setUserName("grahl");
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setRequestedUserName("grahl");
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexByTagNamesAndUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexForGroup
	 */
	@Test
	public void getBibtexForGroup() {
		this.bibtexParam.setGrouping(GroupingEntity.GROUP);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setTagIndex(null);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexForGroup.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexForGroupAndTag
	 */
	@Test
	public void getBibtexForGroupAndTag() {
		this.bibtexParam.setGrouping(GroupingEntity.GROUP);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexForGroupAndTag.class, chainStatus.getChainElement().getClass());

	}

	/**
	 * tests getBibtexForHomePage
	 */
	@Test
	public void getBibtexForHomePage() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexForHomePage.class, chainStatus.getChainElement().getClass());

	}

	/**
	 * tests getBibtexForUser
	 */
	@Test
	public void getBibtexForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setGroupId(GroupID.INVALID.getId());
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexForUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexOfFriendsByTags
	 */
	@Test
	public void getBibtexOfFriendsByTags() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexOfFriendsByTags.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexOfFriendsByUser
	 */
	@Test
	public void getBibtexOfFriendsByUser() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexOfFriendsByUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexPopular
	 */
	@Test
	public void getBibtexPopular() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(Order.POPULAR);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setDays(0);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexPopular.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexViewable
	 */
	@Test
	public void getBibtexViewable() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexViewable.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByAuthor
	 */
	@Test
	public void getBibtexByAuthor() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setGroupId(-1);
		this.bibtexParam.setSearch("Grahl");
		this.bibtexParam.setSearchEntity(SearchEntity.AUTHOR);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibTexByAuthor.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexByAuthorAndTag
	 */
	@Test
	public void getBibtexByAuthorAndTag() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setRequestedGroupName(null);
		this.bibtexParam.setSearch("Grahl");
		this.bibtexParam.setSearchEntity(SearchEntity.AUTHOR);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibTexByAuthorAndTag.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getBibtexBySearch
	 */
	@Test
	public void getBibtexBySearch() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setSearch("Hotho");
		this.bibtexParam.setGroupId(GroupID.PUBLIC.getId());
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setLimit(350);
		this.bibtexParam.setSearchEntity(SearchEntity.ALL);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexSearch.class, chainStatus.getChainElement().getClass());
	}
	/**
	 * tests getBibtexFromBasketForUser
	 */
	@Test
	public void getBibtexFromBasketForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.BASKET);
		this.bibtexParam.setUserName("testuser1");
		this.bibtexParam.setBibtexKey(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setSearch(null);
		this.bibtexParam.setTagIndex(null);
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexFromBasketForUser.class, chainStatus.getChainElement().getClass());
	}
	
	/**
	 * tests getBibtexByFollowedUsers
	 */
	@Test
	public void getBibtexByFollowedUsers() {
		this.bibtexParam.setGrouping(GroupingEntity.FOLLOWER);
		this.bibtexParam.addGroups(new ArrayList<Integer>(0));
		this.bibtexParam.setUserName("testuser2");
		bibtexChain.getFirstElement().perform(this.bibtexParam, this.dbSession, chainStatus);
		assertEquals(GetBibtexByFollowedUsers.class, chainStatus.getChainElement().getClass());		
	}
}