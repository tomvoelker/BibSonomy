/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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

package org.bibsonomy.common.errors;

import org.bibsonomy.common.enums.ErrorSource;

//TODO: find a nice and suitable name for errorsPackage
/**
 * @author sdo
 * @version $Id$
 */
public class ErrorMessage {

	private final ErrorSource errorSource;
	private String errorMessage;
	
	/**
	 * @param errorSource 
	 * @param errorMessage
	 */
	public ErrorMessage(ErrorSource errorSource, String errorMessage) {
		this.errorSource=errorSource;
		this.errorMessage=errorMessage;
	}

	/**
	 * @return the errorSource
	 */
	public ErrorSource getErrorSource() {
		return this.errorSource;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}


	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
	@Override
	public String toString() {
		return errorMessage;
	}

}
