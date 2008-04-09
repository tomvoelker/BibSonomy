package org.bibsonomy.database.managers.chain.bib;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to the bibtex chain.
 * 
 * TODO: the tests should have asserts
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class BibTexChainTest extends AbstractChainTest {

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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
	}

	/**
	 * tests getBibtexByHash
	 */
	@Test
	public void getBibtexByHash() {
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
	}

	/**
	 * tests getBibtexByHashForUser
	 */
	@Test
	public void getBibtexByHashForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);

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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);

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
		this.bibtexParam.setGroupId(-1);
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
	}

	/**
	 * tests getBibtexofFriendsByTags
	 */
	@Test
	public void getBibtexofFriendsByTags() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
	}

	/**
	 * tests getBibtexofFriendsByUser
	 */
	@Test
	public void getBibtexofFriendsByUser() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
	}

	/**
	 * tests getBibtexViewable
	 */
	@Test
	public void getBibtexViewable() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
	}

	/**
	 * tests getBibtexByAuthor
	 */
	@Test
	public void getBibtexByAuthor() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setGroupId(-1);
		this.bibtexParam.setSearch("Grahl");
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
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
		this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
	}

	/**
	 * tests getBibtexBySearch
	 *
	 * TODO: adapt to new test-DB
	 */
	@Ignore
	public void getBibtexBySearch() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setSearch("Hotho");
		this.bibtexParam.setGroupId(0); // group = public
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setLimit(350);
		final List<Post<BibTex>> posts = this.bibtexChain.getFirstElement().perform(bibtexParam, dbSession);
		assertEquals(333, posts.size());
	}
}