package org.bibsonomy.webapp.util.file.csl;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.layout.csl.CslLayoutUtils;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.CslFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.file.AbstractServerFileLogic;
import org.bibsonomy.util.file.FileUtil;

/**
 * TODO: add documentation to this class
 *
 * @author jan
 */
public class ServerCSLFileLogic extends AbstractServerFileLogic implements CslFileLogic{
	
	private final ExtensionChecker extensionChecker = new ListExtensionChecker(Sets.asSet(CslFileLogic.LAYOUT_FILE_EXTENSION));
	
	/**
	 * default constructor
	 * @param path
	 */
	public ServerCSLFileLogic(String path) {
		super(path);
	}
	
	@Override
	public Document writeCSLLayout(final String username, final UploadedFile file) throws Exception{
		final String filename = file.getFileName();
//		this.checkFile(this.extensionChecker, filename);
		
		final String hashedName = CslLayoutUtils.userLayoutHash(username);
		final Document document = new Document();
		document.setFileName(FilenameUtils.getName(filename));
		document.setMd5hash(HashUtils.getMD5Hash(file.getBytes()));
		document.setFileHash(hashedName);
		document.setUserName(username);
		document.setFile(this.writeFile(file, getFilePath(hashedName)));
		return document;
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.util.file.AbstractServerFileLogic#getFilePath(java.lang.String)
	 */
	@Override
	protected String getFilePath(String fileHash) {
		return FileUtil.getFilePath(this.path, fileHash);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.CSLFileLogic#deleteCSLLayout(java.lang.String)
	 */
	@Override
	public boolean deleteCSLLayout(String hash) {
		return new File(this.getFilePath(hash)).delete();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.CSLFileLogic#validCSLLayoutFile(org.bibsonomy.model.util.file.UploadedFile)
	 */
	@Override
	public boolean validCSLLayoutFile(UploadedFile file) {
		return this.extensionChecker.checkExtension(file.getFileName());
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.filesystem.CSLFileLogic#allowedCSLFileExtensions()
	 */
	@Override
	public Collection<String> allowedCSLFileExtensions() {
		return this.extensionChecker.getAllowedExtensions();
	}

}
