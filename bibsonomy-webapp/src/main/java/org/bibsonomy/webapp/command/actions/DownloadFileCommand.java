/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command class for the download or deleted File operation
 * @author cvo
 */
public class DownloadFileCommand extends BaseCommand implements Serializable, DownloadCommand {
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
	 * size of document preview images
	 */
	private PreviewSize preview = null;
	
	/**
	 * type of the resource the document is attached to
	 */
	private Class<? extends Resource> resourcetype;
	
	/**
	 * embed qr code into document or not
	 */
	private boolean qrcode = false;
	

	/**
	 * 
	 * @return content type
	 */
	@Override
	public String getContentType() {
		return this.contentType;
	}

	
	/**
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return path to the file
	 */
	@Override
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
	@Override
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

	public PreviewSize getPreview() {
		return this.preview;
	}

	public void setPreview(PreviewSize preview) {
		this.preview = preview;
	}

	/**
	 * @return the qrcode
	 */
	public boolean isQrcode() {
		return this.qrcode;
	}
	
	/**
	 * @param qrcode the qrcode to set
	 */
	public void setQrcode(boolean qrcode) {
		this.qrcode = qrcode;
	}


	public Class<? extends Resource> getResourcetype() {
		return resourcetype;
	}


	public void setResourcetype(Class<? extends Resource> resourcetype) {
		this.resourcetype = resourcetype;
	}	
}
