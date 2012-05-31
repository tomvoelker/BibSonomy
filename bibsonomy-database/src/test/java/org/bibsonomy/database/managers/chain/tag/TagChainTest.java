package org.bibsonomy.database.managers.chain.tag;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.managers.chain.tag.get.GetAllTags;
import org.bibsonomy.database.managers.chain.tag.get.GetPopularTags;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByBibtexkey;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByExpression;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByResourceSearch;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByUser;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsViewable;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the correct reaction of reach chain element of the tag chain.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class TagChainTest extends AbstractDatabaseManagerTest {
	protected static Chain<List<Tag>, TagParam> tagChain;
	
	/**
	 * sets up the chain
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpChain() {
		tagChain = (Chain<List<Tag>, TagParam>) testDatabaseContext.getBean("tagChain");
	}
	
	/**
	 * get all tags, i.e. most often used tags out of the last 10000
	 */
	@Test
	public void GetAllTags() {
		final TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setTagIndex(null);
		param.setHash(null);
		param.setContentTypeByClass(Resource.class);
		assertEquals(GetAllTags.class, tagChain.getChainElement(param).getClass());
	}

	/**
	 * get popular tags
	 */
	@Test
	public void getPopularTags() {
		final TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setOrder(Order.POPULAR);
		param.setTagIndex(null);
		param.setHash(null);
		param.setRegex(null);
		param.setSearch(null);
		assertEquals(GetPopularTags.class, tagChain.getChainElement(param).getClass());
	}

	/**
	 * get related tags
	 */
	@Test
	@Ignore
	public void getRelatedTags() {
		// TODO: implement test
	}

	/**
	 * get related tags for group
	 */
	@Test
	@Ignore
	public void getRelatedTagsForGroup() {
		// TODO: implement test
	}

	/**
	 * get similar tags
	 */
	@Test
	@Ignore
	public void getSimilarTags() {
		// TODO: implement test
	}

	/**
	 * get tags by author
	 */
	@Test
	public void GetTagsByAuthor() {
		final TagParam param = new TagParam();
		param.setTagIndex(null);
		param.setGrouping(GroupingEntity.ALL);
		param.setAuthor("Stumme");
		param.setContentTypeByClass(BibTex.class);
		assertEquals(GetTagsByResourceSearch.class, tagChain.getChainElement(param).getClass());
	}
	
	/**
	 * get tags by search string
	 */
	@Test
	public void GetTagsBySearchString() {
		final TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setSearch("Test");
		
		param.setRegex(null);
		param.setTagIndex(null);
		param.setHash(null);
		param.setBibtexKey(null);
		assertEquals(GetTagsByResourceSearch.class, tagChain.getChainElement(param).getClass());
	}
	
	/**
	 * get tags by author
	 */
	@Test
	public void GetTagsByAuthorAndTag() {
		final TagParam param = new TagParam();
		
		final Set<Tag> tags = new HashSet<Tag>();
		final List<TagIndex> tagIndex = new LinkedList<TagIndex>();
		for (int i = 0; i < 5; i++) {
			tags.add(new Tag("a" + i));
			tagIndex.add(new TagIndex("a" + i, i + 1));
		}
		param.setTags(tags);
		param.setTagIndex(tagIndex);
		
		param.setGrouping(GroupingEntity.VIEWABLE);
		param.setAuthor("Stumme");
		param.setContentTypeByClass(BibTex.class);
		assertEquals(GetTagsByResourceSearch.class, tagChain.getChainElement(param).getClass());
	}

	/**
	 * get tags by bibtex key
	 */
	@Test
	public void GetTagsByBibtexKey() {
		final TagParam param = new TagParam();
		param.setBibtexKey("test bibtexkey");
		param.setGrouping(GroupingEntity.ALL);
		param.setRequestedUserName(null);
		param.addGroup(GroupID.PUBLIC.getId());
		assertEquals(GetTagsByBibtexkey.class, tagChain.getChainElement(param).getClass());
	}

	/**
	 * get tags by expression
	 */
	@Test
	public void GetTagsByExpression() {
		final TagParam param = new TagParam();
		param.setRegex("web");
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName("hotho");
		param.setContentTypeByClass(Resource.class);
		assertEquals(GetTagsByExpression.class, tagChain.getChainElement(param).getClass());
	}

	/**
	 * get tags by friend of user
	 */
	@Test
	public void GetTagsByFriendOfUser() {
		// TODO
	}

	/**
	 * get tags by group
	 */
	@Test
	public void GetTagsByGroup() {
		final TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.GROUP);
		param.setRegex(null);
		param.getTagIndex().clear();
		param.setRequestedGroupName("requestedGroup");
		param.addGroup(GroupID.PUBLIC.getId());
		assertEquals(GetTagsByGroup.class, tagChain.getChainElement(param).getClass());
	}

	/**
	 * get tags by hash
	 */
	@Test
	@Ignore
	public void GetTagsByHash() {
		// TODO: implement test
	}

	/**
	 * get tags by hash for user
	 */
	@Test
	@Ignore
	public void GetTagsByHashForUser() {
		// TODO: implement test
	}

	/**
	 * Get tags by user
	 */
	@Test
	public void GetTagsByUser() {
		final TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName("hotho");
		param.setUserName("hotho");
		param.setRegex(null);
		param.setTagIndex(null);
		param.setContentTypeByClass(Resource.class);
		assertEquals(GetTagsByUser.class, tagChain.getChainElement(param).getClass());
	}

	/**
	 * get tags viewable
	 */
	@Test
	public void GetTagsViewable() {
		final TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.VIEWABLE);
		param.setSearch(null);
		param.setUserName("hotho");
		param.setRegex(null);
		param.setRequestedGroupName("kde");
		param.setTagIndex(null);
		assertEquals(GetTagsViewable.class, tagChain.getChainElement(param).getClass());
	}

}