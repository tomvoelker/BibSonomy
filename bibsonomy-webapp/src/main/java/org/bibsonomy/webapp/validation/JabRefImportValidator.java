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
package org.bibsonomy.webapp.validation;

import org.bibsonomy.services.filesystem.JabRefFileLogic;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.command.actions.JabRefImportCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * validator for the JabRef layout files
 */
public class JabRefImportValidator implements Validator<SettingsViewCommand> {
	
	private JabRefFileLogic fileLogic;
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return JabRefImportCommand.class.equals(clazz);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		final SettingsViewCommand command = (SettingsViewCommand) target;
		this.checkFileName(command.getFileBegin(), errors, "Begin");
		this.checkFileName(command.getFileItem(), errors, "Item");
		this.checkFileName(command.getFileEnd(), errors, "End");
	}

	/**
	 * @param fileBegin
	 * @param errors 
	 */
	private void checkFileName(CommonsMultipartFile file, Errors errors, final String fieldSuffix) {
		if (file == null || file.getSize() == 0) {
			return; // not specified
		}
		if (!this.fileLogic.validJabRefLayoutFile(new ServerUploadedFile(file))) {
			final String field = "file" + fieldSuffix;
			errors.rejectValue(field, "settings.jabRef.error.fileextension", new Object[] { StringUtils.implodeStringCollection(this.fileLogic.allowedJabRefFileExtensions(), ", ") }, "Only the specified extensions allowed");
		}
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(JabRefFileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}
