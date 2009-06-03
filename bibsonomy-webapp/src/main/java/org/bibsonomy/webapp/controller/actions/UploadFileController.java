package org.bibsonomy.webapp.controller.actions;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.file.FileUploadInterface;
import org.bibsonomy.util.file.HandleFileUpload;
import org.bibsonomy.webapp.command.actions.UploadFileCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
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
public class UploadFileController implements MinimalisticController<UploadFileCommand>, ErrorAware, ValidationAwareController<UploadFileCommand> {
	private static final Logger log = Logger.getLogger(UploadFileController.class);
	
	private Errors errors = null;
	private LogicInterface logic;
	private String docpath;

	public View workOn(UploadFileCommand command) {
		log.debug("workOn started");
		final RequestWrapperContext context = command.getContext();
		
		// check if user is logged in, if not throw an error and go directly back to uploadPage
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
		}
		
		log.debug("user is logged in so start working");
		
		if (errors.hasErrors()){
			return Views.ERROR;
		}
		
		/*
		 * On the first run there can't be a file on the second there has to be one.
		 */
		if (command.getFile() != null){
			log.debug("file is available so start the interesting part");
			/*
			 *  create the interface, the item list to provide multiple files in the
			 *  future and the add the fileitem to the list
			 */
			final List<FileItem> list = new LinkedList<FileItem>();
			list.add(command.getFile().getFileItem());

			/*
			 * start try/catch block to catch possible file exceptions
			 */
			try {
				// add the list to the HandleFile-Object
				final FileUploadInterface up = new HandleFileUpload(list, HandleFileUpload.fileUploadExt);
				
				// create a new document object
				final Document doc = new Document();
				
				// fill the document object with all necessary informations
				doc.setUserName(context.getLoginUser().getName());
				doc.setFileName(up.getFileName());
				doc.setFileHash(up.getFileHash());
				doc.setMd5hash(up.getMd5Hash());
				
				// write the document to the drive ...
				up.writeUploadedFiles(docpath);
				
				// ... and add it to the db
				logic.addDocument(doc, command.getResourceHash());
				
				/*
				 * finally add the document object to the command object to 
				 * make it accessible in the view 
				 */
				command.setDoc(doc);
				
			} catch (Exception ex) {
				errors.reject("error.upload.failed", new Object[]{ex.getLocalizedMessage()}, "Sorry, we could not process your upload request, an unknown error occurred.");
				return Views.ERROR;
			}
		}
		
		return Views.UPLOAD_FILE;
	}

	/**
	 * Get errors
	 */
	public Errors getErrors() {
		return errors;
	}

	/**
	 * Set errors
	 */
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic){
		this.logic = logic;
	}
	
	/**
	 * Get the document path out of the property file
	 * 
	 * @param docpath
	 */
	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}

	/**
	 * Instantiate the command
	 */
	public UploadFileCommand instantiateCommand() {	
		return new UploadFileCommand();
	}


	public Validator<UploadFileCommand> getValidator() {
		return new UploadFileValidator();
	}


	public boolean isValidationRequired(UploadFileCommand command) {
		return true;
	}


}
