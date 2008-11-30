package org.bibsonomy.database.managers.hash.bibtex;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.hash.HashElement;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibTexByAuthor;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibTexByAuthorAndTag;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByConceptForUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByFriends;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByHash;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByHashForUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByTagNames;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByTagNamesAndUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForGroup;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForGroupAndTag;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForHomePageOrPopular;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexOfFriendsByTags;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexOfFriendsByUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexViewable;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Id$
 * @author Andreas Koch
 */
public class BibTexHashTest extends AbstractDatabaseManagerTest {

	protected BibTexHashingManager bibtexMap = new BibTexHashingManager();
	HashElement<? extends Post<? extends Resource>, ? extends GenericParam> elem;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.bibtexMap = new BibTexHashingManager();
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		this.bibtexMap = null;
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
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexByConceptForUser.class);
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
		this.bibtexParam.setNumSimpleTags(0);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexByFriends.class);
	}

	/**
	 * tests getBibtexByHash
	 */
	@Test
	public void getBibtexByHash() {
		bibtexParam.setHash("I_am_a_hash");
		bibtexParam.setBibtexKey(null);
		bibtexParam.setGrouping(GroupingEntity.ALL);
		bibtexParam.setRequestedUserName(null);
		bibtexParam.setTagIndex(null);
		bibtexParam.setOrder(null);
		bibtexParam.setSearch(null);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexByHash.class);
	}

	/**
	 * tests getBibtexByHashForUser
	 */
	@Test
	public void getBibtexByHashForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexByHashForUser.class);
	}

	/**
	 * tests getBibtexByTagNames
	 */
	@Test
	public void getBibtexByTagNames() {
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexByTagNames.class);
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
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexByTagNamesAndUser.class);
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
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexForGroup.class);
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
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexForGroupAndTag.class);
	}

	/**
	 * tests getBibtexForHomePage
	 */
	@Test
	public void getBibtexForHomePage() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexForHomePageOrPopular.class);
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
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexForUser.class);
	}

	/**
	 * tests getBibtexofFriendsByTags
	 */
	@Test
	public void getBibtexofFriendsByTags() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexOfFriendsByTags.class);
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
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexOfFriendsByUser.class);
	}

	/**
	 * tests getBibtexPopular
	 */
	@Test
	public void getBibtexPopular() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(Order.POPULAR);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setTagIndex(null);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexForHomePageOrPopular.class);
	}

	/**
	 * tests getBibtexViewable
	 */
	@Test
	public void getBibtexViewable() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibtexViewable.class);
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
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibTexByAuthor.class);
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
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setSearch("Grahl");
		elem = this.bibtexMap.getMapping(this.bibtexParam);
		Assert.assertEquals(elem.getClass(), GetBibTexByAuthorAndTag.class);
	}
}
