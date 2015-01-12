/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.exceptions;

/** An exception which signalises, that the requested content is not available
 * in the accepted formats of the client, as specified by the "Accept" header. 
 * 
 * Equivalent to HTTP status code 406 Not Acceptable, see 
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * 
 * @author rja
 */
public class NotAcceptableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String[] acceptableContentTypes;

	/**
	 * Constructs a new NotAcceptableException with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param acceptableContentTypes 
	 * 			  an array containing all acceptable content types
	 *            
	 */
	public NotAcceptableException(final String message, final String[] acceptableContentTypes) {
		super(message);
		this.acceptableContentTypes = acceptableContentTypes;
	}

	/**
	 * @return The list of acceptable content types.
	 */
	public String[] getAcceptableContentTypes() {
		return this.acceptableContentTypes;
	}

}