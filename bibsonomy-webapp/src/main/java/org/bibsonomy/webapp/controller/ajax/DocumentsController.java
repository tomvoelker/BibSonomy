package org.bibsonomy.webapp.controller.ajax;

import java.io.File;
import java.util.ResourceBundle;

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
	
	ResourceBundle localizedStrings = ResourceBundle.getBundle("messages");
	
	@Override
	public AjaxDocumentCommand instantiateCommand() {
		return new AjaxDocumentCommand();
	}

	@Override
	public View workOn(AjaxDocumentCommand command) {
		log.debug("workOn started");
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * Check whether user is logged in 
		 */
		if (!context.isUserLoggedIn()) {
			command.setResponseString(generateXmlErrorString(localizedStrings.getString("error.general.login"), command.getFileID()));
			return Views.AJAX_XML;
		}
		
		/*
		 * check ckey
		 */
		if (!command.getContext().isValidCkey()) {
			command.setResponseString(generateXmlErrorString(localizedStrings.getString("error.field.valid.ckey"), command.getFileID()));
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
			return deleteDocument(command);
		} else if ("POST".equals(method)) {
			/*
			 * upload file
			 */
			return uploadFile(command);
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
	private View deleteDocument(AjaxDocumentCommand command) {
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
			
			command.setResponseString(generateXmlErrorString(localizedStrings.getString("post.bibtex.wrongUser"), command.getFileID())); 
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
		String response = localizedStrings.getString("bibtex.actions.filedeleted").replace("{0}", fileName);
		command.setResponseString("<root><status>deleted</status><response>" + response + "</response></root>");
		return Views.AJAX_XML;
	}
	
	/**
	 * This method handles file upload to a temporary directory 
	 * @param command
	 * @return
	 */
	private View uploadFile(AjaxDocumentCommand command) {
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
				buf.append(localizedStrings.getString("logic.or")+" ");
			}
			buf.append(allowedExt[allowedExt.length - 1].toUpperCase());
			
			command.setResponseString(generateXmlErrorString(localizedStrings.getString("error.upload.failed.filetype").replace("{0}", buf.toString()), command.getFileID()));
				
			
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
			command.setResponseString(generateXmlErrorString(localizedStrings.getString("error.500"), command.getFileID()));
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
	private String generateXmlErrorString (String reason, int fileID) {
		String errorMsg = localizedStrings.getString("error.upload.failed").replace("{0}", reason);
		return "<root><status>error</status><reason>"+errorMsg+"</reason><fileid>"+ fileID + "</fileid></root>";
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

}
