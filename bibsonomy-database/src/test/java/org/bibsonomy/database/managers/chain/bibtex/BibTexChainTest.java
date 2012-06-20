package org.bibsonomy.database.managers.chain.bibtex;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByResourceSearch;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexFromBasketForUser;
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
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Tag;
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
public class BibTexChainTest extends AbstractDatabaseManagerTest {
	
	protected static Chain<List<BibTex>, BibTexParam> bibtexChain;
	
	/**
	 * sets up the chain
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpChain() {
		bibtexChain = testDatabaseContext.getBean("publicationChain", Chain.class);
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
		
		assertEquals(GetResourcesByConceptForUser.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		
		assertEquals(GetResourcesByConceptForGroup.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		
		assertEquals(GetBibtexByResourceSearch.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesByFriends.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
	}

	/**
	 * tests getBibtexByHash
	 */
	@Test
	public void getBibtexByHash() {
		final BibTexParam param = new BibTexParam();
		param.setHash("I_am_a_hash");
		param.setBibtexKey(null);
		param.setGrouping(GroupingEntity.ALL);
		param.setRequestedUserName(null);
		param.setTagIndex(null);
		param.setOrder(null);
		param.setSearch(null);
		assertEquals(GetResourcesByHash.class, bibtexChain.getChainElement(param).getClass());
	}

	/**
	 * tests getBibtexByHashForUser
	 */
	@Test
	public void getBibtexByHashForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		assertEquals(GetResourcesByHashForUser.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesByTagNames.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesByTagNamesAndUser.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesForGroup.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesForGroupAndTag.class, bibtexChain.getChainElement(this.bibtexParam).getClass());

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
		assertEquals(GetResourcesForHomepage.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesForUser.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
	}

	/**
	 * tests getBibtexOfFriendsByTags
	 */
	@Test
	public void getBibtexOfFriendsByTags() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		assertEquals(GetResourcesOfFriendsByTags.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesOfFriendsByUser.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetResourcesPopular.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
	}

	/**
	 * tests getBibtexViewable
	 */
	@Test
	public void getBibtexViewable() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		assertEquals(GetResourcesViewable.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetBibtexByResourceSearch.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetBibtexByResourceSearch.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetBibtexByResourceSearch.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
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
		assertEquals(GetBibtexFromBasketForUser.class, bibtexChain.getChainElement(this.bibtexParam).getClass());
	}
	
	/**
	 * tests getBibtexByFollowedUsers
	 */
	@Test
	public void getBibtexByFollowedUsers() {
		this.bibtexParam.setGrouping(GroupingEntity.FOLLOWER);
		this.bibtexParam.addGroups(new ArrayList<Integer>(0));
		this.bibtexParam.setUserName("testuser2");
		assertEquals(GetResourcesByFollowedUsers.class, bibtexChain.getChainElement(this.bibtexParam).getClass());		
	}
	
	/**
	 * tests getBibtexForGroupAndTag
	 * @author rja
	 */
	@Test
	public void getBibtexForGroupAndTag2() {
		final BibTexParam p = new BibTexParam();

		final Set<Tag> tags = new HashSet<Tag>();
		final List<TagIndex> tagIndex = new LinkedList<TagIndex>();

		/*
		 * change number of requested tags here
		 */
		final int numberOfTags = 15;

		for (int i = 0; i < numberOfTags; i++) {
			tags.add(new Tag("a" + i));
			tagIndex.add(new TagIndex("a" + i, i + 1));
		}
		p.setTags(tags);
		p.setTagIndex(tagIndex);

		p.setGrouping(GroupingEntity.GROUP);
		p.setRequestedGroupName("kde");
		p.setRequestedUserName(null);
		p.setHash(null);
		p.setOrder(null);
		p.setSearch("");
		p.setNumSimpleConcepts(0);
		p.setNumSimpleTags(numberOfTags);
		p.setNumTransitiveConcepts(0);
		p.addGroup(GroupID.PUBLIC.getId());
		
		bibtexChain.perform(p, this.dbSession);
	}
	
	/**
	 * test if long tag queries are handled by the resource search
	 */
	@Test
	public void longTagQueries() {
		final BibTexParam param = new BibTexParam();
		for (int i = 0; i < PermissionDatabaseManager.MAX_TAG_SIZE; i++) {
			param.addTagName("test" + i);
		}
		
		assertEquals(GetBibtexByResourceSearch.class, bibtexChain.getChainElement(param).getClass());
	}
}