package org.bibsonomy.webapp.controller.actions;

import java.io.File;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.FileUploadFactory;
import org.bibsonomy.rest.utils.impl.HandleFileUpload;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.JabRefImportCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author cvo
 * @version $Id$
 */
public class JabRefImportController implements MinimalisticController<JabRefImportCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(ImportBookmarksController.class);

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	/**
	 * logic database interface
	 */
	private LogicInterface logic = null;


	/**
	 * the factory used to get an instance of a FileUploadHandler.
	 */
	private FileUploadFactory uploadFactory;

	/**
	 * An instance of the (new!) layout renderer. We need it here to unload
	 * custom user layouts.
	 */
	private final JabrefLayoutRenderer jabrefLayoutRenderer = JabrefLayoutRenderer.getInstance();

	private static final String DELETE = "delete";

	private static final String CREATE = "create";

	@Override
	public View workOn(JabRefImportCommand command) {

		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			/*
			 * FIXME: send user back to this controller
			 */
			return new ExtendedRedirectView("/login");
		}

		final User loginUser = context.getLoginUser();

		/*
		 * check credentials to fight CSRF attacks 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			/*
			 * FIXME: use new settings page when complete
			 */
			return Views.ERROR;
		}


		/*
		 * delete a layout
		 */
		if (DELETE.equals(command.getAction())) {
			final String hash = command.getHash();
			final String userName = loginUser.getName();
			
			log.debug("attempting to delete layout " + hash + " for user " + userName);
			
			final Document document = this.logic.getDocument(userName, hash);

			if (document != null) {
				log.debug("deleting layout " + document.getFileName() + " for user " + userName);
				
				this.logic.deleteDocument(document, null);

				new File(FileUtil.getDocumentPath(this.uploadFactory.getDocpath(), hash)).delete();
				/*
				 * delete layout object from exporter
				 */
				jabrefLayoutRenderer.unloadUserLayout(userName);
			} else {
				errors.reject("error.document_not_found");
			}

		} else if (CREATE.equals(command.getAction())) {
			log.debug("creating layouts for user " + loginUser.getName());
			/*
			 * .beginLAYOUT
			 */
			writeLayoutPart(loginUser, command.getFileBegin(), LayoutPart.BEGIN);
			/*
			 * .item LAYOUT
			 */
			writeLayoutPart(loginUser, command.getFileItem(), LayoutPart.ITEM);
			/*
			 * .end LAYOUT
			 */
			writeLayoutPart(loginUser, command.getFileEnd(), LayoutPart.END);
		}

		
		if (errors.hasErrors()) {
			/*
			 * FIXME: use new settings page when complete
			 */
			return Views.ERROR;
		}
		
		/*
		 * success
		 */
		return new ExtendedRedirectView("/settings?selTab=2");
	}

	/**
	 * Writes the file of the specified layout part to disk and into the 
	 * database.
	 * 
	 * @param loginUser
	 * @param fileItem
	 * @param layoutPart
	 */
	private void writeLayoutPart(final User loginUser, final CommonsMultipartFile fileItem, final LayoutPart layoutPart) {
		if (fileItem != null && fileItem.getSize() > 0) {
			log.debug("writing layout part " + layoutPart + " with file " + fileItem.getOriginalFilename());
			try {
				final String hashedName = JabrefLayoutUtils.userLayoutHash(loginUser.getName(), layoutPart);				
				
				final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(fileItem.getFileItem()), HandleFileUpload.fileLayoutExt);
				/*
				 * write file to disk
				 */
				final Document uploadedFile = uploadFileHandler.writeUploadedFile(hashedName, loginUser);
				/*
				 * store row in database
				 */
				this.logic.createDocument(uploadedFile, null);
			} catch (Exception ex) {
				log.error("Could not add custom " + layoutPart + " layout.", ex);
				throw new RuntimeException("Could not add custom " + layoutPart + " layout: " + ex.getMessage());
			}
		}
	}

	@Override
	public JabRefImportCommand instantiateCommand() {

		final JabRefImportCommand command = new JabRefImportCommand();

		return command;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {

		this.errors = errors;
	}

	/**
	 * @return the logic object for the database connectivity
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * 
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
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
	public void setUploadFactory(FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}
}
