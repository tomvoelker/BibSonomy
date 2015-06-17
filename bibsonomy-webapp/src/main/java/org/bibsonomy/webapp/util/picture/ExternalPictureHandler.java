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
package org.bibsonomy.webapp.util.picture;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.http.HttpStatus;

/**
 * Base class of {@link PictureHandler} implementations applying external picture services.
 * 
 * <p>By default, user's email address will be hashed to identify 
 * him/her against the picture service.</p>
 * 
 * @author cut
 * @see PictureHandler
 */
public abstract class ExternalPictureHandler implements PictureHandler {
	
	/**
	 * Returns URL to profile picture file.</br>
	 * 
	 * @param requestedUser - the requested user
	 * @param fileExtension - requested file extension as {@code .xxx} or empty string
	 * @return URL to picture file
	 */
	protected abstract URL getPictureURL ( User requestedUser, String fileExtension );

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.picture.PictureHandler#getProfilePictureView(org.bibsonomy.model.User, org.bibsonomy.webapp.command.actions.PictureCommand)
	 */
	@Override
	public View getProfilePictureView(User requestedUser, PictureCommand command) {
		final URL pictureURL = getPictureURL( requestedUser, ".jpg" );
		
		ExtendedRedirectView resultV = new ExtendedRedirectView( (present(pictureURL))? pictureURL.toString() : "" );
		resultV.setContentType( "image/jpg" );
		resultV.setHttp10Compatible(false);
		resultV.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
		return resultV;
	}

}