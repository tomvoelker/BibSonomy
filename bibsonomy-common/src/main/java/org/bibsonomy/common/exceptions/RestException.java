/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.common.exceptions;

/**
 * Exception that can be used to provide error message and response statuscode information to the frontend.
 * 
 * @author Jens Illig
  */
public class RestException extends RuntimeException {
	private static final long serialVersionUID = 7907882646866488962L;
	private final int httpCode;
	private final String messageKey;
	private final String message;
	
	/**
	 * Construct
	 * @param httpCode
	 * @param message
	 * @param messageKey
	 */
	public RestException(int httpCode, String message, String messageKey) {
		this.httpCode = httpCode;
		this.message = message;
		this.messageKey = messageKey;
	}

	/**
	 * @return the httpCode
	 */
	public int getHttpCode() {
		return this.httpCode;
	}

	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return this.messageKey;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return this.message;
	}
	
	
}
