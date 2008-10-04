package org.bibsonomy.model;

/**
 * This Class defines a Document
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class Document {
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
	 * md5hash over content of the file 
	 */
	private String md5hash;

	/**
	 * @return md5hash
	 */
	public String getMd5hash() {
		return this.md5hash;
	}

	/**
	 * @param md5hash
	 */
	public void setMd5hash(String md5hash) {
		this.md5hash = md5hash;
	}

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
		if (userName != null) {
			this.userName = userName.toLowerCase(); 
		} else {
			this.userName = userName;
		}
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