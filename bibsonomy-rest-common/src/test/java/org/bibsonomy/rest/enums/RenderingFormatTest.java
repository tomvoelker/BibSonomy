package org.bibsonomy.rest.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RenderingFormatTest {

	@Test
	public void testGetRenderingFormat() {
		assertEquals(RenderingFormat.XML, RenderingFormat.getRenderingFormat("xml"));
		assertEquals(RenderingFormat.XML, RenderingFormat.getRenderingFormat("xMl"));

		try {
			RenderingFormat.getRenderingFormat(null);
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
		
		try {
			RenderingFormat.getRenderingFormat("someUnsupportedRenderingFormat");
			fail("Should throw exception");
		} catch (final ValidationException ex) {
		}		
	}

	@Test
	public void testToString() {
		assertEquals(RenderingFormat.XML.toString(), "XML");
	}
}