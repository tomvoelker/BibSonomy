package org.bibsonomy.util.file.jabref;


import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.JabRefFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.file.AbstractServerFileLogic;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.util.upload.impl.ListExtensionChecker;

/**
 * @author dzo
 * @version $Id$
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
