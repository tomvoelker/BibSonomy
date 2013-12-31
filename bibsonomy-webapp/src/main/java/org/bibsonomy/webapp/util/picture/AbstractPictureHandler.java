package org.bibsonomy.webapp.util.picture;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.View;

/**
 * Base class of {@link PictureHandler} implementations.
 * 
 * @author cut
 * @version $Id:$
 * @see PictureHandler
 */
public abstract class AbstractPictureHandler implements PictureHandler
{
	protected final User requestedUser;
	protected final PictureCommand pictureCommand;
	
	/**
	 * Creates a new {@link AbstractPictureHandler} instance with target user and command.
	 * 
	 * @param user - requested user
	 * @param command - actual picture command
	 */
	public AbstractPictureHandler ( User user, PictureCommand command )
	{
		requestedUser = user;
		pictureCommand = command;
	}

	@Override
	public abstract View getProfilePictureView ();
}
