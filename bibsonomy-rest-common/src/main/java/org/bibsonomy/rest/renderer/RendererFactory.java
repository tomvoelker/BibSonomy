package org.bibsonomy.rest.renderer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.enums.RenderingFormat;
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
		case XML:
		case PDF:
		default:
			return XMLRenderer.getInstance();
		}
	}
}