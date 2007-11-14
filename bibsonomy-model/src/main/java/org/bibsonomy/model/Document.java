package org.bibsonomy.model;

/**
 * This Class defines a Document
 * 
 * @author Christian Kramer
 */

public class Document{
	/**
	 * the filename
	 */
	private String fileName;
	
	/**
	 * the hash to the bibtex resource
	 */
	private String resourceHash;
	
	/**
	 * stores the username of the request
	 */
	private String userName;
	
	/**
	 * stores the hash of the file
	 */
	private String fileHash;

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getResourceHash() {
		return this.resourceHash;
	}

	public void setResourceHash(String resourceHash) {
		this.resourceHash = resourceHash;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFileHash() {
		return this.fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
}