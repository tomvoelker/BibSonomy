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

package org.bibsonomy.rest.utils;

import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;

/**
 * @author  Christian Kramer
 * @version $Id$
 */
public interface FileUploadInterface {

	/**
	 * Writes the uploaded file to the disk and returns the file together
	 * with meta information in the document
	 *
	 * @return The document describing the file (including the file!).
	 * @throws Exception
	 */
	public Document writeUploadedFile() throws Exception;

	/**
	 * Stores the created file on the hard drive and returns a document object
	 * The parameter string is needed for the creation for the hashedName of the document object 
	 * @param hashedName, loginUser
	 * @return
	 * @throws Exception
	 */
	public Document writeUploadedFile(String hashedName, User loginUser) throws Exception;

}