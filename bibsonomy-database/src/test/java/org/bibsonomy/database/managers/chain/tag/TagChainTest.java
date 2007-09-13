package org.bibsonomy.database.managers.chain.tag;

import static org.junit.Assert.assertEquals;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * Tests related to the Tag Chain.
 * 
 * @author Dominik Benz
 * @author miranda
 * @version $Id$
 */
public class TagChainTest extends AbstractChainTest {

	@Test
	public void GetTagsByUser() {
		this.tagParam.setGrouping(GroupingEntity.USER);

		 //start chain
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
		
		//assertEquals(10, tags.size());
		 //assertEquals("1994", tags.get(0));
		 //assertEquals("2003", tags.get(9));
	}

	/*
	  * default is set up to Grouping.ALL
	  */
	
	@Test
	public void GetTagsByGroup() {
		 this.tagParam.setGrouping(GroupingEntity.GROUP);
    	 final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
         
		 //assertEquals(10, tags.size());
		 //assertEquals("!", tags.get(0));
		 //assertEquals("\"test", tags.get(9));
	}

	@Test
	public void GetTagsViewable() {
		 this.tagParam.setGrouping(GroupingEntity.VIEWABLE);
		 this.tagParam.setSearch("");
		 final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
	}

	@Test
	public void GetTagsByExpression() {
		 this.tagParam.setRegex("semantic");
		 this.tagParam.setGrouping(GroupingEntity.ALL);
    	 final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
         
		 //assertEquals(10, tags.size());
		 //assertEquals("!", tags.get(0));
		 //assertEquals("\"test", tags.get(9));
		
		
	}

	
	/*@Test
	public void GetTagByName() {
		

		if (this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession).isEmpty()) {
			System.out.println("I am empty!");
		}
		
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
	}*/
	
	

	@Test
	public void GetAllTags() {
		this.tagParam.setGrouping(GroupingEntity.ALL);
		this.tagParam.setRegex(null);
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
		System.out.println(tags.size());
	}
	
	@Test
	public void GetTagsByAuthor(){
		 this.tagParam.setGrouping(GroupingEntity.VIEWABLE);
		 this.tagParam.setSearch("stumme");
		final List<Tag> tags=this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
	}
	
}