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

package org.bibsonomy.services.filesystem;

import java.io.File;

import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * @author dzo
 */
public interface DocumentFileLogic {
	
	/**
	 * TODO: only return file path?
	 * @param document
	 * @return the file to the document
	 */
	public File getFileForDocument(Document document);
	
	/**
	 * TODO: return only file path?
	 * @param document
	 * @param preview
	 * @return the file to the preview image
	 */
	public File getPreviewFile(Document document, PreviewSize preview);
	
	/**
	 * save file for the user in the filesystem
	 * @param name
	 * @param file
	 * @return the document representing the file
	 * @throws Exception 
	 */
	public Document saveDocumentFile(String name, UploadedFile file) throws Exception;
	
	/**
	 * delete the document with the specified hash
	 * @param fileHash
	 * @return <code>true</code> iff the document was deleted from the filesystem
	 */
	public boolean deleteFileForDocument(final String fileHash);
	
	/**
	 * TODO: move?
	 * @return the extension checker to be used for document uploads
	 */
	public ExtensionChecker getDocumentExtensionChecker();
}
