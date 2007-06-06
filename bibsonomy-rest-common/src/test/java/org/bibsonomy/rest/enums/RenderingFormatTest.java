package org.bibsonomy.rest.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RenderingFormatTest {

	@Test
	public void testGetRenderingFormat() {
		assertEquals(RenderingFormat.XML, RenderingFormat.getRenderingFormat(""));
		assertEquals(RenderingFormat.XML, RenderingFormat.getRenderingFormat("xml"));
		assertEquals(RenderingFormat.XML, RenderingFormat.getRenderingFormat("hurz"));

		assertEquals(RenderingFormat.RDF, RenderingFormat.getRenderingFormat("rdf"));
		assertEquals(RenderingFormat.HTML, RenderingFormat.getRenderingFormat("html"));

		assertEquals(RenderingFormat.RDF, RenderingFormat.getRenderingFormat("RdF"));
		assertEquals(RenderingFormat.HTML, RenderingFormat.getRenderingFormat("hTmL"));
		assertEquals(RenderingFormat.RDF, RenderingFormat.getRenderingFormat("  RdF  "));

		try {
			RenderingFormat.getRenderingFormat(null);
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
	}

	@Test
	public void testToString() {
		assertEquals(RenderingFormat.XML.toString(), "XML");
		assertEquals(RenderingFormat.RDF.toString(), "RDF");
		assertEquals(RenderingFormat.HTML.toString(), "HTML");
	}
}