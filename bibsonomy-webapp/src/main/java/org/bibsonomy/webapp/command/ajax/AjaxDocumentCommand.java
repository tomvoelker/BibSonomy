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
package org.bibsonomy.webapp.command.ajax;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author wla
 */
public class AjaxDocumentCommand extends AjaxCommand {

	private String intraHash;
	
	private String fileName;
	
	private String fileHash;
	
	private String newFileName;
	
	private int fileID;

	private boolean temp;
	
	private MultipartFile file;

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
	
	/**
	 * 
	 * @return the new filename
	 */
	public String getNewFileName() {
		return this.newFileName;
	}
	
	/**
	 * 
	 * @param newFileName the new filename 
	 */
	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}
	
		
}
