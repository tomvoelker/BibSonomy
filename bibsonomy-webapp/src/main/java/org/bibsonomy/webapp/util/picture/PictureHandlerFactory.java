package org.bibsonomy.webapp.util.picture;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;

/**
 * Interface {@code PictureHandlerFactory} handles requests for a target
 * {@link PictureHandler} implementation.
 * 
 * @author cut
 */
public interface PictureHandlerFactory
{
	
	/**
	 * Returns a target {@link PictureHandler} implementation depending on requested user
	 * and the actual picture command.
	 * 
	 * @param user - requested user
	 * @param command - the actual picture command
	 * @return target {@link PictureHandler} implementation
	 */
	public PictureHandler getPictureHandler ( User user, PictureCommand command );
}
