package org.bibsonomy.rest.renderer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.renderer.impl.HTMLRenderer;
import org.bibsonomy.rest.renderer.impl.RDFRenderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class RendererFactoryTest {

	@Test
	public void testGetRenderer() {
		assertTrue(RendererFactory.getRenderer(RenderingFormat.XML) instanceof XMLRenderer);
		assertTrue(RendererFactory.getRenderer(RenderingFormat.RDF) instanceof RDFRenderer);
		assertTrue(RendererFactory.getRenderer(RenderingFormat.HTML) instanceof HTMLRenderer);

		try {
			RendererFactory.getRenderer(null);
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
	}
}