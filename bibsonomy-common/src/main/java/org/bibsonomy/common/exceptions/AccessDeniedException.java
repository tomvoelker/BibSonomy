/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.common.exceptions;

/**
 * NOTE: if you want to redirect the user to the login page (webapp module)
 * you must use the AccessDeniedException from the Spring Security package
 * 
 * @author dzo
 */
public class AccessDeniedException extends RuntimeException {
	private static final long serialVersionUID = -2496286544331707252L;
	
	private static final String DEFAULT_MESSAGE = "You are not authorized to perform the requested operation.";

	/**
	 * creates an AccessDeniedException with {@link #DEFAULT_MESSAGE}
	 */
	public AccessDeniedException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * @see RuntimeException#RuntimeException(String)
	 * @param message
	 */
	public AccessDeniedException(String message) {
		super(message);
	}
}
