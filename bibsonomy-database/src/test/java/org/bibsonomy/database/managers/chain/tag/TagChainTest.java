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
 * @version $Id$
 */
public class TagChainTest extends AbstractChainTest {

	@Test
	public void GetTagsByUser() {
		// set parameters
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setUserName("stumme");

		// start chain
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);

		// assertEquals(10, tags.size());
		// assertEquals("1994", tags.get(0));
		// assertEquals("2003", tags.get(9));
	}

	@Test
	public void GetTagsByGroup() {
		// this.authUser = "hotho";
		// this.grouping = GroupingEntity.GROUP;
		// this.groupingName = "kde";
		// this.start = 0;
		// this.end = 10;

		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);

		// assertEquals(10, tags.size());
		// assertEquals("!", tags.get(0));
		// assertEquals("\"test", tags.get(9));
	}

	@Test
	public void GetTagsViewable() {
		// TODO final List<Tag> tags =
		// this.tagChain.getFirstElementForTag().perform(this.authUser,
		// this.grouping, this.groupingName, this.regex, this.subTags,
		// this.superTags, this.subSuperTagsTransitive, this.tagName,
		// this.start, this.end, this.dbSession);
	}

	@Test
	public void GetTagsByExpression() {
		// TODO final List<Tag> tags =
		// this.tagChain.getFirstElementForTag().perform(this.authUser,
		// this.grouping, this.groupingName, this.regex, this.subTags,
		// this.superTags, this.subSuperTagsTransitive, this.tagName,
		// this.start, this.end, this.dbSession);
	}

	@Test
	public void GetTagByName() {
		// set the test parameters

		if (this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession).isEmpty()) {
			System.out.println("I am empty!");
		}

		// put them into the queue
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);

		// assertEquals(1, tags.size()); // we should have only a single tag
		// // assertEquals(3444, tags.get(0).getCount());
		// assertEquals("web", tags.get(0).getName());

	}

	@Test
	public void GetAllTags() {
		final List<Tag> tags = this.tagChain.getFirstElement().perform(this.tagParam, this.dbSession);
	}
}