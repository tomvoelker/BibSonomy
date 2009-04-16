package org.bibsonomy.model.util;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class TagUtilsTest {

	@Test
	public void testGetMaxUserCount() {
//		fail("Not yet implemented");
	}

	@Test
	public void testGetMaxGlobalcountCount() {
//		fail("Not yet implemented");
	}

	@Test
	public void testToTagString() {
		
		final List<Tag> tags = new LinkedList<Tag>();
		
		Assert.assertEquals("", TagUtils.toTagString(tags, " "));

		tags.add(new Tag("foo"));
		
		Assert.assertEquals("foo", TagUtils.toTagString(tags, " "));

		tags.add(new Tag("bar"));
		
		Assert.assertEquals("foo bar", TagUtils.toTagString(tags, " "));

		tags.add(new Tag("blubb"));
		
		Assert.assertEquals("foo bar blubb", TagUtils.toTagString(tags, " "));

	}

}
