/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
	 * @return the absolute path of the file
	 */
	public String getAbsolutePath();
	
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

	/**
	 * returns the purpose of the file.
	 * @return FilePurpose
	 */
	public FilePurpose getPurpose ();

}
