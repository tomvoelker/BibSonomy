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
	
	/**
	 * @return md5hash
	 */
	public abstract String getMd5Hash();
}