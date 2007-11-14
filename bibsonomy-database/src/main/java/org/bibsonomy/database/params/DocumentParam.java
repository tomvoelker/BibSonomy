package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.HashID;

/**
 * 
 * @version $Id$
 * @author Christian Kramer
 *
 */
public class DocumentParam extends GenericParam{
	
	/**
	 * holds the hash of/to the file
	 */
	private String fileHash;
	
	/**
	 * the name of the file
	 */
	private String fileName;
	
	/**
	 * the hash of the resource
	 */
	private String resourceHash;
	
	/**
	 * the contentId of the bibtex entry
	 */
	private int contentId;
	
	/**
	 * defines the needed simhash which should be unique for each bibtex entry
	 */
	private HashID requestedSimHash;
	
	public DocumentParam(){
		this.requestedSimHash = HashID.INTRA_HASH;
	}
	
	public String getFileHash() {
		return this.fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

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

	public int getContentId() {
		return this.contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public int getRequestedSimHash() {
		return this.requestedSimHash.getId();
	}

	public void setRequestedSimHash(HashID requestedSimHash) {
		this.requestedSimHash = requestedSimHash;
	}
}