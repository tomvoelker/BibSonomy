package org.bibsonomy.util.fileutil;

import org.apache.log4j.Logger;

/**
 *
 * @version $Id$
 * @author  Christian Kramer
 *
 */
public interface FileUploadInterface {

	/**
	 * @param rootPath
	 * @return uri
	 * @throws Exception
	 */
	public abstract void writeUploadedFiles(final String rootPath, final String docPath) throws Exception;

	/**
	 * @return fileHash
	 */
	public abstract String getFileHash();

	/**
	 * @return fileName
	 */
	public abstract String getFileName();

}