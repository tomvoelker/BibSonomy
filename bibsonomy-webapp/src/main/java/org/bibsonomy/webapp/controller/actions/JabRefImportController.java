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
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.actions.ExportFormatImportCommand;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller for storing and deleting jabref layout files
 * - /import/jabref
 * 
 * @author cvo
 */
public class JabRefImportController extends AbstractExportFormatImportController {
	private static final Log log = LogFactory.getLog(ImportBookmarksController.class);

	/**
	 * An instance of the (new!) layout renderer. We need it here to unload
	 * custom user layouts.
	 */
	private JabrefLayoutRenderer jabrefLayoutRenderer;


	@Override
	protected void onExportFormatDelete(String userName, String hash, Document document) {
		this.fileLogic.deleteJabRefLayout(hash);

		/*
		 * delete layout object from exporter
		 */
		this.jabrefLayoutRenderer.unloadUserLayout(userName);
	}


	@Override
	protected void writeExportFiles(User loginUser, ExportFormatImportCommand command) {
		log.debug("creating layouts for user " + loginUser.getName());
				/*
				 * .beginLAYOUT
				 */
		this.writeLayoutPart(loginUser, command.getFileBegin(), LayoutPart.BEGIN);
				/*
				 * .item LAYOUT
				 */
		this.writeLayoutPart(loginUser, command.getFileItem(), LayoutPart.ITEM);
				/*
				 * .end LAYOUT
				 */
		writeLayoutPart(loginUser, command.getFileEnd(), LayoutPart.END);
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


	/**
	 * @param jabrefLayoutRenderer the jabrefLayoutRenderer to set
	 */
	@Required
	public void setJabrefLayoutRenderer(JabrefLayoutRenderer jabrefLayoutRenderer) {
		this.jabrefLayoutRenderer = jabrefLayoutRenderer;
	}
}
