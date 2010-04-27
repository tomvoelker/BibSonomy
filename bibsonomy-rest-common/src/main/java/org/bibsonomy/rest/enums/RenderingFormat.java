/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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