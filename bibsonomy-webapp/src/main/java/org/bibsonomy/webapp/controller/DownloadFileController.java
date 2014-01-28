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
