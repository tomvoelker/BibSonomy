package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command class for the download or deleted File operation
 * @author cvo
 * @version $Id$
 */
public class DownloadFileCommand extends BaseCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5650155398969930691L;

	/**
	 * the filename of the document which should be downloaded
	 */
	private String filename = null;
	
	/**
	 * intrahash of the file
	 */
	private String intrahash = null;
	
	/**
	 * user who wants to download the file
	 */
	private String requestedUser = null;
	
	/**
	 * path to file 
	 */
	private String pathToFile = null;
	
	/**
	 * content type of the file 
	 */
	private String contentType = null;
	
	/**
	 * download / delete
	 */
	private String action = null;
	
	/**
	 * 
	 * @return action
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

	/**
	 * 
	 * @return content type
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * 
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 
	 * @return path to file
	 */
	public String getPathToFile() {
		return this.pathToFile;
	}

	/**
	 * 
	 * @param pathToFile
	 */
	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}

	/**
	 * 
	 * @return user who request the file
	 */
	public String getRequestedUser() {
		return this.requestedUser;
	}

	/**
	 * 
	 * @param requestedUser
	 */
	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}

	/**
	 * 
	 * @return filename of the requested file
	 */
	public String getFilename() {
		return this.filename;
	}

	/**
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * 
	 * @return intrahash of the file
	 */
	public String getIntrahash() {
		return this.intrahash;
	}

	/**
	 * 
	 * @param intrahash
	 */
	public void setIntrahash(String intrahash) {
		this.intrahash = intrahash;
	}	
}
