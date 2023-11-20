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
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ice
 */
@Getter
@Setter
public class PictureCommand extends BaseCommand implements Serializable, DownloadCommand {

	private static final long serialVersionUID = -3444057502420374593L;
	
	private String requestedUser;
	
	private String filename;
	
	private String pathToFile;
	
	private String contentType;
	
	private MultipartFile file;
	
	private boolean delete;

	/** user's Gravatar email address to be set. */
	private String gravatarAddress;

	/**
	 * @return the filename
	 */
	@Override
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the pathToFile
	 */
	@Override
	public String getPathToFile() {
		return pathToFile;
	}

	/**
	 * @return the contentType
	 */
	@Override
	public String getContentType() {
		return contentType;
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
