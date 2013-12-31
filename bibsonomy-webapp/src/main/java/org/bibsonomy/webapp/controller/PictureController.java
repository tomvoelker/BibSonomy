package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.picture.GravatarStdPictureHandlerFactory;
import org.bibsonomy.webapp.util.picture.PictureHandler;
import org.bibsonomy.webapp.util.picture.PictureHandlerFactory;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * this controller returns handles picture upload and download
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
	
	private FileLogic fileLogic;
	private RequestLogic requestLogic;
	
	private LogicInterface logic;

	private Errors errors = null;
	
	private final PictureHandlerFactory pictureHandlerFactory;
	
	/**
	 * Creates a new {@code PictureController} instance.
	 */
	public PictureController ()
	{
		//TODO: use bean + getter
		pictureHandlerFactory = new GravatarStdPictureHandlerFactory();
	}
	
	@Override
	public PictureCommand instantiateCommand() {
		PictureCommand command = new PictureCommand();

		return command;
	}

	@Override
	public View workOn(PictureCommand command) {
		final String method = requestLogic.getMethod();
		if ("GET".equals(method)) { 
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
		
		User requestedUser = logic.getUserDetails(requestedUserName);
		
		/*
		 * TODO: we should test if user's profile picture is visible anyway.
		 * Otherwise Gravatar picture mustn't be shown, too.
		 */
		
		PictureHandler handler = pictureHandlerFactory.getPictureHandler( requestedUser, command );
		return handler.getProfilePictureView();
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
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
	
	/**
	 * Sets this controller's DBLogic.
	 * @param dbl
	 */
	public void setLogic ( LogicInterface dbl )
	{
		logic = dbl;
	}
}
