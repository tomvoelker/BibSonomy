/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * This Class defines a Document
 * 
 * @author Christian Kramer
 */
public class Document implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2511680947761687645L;

	/** the filename */
	private String fileName;
	
	/** is the document a temporary file in the filesystem? */
	private boolean temp;

	/** the username of the document */
	private String userName;

	/** the hash of the file */
	private String fileHash;
	
	/** md5hash over content of the file  */
	private String md5hash;
	
	/** The date at which the document has been saved. */
	private Date date;
	
	/** The actual file ... sometimes it's contained in the document! */
	private File file;

	/**
	 * @return the temp
	 */
	public boolean isTemp() {
		return this.temp;
	}

	/**
	 * @param temp the temp to set
	 */
	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	/**
	 * @return md5hash
	 */
	public String getMd5hash() {
		return this.md5hash;
	}

	/**
	 * @param md5hash
	 */
	public void setMd5hash(String md5hash) {
		this.md5hash = md5hash;
	}

	/**
	 * @return fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		if (userName != null) {
			this.userName = userName.toLowerCase(); 
		} else {
			this.userName = userName;
		}
	}

	/**
	 * @return fileHash
	 */
	public String getFileHash() {
		return this.fileHash;
	}

	/**
	 * @param fileHash
	 */
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	@Override
	public String toString() {
		return fileName;
	}
}