package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ResourceTest {

	@Test
	public void testGetResourceType() {
		assertEquals(Resource.class, Resource.getResource("all"));
		assertEquals(BibTex.class, Resource.getResource("bibtex"));
		assertEquals(Bookmark.class, Resource.getResource("bookmark"));

		assertEquals(Resource.class, Resource.getResource(" All"));
		assertEquals(BibTex.class, Resource.getResource("BIBTEX"));
		assertEquals(Bookmark.class, Resource.getResource("BookMark "));

		try {
			Resource.getResource("foo bar");
			fail("Should throw exception");
		} catch (final UnsupportedResourceTypeException ex) {
		}

		try {
			Resource.getResource("");
			fail("Should throw exception");
		} catch (final UnsupportedResourceTypeException ex) {
		}

		try {
			Resource.getResource(null);
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
	}

	/*
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	@Test
	public void testToString() {
		assertEquals("BIBTEX", Resource.toString(BibTex.class));
		assertEquals("BOOKMARK", Resource.toString(Bookmark.class));
		assertEquals("ALL", Resource.toString(Resource.class));
	}
}