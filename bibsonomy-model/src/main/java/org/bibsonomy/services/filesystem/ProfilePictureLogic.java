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
package org.bibsonomy.services.filesystem;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.bibsonomy.model.util.file.UploadedFile;

/**
 * @author dzo
 */
public interface ProfilePictureLogic {
	
	/** the profile picture file extension */
	String FILE_EXTENSION = ".jpg";
	
	/** all allowed file extensions for upload */
	Collection<String> PICTURE_EXTENSIONS = Arrays.asList("png", FILE_EXTENSION, "jpeg");
	
	/**
	 * saves a profile picture for the provided username
	 * @param username
	 * @param pictureFile
	 * @throws Exception TODO
	 */
	void saveProfilePictureForUser(final String username, final UploadedFile pictureFile) throws Exception;
		
	/**
	 * deletes the profile picture of user (identified by username)
	 * @param username
	 */
	void deleteProfilePictureForUser(final String username);
	
	/**
	 * @param username
	 * @return the profile picture
	 */
	File getProfilePictureForUser(final String username);

	/**
	 * @param username
	 * @return <code>true</code> iff the specified user has a profile picture that is visible to the logged in user
	 */
	boolean hasProfilePicture(final String username);
}
