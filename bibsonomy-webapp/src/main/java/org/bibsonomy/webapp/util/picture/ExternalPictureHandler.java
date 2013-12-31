package org.bibsonomy.webapp.util.picture;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

/**
 * Base class of {@link PictureHandler} implementations applying external picture services.
 * 
 * <p>By default, user's email address will be hashed to identify 
 * him/her against the picture service.</p>
 * 
 * @author cut
 * @version $Id:$
 * @see PictureHandler
 */
public abstract class ExternalPictureHandler extends AbstractPictureHandler
{

	/**
	 * Creates a new {@link ExternalPictureHandler} instance with target user and command.
	 * 
	 * @param user - requested user
	 * @param command - actual picture command
	 */
	public ExternalPictureHandler ( User user, PictureCommand command )
	{
		super(user, command);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns URL to profile picture file.</br>
	 * 
	 * @param userAddress - address (e.g. mail) identifiing requested user
	 * @param fileExtension - requested file extension as {@code .xxx} or empty string
	 * @return URL to picture file
	 */
	protected abstract URL getPictureURL ( String userAddress, String fileExtension );

	@Override
	public View getProfilePictureView ()
	{	
		URL pictureURL = getPictureURL( requestedUser.getEmail(), ".jpg" );
		
		ExtendedRedirectView resultV = new ExtendedRedirectView( (present(pictureURL))? pictureURL.toString() : "" );
		resultV.setContentType( "image/jpg" );
		return resultV;
	}

}