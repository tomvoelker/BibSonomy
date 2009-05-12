package org.bibsonomy.util.file;

import org.bibsonomy.util.StringUtils;


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

	/**
	 * Depending on the extension of the file, returns the correct MIME content
	 * type. NOTE: the method looks only at the name of the file not at the
	 * content!
	 * 
	 * @param filename
	 *            - name of the file.
	 * @return - the MIME content type of the file.
	 */
	public static String getContentType(final String filename) {
		if (StringUtils.matchExtension(filename, "ps")) {
			return "application/postscript";
		} else if (StringUtils.matchExtension(filename, "pdf")) {
			return "application/pdf";
		} else if (StringUtils.matchExtension(filename, "txt")) {
			return "text/plain";
		} else if (StringUtils.matchExtension(filename, "djv", "djvu")) {
			return "image/vnd.djvu";
		} else {
			return "application/octet-stream";
		}
	}

}
