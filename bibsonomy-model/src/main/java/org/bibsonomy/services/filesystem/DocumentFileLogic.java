package org.bibsonomy.services.filesystem;

import java.io.File;

import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * @author dzo
 * @version $Id$
 */
public interface DocumentFileLogic {
	
	/**
	 * TODO: only return file path?
	 * @param document
	 * @return the file to the document
	 */
	public File getFileForDocument(Document document);
	
	/**
	 * TODO: return only file path?
	 * @param document
	 * @param preview
	 * @return the file to the preview image
	 */
	public File getPreviewFile(Document document, PreviewSize preview);
	
	/**
	 * save file for the user in the filesystem
	 * @param name
	 * @param file
	 * @return the document representing the file
	 * @throws Exception 
	 */
	public Document saveDocumentFile(String name, UploadedFile file) throws Exception;
	
	/**
	 * delete the document with the specified hash
	 * @param fileHash
	 * @return <code>true</code> iff the document was deleted from the filesystem
	 */
	public boolean deleteFileForDocument(final String fileHash);
	
	/**
	 * TODO: move?
	 * @return the extension checker to be used for document uploads
	 */
	public ExtensionChecker getDocumentExtensionChecker();
}
