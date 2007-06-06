package org.bibsonomy.rest.renderer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.enums.RenderingFormat;
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
	 * Returns the renderer for the given format; it defaults to the XML renderer.
	 */
	public static Renderer getRenderer(final RenderingFormat renderingFormat) {
		if (renderingFormat == null) throw new InternServerException("RenderingFormat is null");

		switch (renderingFormat) {
		case HTML:
			return new HTMLRenderer();
		case RDF:
			return new RDFRenderer();
		case XML:
		default:
			return XMLRenderer.getInstance();
		}
	}
}