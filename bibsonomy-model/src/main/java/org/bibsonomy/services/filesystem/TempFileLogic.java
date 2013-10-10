package org.bibsonomy.services.filesystem;

import java.io.File;

import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * @author dzo
 * @version $Id$
 */
public interface TempFileLogic {
	
	/**
	 * @param name
	 * @return the file with the specified name (read only)
	 */
	public File getTempFile(final String name);
	
	/**
	 * write file to tmp directory
	 * @param file
	 * @param extensionChecker
	 * @return the file written (read only)
	 * @throws Exception TODO
	 */
	public File writeTempFile(final UploadedFile file, ExtensionChecker extensionChecker) throws Exception;
	
	/**
	 * the tmp file to delete
	 * @param name
	 */
	public void deleteTempFile(final String name);
}
