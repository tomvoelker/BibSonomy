package org.bibsonomy.rest.renderer.enums;

import org.bibsonomy.rest.exceptions.InternServerException;

/**
 * The supported rendering formats.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public enum RenderingFormat {
	XML, RDF, HTML;

	/**
	 * Returns the rendering format to the given string.
	 */
	public static RenderingFormat getRenderingFormat(final String renderingFormat) {
		if (renderingFormat == null) throw new InternServerException("RenderingFormat is null");
		final String format = renderingFormat.toLowerCase();
		if ("xml".equals(format) || "".equals(format)) {
			return XML;
		} else if ("rdf".equals(format)) {
			return RDF;
		} else if ("html".equals(format)) {
			return HTML;
		} else {
			throw new InternServerException("RenderingFormat (" + renderingFormat + ") is not supported");
		}		
	}
}