/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.testutil;

import java.io.File;
import java.util.Collection;

import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * @author dzo
 */
public class DummyFileLogic implements FileLogic {

	@Override
	public void saveProfilePictureForUser(String username, UploadedFile pictureFile) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteProfilePictureForUser(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public File getProfilePictureForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getTempFile(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File writeTempFile(UploadedFile file, ExtensionChecker extensionChecker) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTempFile(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public Document writeJabRefLayout(String username, UploadedFile file, LayoutPart layoutPart) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteJabRefLayout(String hash) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public File getFileForDocument(Document document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getPreviewFile(Document document, PreviewSize preview) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document saveDocumentFile(String name, UploadedFile file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteFileForDocument(String fileHash) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExtensionChecker getDocumentExtensionChecker() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.JabRefFileLogic#allowedJabRefFileExtensions()
	 */
	@Override
	public Collection<String> allowedJabRefFileExtensions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.JabRefFileLogic#validJabRefLayoutFile(org.bibsonomy.model.util.file.UploadedFile)
	 */
	@Override
	public boolean validJabRefLayoutFile(UploadedFile file) {
		// TODO Auto-generated method stub
		return false;
	}
}
