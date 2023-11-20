/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * This Class defines a Document
 * 
 * @author Christian Kramer
 */
@Getter
@Setter
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
	 * @param userName
	 */
	public void setUserName(String userName) {
		if (userName != null) {
			this.userName = userName.toLowerCase(); 
		} else {
			this.userName = userName;
		}
	}

	@Override
	public String toString() {
		return fileName;
	}
}