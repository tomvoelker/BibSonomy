package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.file.UploadedFile;
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

	protected static class GravatarHandler
	{
		private final URL gravURI;
		
		protected GravatarHandler ( String gravAddress, String fileExtension )
		{
			gravURI = generateGravURI( gravAddress, "404", fileExtension );
		}
		
		@Deprecated
		protected boolean hasGravatarPicture ()
		{
			if ( gravURI == null )
				return false;
			
			//else:
			HttpURLConnection httpconn = null;
			try {
				URLConnection connection = gravURI.openConnection();
				if ( connection instanceof HttpURLConnection ) //should be the case!
				{
					httpconn = (HttpURLConnection) connection;
					httpconn.connect();
					int responsecode = httpconn.getResponseCode();
					
					//if response is neither 2xx nor 404, connection failed due other reasons than "no picture";
					//act as in case of I/O exception.
					return ( responsecode != 404 );
				}
			}
			catch ( IOException ioe ) {
				//unable to connect:
				//nothing to do, but use Gravatar, however.
				return true;
			}
			finally {
				if ( httpconn != null )
					httpconn.disconnect();
			}
			
			//all else -- even though I'm not aware how this shall ever happen:
			return false;
		}
		
		protected String getURLString ()
		{
			return gravURI.toString();
		}
		
		/**
		 * Generates Gravatar URI for this request's email address.
		 * 
		 * @param address :	Gravatar email address as String
		 * @param defaultBehav : specifies Gravatar behaviour if there is no picture for the address
		 * @param fileExtension : requested file extension or empty String.
		 * @return Gravatar URI as String
		 */
		protected static URL generateGravURI ( String address, String defaultBehav, String fileExtension )
		{
			if ( address == null || address.isEmpty() )
				return null;
			
			//else:
			//hash user's gravatar email, use default-picture "mystery-man", use resolution 128x128;
			try {
				return new URL( String.format("http://www.gravatar.com/avatar/%s%s?d=%s&s=128", hashGravAddress(address), fileExtension, defaultBehav) );
			} 
			catch (MalformedURLException ex) {
				//shouldn't happen!
				return null;
			}
		}
		
		protected static String hashGravAddress ( String address )
		{
			if ( address == null || address.isEmpty() )
			{	
				return "0";
			}
			
			//else:
			String result = DigestUtils.md5Hex( address.trim().toLowerCase() );
			return result;
		}
	}
	
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
	private View downloadPicture(final PictureCommand command) 
	{	
		
		final String requestedUserName = command.getRequestedUser();
		
		User requestedUser = logic.getUserDetails(requestedUserName);
		//use user email as Gravatar address
		String gravAddress = requestedUser.getEmail();
		
		/*
		 * TODO: we should test if user's profile picture is visible anyway.
		 * Otherwise Gravatar picture mustn't be shown, too.
		 */
		boolean useGravatar = requestedUser.getUseExternalPicture();
		
			
		if ( useGravatar ) //&& !hasLocalPic )
		{
			GravatarHandler gravHandler = new GravatarHandler( gravAddress, ".jpg" );
			ExtendedRedirectView resultV = new ExtendedRedirectView( gravHandler.getURLString() );
			resultV.setContentType( "image/jpg" );
			return resultV;
		}
		
		//else:
		UploadedFile profilePicture = requestedUser.getProfilePicture();
		
		command.setPathToFile( profilePicture.getAbsolutePath() );
		command.setContentType( FileUtil.getContentType(profilePicture.getFileName()) );
		command.setFilename( requestedUserName + ProfilePictureLogic.FILE_EXTENSION );
		return Views.DOWNLOAD_FILE;
	}
	

	/**
	 * This method manage the picture upload
	 * 
	 * @param command
	 * @return Error view or null if upload successful
	 * @deprecated now managed by UserUpdateController#workOn
	 */
	@Deprecated
	private Views uploadPicture(final PictureCommand command)
	{
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
		
		final MultipartFile file = command.getFile();
		
		
		if ( present(file) && file.getSize() > 0) {
			/*
			 * a file is given
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
			 * no file given, but POST request --> delete picture
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
