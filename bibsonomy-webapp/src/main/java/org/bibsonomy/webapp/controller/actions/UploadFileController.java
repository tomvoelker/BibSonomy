package org.bibsonomy.webapp.controller.actions;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.upload.FileUploadInterface;
import org.bibsonomy.util.upload.impl.FileUploadFactory;
import org.bibsonomy.webapp.command.actions.UploadFileCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UploadFileValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author daill
 * @version $Id$
 */
public class UploadFileController implements ValidationAwareController<UploadFileCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(UploadFileController.class);

	private Errors errors = null;
	private LogicInterface logic;
	
	
    /**
     * the factory used to get an instance of a FileUploadHandler.
     */
    private FileUploadFactory uploadFactory;

    @Override
	public View workOn(UploadFileCommand command) {
		log.debug("workOn started");
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
			/*
			 * FIXME: correct URL?
			 */
			return Views.ERROR;
		}

		/*
		 * On the first run there can't be a file on the second there has to be
		 * one.
		 */
		if (command.getFile() != null) {
			log.debug("file is available so start the interesting part");

			try {
				
				final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(command.getFile().getFileItem()), FileUploadInterface.fileUploadExt);

				final Document document = uploadFileHandler.writeUploadedFile();
				document.setUserName(context.getLoginUser().getName());
				
				// ... and add it to the db
				logic.createDocument(document, command.getResourceHash());

				/*
				 * finally add the document object to the command object to make
				 * it accessible in the view
				 */
				command.setDoc(document);

			} catch (Exception ex) {
				errors.reject("error.upload.failed", new Object[] { ex.getLocalizedMessage() }, "Sorry, we could not process your upload request, an unknown error occurred.");
				return Views.ERROR;
			}
		}

		return Views.UPLOAD_FILE;
	}

	/**
	 * Get errors
	 */
    @Override
    public Errors getErrors() {
		return errors;
	}

	/**
	 * Set errors
	 */
    @Override
    public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * Instantiate the command
	 */
	@Override
	public UploadFileCommand instantiateCommand() {
		return new UploadFileCommand();
	}

	@Override
	public Validator<UploadFileCommand> getValidator() {
		return new UploadFileValidator();
	}

	@Override
	public boolean isValidationRequired(UploadFileCommand command) {
		return true;
	}

	/**
	 * @return FileUploadFactory
	 */
	public FileUploadFactory getUploadFactory() {
		return this.uploadFactory;
	}

	/**
	 * @param uploadFactory
	 */
	public void setUploadFactory(final FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}
}
