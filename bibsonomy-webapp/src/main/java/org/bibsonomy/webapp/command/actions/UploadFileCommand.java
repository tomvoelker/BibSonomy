package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.model.Document;
import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author daill
 * @version $Id$
 */
public class UploadFileCommand extends BaseCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2142612449965274710L;
	
	private CommonsMultipartFile file;
	private Document doc;
	private String resourceHash;
	private String apiUrl;
	private String refererUrl;
	
	/**
	 * @return String
	 */
	public String getRefererUrl() {
		return this.refererUrl;
	}

	/**
	 * @param refererUrl
	 */
	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	/**
	 * @return String
	 */
	public String getApiUrl() {
		return this.apiUrl;
	}

	/**
	 * @param apiUrl
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * @return Document
	 */
	public Document getDoc() {
		return this.doc;
	}

	/**
	 * @param doc
	 */
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	/**
	 * @return String
	 */
	public String getResourceHash() {
		return this.resourceHash;
	}

	/**
	 * @param resourceHash
	 */
	public void setResourceHash(String resourceHash) {
		this.resourceHash = resourceHash;
	}

	/**
	 * @return CommonsMultipartFile
	 */
	public CommonsMultipartFile getFile() {
		return this.file;
	}

	/**
	 * @param file
	 */
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	

}
