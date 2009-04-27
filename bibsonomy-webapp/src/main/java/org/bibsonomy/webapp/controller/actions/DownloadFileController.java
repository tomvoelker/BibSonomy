package org.bibsonomy.webapp.controller.actions;

import java.io.File;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
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
		// TODO Auto-generated method stub
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

		String intrahash = command.getIntrahash();
		String requestedUser = command.getRequestedUser();
		String fileName = command.getFilename();

		if (command.getAction().equals(DOWNLOAD)) {

			Document document = logic.getDocument(requestedUser, intrahash, fileName);

			if (document != null) {

				command.setPathToFile(this.docpath + document.getFileHash().substring(0, 2) + "/" + document.getFileHash());

				command.setContentType(getContentType(document.getFileName()));

			} else {
				// TODO implement adapted error message
				return Views.ERROR;
			}
		} else if (command.getAction().equals(DELETE)) {

			if (command.getContext().isValidCkey()) {

				Document document = logic.getDocument(requestedUser, intrahash, fileName);

				if (document != null) {

					logic.deleteDocument(requestedUser, intrahash, fileName);

					File file = new File(this.docpath + document.getFileHash().substring(0, 2) + "/" + document.getFileHash());

					file.delete();
				}

			} else {

				errors.reject("error.field.valid.ckey");
			}

			if (errors.hasErrors()) {

				return Views.ERROR;
			} else {

				String refererURL = "/bibtex/" + HashID.INTRA_HASH.getId() + intrahash + "/" + requestedUser;

				return new ExtendedRedirectView(refererURL);
			}
		}

		return Views.DOWNLOAD_FILE;
	}

	/**
	 * 
	 * @param errors
	 */
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
		// TODO Auto-generated method stub
		return this.errors;
	}

	/**
	 * Depending on the extension of the file, returns the correct MIME content
	 * type. NOTE: the method looks only at the name of the file not at the
	 * content!
	 * 
	 * @param filename
	 *            - name of the file.
	 * @return - the MIME content type of the file.
	 */
	private String getContentType(String filename) {
		if (StringUtils.matchExtension(filename, "ps")) {
			return "application/postscript";
		} else if (StringUtils.matchExtension(filename, "pdf")) {
			return "application/pdf";
		} else if (StringUtils.matchExtension(filename, "txt")) {
			return "text/plain";
		} else if (StringUtils.matchExtension(filename, "djv", "djvu")) {
			return "image/vnd.djvu";
		} else {
			return "application/octet-stream";
		}
	}
}
