package org.bibsonomy.database.managers.chain.bib;

/*
 * @author Miranda Grahl 
 */



/* default values
 * userName:hotho
 * requestedUserName:grahl
 * grouping:all
 * RequestedGroupingName:kde
 * Hash:0000175071e6141a7d36835489f922ef
 * 
 */

import static org.junit.Assert.*;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.Order;
import org.junit.Test;


public class BibTexChainTest extends AbstractChainTest {

	@Test
	public void getBibtexByConceptForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(3);
		this.bibtexParam.setNumSimpleTags(0);
		this.bibtexParam.setNumTransitiveConcepts(0);

		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);

	}

	@Test
	public void getBibtexByFriends() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setRequestedGroupName(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);

		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);	

	}
	@Test
	public void getBibtexByHash() {
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}

	@Test
	public void getBibtexByHashForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);

	}
	@Test
	public void getBibtexByTagNames() {
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}
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
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}
	@Test
	public void getBibtexForGroup() {

		this.bibtexParam.setGrouping(GroupingEntity.GROUP);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setTagIndex(null);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}
	@Test
	public void getBibtexForGroupAndTag() {
		this.bibtexParam.setGrouping(GroupingEntity.GROUP);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);

	}
	@Test
	public void getBibtexForHomePage() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);


	}
	@Test
	public void getBibtexForUser() {
		this.bibtexParam.setGrouping(GroupingEntity.USER);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setGroupId(-1);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}
	@Test
	public void getBibtexofFriendsByTags() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}
	@Test
	public void getBibtexofFriendsByUser() {
		this.bibtexParam.setGrouping(GroupingEntity.FRIEND);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setNumSimpleConcepts(0);
		this.bibtexParam.setNumSimpleTags(3);
		this.bibtexParam.setNumTransitiveConcepts(0);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}
	@Test
	public void getBibtexPopular() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(Order.POPULAR);
		this.bibtexParam.setTagIndex(null);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}
	@Test
	public void getBibtexViewable() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}

	@Test
	public void getBibtexByAuthor() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);	
		this.bibtexParam.setTagIndex(null);
		this.bibtexParam.setGroupId(-1);
		this.bibtexParam.setSearch("Grahl");
		final List<Post<BibTex>> posts = this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}

	@Test
	public void getBibtexByAuthorAndTag() {
		this.bibtexParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bibtexParam.setHash(null);
		this.bibtexParam.setOrder(null);
		this.bibtexParam.setRequestedGroupName(null);
		this.bibtexParam.setSearch("Grahl");
		final List<Post<BibTex>> posts=this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
	}

	@Test
	public void getBibtexBySearch() {
		this.bibtexParam.setGrouping(GroupingEntity.ALL);
		this.bibtexParam.setSearch("Hotho");
		this.bibtexParam.setGroupId(0); // group = public
		this.bibtexParam.setRequestedUserName(null);
		this.bibtexParam.setLimit(350);
		final List<Post<BibTex>> posts = this.bibtexChain.getFirstElement().perform(bibtexParam,dbSession);
		assertEquals(333, posts.size());
	}	


}