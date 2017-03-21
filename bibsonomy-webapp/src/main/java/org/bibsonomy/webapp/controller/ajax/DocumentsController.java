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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.ajax.AjaxDocumentCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles document upload for publication posts.
 * 
 * FIXME: proper XML escaping missing
 * TODO: instead of xml return json and use the provided generic method to handle
 * errors
 * 
 * @author wla
 */
public class DocumentsController extends AjaxController implements MinimalisticController<AjaxDocumentCommand> {
	private static final Log log = LogFactory.getLog(DocumentsController.class);

	private static final String FORBIDDEN_SYMBOLS = ".*[<>/\\\\].*";

	private MessageSource messageSource;
	private FileLogic fileLogic;

	/**
	 * max file size, currently 50mb
	 */
	private final int maxFileSizeMB = 50;
	private final long maxFileSize = maxFileSizeMB * 1024 * 1024;

	@Override
	public AjaxDocumentCommand instantiateCommand() {
		return new AjaxDocumentCommand();
	}

	@Override
	public View workOn(final AjaxDocumentCommand command) {
		log.debug("workOn started");
		final RequestWrapperContext context = command.getContext();
		final Locale locale = requestLogic.getLocale();
		/*
		 * Check whether user is logged in 
		 */
		if (!context.isUserLoggedIn()) {
			command.setResponseString(getXmlError("error.general.login", null, command.getFileID(), null, locale));
			return Views.AJAX_XML;
		}

		/*
		 * check ckey
		 */
		if (!command.getContext().isValidCkey()) {
			command.setResponseString(getXmlError("error.field.valid.ckey", null, command.getFileID(), null, locale));
			return Views.AJAX_XML;
		}

		/*
		 * check request method, GET is delete file request, POST is upload or rename File request
		 */
		final String method = requestLogic.getMethod();
		final String response;
		
		if ("GET".equals(method)) {
			/*
			 * delete file
			 */
			if (command.isTemp()) {
				response = deleteTempDocument(command);
			} else {
				response = deleteDocument(command, locale);	
			}
		} else if ("POST".equals(method)) {
			if ("rename".equalsIgnoreCase(command.getAction())) {
				/*
				 * rename file
				 */
				response = renameFile(command, locale);
			} else {
				/*
				 * upload file
				 */
				response = uploadFile(command, locale);
			}
		} else {
			return Views.ERROR;
		}
		command.setResponseString(response);
		return Views.AJAX_XML;
	}

	private boolean canEdit(User user, Document document) {
		final String documentOwner = document.getUserName();
		boolean canEdit = user.getName().equals(documentOwner);
		for (Group g: user.getGroups()) {
			canEdit = canEdit ||
					g.getName().equals(documentOwner)
							&& g.getGroupMembershipForUser(user.getName()).getGroupRole().hasRole(GroupRole.ADMINISTRATOR);
		}

		return canEdit;
	}

	/**
	 * renames an existing file in the filesystem and database
	 * @param command
	 * @param locale
	 * @return
	 */
	private String renameFile(AjaxDocumentCommand command, Locale locale) {
		log.debug("start renaming file");
		final User loginUser = command.getContext().getLoginUser();
		final String ownerName = command.getOwnerName();
		final String intraHash = command.getIntraHash();
		final String fileName  = command.getFileName();
		final String newName = command.getNewFileName();
		final Document document = logic.getDocument(ownerName, intraHash, fileName);
		
		/*
		 * unsupported file extensions
		 */
		if (!this.fileLogic.getDocumentExtensionChecker().checkExtension(newName)) {
			return getXmlRenameError("error.upload.failed.filetype", new Object[] { StringUtils.implodeStringCollection(this.fileLogic.getDocumentExtensionChecker().getAllowedExtensions(), ", ")}, command.getFileID(), fileName, locale);	
		}
		
		if (!present(document)) {
			return getXmlRenameError("error.document_not_found", null, command.getFileID(), null, locale);
		}
		
		/*
		 * check if the new name contains forbidden symbols
		 */
		if (Pattern.matches(FORBIDDEN_SYMBOLS, newName)) {
			return getXmlRenameError("error.document_invalid_symbols", null, command.getFileID(), null, locale);
		}
		
		/*
		 * 
		 * check whether logged-in user is the document owner
		 */
		if (!this.canEdit(loginUser, document)) {
			return getXmlRenameError("post.bibtex.wrongUser", null, command.getFileID(), null, locale); 
		}
		document.setFileName(newName);
		/*
		 * rename document in database
		 */
		logic.updateDocument(ownerName, intraHash, fileName, document);
		
		
		final String response = messageSource.getMessage("bibtex.actions.filerenamed", new Object[] {StringEscapeUtils.escapeXml(fileName), StringEscapeUtils.escapeXml(newName)}, locale);
		
		return  "<root><status>renamed</status>" +
				"<response>" + response + "</response>" +
				"<oldName>" + StringEscapeUtils.escapeXml(fileName) + "</oldName>" +
				"<newName>" + StringEscapeUtils.escapeXml(newName) + "</newName>" +
				"</root>";
	}

	/**
	 * This method removes temporary file
	 * @param command
	 * @return status=ok
	 */
	private String deleteTempDocument(AjaxDocumentCommand command) {
		/*
		 * delete temporary saved file
		 */
		this.fileLogic.deleteTempFile(command.getFileHash().substring(32));
		return "<root><status>ok</status><fileid>" + command.getFileID() + "</fileid></root>";
	}

	/**
	 * This method deletes an existing  file from filesystem and database 
	 * @param command
	 * @return
	 */
	private String deleteDocument(final AjaxDocumentCommand command, final Locale locale) {
		log.debug("start deleting file");
		final User loginUser = command.getContext().getLoginUser();
		final String ownerName  = command.getOwnerName();
		final String intraHash = command.getIntraHash();
		final String fileName  = command.getFileName();
		final Document document = logic.getDocument(ownerName, intraHash, fileName);

		if (!present(document)) {
			return getXmlError("error.document_not_found", null, command.getFileID(), null, locale);
		}
		/*
		 * check whether logged-in user is the document owner
		 */
		if (!this.canEdit(loginUser, document)) {
			return getXmlError("post.bibtex.wrongUser", null, command.getFileID(), null, locale); 
		}

		/*
		 * delete entry in database
		 */
		logic.deleteDocument(document, intraHash);

		/*
		 * delete file on disk
		 */
		this.fileLogic.deleteFileForDocument(document.getFileHash());
		final String response = messageSource.getMessage("bibtex.actions.filedeleted", new Object[] {fileName}, locale); 
		return "<root><status>deleted</status><response>" + response + "</response></root>";
	}
	
	

	/**
	 * This method handles the file upload to the server 
	 * @param command
	 * @return
	 */
	private String uploadFile(final AjaxDocumentCommand command, final Locale locale) {
		log.debug("Start uploading file");

		/*
		 * the uploaded file
		 */
		final MultipartFile file = command.getFile();
		final int fileID = command.getFileID();
		/*
		 * unsupported file extensions
		 */
		final String fileName = file.getOriginalFilename();
		if (!this.fileLogic.getDocumentExtensionChecker().checkExtension(fileName)) {
			return getXmlError("error.upload.failed.filetype", new Object[] { StringUtils.implodeStringCollection(this.fileLogic.getDocumentExtensionChecker().getAllowedExtensions(), ", ")}, fileID, fileName, locale);	
		}

		/*
		 * wrong file size
		 */
		// TODO: move file size check to ServerDocumentFileLogic!
		final long size = file.getSize();
		if (size >= this.maxFileSize) {
			return getXmlError("error.upload.failed.size", new Object[] { Integer.valueOf(maxFileSizeMB) }, fileID, fileName, locale);
		} else if (size == 0) {
			return getXmlError("error.upload.failed.size0", null, fileID, fileName, locale);
		}
		final String intraHash = command.getIntraHash();
		final String hash;
		try {
			if (command.isTemp()) { // /editPublication
				final File writtenFile = this.fileLogic.writeTempFile(new ServerUploadedFile(file), this.fileLogic.getDocumentExtensionChecker());
				hash = writtenFile.getName();
			} else { // /bibtex/....
				final Document document = this.fileLogic.saveDocumentFile(command.getOwnerName(), new ServerUploadedFile(file));
				this.logic.createDocument(document, intraHash);
				hash = document.getMd5hash();
			}
		} catch (final Exception ex) {
			log.error("Could not write uploaded file.", ex);
			return getXmlError("error.500", null, fileID, fileName, locale);
		}

		return "<root>" +
		"<status>ok</status>" +
		"<fileid>" + fileID + "</fileid>" +
		"<filehash>" + hash + "</filehash>" +
		"<filename>" + fileName + "</filename>" +
		"<intrahash>" + intraHash + "</intrahash>" +
		"</root>";
	}

	/**
	 * generates AJAX_XML response string with status = error and given reason
	 * @param reason error reason
	 * @return
	 */
	private String getXmlError (final String messageCode, final Object[] arguments, int fileID, final String fileName, Locale locale) {
		final String reason = messageSource.getMessage(messageCode, arguments, locale);
		final String errorMsg = messageSource.getMessage("error.upload.failed", new Object[] {reason}, locale);
		return "<root><status>error</status><reason>" + errorMsg + "</reason><fileid>"+ fileID + "</fileid><filename>" + fileName + "</filename></root>";
	}
	
	/**
	 * generates an AJAX_XML response for failed file renaming with the specified reason
	 * 
	 * @param messageCode the reason to display
	 * @param arguments
	 * @param fileID
	 * @param fileName
	 * @param locale
	 * @return
	 */
	private String getXmlRenameError(final String messageCode, final Object[] arguments, int fileID, final String fileName, Locale locale) {
		final String reason = messageSource.getMessage(messageCode, arguments, locale);
		final String errorMsg = messageSource.getMessage("error.rename.failed", new Object[] {reason}, locale);
		return "<root><status>error</status><reason>" + errorMsg + "</reason><fileid>"+ fileID + "</fileid><filename>" + fileName + "</filename></root>";
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}

}
