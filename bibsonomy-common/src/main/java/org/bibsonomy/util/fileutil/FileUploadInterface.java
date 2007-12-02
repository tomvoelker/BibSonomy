package org.bibsonomy.util.fileutil;

/**
 * @author  Christian Kramer
 * @version $Id$
 */
public interface FileUploadInterface {

	/**
	 * @param rootPath
	 * @param docPath 
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