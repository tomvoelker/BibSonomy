package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.picture.PictureHandler;
import org.bibsonomy.webapp.util.picture.PictureHandlerFactory;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * this controller handles picture download
 * @author wla, cut
 */
public class PictureController implements MinimalisticController<PictureCommand>, ErrorAware, RequestAware {

	static {
		/*
		 * set the headless mode for awt library
		 * FIXME does it work? Should we really do this here? Better in a 
		 * Tomcat config file, right?!
		 */
		System.setProperty("java.awt.headless", "true");
	}
	
	/**
	 * This is the default to state whether a Gravatar profile picture shall be used preferred to any locally uploaded file.
	 */
	protected static boolean PREFER_GRAVATAR_DEFAULT = true;
	
	private RequestLogic requestLogic;
	
	private LogicInterface logic;

	private Errors errors = null;
	
	private PictureHandlerFactory pictureHandlerFactory;
	
	/**
	 * Creates a new {@code PictureController} instance.
	 */
	public PictureController ()
	{
		//nothing to do
	}
	
	@Override
	public PictureCommand instantiateCommand() {
		return new PictureCommand();
	}

	@Override
	public View workOn(final PictureCommand command) {
		final String method = requestLogic.getMethod();
		
		if ( command.getRequestedUser() != null && "GET".equals(method) )
		{ 
			/*
			 * picture download
			 */
			return downloadPicture(command);
		}
		
		//else:
		return Views.ERROR;
	}

	/**
	 * Returns a view with the requested picture.
	 * 
	 * @param command
	 * @return
	 */
	private View downloadPicture ( final PictureCommand command ) 
	{	
		
		final String requestedUserName = command.getRequestedUser();
		
		final User requestedUser = logic.getUserDetails(requestedUserName);

		PictureHandler handler;
		
		// test if user's profile picture is visible
		if ( isPictureVisible(requestedUser, command.getLoginUser()) )
			handler = pictureHandlerFactory.getPictureHandler( requestedUser, command );
		else
		{
			//elsewise handle request like a request for default user
			final User user = logic.getUserDetails( "" );
			handler = pictureHandlerFactory.getPictureHandler( user, command );
		}
		return handler.getProfilePictureView();
	}
	
	/**
	 * Checks if the loginUser may see the profile picture of the requested user.
	 * 
	 * @param requestedUser
	 * @param loginUserName
	 * @return true if and only if the user logged in may see the picture of the user requested
	 */
	private boolean isPictureVisible ( final User requestedUser, final User loginUser )
	{
		final String requestedUserName = requestedUser.getName();
		final String loginUserName = loginUser.getName();
		
		/*
		 * login user may always see his/her photo
		 */
		if ( present(loginUserName) && loginUserName.equals(requestedUserName) ) 
			return true;
		
		/*
		 * Check the visibility depending on the profile privacy level.
		 */
		final ProfilePrivlevel visibility = requestedUser.getSettings().getProfilePrivlevel();
		switch(visibility) {
		case PUBLIC:
			return true;
		case FRIENDS:
			if (present(loginUserName)) //TODO: why shouldn't it?!
			{
				final List<User> friends = logic.getUserRelationship(requestedUserName, UserRelation.OF_FRIEND, null);
				for ( final User friend : friends )
				{
					if ( loginUserName.equals(friend.getName()) )
						return true;
				}
			}
			//all else:
			//$FALL-THROUGH$
		case PRIVATE:
			//only the requested user her-/hisself may see her/his profile picture;
			//we already tested above if login equals requested user! (nothing to do)
			//$FALL-THROUGH$
		default:
			return false;
		}
	}
	

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param requestLogic the requestLogic to set
	 */
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * Sets this controller's DBLogic.
	 * @param dbl
	 */
	public void setLogic ( final LogicInterface dbl )
	{
		logic = dbl;
	}
	
	/**
	 * Sets this controller's {@link PictureHandlerFactory} instance.
	 * @param factory
	 */
	public void setPictureHandlerFactory ( final PictureHandlerFactory factory )
	{
		pictureHandlerFactory = factory;
	}
}
