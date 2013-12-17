/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model.util.file;

import java.io.File;
import java.io.IOException;

/**
 * @author dzo
  */
public interface UploadedFile {

	/**
	 * @return the name of the file
	 */
	public String getFileName();
	
	/**
	 * @return the content as byte array
	 * @throws IOException 
	 */
	public byte[] getBytes() throws IOException;
	
	/**
	 * transfers the file (e. g. in memory to the file system)
	 * @param fileInFileSytem
	 * @throws Exception 
	 */
	public void transferTo(File fileInFileSytem) throws Exception;

}
