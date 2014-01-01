package org.bibsonomy.webapp.util.picture;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;

/**
 * Base class of {@link PictureHandlerFactory} implementation returning either a 
 * {@link ServerPictureHandler} or any {@link ExternalPictureHandler} implementation.
 * @author cut
 * @version $Id:$
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
