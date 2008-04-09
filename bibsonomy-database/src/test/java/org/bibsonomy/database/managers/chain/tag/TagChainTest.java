package org.bibsonomy.database.managers.chain.tag;

import static org.junit.Assert.assertEquals;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Tag;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to the Tag Chain.
 * 
 * @author Dominik Benz
 * @author miranda
 * @version $Id$
 */
public class TagChainTest extends AbstractChainTest {

	/**
	 * Get tags by user
	 */
	@Test
	public void GetTagsByUser() {
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setRequestedUserName("hotho");
		this.tagParam.setUserName("hotho");
		this.tagParam.setGroupId(GroupID.INVALID.getId());
		this.tagParam.setRegex(null);
		this.tagParam.setLimit(1500);
		this.tagParam.setTagIndex(null);
		 //start chain
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);		
		assertEquals(1408, tags.size());
		this.resetParameters();
	}
	
	/**
	 *  get tags by group
	 */
	@Test
	public void GetTagsByGroup() {
		 this.tagParam.setGrouping(GroupingEntity.GROUP);
		 this.tagParam.setLimit(10);
		 this.tagParam.setGroupId(GroupID.INVALID.getId());
		 this.tagParam.addGroup(GroupID.PUBLIC.getId());
    	 final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);         
		 assertEquals(10, tags.size());
		 this.resetParameters();
	}

	/**
	 * get tags viewable
	 */
	@Test
	public void GetTagsViewable() {
		 this.tagParam.setGrouping(GroupingEntity.VIEWABLE);
		 this.tagParam.setSearch("");
		 this.tagParam.setUserName("hotho");
		 this.tagParam.setRegex(null);
		 this.tagParam.setRequestedGroupName("kde");
		 this.tagParam.setTagIndex(null);
		 this.tagParam.setLimit(1000);
		 this.tagParam.setOffset(0);
		 final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
		 assertEquals(333, tags.size());		 
		 this.resetParameters();
	}

	/**
	 * get tags by expression
	 */
	@Test
	public void GetTagsByExpression() {
		 this.tagParam.setRegex("web");
		 this.tagParam.setGrouping(GroupingEntity.USER);
		 this.tagParam.setRequestedUserName("hotho");
		 this.tagParam.setLimit(100);
    	 final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);         
		 assertEquals(12, tags.size());		
		 this.resetParameters();		 
	}

	/**
	 * get all tags, i.e. most often used tags out of the last 10000
	 */
	@Test
	public void GetAllTags() {
		this.tagParam.setGrouping(GroupingEntity.ALL);
		this.tagParam.setLimit(100);
		this.tagParam.setTagIndex(null);
		System.out.println("getAllTags");
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
		assertEquals(1, tags.size()); // only 'dblp' in the current test database
		this.resetParameters();
	}
	
	/**
	 * get tags by author
	 * 
	 * TODO: adapt to new Test DB
	 */
	@Ignore
	public void GetTagsByAuthor(){
		this.tagParam.setTagIndex(null);
		this.tagParam.setGrouping(GroupingEntity.VIEWABLE);
		this.tagParam.setSearch("Stumme");
		this.tagParam.setContentTypeByClass(BibTex.class);
		this.tagParam.setLimit(1000);
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
		assertEquals(342, tags.size());
	}
	
}