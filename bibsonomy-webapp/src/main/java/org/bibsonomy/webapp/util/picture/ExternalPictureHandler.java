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
public abstract class ExternalPictureHandler extends PictureHandler {

	/**
	 * default constructor with the user to handle
	 *
	 * @param requestedUser
	 */
	public ExternalPictureHandler(final User requestedUser) {
		super(requestedUser);
	}

	/**
	 * Returns URL to profile picture file.</br>
	 * 
	 * @param requestedUser - the requested user
	 * @param fileExtension - requested file extension as {@code .xxx} or empty string
	 * @return URL to picture file
	 */
	protected abstract URL getPictureURL(final User requestedUser, final String fileExtension);

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.picture.PictureHandler#getProfilePictureView(org.bibsonomy.model.User, org.bibsonomy.webapp.command.actions.PictureCommand)
	 */
	@Override
	public View getProfilePictureView(final PictureCommand command) {
		final URL pictureURL = this.getPictureURL(this.requestedUser, ".jpg");
		
		final ExtendedRedirectView redirectView = new ExtendedRedirectView((present(pictureURL))? pictureURL.toString() : "" );
		redirectView.setContentType( "image/jpg" );
		redirectView.setHttp10Compatible(false);
		redirectView.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
		return redirectView;
	}

}