package org.bibsonomy.database.managers.chain.tag;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.tag.get.GetAllTags;
import org.bibsonomy.database.managers.chain.tag.get.GetPopularTags;
import org.bibsonomy.database.managers.chain.tag.get.GetRelatedTagsByAuthorAndTags;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByAuthor;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByBibtexkey;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByExpression;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByUser;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsViewable;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.junit.Test;

/**
 * Tests the correct reaction of reach chain element of the tag chain.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class TagChainTest extends AbstractChainTest {

	/**
	 * get all tags, i.e. most often used tags out of the last 10000
	 */
	@Test
	public void GetAllTags() {
		TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setTagIndex(null);
		param.setHash(null);
		param.setContentTypeByClass(Resource.class);
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetAllTags.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * get popular tags
	 */
	@Test
	public void getPopularTags() {
		TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setOrder(Order.POPULAR);
		param.setTagIndex(null);
		param.setHash(null);
		param.setRegex(null);
		param.setSearch(null);
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetPopularTags.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * get related tags
	 */
	@Test
	public void getRelatedTags() {
		// TODO
	}

	/**
	 * get related tags for group
	 */
	@Test
	public void getRelatedTagsForGroup() {
		// TODO
	}

	/**
	 * get similar tags
	 */
	@Test
	public void getSimilarTags() {
		// TODO
	}

	/**
	 * get tags by author
	 */
	@Test
	public void GetTagsByAuthor() {
		TagParam param = new TagParam();
		param.setTagIndex(null);
		param.setGrouping(GroupingEntity.VIEWABLE);
		param.setSearch("Stumme");
		param.setContentTypeByClass(BibTex.class);
		param.setSearchEntity(SearchEntity.AUTHOR);
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetTagsByAuthor.class, this.chainStatus.getChainElement().getClass());
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
		param.setSearch("Stumme");
		param.setContentTypeByClass(BibTex.class);
		param.setSearchEntity(SearchEntity.AUTHOR);
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetRelatedTagsByAuthorAndTags.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * get tags by bibtex key
	 */
	@Test
	public void GetTagsByBibtexKey() {
		TagParam param = new TagParam();
		param.setBibtexKey("test bibtexkey");
		param.setGrouping(GroupingEntity.ALL);
		param.setRequestedUserName(null);
		param.addGroup(GroupID.PUBLIC.getId());
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetTagsByBibtexkey.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * get tags by expression
	 */
	@Test
	public void GetTagsByExpression() {
		TagParam param = new TagParam();
		param.setRegex("web");
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName("hotho");
		param.setContentTypeByClass(Resource.class);
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetTagsByExpression.class, this.chainStatus.getChainElement().getClass());
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
		TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.GROUP);
		param.setRegex(null);
		param.getTagIndex().clear();
		param.setRequestedGroupName("requestedGroup");
		param.addGroup(GroupID.PUBLIC.getId());		
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetTagsByGroup.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * get tags by hash
	 */
	@Test
	public void GetTagsByHash() {
		// TODO
	}

	/**
	 * get tags by hash for user
	 */
	@Test
	public void GetTagsByHashForUser() {
		// TODO
	}

	/**
	 * Get tags by user
	 */
	@Test
	public void GetTagsByUser() {
		TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName("hotho");
		param.setUserName("hotho");
		param.setRegex(null);
		param.setTagIndex(null);
		param.setContentTypeByClass(Resource.class);
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetTagsByUser.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * get tags viewable
	 */
	@Test
	public void GetTagsViewable() {
		TagParam param = new TagParam();
		param.setGrouping(GroupingEntity.VIEWABLE);
		param.setSearch(null);
		param.setUserName("hotho");
		param.setRegex(null);
		param.setRequestedGroupName("kde");
		param.setTagIndex(null);
		this.tagChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetTagsViewable.class, this.chainStatus.getChainElement().getClass());
	}

}