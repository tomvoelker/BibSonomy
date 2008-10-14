package org.bibsonomy.rest.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;

/**
 * The supported rendering formats.
 * 
 * PLEASE NOTE: When adding new Rendering Formats, don't forget to update the methods
 * getRenderingFormat and toMimeType!
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public enum RenderingFormat {

	/** currently only XML is supported */
	XML,
	PDF;

	/**
	 * Returns the rendering format to the given string.
	 */
	public static RenderingFormat getRenderingFormat(final String renderingFormat) {
		if (renderingFormat == null) throw new InternServerException("RenderingFormat is null");

		final String format = renderingFormat.toLowerCase().trim();
		if ("xml".equals(format)) {
			return XML;
		}
		
		if ("pdf".equals(format)){
			return PDF;
		}
		throw new ValidationException("Format " + format + " is not supported.");
	}
	
	/**
	 * Get a string representation of the MIME type of the current rendering format
	 * (according to http://www.iana.org/assignments/media-types/)
	 * 
	 * @return mimeType - a string representation of the content type
	 */
	public String toMimeType() {
		switch (this) {
			case XML: return "text/xml";
			case PDF: return "application/pdf";
		}
		throw new InternServerException("No MIME-Type defined for format " + this.toString());
	}
}