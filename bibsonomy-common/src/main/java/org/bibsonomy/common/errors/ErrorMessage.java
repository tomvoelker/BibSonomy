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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;


//TODO: find a nice and suitable name for errorsPackage
/**
 * @author sdo
 * @version $Id$
 */
public class ErrorMessage {


	private String errorMessage;
	private String localizedMessageKey;
	private ArrayList<String> parameters=null;
	

	/**
	 * 
	 */
	public ErrorMessage() {
		
	}
	/**
	 * @param errorMessage is like the exception message
	 * @param localizedMessageKey 	is a key to the corresponding localized String in the message_properties files
	 * @param parameters are some Strings for the localized message
	 */
	public ErrorMessage(String errorMessage, String localizedMessageKey, ArrayList<String>parameters) {
		this.errorMessage=errorMessage;
		this.localizedMessageKey=localizedMessageKey;
		if (present(parameters)) {
			this.parameters=parameters;
		} else {
			this.parameters= new ArrayList<String>();
		}
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
	
	/**
	 * @return the localizedMessageKey
	 */
	public String getLocalizedMessageKey() {
		return this.localizedMessageKey;
	}

	/**
	 * @param localizedMessageKey the localizedMessageKey to set
	 */
	public void setLocalizedMessageKey(String localizedMessageKey) {
		this.localizedMessageKey = localizedMessageKey;
	}

	/**
	 * @return the parameters
	 */
	public ArrayList<String> getParameters() {
		return this.parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}
	
	
	@Override
	public String toString() {
		return errorMessage;
	}

}
