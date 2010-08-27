package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author ice
 * @version $Id$
 */
public class PictureCommand extends BaseCommand implements Serializable, DownloadCommand {

	private static final long serialVersionUID = -3444057502420374593L;
	
	private String requestedUser;
	
	private String filename;
	
	private String pathToFile;
	
	private String contentType;
	
	private CommonsMultipartFile file;
	
	private boolean delete;

	/**
	 * @param RequestedUser the getRequestedUser to set
	 */
	public void setRequestedUser(String RequestedUser) {
		this.requestedUser = RequestedUser;
	}

	/**
	 * @return the getRequestedUser
	 */
	public String getRequestedUser() {
		return requestedUser;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the filename
	 */
	@Override
	public String getFilename() {
		return filename;
	}


	/**
	 * @param pathToFile the pathToFile to set
	 */
	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}

	/**
	 * @return the pathToFile
	 */
	@Override
	public String getPathToFile() {
		return pathToFile;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the contentType
	 */
	@Override
	public String getContentType() {
		return contentType;
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
	 * @param delete the delete to set
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	/**
	 * @return the delete
	 */
	public boolean isDelete() {
		return delete;
	}


	
}
