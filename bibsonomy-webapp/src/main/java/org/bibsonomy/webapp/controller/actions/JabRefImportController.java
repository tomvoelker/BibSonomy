package org.bibsonomy.webapp.controller.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.HandleFileUpload;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.JabRefImportCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author cvo
 * @version $Id$
 */
public class JabRefImportController implements MinimalisticController<JabRefImportCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(ImportController.class);

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	/**
	 * logic database interface
	 */
	private LogicInterface logic = null;
	

	/**
	 * handle file upload
	 */
	private FileUploadInterface uploadFileHandler = null;

	/**
	 * An instance of the (new!) layout renderer. We need it here to unload
	 * custom user layouts.
	 */
	private JabrefLayoutRenderer jabrefLayoutRenderer = null;

	private static final String DELETE = "delete";

	private static final String CREATE = "create";

	@Override
	public View workOn(JabRefImportCommand command) {

		jabrefLayoutRenderer = JabrefLayoutRenderer.getInstance();
		
		User user = command.getContext().getLoginUser();

		if (user != null) {

			// creates a new layout in the database
			if (DELETE.equals(command.getAction())) {

				if (command.getContext().isValidCkey()) {

					final String hash = command.getHash();

					Document document = this.logic.getDocument(user.getName(), hash);

					if (document != null) {
						this.logic.deleteDocument(document, null);

						new File(FileUtil.getDocumentPath(uploadFileHandler.getDocpath(), hash)).delete();
						/*
						 * delete layout object from exporter
						 */
						jabrefLayoutRenderer.unloadUserLayout(user.getName());
					}
				}

				// deletes a layout definition of the user from the database
			} else if (CREATE.equals(command.getAction())) {

				List<Document> documents = new ArrayList<Document>();

				Document buildDocument = null;
				
				String hashedName = null;
				
				if (command.getFileBegin() != null && command.getFileBegin().getSize() > 0) {
					
					List<FileItem> list = Collections.singletonList(command.getFileBegin().getFileItem());
					uploadFileHandler.setUp(list, HandleFileUpload.fileLayoutExt);					
					hashedName = JabrefLayoutUtils.userLayoutHash(user.getName(), LayoutPart.BEGIN);
					
					try {
						buildDocument = uploadFileHandler.writeUploadedFile(hashedName, user);
						documents.add(buildDocument);
					} catch (Exception ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
				if (command.getFileItem() != null && command.getFileItem().getSize() > 0) {
					
					List<FileItem> list = Collections.singletonList(command.getFileItem().getFileItem());
					uploadFileHandler.setUp(list, HandleFileUpload.fileLayoutExt);
					hashedName = JabrefLayoutUtils.userLayoutHash(user.getName(), LayoutPart.ITEM);
					
					try {
						buildDocument = uploadFileHandler.writeUploadedFile(hashedName, user);
						documents.add(buildDocument);
					} catch (Exception ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
				if (command.getFileEnd() != null && command.getFileEnd().getSize() > 0) {

					List<FileItem> list = Collections.singletonList(command.getFileEnd().getFileItem());
					uploadFileHandler.setUp(list, HandleFileUpload.fileLayoutExt);
					hashedName = JabrefLayoutUtils.userLayoutHash(user.getName(), LayoutPart.END);
					
					try {
						buildDocument = uploadFileHandler.writeUploadedFile(hashedName, user);
						documents.add(buildDocument);
					} catch (Exception ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}

				for (Document doc : documents) {

					if (this.logic.createDocument(doc, null) != null) {
						System.out.println("success");
					}
				}
			}
		} else {

			return new ExtendedRedirectView("/login");
		}

		return new ExtendedRedirectView("/settingsnew?selTab=2");
	}

	@Override
	public JabRefImportCommand instantiateCommand() {

		final JabRefImportCommand command = new JabRefImportCommand();

		return command;
	}

	@Override
	public Errors getErrors() {
		// TODO Auto-generated method stub
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {

		this.errors = errors;
	}

	/**
	 * @return the logic object for the database connectivity
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * 
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public FileUploadInterface getUploadFileHandler() {
		return this.uploadFileHandler;
	}

	public void setUploadFileHandler(FileUploadInterface uploadFileHandler) {
		this.uploadFileHandler = uploadFileHandler;
	}
}
