package org.bibsonomy.webapp.util.file.temp;

import java.io.File;

import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.TempFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.util.file.AbstractServerFileLogic;

/**
 * @author dzo
 */
public class ServerTempFileLogic extends AbstractServerFileLogic implements TempFileLogic {
	
	/**
	 * @param path
	 */
	public ServerTempFileLogic(String path) {
		super(path);
	}
	
	@Override
	public File getTempFile(String name) {
		final File file = new File(this.getFilePath(name));
		file.setReadOnly();
		return file;
	}
	
	@Override
	public File writeTempFile(UploadedFile file, ExtensionChecker extensionChecker) throws Exception {
		return this.writeFile(file, extensionChecker);
	}

	@Override
	protected String getFilePath(String fileHash) {
		return this.path + "/" + fileHash;
	}

	@Override
	public void deleteTempFile(String name) {
		new File(getFilePath(name)).delete();
	}
}
