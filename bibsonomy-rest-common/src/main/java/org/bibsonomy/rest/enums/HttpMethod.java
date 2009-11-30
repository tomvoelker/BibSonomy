/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;

/**
 * The supported HTTP-Methods.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public enum HttpMethod {

	GET, POST, PUT, DELETE, HEAD;

	/**
	 * Returns the corresponding HttpMethod-enum for the given string.
	 */
	public static HttpMethod getHttpMethod(final String httpMethod) {
		if (httpMethod == null) throw new InternServerException("HTTP-Method is null");

		final String method = httpMethod.toLowerCase().trim();
		if ("get".equals(method)) {
			return GET;
		} else if ("post".equals(method)) {
			return POST;
		} else if ("put".equals(method)) {
			return PUT;
		} else if ("delete".equals(method)) {
			return DELETE;
		} else if ("head".equals(method)) {
			return HEAD;
		} else {
			throw new UnsupportedHttpMethodException(httpMethod);
		}
	}
}