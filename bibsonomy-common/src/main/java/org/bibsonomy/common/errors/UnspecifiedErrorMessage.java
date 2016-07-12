/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.common.errors;

/**
 * Use this message if something went wrong and doesn't fit one of the other ErrorMessages.
 * Add the Exception, that occurred.
 * 
 * @author sdo
 */
public class UnspecifiedErrorMessage extends ErrorMessage {
	private final Exception ex;
	
	/**
	 * @param ex The exception that was caught.
	 */
	public UnspecifiedErrorMessage(Exception ex) {
		super(ex.getClass().getName() + " : " + ex.getMessage(), "database.exception.unspecified");
		
		this.ex = ex;
	}
	
	/**
	 * @return the exception, that caused the error
	 */
	public Exception getException() {
		return ex;
	}
}
