/**
 * BibSonomy-Web-Common - Common things for web
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
package org.bibsonomy.util.file;

import java.io.File;
import java.util.Collection;

import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.DocumentFileLogic;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.services.filesystem.JabRefFileLogic;
import org.bibsonomy.services.filesystem.ProfilePictureLogic;
import org.bibsonomy.services.filesystem.TempFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * @author dzo
 */
public class ServerFileLogic implements FileLogic {
	
	private ProfilePictureLogic profileFileLogic;
	private TempFileLogic tempFileLogic;
	private JabRefFileLogic jabRefFileLogic;
	private DocumentFileLogic documentFileLogic;
	
	@Override
	public File getFileForDocument(Document document) {
		return this.documentFileLogic.getFileForDocument(document);
	}
	
	@Override
	public File getPreviewFile(Document document, PreviewSize preview) {
		return this.documentFileLogic.getPreviewFile(document, preview);
	}
	
	@Override
	public Document saveDocumentFile(String name, UploadedFile file) throws Exception {
		return this.documentFileLogic.saveDocumentFile(name, file);
	}
	
	@Override
	public boolean deleteFileForDocument(String fileHash) {
		return this.documentFileLogic.deleteFileForDocument(fileHash);
	}
	
	@Override
	public void saveProfilePictureForUser(String username, UploadedFile pictureFile) throws Exception {
		this.profileFileLogic.saveProfilePictureForUser(username, pictureFile);
	}

	@Override
	public void deleteProfilePictureForUser(String username) {
		this.profileFileLogic.deleteProfilePictureForUser(username);
	}

	@Override
	public File getProfilePictureForUser(String username) {
		return this.profileFileLogic.getProfilePictureForUser(username);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.JabRefFileLogic#writeJabRefLayout(java.lang.String, org.bibsonomy.model.util.file.UploadedFile, org.bibsonomy.common.enums.LayoutPart)
	 */
	@Override
	public Document writeJabRefLayout(String username, UploadedFile file, LayoutPart layoutPart) throws Exception {
		return this.jabRefFileLogic.writeJabRefLayout(username, file, layoutPart);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.JabRefFileLogic#validJabRefLayoutFile(org.bibsonomy.model.util.file.UploadedFile)
	 */
	@Override
	public boolean validJabRefLayoutFile(UploadedFile file) {
		return this.jabRefFileLogic.validJabRefLayoutFile(file);
	}
	
	@Override
	public boolean deleteJabRefLayout(String hash) {
		return this.jabRefFileLogic.deleteJabRefLayout(hash);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.JabRefFileLogic#allowedJabRefFileExtensions()
	 */
	@Override
	public Collection<String> allowedJabRefFileExtensions() {
		return this.jabRefFileLogic.allowedJabRefFileExtensions();
	}

	@Override
	public File writeTempFile(UploadedFile file, ExtensionChecker extensionChecker) throws Exception {
		return this.tempFileLogic.writeTempFile(file, extensionChecker);
	}

	@Override
	public void deleteTempFile(String name) {
		this.tempFileLogic.deleteTempFile(name);
	}

	/**
	 * @param profileFileLogic the profileFileLogic to set
	 */
	public void setProfileFileLogic(ProfilePictureLogic profileFileLogic) {
		this.profileFileLogic = profileFileLogic;
	}

	/**
	 * @param tempFileLogic the tempFileLogic to set
	 */
	public void setTempFileLogic(TempFileLogic tempFileLogic) {
		this.tempFileLogic = tempFileLogic;
	}

	/**
	 * @param jabRefFileLogic the jabRefFileLogic to set
	 */
	public void setJabRefFileLogic(JabRefFileLogic jabRefFileLogic) {
		this.jabRefFileLogic = jabRefFileLogic;
	}

	/**
	 * @param documentFileLogic the documentFileLogic to set
	 */
	public void setDocumentFileLogic(DocumentFileLogic documentFileLogic) {
		this.documentFileLogic = documentFileLogic;
	}
	
	@Override
	public ExtensionChecker getDocumentExtensionChecker() {
		return this.documentFileLogic.getDocumentExtensionChecker();
	}

	@Override
	public File getTempFile(String name) {
		return this.tempFileLogic.getTempFile(name);
	}
}