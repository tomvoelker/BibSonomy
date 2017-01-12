/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.command.actions.CSLImportCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.CslImportValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CSLImportController extends SettingsPageController {
	private static final Log log = LogFactory.getLog(ImportBookmarksController.class);	
	
	private static final String DELETE = "delete";

	private static final String CREATE = "create";
	
	private CSLFilesManager cslFilesManager;
	private CslImportValidator validator;
	private FileLogic fileLogic;
	
	@Override
	public View workOn(final SettingsViewCommand command) {
		final CSLImportCommand CSLImpCom = (CSLImportCommand) command;
		final RequestWrapperContext context = CSLImpCom.getContext();
		
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
			final String hash = CSLImpCom.getHash();
			final String userName = loginUser.getName();
			
			log.debug("attempting to delete layout " + hash + " for user " + userName);
			
			final Document document = this.logic.getDocument(userName, hash);

			if (document != null) {
				log.debug("deleting layout " + document.getFileName() + " for user " + userName);
				
				this.logic.deleteDocument(document, null);
				this.fileLogic.deleteCSLLayout(hash);
				
				/*
				 * delete layout object from exporter
				 */
				this.cslFilesManager.unloadUserLayout(userName);
			} else {
				errors.reject("error.document_not_found");
			}

		} else if (CREATE.equals(command.getAction())) {
			this.validator.validate(command, errors);
			if (!this.errors.hasErrors()) {
				log.debug("creating layouts for user " + loginUser.getName());
				/*
				 * .item LAYOUT
				 */
				writeLayoutPart(loginUser, CSLImpCom.getFileItem());
			}
		}
		
		/*
		 * Show SettingsView-ImportTab(7)
		 */
		command.setSelTab(Integer.valueOf(7));
		return super.workOn(command);
	}
	
	@Override
	public SettingsViewCommand instantiateCommand() {
		return new CSLImportCommand();
	}
	
	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
	
	/**
	 * Writes the file of the specified layout part to disk and into the 
	 * database.
	 * 
	 * @param loginUser
	 * @param fileItem
	 * @param layoutPart
	 */
	private void writeLayoutPart(final User loginUser, final MultipartFile fileItem) {
		if (fileItem != null && fileItem.getSize() > 0) {
			log.debug("writing layout part with file " + fileItem.getOriginalFilename());
			try {
				/*
				 * write file to disk
				 */
				final Document uploadedFile = this.fileLogic.writeCSLLayout(loginUser.getName(), new ServerUploadedFile(fileItem));
				/*
				 * store document in database
				 */
				this.logic.createDocument(uploadedFile, null);
			} catch (final Exception ex) {
				errors.reject("settings.csl.error.import", new Object[]{ex.getMessage()}, null);
			}
		}
	}
	
	/**
	 * @param validator the validator to set
	 */
	public void setValidator(CslImportValidator validator) {
		this.validator = validator;
	}

	/**
	 * @param cslFileManager the cslFileManager to set
	 */
	public void setCslFileManager(CSLFilesManager cslFileManager) {
		this.cslFilesManager = cslFileManager;
	}
}
