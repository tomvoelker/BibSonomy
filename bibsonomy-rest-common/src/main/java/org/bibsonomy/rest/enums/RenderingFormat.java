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

/**
 * The supported rendering formats.
 * TODO: rename to MediaType
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public enum RenderingFormat {
	
	/**
	 * xml format
	 */
	XML("text", "xml"), // TODO: why not "application/xml"
	
	/**
	 * json format
	 */
	JSON("application", "json"),
	
	/**
	 * TODO: improve documentation
	 */
	PDF("application", "pdf");
	
	private final String type;
	private final String subtype;

	private RenderingFormat(String type, String subtype) {
		this.type = type;
		this.subtype = subtype;
	}
	
	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return this.type + "/" + this.subtype;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return this.subtype;
	}

	/**
	 * returns always a valid MediaType 
	 * default is {@link RenderingFormat#DEFAULT}
	 * @param mimeType
	 * @return the media type to the given MIME type
	 */
	public static RenderingFormat getMediaType(String mimeType) {
		if (mimeType != null) {
			mimeType = mimeType.toLowerCase().trim();
			
			for (final RenderingFormat mediaType : RenderingFormat.values()) {
				if (mimeType.startsWith(mediaType.getMimeType())) {
					return mediaType;
				}
			}
		}
		
		return null;
	}

	/**
	 * @param renderingFormat 
	 * @return the rendering format to the given string.
	 */
	public static RenderingFormat getMediaTypeByFormat(final String renderingFormat) {
		if (renderingFormat == null) throw new InternServerException("RenderingFormat is null");

		final String format = renderingFormat.toLowerCase().trim();
		if ("xml".equals(format)) {
			return XML;
		}
		
		if ("json".equals(format)) {
			return JSON;
		}
		
		if ("pdf".equals(format)){
			return PDF;
		}
		
		return null;
	}
}