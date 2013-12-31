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
 * @version $Id:$
 */
public class ServerPictureHandler extends AbstractPictureHandler
{

	/**
	 * Creates a new {@link ServerPictureHandler} instance with target user and command.
	 * 
	 * @param user - requested user
	 * @param command - actual picture command
	 */
	public ServerPictureHandler ( User user, PictureCommand command )
	{
		super(user, command);
	}

	@Override
	public View getProfilePictureView ()
	{
		UploadedFile profilePicture = requestedUser.getProfilePicture();
		
		pictureCommand.setPathToFile( profilePicture.getAbsolutePath() );
		pictureCommand.setContentType( FileUtil.getContentType(profilePicture.getFileName()) );
		pictureCommand.setFilename( requestedUser.getName() + ProfilePictureLogic.FILE_EXTENSION );
		
		return Views.DOWNLOAD_FILE;
	}
}
