/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ice
 */
public class PictureCommand extends BaseCommand implements Serializable, DownloadCommand {

	private static final long serialVersionUID = -3444057502420374593L;
	
	private String requestedUser;
	
	private String filename;
	
	private String pathToFile;
	
	private String contentType;
	
	private MultipartFile file;
	
	private boolean delete;

	private String gravatarAddress;
	

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
	 * Sets user's Gravatar email address to be set.
	 * 
	 * @param address
	 */
	public void setGravatarAddress ( String address )
	{
		gravatarAddress = address;
	}
	
	/**
	 * Returns user's Gravatar email address to be set.
	 * @return gravAddress
	 */
	public String getGravatarAddress ()
	{
		return gravatarAddress;
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
	public void setFile(MultipartFile file) {
		this.file = file;
	}

	/**
	 * @return the file
	 */
	public MultipartFile getFile() {
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
	
	/**
	 * Returns the user logged in.
	 * @return an instance of the user logged in
	 */
	public User getLoginUser ()
	{
		return getContext().getLoginUser();
	}
	
}
