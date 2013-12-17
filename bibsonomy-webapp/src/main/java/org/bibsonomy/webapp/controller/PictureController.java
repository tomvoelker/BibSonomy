package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.services.filesystem.ProfilePictureLogic;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 * this controller returns handles picture upload and download
 * @author wla
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
	

	@Override
	public PictureCommand instantiateCommand() {
		PictureCommand command = new PictureCommand();

		command.setUser(new User());
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
		} else if ("POST".equals(method)) {
			/*
			 *  picture upload
			 */
			final Views view = uploadPicture(command);
			if (present(view)) {
				return view;
			}
			return new ExtendedRedirectView("/settings");
		} else { 
			return Views.ERROR;
		}

	}

	/**
	 * Returns a view with the requested picture.
	 * 
	 * @param command
	 * @return
	 */
	private View downloadPicture(final PictureCommand command) 
	{	
		/*
		 * Use default whether gravatar shall be preferred.
		 * 
		 * One may wire this statically, or read from user settings, command or whatever.
		 */
		boolean preferGravatar = PREFER_GRAVATAR_DEFAULT;
		
		final String requestedUserName = command.getRequestedUser();
		final String loginUserName = command.getContext().getLoginUser().getName();
		
		User requestedUser = logic.getUserDetails(requestedUserName);
		String gravAddress = requestedUser.getGravatarAddress();
		
		//TODO: determine whether user possesses local profile picture.
			
		if ( preferGravatar && gravAddress != null && !gravAddress.isEmpty() )
		{
			ExtendedRedirectView resultV = new ExtendedRedirectView( generateGravURI(gravAddress, ".jpg") );
			resultV.setContentType( "image/jpg" );
			return resultV;
		}
		
		//else:
		final File profilePicture = this.fileLogic.getProfilePictureForUser(loginUserName, requestedUserName);
		command.setPathToFile(profilePicture.getAbsolutePath());
		command.setContentType(FileUtil.getContentType(profilePicture.getName()));
		command.setFilename(requestedUserName + ProfilePictureLogic.FILE_EXTENSION);
		return Views.DOWNLOAD_FILE;
	}
	
	/**
	 * Generates Gravatar URI for this request's email address.
	 * 
	 * @param address :	Gravatar email address as String
	 * @return Gravatar URI as String
	 */
	protected String generateGravURI ( String address, String fileExtension )
	{
		//hash user's gravatar email, use default-picture "mystery-man", use resolution 128x128;
		return String.format( "http://www.gravatar.com/avatar/%s%s?d=mm&s=128", hashGravAddress(address), fileExtension );
	}
	
	protected String hashGravAddress ( String address )
	{
		if ( address.isEmpty() )
		{	
			return "0";
		}
		
		//else:
		String result = DigestUtils.md5Hex( address.trim().toLowerCase() );
		return result;
	}

	/**
	 * This method manage the picture upload
	 * 
	 * @param command
	 * @return Error view or null if upload successful 
	 */
	private Views uploadPicture(final PictureCommand command)
	{
		/*
		 * Use default whether gravatar shall be preferred.
		 * 
		 * One may wire this statically, or read from user settings, command or whatever.
		 */
		boolean preferGravatar = PREFER_GRAVATAR_DEFAULT;
		
		final RequestWrapperContext context = command.getContext();

		// check if user is logged in, if not throw an error and go directly
		// back to uploadPage
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.ERROR;
		}

		/*
		 * check credentials to fight CSRF attacks 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}

		User loginUser = context.getLoginUser();
		final String loginUserName = loginUser.getName();
		
		User commandUser = command.getUser();
		String gravAddress = commandUser.getGravatarAddress();

		final MultipartFile file = command.getFile();
		boolean useLocalPicture = ( !preferGravatar || gravAddress == null || gravAddress.isEmpty() );
		
		// save Gravatar email address anyway
		try {
			loginUser.setGravatarAddress( gravAddress );
			logic.updateUser(loginUser, UserUpdateOperation.UPDATE_CORE);
		}
		catch ( DatabaseException dbex ) {
			//TODO
			throw dbex;
		}
		
		if ( useLocalPicture && present(file) && file.getSize() > 0) {
			/*
			 * do not prefer Gravatar or no email address given AND a file is given
			 *	--> save it
			 */
			try {
				this.fileLogic.saveProfilePictureForUser(loginUserName, new ServerUploadedFile(file));
			} catch (Exception ex) {
				errors.reject("error.upload.failed", new Object[] { ex.getLocalizedMessage() }, "Sorry, we could not process your upload request, an unknown error occurred.");
				return Views.ERROR;
			}
		} 
		else { 
			/*
			 * prefer gravatar or no file given, but POST request --> delete picture
			 */
			this.fileLogic.deleteProfilePictureForUser(loginUserName);
		}

		return null;
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
