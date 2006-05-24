package org.bibsonomy.rest.renderer.enums;

import org.bibsonomy.rest.exceptions.InternServerException;

import junit.framework.TestCase;

public class RenderingFormatTest extends TestCase {

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
}