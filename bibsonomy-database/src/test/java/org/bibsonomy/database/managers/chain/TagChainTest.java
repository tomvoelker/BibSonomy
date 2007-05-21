/**
 * 
 */
package org.bibsonomy.database.managers.chain;

import static org.junit.Assert.assertEquals;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.GroupingEntityTest;
import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * Tests related to the Tag Chain.
 * 
 * Basically it is tested for each chain element if it does what it is expected to do.
 * 
 * @author dbenz
 * 
 */
public class TagChainTest extends AbstractChainTest {

	/**
	 * 
	 */
	@Test
	public void GetTagsByUser() {
		
		// set parameters
		this.authUser = "stumme";
		this.grouping = GroupingEntity.USER;
		this.groupingName = "stumme";
		this.start = 0;
		this.end = 10;
		
		// start chain
		final List<Tag> tags = this.tagChain.getFirstElementForTag().perform(this.authUser, this.grouping, this.groupingName, this.regex, this.subTags, this.superTags, this.subSuperTagsTransitive, this.tagName, this.start, this.end, this.dbSession);
		
		assertEquals(10, tags.size());
		assertEquals("1994", tags.get(0));
		assertEquals("2003", tags.get(9));
		// assertEquals(15, tags.get(9).getCount());
				
		this.resetParameters();
	}	
	
	@Test
	public void GetTagsByGroup() {
		
		this.authUser = "hotho";
		this.grouping = GroupingEntity.GROUP;
		this.groupingName = "kde";
		this.start = 0;
		this.end = 10;
		
		final List<Tag> tags = this.tagChain.getFirstElementForTag().perform(this.authUser, this.grouping, this.groupingName, this.regex, this.subTags, this.superTags, this.subSuperTagsTransitive, this.tagName, this.start, this.end, this.dbSession);
		
		assertEquals(10, tags.size());
		assertEquals("!", tags.get(0));
		// assertEquals("7", tags.get(0).getCount());
		assertEquals("\"test", tags.get(9));
		// assertEquals("1", tags.get(9).getCount());		
		
	}	
	
	@Test
	public void GetTagsViewable() {
		// TODO final List<Tag> tags = this.tagChain.getFirstElementForTag().perform(this.authUser, this.grouping, this.groupingName, this.regex, this.subTags, this.superTags, this.subSuperTagsTransitive, this.tagName, this.start, this.end, this.dbSession);
	}	
	
	@Test
	public void GetTagsByExpression() {		
		// TODO final List<Tag> tags = this.tagChain.getFirstElementForTag().perform(this.authUser, this.grouping, this.groupingName, this.regex, this.subTags, this.superTags, this.subSuperTagsTransitive, this.tagName, this.start, this.end, this.dbSession);
	}	
	
	@Test
	public void GetTagByName() {

		// set the test parameters
		this.tagName = "web";
		this.subTags = false;
		this.superTags = false;
		
		if (this.tagChain.getFirstElementForTag().perform(this.authUser, this.grouping, this.groupingName, this.regex, this.subTags, this.superTags, this.subSuperTagsTransitive, this.tagName, this.start, this.end, this.dbSession).isEmpty()) {
			System.out.println("I am empty!");			
		}
		
		// put them into the queue
		final List<Tag> tags = this.tagChain.getFirstElementForTag().perform(this.authUser, this.grouping, this.groupingName, this.regex, this.subTags, this.superTags, this.subSuperTagsTransitive, this.tagName, this.start, this.end, this.dbSession);
		
		assertEquals(1, tags.size()); // we should have only a single tag		
		assertEquals(3444, tags.get(0).getCount());		
		assertEquals("web", tags.get(0).getName());
		
	}
	
	@Test
	public void GetAllTags() {				
		final List<Tag> tags = this.tagChain.getFirstElementForTag().perform(this.authUser, this.grouping, this.groupingName, this.regex, this.subTags, this.superTags, this.subSuperTagsTransitive, this.tagName, this.start, this.end, this.dbSession);
		
	}
			
}
