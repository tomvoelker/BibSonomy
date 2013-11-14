package org.bibsonomy.testutil;

import java.io.File;

import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * @author dzo
 * @version $Id$
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
	public File getProfilePictureForUser(String loggedinUser, String username) {
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

	@Override
	public boolean hasVisibleProfilePicture(String loggedinUser, String username) {
		return false;
	}

}
