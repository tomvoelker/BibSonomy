package org.bibsonomy.util.fileutil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Handles the file download
 * 
 * @version $Id$
 * @author Christian Kramer
 */
public class HandleFileDownload implements FileDownloadInterface {

	private BufferedInputStream buf;

	/**
	 * @param rootPath
	 * @param docPath 
	 * @param fileHash
	 * @throws FileNotFoundException
	 */
	public HandleFileDownload(final String rootPath, final String docPath, final String fileHash) throws FileNotFoundException {
		// create the path to the documents
		final String documentPath = rootPath + docPath;
		// get the file
		final File document = new File(documentPath + fileHash.substring(0, 2) + "/" + fileHash);

		// if the document is readable create a bufferedstream
		if (document.canRead()) {
			this.buf = new BufferedInputStream(new FileInputStream(document));
		} else {
			throw new FileNotFoundException("The requested file doesn't exists");
		}
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileDownloadInterface#getBuf()
	 */
	public BufferedInputStream getBuf() {
		return this.buf;
	}
}