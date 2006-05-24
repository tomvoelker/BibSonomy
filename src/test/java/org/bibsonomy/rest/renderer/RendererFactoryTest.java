package org.bibsonomy.rest.renderer;

import junit.framework.TestCase;

import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.impl.HTMLRenderer;
import org.bibsonomy.rest.renderer.impl.RDFRenderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

public class RendererFactoryTest extends TestCase {

	public void testGetRenderer() {
		assertTrue(RendererFactory.getRenderer("xml") instanceof XMLRenderer);
		assertTrue(RendererFactory.getRenderer("rdf") instanceof RDFRenderer);
		assertTrue(RendererFactory.getRenderer("html") instanceof HTMLRenderer);

		assertTrue(RendererFactory.getRenderer("") instanceof XMLRenderer);

		try {
			RendererFactory.getRenderer(null);
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
	}
}