package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author cvo
 */
public class JabRefImportCommand extends SettingsViewCommand{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2852728956746251923L;

	/** the file to import **/
	private CommonsMultipartFile fileBegin;
	
	private CommonsMultipartFile fileItem;
	
	private CommonsMultipartFile fileEnd;
	
	
	/**
	 * hash of the layout definition
	 */
	private String hash;

	/**
	 * @return the fileBegin
	 */
	public CommonsMultipartFile getFileBegin() {
		return this.fileBegin;
	}

	/**
	 * @param fileBegin the fileBegin to set
	 */
	public void setFileBegin(CommonsMultipartFile fileBegin) {
		this.fileBegin = fileBegin;
	}

	/**
	 * @return the fileItem
	 */
	public CommonsMultipartFile getFileItem() {
		return this.fileItem;
	}

	/**
	 * @param fileItem the fileItem to set
	 */
	public void setFileItem(CommonsMultipartFile fileItem) {
		this.fileItem = fileItem;
	}

	/**
	 * @return the fileEnd
	 */
	public CommonsMultipartFile getFileEnd() {
		return this.fileEnd;
	}

	/**
	 * @param fileEnd the fileEnd to set
	 */
	public void setFileEnd(CommonsMultipartFile fileEnd) {
		this.fileEnd = fileEnd;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
}
