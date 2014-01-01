package org.bibsonomy.webapp.util.picture;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;

/**
 * A {@link StandardPictureHandlerFactory} implementation using
 * <a href="http://gravatar.com">Gravatar</a> profile picture service.
 * 
 * @author cunis
 * @version $Id:$
 */
public class GravatarStdPictureHandlerFactory extends StandardPictureHandlerFactory
{

	@Override
	public GravatarPictureHandler getExternalHandler ( User user, PictureCommand command )
	{
		return new GravatarPictureHandler( user, command );
	}
}
