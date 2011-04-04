package org.bibsonomy.webapp.controller.ajax;

import java.io.File;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Document;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.util.upload.FileUploadInterface;
import org.bibsonomy.webapp.command.ajax.AjaxDocumentCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;

/**
 * @author wla
 * @version $Id$
 */
public class DocumentsController extends AjaxController implements MinimalisticController<AjaxDocumentCommand> {
	
	/**
	 * Path to the documents folder
	 */
	private String docPath;
	private String tempPath;
	private String fileHash;
	private static final Log log = LogFactory.getLog(DocumentsController.class);
	private MessageSource messageSource;
	
	
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
	public View workOn(AjaxDocumentCommand command) {
		log.debug("workOn started");
		final RequestWrapperContext context = command.getContext();
		Locale locale = requestLogic.getLocale();
		/*
		 * Check whether user is logged in 
		 */
		if (!context.isUserLoggedIn()) {
			command.setResponseString(generateXmlErrorString(messageSource.getMessage("error.general.login", null, locale), command.getFileID(), null, locale));
			return Views.AJAX_XML;
		}
		
		/*
		 * check ckey
		 */
		if (!command.getContext().isValidCkey()) {
			command.setResponseString(generateXmlErrorString(messageSource.getMessage("error.field.valid.ckey", null, locale), command.getFileID(), null, locale));
			return Views.AJAX_XML;
		}
		
		/*
		 * check request method, GET is delete file request, POST is upload File request
		 */
		final String method = requestLogic.getMethod();
		if ("GET".equals(method)) {
			/*
			 * delete file
			 */
			if (command.isTemp()) {
				return deleteTempDocument(command);
			}
			return deleteDocument(command, locale);
		} else if ("POST".equals(method)) {
			/*
			 * upload file
			 */
			return uploadFile(command, locale);
		} else {
			return Views.ERROR;
		}

	}
	
	/**
	 * This method removes temporary file
	 * @param command
	 * @return status=ok
	 */
	private View deleteTempDocument(AjaxDocumentCommand command) {
		/*
		 * temporary saved file
		 */
		File tmpFile = new File(tempPath+command.getFileHash().substring(32));
		tmpFile.delete();
		command.setResponseString("<root><status>ok</status><fileid>" + command.getFileID() + "</fileid></root>");
		return Views.AJAX_XML;
	}
	
	/**
	 * This method deletes an existing  file from filesystem and database 
	 * @param command
	 * @return
	 */
	private View deleteDocument(AjaxDocumentCommand command, Locale locale) {
		log.debug("start deleting file");
		String userName = command.getContext().getLoginUser().getName();
		String intraHash = command.getIntraHash();
		String fileName = command.getFileName();
		Document document = logic.getDocument(userName, intraHash, fileName);
		
		/*
		 * check whether logged-in user is the document owner
		 */
		String documentOwner = document.getUserName();
		if (!documentOwner.equals(userName)) {
			
			command.setResponseString(generateXmlErrorString(messageSource.getMessage("post.bibtex.wrongUser", null, locale), command.getFileID(), null, locale)); 
			return Views.AJAX_XML;
		}
		
		/*
		 * delete entry in database
		 */
		logic.deleteDocument(document, intraHash);
		
		/*
		 * delete file on disk
		 */
		new File(FileUtil.getFilePath(docPath, document.getFileHash())).delete();
		String response = messageSource.getMessage("bibtex.actions.filedeleted", new Object[] {fileName}, locale); 
		command.setResponseString("<root><status>deleted</status><response>" + response + "</response></root>");
		return Views.AJAX_XML;
	}
	
	/**
	 * This method handles file upload to a temporary directory 
	 * @param command
	 * @return
	 */
	private View uploadFile(AjaxDocumentCommand command, Locale locale) {
		log.debug("Start uploading file");
		
		/*
		 * unsupported file extensions
		 */
		if (!StringUtils.matchExtension(command.getFile().getFileItem().getName(), FileUploadInterface.fileUploadExt)) {
		
			String[] allowedExt = FileUploadInterface.fileUploadExt;
			
			final StringBuilder buf = new StringBuilder();
			for (int i = 0; i < allowedExt.length - 1; i++) {
				buf.append(allowedExt[i].toUpperCase() + ", ");
			}
			
			if (allowedExt.length > 1) {
				buf.append(messageSource.getMessage("logic.or", null, locale)+" ");
			}
			buf.append(allowedExt[allowedExt.length - 1].toUpperCase());
			
			command.setResponseString(generateXmlErrorString(messageSource.getMessage("error.upload.failed.filetype", new Object[] {buf.toString()}, locale), command.getFileID(), command.getFile().getFileItem().getName(), locale));	
			
			return Views.AJAX_XML;
		}
		
		/*
		 * wrong file size
		 */
		long size = command.getFile().getFileItem().getSize();
		if (size >= maxFileSize) {
			String errorMsg = messageSource.getMessage("error.upload.failed.size", new Object[] {maxFileSizeMB}, locale);
			command.setResponseString(generateXmlErrorString(errorMsg, command.getFileID(), command.getFile().getFileItem().getName(), locale));
			return Views.AJAX_XML;
		} else if (size == 0) {
			command.setResponseString(generateXmlErrorString(messageSource.getMessage("error.upload.failed.size0", null, locale), command.getFileID(), command.getFile().getFileItem().getName(), locale));
			return Views.AJAX_XML;
		}
		
		File uploadFile;
		fileHash = FileUtil.getRandomFileHash(command.getFile().getFileItem().getName());
		if (command.isTemp()) { // /editPublication
			uploadFile = new File(tempPath + fileHash);
		} else { // /bibtex/....
			uploadFile = new File(FileUtil.getFilePath(docPath, fileHash));
		}
		String md5Hash = HashUtils.getMD5Hash(command.getFile().getFileItem().get());
		try {
			command.getFile().getFileItem().write(uploadFile);
		} catch (Exception ex) {
			log.error("Could not write uploaded file.", ex);
			command.setResponseString(generateXmlErrorString(messageSource.getMessage("error.500", null, locale), command.getFileID(), null, locale));
			return Views.AJAX_XML;
		}
		
		if (!command.isTemp()) {
			Document document = new Document();
			document.setFileName(command.getFile().getFileItem().getName());
			document.setFileHash(fileHash);
			document.setMd5hash(md5Hash);
			document.setUserName(command.getContext().getLoginUser().getName());
			
			/*
			 * add document to the data base
			 */
			logic.createDocument(document, command.getIntraHash());
			
			/*
			 * clear fileHash (randomFileName), so only md5Hash over the file content will be sent back
			 */
			fileHash ="";
		}
		
		command.setResponseString("<root><status>ok</status><fileid>" + command.getFileID() + "</fileid><filehash>" + md5Hash + fileHash + "</filehash><filename>" + command.getFile().getFileItem().getName() + "</filename></root>");
		return Views.AJAX_XML;
	}

	/**
	 * generates AJAX_XML response string with status = error and given reason
	 * @param reason error reason
	 * @return
	 */
	private String generateXmlErrorString (String reason, int fileID, String fileName, Locale locale) {
		String errorMsg = messageSource.getMessage("error.upload.failed", new Object[] {reason}, locale);
		return "<root><status>error</status><reason>"+errorMsg+"</reason><fileid>"+ fileID + "</fileid><filename>" + fileName + "</filename></root>";
	}
	
	/**
	 * @param docPath
	 *            path to the documents folder to set
	 */
	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

	/**
	 * @return the docPath
	 */
	public String getDocPath() {
		return docPath;
	}

	/**
	 * @param tempPath
	 *            the tempPath to set
	 */
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	/**
	 * @return the tempPath
	 */
	public String getTempPath() {
		return tempPath;
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
