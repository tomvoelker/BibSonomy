/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 18.02.2014
 */
package org.bibsonomy.layout.csl.model;

import java.io.File;
import java.util.Date;

import org.bibsonomy.model.Document;

/**
 * Wraps {@link Document} objects such that the json serializer never runs into
 * dangerous (current&future) properties of {@link Document} which may contain
 * recursive structures (such as {@link File} does).
 * 
 * @author Jens Illig
 */
public class DocumentCslWrapper {
	private final Document document;

	public DocumentCslWrapper(Document document) {
		this.document = document;
	}

	@Override
	public int hashCode() {
		return document.hashCode();
	}

	public boolean isTemp() {
		return document.isTemp();
	}

	public void setTemp(boolean temp) {
		document.setTemp(temp);
	}

	public String getMd5hash() {
		return document.getMd5hash();
	}

	public void setMd5hash(String md5hash) {
		document.setMd5hash(md5hash);
	}

	public String getFileName() {
		return document.getFileName();
	}

	public void setFileName(String fileName) {
		document.setFileName(fileName);
	}

	public String getUserName() {
		return document.getUserName();
	}

	public void setUserName(String userName) {
		document.setUserName(userName);
	}

	public String getFileHash() {
		return document.getFileHash();
	}

	public void setFileHash(String fileHash) {
		document.setFileHash(fileHash);
	}

	public Date getDate() {
		return document.getDate();
	}

	public void setDate(Date date) {
		document.setDate(date);
	}

	@Override
	public String toString() {
		return document.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return ((obj == this) || ((obj instanceof DocumentCslWrapper) && (((DocumentCslWrapper) obj).document.equals(this.document))));
	}
}
