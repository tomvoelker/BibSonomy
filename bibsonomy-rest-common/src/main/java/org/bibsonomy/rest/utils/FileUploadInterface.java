/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

import java.io.File;

import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;

/**
 * @author  Christian Kramer
 * @version $Id$
 */
public interface FileUploadInterface {

	/**
	 * writes the uploaded file to the disk
	 * @return file
	 * @throws Exception
	 */
	public File writeUploadedFile() throws Exception;

	/**
	 * Stores the created file on the hard drive and returns a document object
	 * The parameter string is needed for the creation for the hashedName of the document object 
	 * @param hashedName, loginUser
	 * @return
	 * @throws Exception
	 */
	public Document writeUploadedFile(String hashedName, User loginUser) throws Exception;
	
	/**
	 * 
	 * @param docpath
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public Document writeUploadedFile(String docpath, String userName) throws Exception;
	
	/**
	 * 
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public Document writeUploadedFile(String userName) throws Exception;	
}