package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;

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
 * @version $Id$
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
	
	private FileLogic fileLogic;
	private RequestLogic requestLogic;

	private Errors errors = null;

	@Override
	public PictureCommand instantiateCommand() {
		return new PictureCommand();
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
	private View downloadPicture(final PictureCommand command) {
		final String requestedUserName = command.getRequestedUser();
		final String loginUserName = command.getContext().getLoginUser().getName();
		
		final File profilePicture = this.fileLogic.getProfilePictureForUser(loginUserName, requestedUserName);
		command.setPathToFile(profilePicture.getAbsolutePath());
		command.setContentType(FileUtil.getContentType(profilePicture.getName()));
		command.setFilename(requestedUserName + ProfilePictureLogic.FILE_EXTENSION);
		return Views.DOWNLOAD_FILE;
	}

	/**
	 * This method manage the picture upload
	 * 
	 * @param command
	 * @return Error view or null if upload successful 
	 */
	private Views uploadPicture(final PictureCommand command) {
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

		final String loginUserName = context.getLoginUser().getName();

		final MultipartFile file = command.getFile();
		if (present(file) && file.getSize() > 0) {
			/*
			 * a file is given --> save it
			 */
			try {
				this.fileLogic.saveProfilePictureForUser(loginUserName, new ServerUploadedFile(file));
			} catch (Exception ex) {
				errors.reject("error.upload.failed", new Object[] { ex.getLocalizedMessage() }, "Sorry, we could not process your upload request, an unknown error occurred.");
				return Views.ERROR;
			}
		} else { 
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
}
