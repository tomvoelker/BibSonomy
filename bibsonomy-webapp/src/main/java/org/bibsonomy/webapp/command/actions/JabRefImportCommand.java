package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author cvo
 * @version $Id$
 */
public class JabRefImportCommand extends BaseCommand{
	
	/** the file to import **/
	private CommonsMultipartFile fileBegin = null;
	
	private CommonsMultipartFile fileItem = null;
	
	private CommonsMultipartFile fileEnd = null;
	
	/**
	 * contains the string for the action. The action could be create or delete
	 */
	private String action = null;

	/**
	 * name of the begin layout file
	 */
	private String beginName = null;
	
	/**
	 * hash of the begin layout file
	 */
	private String beginHash = null;
	
	/**
	 * name of the item layout file
	 */
	private String itemName = null;
	
	/**
	 * hash of the begin layout file
	 */
	private String itemHash = null;
	
	/**
	 * name of the end layout file
	 */
	private String endName = null;
	
	/**
	 * hash of the end layout file
	 */
	private String endHash = null;
	
	/**
	 * hash of the layout definition
	 */
	private String hash = null;
	

	/**
	 * @return the current chosen action, this could be create or delete 
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * 
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	public String getBeginName() {
		return this.beginName;
	}

	public String getItemName() {
		return this.itemName;
	}

	public String getItemHash() {
		return this.itemHash;
	}

	public String getEndName() {
		return this.endName;
	}

	public String getEndHash() {
		return this.endHash;
	}

	public void setBeginName(String beginName) {
		this.beginName = beginName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setItemHash(String itemHash) {
		this.itemHash = itemHash;
	}

	public void setEndName(String endName) {
		this.endName = endName;
	}

	public void setEndHash(String endHash) {
		this.endHash = endHash;
	}

	public void setBeginHash(String beginHash) {
		this.beginHash = beginHash;
	}

	public String getBeginHash() {
		return beginHash;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public CommonsMultipartFile getFileBegin() {
		return this.fileBegin;
	}

	public CommonsMultipartFile getFileItem() {
		return this.fileItem;
	}

	public CommonsMultipartFile getFileEnd() {
		return this.fileEnd;
	}

	public void setFileBegin(CommonsMultipartFile fileBegin) {
		this.fileBegin = fileBegin;
	}

	public void setFileItem(CommonsMultipartFile fileItem) {
		this.fileItem = fileItem;
	}

	public void setFileEnd(CommonsMultipartFile fileEnd) {
		this.fileEnd = fileEnd;
	}
}
