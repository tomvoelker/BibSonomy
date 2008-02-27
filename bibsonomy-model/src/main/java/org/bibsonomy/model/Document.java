package org.bibsonomy.model;

/**
 * This Class defines a Document
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class Document{
	/**
	 * the filename
	 */
	private String fileName;
	
	/**
	 * the username of the document
	 */
	private String userName;
	
	/**
	 * the hash of the file
	 */
	private String fileHash;

	/**
	 * @return fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return fileHash
	 */
	public String getFileHash() {
		return this.fileHash;
	}

	/**
	 * @param fileHash
	 */
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
}