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

package org.bibsonomy.util.file.jabref;


import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.JabRefFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.file.AbstractServerFileLogic;
import org.bibsonomy.util.file.FileUtil;

/**
 * @author dzo
  */
public class ServerJabRefFileLogic extends AbstractServerFileLogic implements JabRefFileLogic {

	private final ExtensionChecker extensionChecker = new ListExtensionChecker(Sets.asSet(JabrefLayoutUtils.layoutFileExtension));
	
	/**
	 * default constructor
	 * @param path
	 */
	public ServerJabRefFileLogic(String path) {
		super(path);
	}
	
	@Override
	public Document writeJabRefLayout(final String username, final UploadedFile file, final LayoutPart layoutPart) throws Exception {
		final String filename = file.getFileName();
		this.checkFile(this.extensionChecker, filename);
		
		final String hashedName = JabrefLayoutUtils.userLayoutHash(username, layoutPart);
		final Document document = new Document();
		document.setFileName(FilenameUtils.getName(filename));
		document.setMd5hash(HashUtils.getMD5Hash(file.getBytes()));
		document.setFileHash(hashedName);
		document.setUserName(username);
		document.setFile(this.writeFile(file, getFilePath(hashedName)));
		return document;
	}
	
	@Override
	public boolean deleteJabRefLayout(String hash) {
		return new File(this.getFilePath(hash)).delete();
	}
	
	@Override
	protected String getFilePath(String fileHash) {
		return FileUtil.getFilePath(this.path, fileHash);
	}
}
