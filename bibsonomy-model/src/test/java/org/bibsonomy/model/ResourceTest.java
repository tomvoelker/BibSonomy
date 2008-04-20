package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ResourceTest {

	/**
	 * tests getResource
	 */
	@Test
	public void getResource() {
		assertEquals(Resource.class, Resource.getResource("all"));
		assertEquals(BibTex.class, Resource.getResource("bibtex"));
		assertEquals(Bookmark.class, Resource.getResource("bookmark"));

		assertEquals(Resource.class, Resource.getResource(" All"));
		assertEquals(BibTex.class, Resource.getResource("BIBTEX"));
		assertEquals(Bookmark.class, Resource.getResource("BookMark "));

		for (final String resourceType : new String[] { "", " ", null, "foo bar" }) {
			try {
				Resource.getResource(resourceType);
				fail("Expected UnsupportedResourceTypeException");
			} catch (final UnsupportedResourceTypeException ignored) {
			}
		}
	}

	/**
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