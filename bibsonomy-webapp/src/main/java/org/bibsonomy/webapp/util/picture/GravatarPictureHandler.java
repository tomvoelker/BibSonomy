/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.model.User;
import org.bibsonomy.util.StringUtils;

/**
 * A {@link PictureHandler} implementation requesting user's <a href="http://gravatar.com">Gravatar</a> profile picture.
 * 
 * @author cut
 * @see PictureHandler
 * @see ExternalPictureHandler
 * @see <a href="http://gravatar.com/site/implement/">http://gravatar.com/site/implement/</a>
 */
public class GravatarPictureHandler extends ExternalPictureHandler {

	/**
	 * The Gravatar request url, where
	 * <ul>
	 * 	<li>first variable is user's hash;</li>
	 * 	<li>second picture file extension;</li>
	 * 	<li>third default behaviour.</li>
	 * </ul>
	 * See also {@link <a href="http://gravatar.com/site/implement/images/">http://gravatar.com/site/implement/images/</a>}
	 */
	protected final static String GRAVATAR_REQ_URL 
										= "http://www.gravatar.com/avatar/%s%s?d=%s&s=128";
	
	/**
	 * Gravatar request url actual parameter indicating the behaviour, if there isn't any picture file uploaded.</br>
	 * 
	 * <p>
	 * 	See also {@link <a href="http://de.gravatar.com/site/implement/images/">http://de.gravatar.com/site/implement/images/</a>}
	 * </p>
	 */
	protected final static String DEFAULT_BEHAVIOUR = "mm";
	
	
	/**
	 * Generates Gravatar URI for this request's email address.
	 * 
	 * @param requesteUser :	the requested user
	 * @param defaultBehav : specifies Gravatar behaviour if there is no picture for the address
	 * @param fileExtension : requested file extension or empty String.
	 * @return Gravatar URI as String
	 */
	@Override
	protected URL getPictureURL(User requestedUser, String fileExtension) {
		// hash user's gravatar email, use default-picture "mystery-man", use resolution 128x128;
		try {
			return new URL(String.format(GRAVATAR_REQ_URL, StringUtils.getMD5Hash(requestedUser.getEmail()), fileExtension, DEFAULT_BEHAVIOUR));
		} catch (MalformedURLException ex) {
			//shouldn't happen!
			return null;
		}
	}

}