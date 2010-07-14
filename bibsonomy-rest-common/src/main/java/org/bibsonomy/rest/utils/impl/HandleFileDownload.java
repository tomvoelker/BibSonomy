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

package org.bibsonomy.rest.utils.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.bibsonomy.rest.utils.FileDownloadInterface;
import org.bibsonomy.util.file.FileUtil;

/**
 * Handles the file download
 * 
 * @version $Id$
 * @author Christian Kramer
 */
public class HandleFileDownload implements FileDownloadInterface {

	private BufferedInputStream buf;
	
	/**
	 * @param docPath 
	 * @param fileHash
	 * @throws FileNotFoundException
	 */
	public HandleFileDownload(final String docPath, final String fileHash) throws FileNotFoundException {
		// get the file
		final File document = new File(FileUtil.getDocumentPath(docPath, fileHash));

		// if the document is readable create a bufferedstream
		if (document.canRead()) {
			this.buf = new BufferedInputStream(new FileInputStream(document));
		} else {
			throw new FileNotFoundException("The requested file doesn't exists");
		}
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileDownloadInterface#getBuf()
	 */
	@Override
	public BufferedInputStream getBuf() {
		return this.buf;
	}
}