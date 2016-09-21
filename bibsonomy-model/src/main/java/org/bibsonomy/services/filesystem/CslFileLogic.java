package org.bibsonomy.services.filesystem;

import java.util.Collection;

import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public interface CslFileLogic {
	/**
	 * The file extension of layout filter file names.
	 */
	public static final String LAYOUT_FILE_EXTENSION = "csllayout";

	/**
	 * 
	 * @param username
	 * @param file
	 * @return the document representing the csl layout file
	 * @throws Exception
	 */
	public Document writeCSLLayout(String username, UploadedFile file) throws Exception;
	
	/**
	 * @param hash
	 * @return <code>true</code> if file was deleted
	 */
	public boolean deleteCSLLayout(final String hash);
	
	/**
	 * 
	 * @param file
	 * @return <code>true</code> iff the file is valid (currently only checks the
	 * file extension)
	 */
	public boolean validCSLLayoutFile(final UploadedFile file);
	
	/**
	 * @return a set of all allowed file extensions for jabref layout files
	 */
	public Collection<String> allowedCSLFileExtensions();
}
