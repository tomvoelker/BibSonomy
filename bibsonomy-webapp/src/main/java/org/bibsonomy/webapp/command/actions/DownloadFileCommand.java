package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author cvo
 * @version $Id$
 */
public class DownloadFileCommand extends BaseCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5650155398969930691L;


	private String filename = null;
	
	private String intrahash = null;
	
	private String requestedUser = null;
	
	private String pathToFile = null;
	
	private String contentType = null;
	
	private String action = null;
	
	
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getPathToFile() {
		return this.pathToFile;
	}

	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}

	public String getRequestedUser() {
		return this.requestedUser;
	}

	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getIntrahash() {
		return this.intrahash;
	}

	public void setIntrahash(String intrahash) {
		this.intrahash = intrahash;
	}	
}
