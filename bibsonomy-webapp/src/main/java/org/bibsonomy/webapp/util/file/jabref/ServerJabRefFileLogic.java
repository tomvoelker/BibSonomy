/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.file.jabref;


import java.io.File;
import java.util.Collection;

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

	private final ExtensionChecker extensionChecker = new ListExtensionChecker(Sets.asSet(JabRefFileLogic.LAYOUT_FILE_EXTENSION));
	
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.JabRefFileLogic#validJabRefLayoutFile(org.bibsonomy.model.util.file.UploadedFile)
	 */
	@Override
	public boolean validJabRefLayoutFile(UploadedFile file) {
		return this.extensionChecker.checkExtension(file.getFileName());
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.JabRefFileLogic#allowedJabRefFileExtensions()
	 */
	@Override
	public Collection<String> allowedJabRefFileExtensions() {
		return this.extensionChecker.getAllowedExtensions();
	}
}
