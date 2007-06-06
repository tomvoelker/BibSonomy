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
public class ResourceTypeTest {

	@Test
	public void testGetResourceType() {
		assertEquals(Resource.class, Resource.getResourceType("all"));
		assertEquals(BibTex.class, Resource.getResourceType("bibtex"));
		assertEquals(Bookmark.class, Resource.getResourceType("bookmark"));

		assertEquals(Resource.class, Resource.getResourceType(" All"));
		assertEquals(BibTex.class, Resource.getResourceType("BIBTEX"));
		assertEquals(Bookmark.class, Resource.getResourceType("BookMark "));

		try {
			Resource.getResourceType("foo bar");
			fail("Should throw exception");
		} catch (final UnsupportedResourceTypeException ex) {
		}

		try {
			Resource.getResourceType("");
			fail("Should throw exception");
		} catch (final UnsupportedResourceTypeException ex) {
		}

		try {
			Resource.getResourceType(null);
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