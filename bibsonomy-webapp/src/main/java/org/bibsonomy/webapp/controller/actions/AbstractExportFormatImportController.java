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
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.command.actions.ExportFormatImportCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * controller for storing and deleting export format files
 *
 * @author dzo
 * @author jp
 */
public abstract class AbstractExportFormatImportController extends SettingsPageController implements ValidationAwareController<SettingsViewCommand> {
	private static final Log log = LogFactory.getLog(ImportBookmarksController.class);

	private static final String DELETE = "delete";
	private static final String CREATE = "create";

	private Validator<SettingsViewCommand> validator;
	protected FileLogic fileLogic;

	@Override
	public final View workOn(final SettingsViewCommand command) {
		final ExportFormatImportCommand importCommand = (ExportFormatImportCommand) command;
		final RequestWrapperContext context = importCommand.getContext();

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
			final String hash = importCommand.getHash();
			final String userName = loginUser.getName();

			log.debug("attempting to delete layout " + hash + " for user " + userName);

			final Document document = this.logic.getDocument(userName, hash);

			if (document != null) {
				log.debug("deleting layout " + document.getFileName() + " for user " + userName);

				this.logic.deleteDocument(document, null);

				// unload the export format, …
				this.onExportFormatDelete(userName, hash, document);
			} else {
				errors.reject("error.document_not_found");
			}

		} else if (CREATE.equals(command.getAction())) {
			// validate the command before creating the file(s)
			this.validator.validate(command, errors);

			if (!this.errors.hasErrors()) {
				log.debug("creating layouts for user " + loginUser.getName());
				/*
				 * write the file(s)
				 */
				this.writeExportFiles(loginUser, importCommand);
			}
		}

		final ExtendedRedirectViewWithAttributes extendedRedirectViewWithAttributes = new ExtendedRedirectViewWithAttributes("/settings?selTab=" + String.valueOf(SettingsViewCommand.LAYOUT_IDX));
		extendedRedirectViewWithAttributes.addAttribute(ExtendedRedirectViewWithAttributes.ERRORS_KEY, this.errors);
		return extendedRedirectViewWithAttributes;
	}

	/**
	 * this method is called after the file was deleted from the file system
	 *
	 * @param userName
	 * @param hash
	 * @param document
	 */
	protected abstract void onExportFormatDelete(String userName, String hash, Document document);

	/**
	 * Writes the file of the specified layout part to disk and into the
	 * database.
	 *
	 * @param loginUser
	 * @param command
	 */
	protected abstract void writeExportFiles(final User loginUser, final ExportFormatImportCommand command);

	@Override
	public final SettingsViewCommand instantiateCommand() {
		return new ExportFormatImportCommand();
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(Validator<SettingsViewCommand> validator) {
		this.validator = validator;
	}

	@Override
	public boolean isValidationRequired(SettingsViewCommand command) {
		return false;
	}

	@Override
	public Validator<SettingsViewCommand> getValidator() {
		return this.validator;
	}
}

