package org.bibsonomy.recommender.tag.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.junit.Test;


public class RecommendedTagTest {

	/**
	 * Whitespace containing and NULL tags are not allowed. 
	 */
	@Test
	public void testSetName() {
		RecommendedTag rec = new RecommendedTag();

		/*
		 * valid tags
		 */
		rec.setName("foo");
		rec.setName("актрисы");
		rec.setName("ä?ö.-ü");
		rec.setName("...");
		rec.setName(null);

		/*
		 * invalid tags
		 */
		try {
			rec.setName("foo ");
			rec.setName("актр\nисы");
			rec.setName("ä?ö.-\rü");
			fail("given tags should throw an " + InvalidModelException.class.getSimpleName());
		} catch (InvalidModelException e) {
			// OK 
		}

	}

	/**
	 * recommended tags should be equal independent of their case.
	 */
	@Test
	public void testEqualsObject() {
		assertEquals(new RecommendedTag("foo", 0, 0), new RecommendedTag("FOO", 2, 1));
		assertEquals(new RecommendedTag("foo", 0, 0), new RecommendedTag("foo", 2, 1));
		assertEquals(new RecommendedTag("FOO", 0, 0), new RecommendedTag("FOO", 2, 1));
		assertEquals(new RecommendedTag("foO", 0, 0), new RecommendedTag("FoO", 2, 1));

		assertFalse(new RecommendedTag("foo1", 0, 0).equals(new RecommendedTag("FOO", 2, 1)));
		assertFalse(new RecommendedTag("fooö", 0, 0).equals(new RecommendedTag("FOOÄ", 2, 1)));
		assertFalse(new RecommendedTag("foo.", 0, 0).equals(new RecommendedTag("FOO-", 2, 1)));
		assertFalse(new RecommendedTag("foo-", 0, 0).equals(new RecommendedTag("FOO.", 2, 1)));

	}

	@Test
	public void testRecommendedTagStringDoubleDouble() {
		/*
		 * valid tags
		 */
		new RecommendedTag("foo", 0, 0);
		new RecommendedTag("актрисы", 0, 0);
		new RecommendedTag("ä?ö.-ü", 0, 0);
		new RecommendedTag("...", 0, 0);
		new RecommendedTag(null, 0, 0);
		
		/*
		 * invalid tags
		 */
		try {	
			new RecommendedTag("foo ", 0, 0);
			new RecommendedTag("foo\n", 0, 0);
			new RecommendedTag("fo\ro", 0, 0);
			fail("given tags should throw an " + InvalidModelException.class.getSimpleName());
		} catch (InvalidModelException e) {
			// OK 
		}

	}

	/**
	 * tests hash code function
	 */
	@Test
	public void testHashcode() {
		assertEquals(new RecommendedTag("foo", 0, 0).hashCode(), new RecommendedTag("FOO", 2, 1).hashCode());
		assertEquals(new RecommendedTag("foo", 0, 0).hashCode(), new RecommendedTag("foo", 2, 1).hashCode());
		assertEquals(new RecommendedTag("FOO", 0, 0).hashCode(), new RecommendedTag("FOO", 2, 1).hashCode());
		assertEquals(new RecommendedTag("foO", 0, 0).hashCode(), new RecommendedTag("FoO", 2, 1).hashCode());
	}
}
