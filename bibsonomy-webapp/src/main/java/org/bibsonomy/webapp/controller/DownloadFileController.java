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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.QRCodeRenderer;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.DownloadFileCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * controller for viewing uploaded documents
 *   - /documents/INTRAHASH/USERNAME/FILENAME
 * 
 * @author cvo
 */
public class DownloadFileController implements MinimalisticController<DownloadFileCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(DownloadFileController.class);
	
	/**
	 * logical interface to BibSonomy's core functionality
	 */
	private LogicInterface logic = null;
	private FileLogic fileLogic;
	
	/**
	 * qr code renderer
	 */
	private QRCodeRenderer qrCodeRenderer;

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	@Override
	public DownloadFileCommand instantiateCommand() {
		return new DownloadFileCommand();
	}

	@Override
	public View workOn(final DownloadFileCommand command) {
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		final String intrahash = command.getIntrahash();
		final String requestedUser = command.getRequestedUser();

		final Document document = logic.getDocument(requestedUser, intrahash, command.getFilename());

		if (!present(document)) {
			this.errors.reject("error.document_not_found");
			return Views.ERROR;
		}

		/*
		 * default: handle document download
		 */
		final PreviewSize preview = command.getPreview();
		if (present(preview)) {
			final File previewFile = this.fileLogic.getPreviewFile(document, preview);
			command.setPathToFile(previewFile.getAbsolutePath());
			/*
			 * preview images are always JPEGs!
			 */
			command.setContentType(FileUtil.CONTENT_TYPE_IMAGE_JPEG);
			command.setFilename(command.getFilename() + "." + FileUtil.CONTENT_TYPE_IMAGE_JPEG);
			
		} else {
			
			final File file = fileLogic.getFileForDocument(document);
			final String filePath = file.getAbsolutePath();
			
			/*
			 * TODO: move to document logic?
			 * check if document has property qrcode
			 */
			if (command.isQrcode()) {
				
				final String qrFilePath;
				
				/*
				 * try to embed qrcode
				 */
				try {
					qrFilePath = qrCodeRenderer.manipulate(filePath, command.getRequestedUser(), command.getIntrahash());
				} catch (final Exception e) {
					log.error("Error rendering QR-code in document", e);
					errors.reject("error.document_not_converted");
					return Views.ERROR;
				}
				
				/*
				 * set path to manipulated file.
				 */
				command.setPathToFile(qrFilePath);
				
			} else {
				command.setPathToFile(filePath);
			}
			
			command.setContentType(FileUtil.getContentType(document.getFileName()));
		}
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
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	/**
	 * @param qrCodeRenderer the qrCodeRenderer to set
	 */
	public void setQrCodeRenderer(QRCodeRenderer qrCodeRenderer) {
		this.qrCodeRenderer = qrCodeRenderer;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}
