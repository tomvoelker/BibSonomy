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
/**
 * 
 */
package org.bibsonomy.webapp.util.picture;

import org.bibsonomy.model.User;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.ProfilePictureLogic;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * A {@link PictureHandler} implementation requesting a locally to-server 
 * uploaded profile picture. 
 * 
 * @author cut
 */
public class ServerPictureHandler implements PictureHandler {

	@Override
	public View getProfilePictureView (final User requestedUser, final PictureCommand command) {
		final UploadedFile profilePicture = requestedUser.getProfilePicture();
		
		command.setPathToFile(profilePicture.getAbsolutePath());
		command.setContentType(FileUtil.getContentType(profilePicture.getFileName()));
		command.setFilename(requestedUser.getName() + ProfilePictureLogic.FILE_EXTENSION);
		
		return Views.DOWNLOAD_FILE;
	}
}
