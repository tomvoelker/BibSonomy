/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.command.actions.JabRefImportCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.JabRefImportValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller for storing and deleting jabref layout files
 * - /import/jabref
 * 
 * @author cvo
 */
public class JabRefImportController extends SettingsPageController implements ValidationAwareController<SettingsViewCommand> {
	private static final Log log = LogFactory.getLog(ImportBookmarksController.class);
	
	private static final String DELETE = "delete";

	private static final String CREATE = "create";
	
	private JabRefImportValidator validator;
	private FileLogic fileLogic;

	/**
	 * An instance of the (new!) layout renderer. We need it here to unload
	 * custom user layouts.
	 */
	private JabrefLayoutRenderer jabrefLayoutRenderer;

	@Override
	public View workOn(final SettingsViewCommand command) {
		final JabRefImportCommand jabImpCommand = (JabRefImportCommand) command;
		final RequestWrapperContext context = jabImpCommand.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		final User loginUser = context.getLoginUser();

		/*
		 * check credentials to fight CSRF attacks 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.SETTINGSPAGE;
		}

		/*
		 * delete a layout
		 */
		if (DELETE.equals(command.getAction())) {
			final String hash = jabImpCommand.getHash();
			final String userName = loginUser.getName();
			
			log.debug("attempting to delete layout " + hash + " for user " + userName);
			
			final Document document = this.logic.getDocument(userName, hash);

			if (document != null) {
				log.debug("deleting layout " + document.getFileName() + " for user " + userName);
				
				this.logic.deleteDocument(document, null);
				this.fileLogic.deleteJabRefLayout(hash);
				
				/*
				 * delete layout object from exporter
				 */
				this.jabrefLayoutRenderer.unloadUserLayout(userName);
			} else {
				errors.reject("error.document_not_found");
			}

		} else if (CREATE.equals(command.getAction())) {
			this.validator.validate(command, errors);
			if (!this.errors.hasErrors()) {
				log.debug("creating layouts for user " + loginUser.getName());
				/*
				 * .beginLAYOUT
				 */
				writeLayoutPart(loginUser, jabImpCommand.getFileBegin(), LayoutPart.BEGIN);
				/*
				 * .item LAYOUT
				 */
				writeLayoutPart(loginUser, jabImpCommand.getFileItem(), LayoutPart.ITEM);
				/*
				 * .end LAYOUT
				 */
				writeLayoutPart(loginUser, jabImpCommand.getFileEnd(), LayoutPart.END);
			}
		}
		
		/*
		 * Show SettingsView-ImportTab(2)
		 */
		command.setSelTab(Integer.valueOf(2));
		return super.workOn(command);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#getValidator()
	 */
	@Override
	public JabRefImportValidator getValidator() {
		return this.validator;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#isValidationRequired(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public boolean isValidationRequired(final SettingsViewCommand command) {
		return false;
	}
	
	/**
	 * Writes the file of the specified layout part to disk and into the 
	 * database.
	 * 
	 * @param loginUser
	 * @param fileItem
	 * @param layoutPart
	 */
	private void writeLayoutPart(final User loginUser, final MultipartFile fileItem, final LayoutPart layoutPart) {
		if (fileItem != null && fileItem.getSize() > 0) {
			log.debug("writing layout part " + layoutPart + " with file " + fileItem.getOriginalFilename());
			try {
				/*
				 * write file to disk
				 */
				final Document uploadedFile = this.fileLogic.writeJabRefLayout(loginUser.getName(), new ServerUploadedFile(fileItem), layoutPart);
				/*
				 * store document in database
				 */
				this.logic.createDocument(uploadedFile, null);
			} catch (final Exception ex) {
				errors.reject("settings.jabRef.error.import", new Object[]{layoutPart,ex.getMessage()}, null);
			}
		}
	}

	@Override
	public SettingsViewCommand instantiateCommand() {
		return new JabRefImportCommand();
	}
	
	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
	
	/**
	 * @param jabrefLayoutRenderer the jabrefLayoutRenderer to set
	 */
	@Required
	public void setJabrefLayoutRenderer(JabrefLayoutRenderer jabrefLayoutRenderer) {
		this.jabrefLayoutRenderer = jabrefLayoutRenderer;
	}
	
	
	/**
	 * @param validator the validator to set
	 */
	public void setValidator(JabRefImportValidator validator) {
		this.validator = validator;
	}
}
