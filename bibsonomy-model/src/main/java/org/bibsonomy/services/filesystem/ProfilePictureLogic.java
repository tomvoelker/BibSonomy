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
	public static final String FILE_EXTENSION = ".jpg";
	
	/** all allowed file extensions for upload */
	public static final Collection<String> PICTURE_EXTENSIONS = Arrays.asList("png", FILE_EXTENSION, "jpeg");
	
	/**
	 * saves a profile picture for the provided username
	 * @param username
	 * @param pictureFile
	 * @throws Exception TODO
	 */
	public void saveProfilePictureForUser(final String username, final UploadedFile pictureFile) throws Exception;
		
	/**
	 * deletes the profile picture of user (identified by username)
	 * @param username
	 */
	public void deleteProfilePictureForUser(final String username);
	
	/**
	 * @param username
	 * @return the profile picture
	 */
	public File getProfilePictureForUser(final String username);
}
