package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.UrlUtils;
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

	private final static String ACTION_DELETE = "delete";

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

		final Document document = logic.getDocument(requestedUser, intrahash, command.getFilename());

		if (!present(document)) {
			errors.reject("error.document_not_found");
			return Views.ERROR;
		}

		if (ACTION_DELETE.equals(command.getAction())) {
			/*
			 * handle document deletion
			 */
			if (!command.getContext().isValidCkey()) {
				errors.reject("error.field.valid.ckey");
				return Views.ERROR;
			}
			/*
			 * delete entry in database
			 */
			logic.deleteDocument(document, intrahash);
			/*
			 * delete file on disk
			 */
			new File(FileUtil.getDocumentPath(docpath, document.getFileHash())).delete();
			/*
			 * return to bibtex details page
			 */
			return new ExtendedRedirectView(("/bibtex/" + HashID.INTRA_HASH.getId() + intrahash + "/" + UrlUtils.safeURIEncode(requestedUser)));
		} 

		/*
		 * default: handle document download
		 */
		command.setPathToFile(FileUtil.getDocumentPath(docpath, document.getFileHash()));
		command.setContentType(FileUtil.getContentType(document.getFileName()));
		/*
		 * stream document to user
		 */
		return Views.DOWNLOAD_FILE;

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
