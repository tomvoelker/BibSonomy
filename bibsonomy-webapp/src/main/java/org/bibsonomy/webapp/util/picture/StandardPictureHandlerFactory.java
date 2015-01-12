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

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;

/**
 * Base class of {@link PictureHandlerFactory} implementation returning either a 
 * {@link ServerPictureHandler} or any {@link ExternalPictureHandler} implementation.
 * @author cut
 */
public abstract class StandardPictureHandlerFactory implements PictureHandlerFactory
{

	/**
	 * Returns a target {@link ExternalPictureHandler} implementation depending on 
	 * requested user and the actual picture command.
	 * 
	 * @param user - requested user
	 * @param command - the actual picture command
	 * @return target {@link ExternalPictureHandler} implementation
	 */
	public abstract ExternalPictureHandler getExternalHandler ( User user, PictureCommand command );
	
	
	@Override
	public PictureHandler getPictureHandler ( User user, PictureCommand command )
	{
		if ( !user.getUseExternalPicture() )
			return new ServerPictureHandler(user, command);
		
		//else:
		return getExternalHandler( user, command );
	}

}
