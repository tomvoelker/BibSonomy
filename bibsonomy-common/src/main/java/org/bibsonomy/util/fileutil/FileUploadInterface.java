package org.bibsonomy.util.fileutil;

/**
 * @author  Christian Kramer
 * @version $Id$
 */
public interface FileUploadInterface {

	/**
	 * @param docPath 
	 * @throws Exception
	 */
	public abstract void writeUploadedFiles(final String docPath) throws Exception;

	/**
	 * @return fileHash
	 */
	public abstract String getFileHash();

	/**
	 * @return fileName
	 */
	public abstract String getFileName();
}