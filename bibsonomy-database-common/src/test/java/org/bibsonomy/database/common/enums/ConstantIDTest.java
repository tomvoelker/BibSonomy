package org.bibsonomy.database.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class ConstantIDTest {

	@Test
	public void testGetContentTypeByClass() {
		assertEquals(ConstantID.BOOKMARK_CONTENT_TYPE, ConstantID.getContentTypeByClass(Bookmark.class));
		assertEquals(ConstantID.BIBTEX_CONTENT_TYPE, ConstantID.getContentTypeByClass(BibTex.class));
		assertEquals(ConstantID.BIBTEX_CONTENT_TYPE, ConstantID.getContentTypeByClass(GoldStandardPublication.class));
	}

	@Test
	public void testGetClassByContentType() {
		assertEquals(Bookmark.class, ConstantID.getClassByContentType(ConstantID.BOOKMARK_CONTENT_TYPE));
		assertEquals(BibTex.class, ConstantID.getClassByContentType(ConstantID.BIBTEX_CONTENT_TYPE));
	}

	@Test
	public void testGetClassByContentTypeInt() throws Exception {
		assertEquals(Bookmark.class, ConstantID.getClassByContentType(1));
		assertEquals(BibTex.class, ConstantID.getClassByContentType(2));
		try {
			ConstantID.getClassByContentType(3);
			fail("expected " + UnsupportedResourceTypeException.class.getName());
		} catch (final UnsupportedResourceTypeException e) {
			
		}
		try {
			ConstantID.getClassByContentType(0);
			fail("expected " + UnsupportedResourceTypeException.class.getName());
		} catch (final UnsupportedResourceTypeException e) {
			
		}

	}
	
}
