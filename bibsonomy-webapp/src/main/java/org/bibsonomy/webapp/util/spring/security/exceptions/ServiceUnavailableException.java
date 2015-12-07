/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * An exception which signalises, that the called service is currently not available.
 * Caller must provide the number of seconds, after which the client may try to call
 * the service again.  
 * 
 * Equivalent to HTTP status code 503 Service Unavailable, see 
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * 
 * @author rja
 */
public class ServiceUnavailableException extends AuthenticationException {
	private static final long serialVersionUID = -526729562785871024L;
	
	
	private final long retryAfter;

	/**
	 * Constructs a new ServiceUnavailableException with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param retryAfter - the number of seconds the client has to wait until the
	 * service is available again. 
	 */
	public ServiceUnavailableException(final String message, final long retryAfter) {
		super(message);
		this.retryAfter = retryAfter;
	}

	/**
	 * @return The number of seconds the client has to wait until the service is 
	 * available again.
	 */
	public long getRetryAfter() {
		return this.retryAfter;
	}
}