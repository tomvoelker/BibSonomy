/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command class for the download or deleted File operation
 * @author cvo
 */
@Getter
@Setter
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
	 * @return path to the file
	 */
	@Override
	public String getPathToFile() {
		return this.pathToFile;
	}

	/**
	 * 
	 * @return filename of the requested file
	 */
	@Override
	public String getFilename() {
		return this.filename;
	}

}
