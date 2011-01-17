package org.bibsonomy.webapp.command.ajax;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author wla
 * @version $Id$
 */
public class AjaxDocumentCommand extends AjaxCommand {

	private String intraHash;
	
	private String fileName;
	
	private String fileHash;
	
	private int fileID;
	
	private boolean temp;
	
	private CommonsMultipartFile file;

	/**
	 * @param intraHash the intraHash to set
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	/**
	 * @return the intraHash
	 */
	public String getIntraHash() {
		return intraHash;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileHash the fileHash to set
	 */
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	/**
	 * @return the fileHash
	 */
	public String getFileHash() {
		return fileHash;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	/**
	 * @return the file
	 */
	public CommonsMultipartFile getFile() {
		return file;
	}

	/**
	 * @param fileID the fileID to set
	 */
	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	/**
	 * @return the fileID
	 */
	public int getFileID() {
		return fileID;
	}

	/**
	 * @param temp the temp to set
	 */
	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	/**
	 * @return the temp
	 */
	public boolean isTemp() {
		return temp;
	}
	
	
	
		
}
