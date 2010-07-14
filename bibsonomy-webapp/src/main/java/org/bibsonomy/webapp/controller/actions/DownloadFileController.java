package org.bibsonomy.webapp.controller.actions;

import java.io.File;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.DownloadFileCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
 */
public class DownloadFileController implements MinimalisticController<DownloadFileCommand>, ErrorAware {

	private final static String DOWNLOAD = "download";

	private final static String DELETE = "delete";

	/**
	 * logical interface to BibSonomy's core functionality
	 */
	private LogicInterface logic = null;

	/**
	 * document path
	 */
	private String docpath = null;

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	@Override
	public DownloadFileCommand instantiateCommand() {
		return new DownloadFileCommand();
	}

	@Override
	public View workOn(DownloadFileCommand command) {

		final RequestWrapperContext context = command.getContext();

		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
		}

		if (errors.hasErrors()) {
			return Views.ERROR;
		}

		final String intrahash     = command.getIntrahash();
		final String requestedUser = command.getRequestedUser();
		final String fileName      = command.getFilename();

		if (command.getAction().equals(DOWNLOAD)) {
			/*
			 * handle document download
			 */
			final Document document = logic.getDocument(requestedUser, intrahash, fileName);

			if (document != null) {
				command.setPathToFile(FileUtil.getDocumentPath(docpath, document.getFileHash()));
				command.setContentType(FileUtil.getContentType(document.getFileName()));
				/*
				 * stream document to user
				 */
				return Views.DOWNLOAD_FILE;
			} 
			
			errors.reject("error.document_not_found");

		} else if (command.getAction().equals(DELETE)) {
			/*
			 * handle document deletion
			 */
			if (command.getContext().isValidCkey()) {
				final Document document = logic.getDocument(requestedUser, intrahash, fileName);

				if (document != null) {

					/*
					 * delete entry in database
					 */
					logic.deleteDocument(document, intrahash);
					new File(FileUtil.getDocumentPath(docpath, document.getFileHash())).delete();
					/*
					 * return to bibtex details page
					 * FIXME: properly encode user name and intrahash
					 */
					return new ExtendedRedirectView(("/bibtex/" + HashID.INTRA_HASH.getId() + intrahash + "/" + requestedUser));
				}
				
				errors.reject("error.document_not_found");

			} else {
				errors.reject("error.field.valid.ckey");
			}
		}

		/*
		 * if we arrive here, there MUST be some errors ...
		 */
		return Views.ERROR;
	}


	/**
	 * 
	 * @param errors
	 */
	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param docpath
	 */
	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

}
