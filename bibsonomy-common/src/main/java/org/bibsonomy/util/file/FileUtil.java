package org.bibsonomy.util.file;


/**
 * @author rja
 * @version $Id$
 */
public class FileUtil {
	
	/**
	 * Constructs the file path of a document
	 * 
	 * @param documentPath - the absolute path to the document directory in the filesystem
	 * @param documentFileHash - the filehash of the document 
	 * 
	 * @return The absolute path of the document on the file system.
	 */
	public static String getDocumentPath(final String documentPath, final String documentFileHash) {
		return documentPath + documentFileHash.substring(0, 2) + "/" + documentFileHash;
	}


}
