package org.bibsonomy.rest.renderer;

import org.bibsonomy.rest.renderer.enums.RenderingFormat;
import org.bibsonomy.rest.renderer.impl.HTMLRenderer;
import org.bibsonomy.rest.renderer.impl.RDFRenderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * A factory to get implementations of the
 * {@link org.bibsonomy.rest.renderer.Renderer}-interface.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class RendererFactory {

	/**
	 * Returns the renderer for the given format.
	 */
	public static Renderer getRenderer(final String format) {
		final RenderingFormat renderingformat = RenderingFormat.getRenderingFormat(format);
		switch (renderingformat) {
		case HTML:
			return new HTMLRenderer();
		case RDF:
			return new RDFRenderer();
		case XML:
		default:
			return new XMLRenderer();
		}
	}
}