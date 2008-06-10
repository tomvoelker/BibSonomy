package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class TagTest {

	/**
	 * tests the basics
	 */
	@Test
	public void simple() {
		// constructor
		assertNull(new Tag().getName());
		assertEquals("test-tag", new Tag("test-tag").getName());

		// lazy initialization
		assertNotNull(new Tag().getPosts());
		assertNotNull(new Tag().getSubTags());
		assertNotNull(new Tag().getSuperTags());
	}

	/**
	 * tests setSubtagsString
	 */
	@Test
	public void setSubtagsString() {
		final Tag t1 = new Tag("t1");
		t1.setSubtagsString("t2 t3 t4");
		assertEquals(3, t1.getSubTags().size());
	}
}