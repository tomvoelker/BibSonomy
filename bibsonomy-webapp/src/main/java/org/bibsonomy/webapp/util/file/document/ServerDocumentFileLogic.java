/**
 *
 *  BibSonomy-Web-Common - Common things for web
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

package org.bibsonomy.webapp.util.file.document;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.DocumentFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.file.AbstractServerFileLogic;
import org.bibsonomy.util.file.FileUtil;

/**
 * @author dzo
 */
public class ServerDocumentFileLogic extends AbstractServerFileLogic implements DocumentFileLogic {
	
	private ExtensionChecker extensionChecker;
	
	/**
	 * @param path
	 */
	public ServerDocumentFileLogic(String path) {
		super(path);
	}
	
	@Override
	public File getFileForDocument(Document document) {
		final File file = new File(this.getFilePath(document.getFileHash()));
		file.setReadOnly();
		return file;
	}
	
	@Override
	public File getPreviewFile(Document document, PreviewSize preview) {
		final File file = new File(FileUtil.getUserDocumentPreviewPath(this.path, document.getFileHash(), document.getFileName(), preview));
		file.setReadOnly();
		return file;
	}
	
	@Override
	public Document saveDocumentFile(String username, UploadedFile file) throws Exception {
		final String fileName = file.getFileName();
		this.checkFile(this.extensionChecker, fileName);
		
		final String fileHash = this.getFileHash(fileName);
		final Document document = new Document();
		document.setUserName(username);
		document.setFileName(FilenameUtils.getName(fileName));
		document.setMd5hash(HashUtils.getMD5Hash(file.getBytes()));
		document.setFile(this.writeFile(file, getFilePath(fileHash)));
		document.setFileHash(fileHash);
		return document;
	}
	
	@Override
	public boolean deleteFileForDocument(final String fileHash) {
		return new File(getFilePath(fileHash)).delete();
	}

	@Override
	protected String getFilePath(String fileHash) {
		return FileUtil.getFilePath(this.path, fileHash);
	}
	
	/**
	 * @param extensionChecker the extensionChecker to set
	 */
	public void setExtensionChecker(ExtensionChecker extensionChecker) {
		this.extensionChecker = extensionChecker;
	}

	/**
	 * @return the extensionChecker
	 */
	@Override
	public ExtensionChecker getDocumentExtensionChecker() {
		return extensionChecker;
	}
}
