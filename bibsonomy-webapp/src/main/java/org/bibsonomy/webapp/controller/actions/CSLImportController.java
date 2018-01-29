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
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.actions.ExportFormatImportCommand;

import org.springframework.web.multipart.MultipartFile;

/**
 * controller for storing and deleting csl layout files
 * - /import/csl
 * @author jp
 */
public class CSLImportController extends AbstractExportFormatImportController {
	private static final Log log = LogFactory.getLog(CSLImportController.class);

	@Override
	protected void onExportFormatDelete(final String userName, final String hash, final Document document) {
		this.fileLogic.deleteCSLLayout(hash);

		/*
		 * delete layout object from exporter
		 */
		this.cslFilesManager.unloadUserLayout(userName, document.getFileName());
	}

	@Override
	protected void writeExportFiles(final User loginUser, final ExportFormatImportCommand command) {
		final MultipartFile fileItem = command.getFileItem();
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
				this.cslFilesManager.reloadLayoutsForUser(loginUser.getName());
			} catch (final Exception ex) {
				errors.reject("settings.csl.error.import", new Object[]{ex.getMessage()}, null);
			}
		}
	}
}
