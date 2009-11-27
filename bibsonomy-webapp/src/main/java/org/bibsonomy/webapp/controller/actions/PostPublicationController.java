package org.bibsonomy.webapp.controller.actions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.FileUploadFactory;
import org.bibsonomy.rest.utils.impl.HandleFileUpload;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author ema
 * @version $Id$
 */
public class PostPublicationController extends SingleResourceListController implements MinimalisticController<PostPublicationCommand>, ErrorAware {
	private static final Group PUBLIC_GROUP = GroupUtils.getPublicGroup();
	private static final Group PRIVATE_GROUP = GroupUtils.getPrivateGroup();

	private static final Pattern fileEnding = Pattern.compile("\\.([a-zA-Z]+)");
	private static final Log log = LogFactory.getLog(UploadFileController.class);

	//will be filled when i have created the commands.
	public static  final String ACTION_SAVE_BEFORE_EDIT ="";

	private Errors errors = null;

	private EditPublicationController editPublicationController;

	public EditPublicationController getEditPublicationController() {
		return this.editPublicationController;
	}

	public void setEditPublicationController(EditPublicationController editPublicationController) {
		this.editPublicationController = editPublicationController;
	}


	/**
	 * the factory used to get an instance of a FileUploadHandler.
	 */
	private FileUploadFactory uploadFactory;

	public FileUploadFactory getUploadFactory() {
		return this.uploadFactory;
	}

	public void setUploadFactory(FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}

	@Override
	public PostPublicationCommand instantiateCommand() {
		/*
		 * initialize post & resource
		 */
		final PostPublicationCommand command = new PostPublicationCommand();
		command.setGroups(new ArrayList<String>());

		command.setPost(new Post<BibTex>());
		command.setAbstractGrouping(PUBLIC_GROUP.getName());
		command.getPost().setResource(new BibTex());

		return command;
	}

	public View workOn(PostPublicationCommand command) {
		log.debug("workOn started");

		/*
		 * This variable will hold the information contained in the bibtex/endnote-file or selection field
		 */
		String snippet = null;

		/*
		 * Tab 2 (Upload Snippet)
		 */
		if (ValidationUtils.present(command.getSelection())) {
			snippet = command.getSelection();
		} else if(ValidationUtils.present(command.getFile())) {

			/*
			 * Tab 3 (Upload BibTex/Endnote)
			 */
			/*
			 * get temporary file from the command with the factory
			 */
			CommonsMultipartFile uploadedFile = command.getFile();
			final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(uploadedFile.getFileItem()), 
					HandleFileUpload.bibtexEndnoteExt);

			Document uploadedDocument=null;
			BufferedReader reader=null;
			try {
				uploadedDocument = uploadFileHandler.writeUploadedFile();
				reader = new BufferedReader(new FileReader(uploadedDocument.getFile()));
			} catch (FileNotFoundException ex1) {
				errors.reject("error.upload.failed", "an error occurred during accessing your file.");
			} catch (Exception ex1) {
				errors.reject("error.upload.failed", "an error occurred during accessing your file.");
			}

			if(reader==null)
			{
				//return to the old view showing the error.
			}

			/*
			 * extract the file ending
			 */
			String fileSuffix = null;
			String uploadFileName = uploadedDocument.getFileName();

			Matcher patMat = fileEnding.matcher(uploadFileName);
			if(patMat.find())
				fileSuffix = patMat.group(1).toLowerCase(); 

			/*
			 * in case the uploaded file is endnote				
			 */
			if(HandleFileUpload.bibtexEndnoteExt[1].equals(fileSuffix))
			{
				try {
					EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
					reader = (BufferedReader) converter.EndnoteToBibtex(reader);
				} catch (Exception ex) {
					errors.reject("error.upload.failed", "the submitted file does not contain valid endnotes.");
				}
			}

			/*
			 * extract the file contents from the file
			 */

			try {
				snippet = convertFileToString(reader);
			} catch (Exception ex) {
				errors.reject("error.upload.failed", "an error occurred during accessing your file.");
			}

			if(snippet==null)
			{
				//return to the old view showing the error.
			}

			/*
			 * clear temporary file
			 */
			uploadedDocument.getFile().delete();
		} else {
			errors.reject("error.upload.failed", "there was no bibtex or endnote entered.");
			//this way of describing the error includes the bibtex snippet 
			//3 opportunities that are ok: bibtex snippet or file and endnote file
		}

		if (ValidationUtils.present(snippet)) {
			errors.reject("error.upload.failed", "there was no bibtex or endnote entered.");
			//this way of describing the error includes the bibtex snippet 
			//3 opportunities that are ok: bibtex snippet or file and endnote file



		}
		return Views.POST_PUBLICATION;
	}

	private void savePublicationsForUser(ListCommand<Post<BibTex>> postListCommand, boolean isOverwrite, User user, Map<String, String> newBookmarkEntries, Map<String, String> updatedBookmarkEntries, List<String> nonCreatedBookmarkEntries)
	{
		for(Post<BibTex> post : postListCommand.getList())
		{
			final List<?> singletonList = Collections.singletonList(post);
			final String title = post.getResource().getTitle();

			/*
			 * Overwrite the date with the current date, if not posted by the DBLP user.
			 * If DBLP does not provide a date, we have to set the date, too.
			 */
			if (!UserUtils.isDBLPUser(user.getName()) || post.getDate() == null) {
				/*
				 * update date TODO: don't we want to keep the posting date unchanged
				 * and only update the date? --> actually, this does currently not work,
				 * since the DBLogic doesn't set the date and thus we get a NPE from the
				 * database
				 */
				post.setDate(new Date());	
			}

			try {
				// throws an exception if the bookmark already exists in the
				// system
				final List<String> createdPostHash = logic.createPosts((List<Post<?>>) singletonList);
				newBookmarkEntries.put(createdPostHash.get(0), title);
			} catch (IllegalArgumentException e) {
				// checks whether the update bookmarks checkbox is checked
				if (isOverwrite) {

					final List<String> createdPostHash = logic.updatePosts((List<Post<?>>) singletonList, PostUpdateOperation.UPDATE_ALL);
					updatedBookmarkEntries.put(createdPostHash.get(0), title);
				} else {
					nonCreatedBookmarkEntries.add(title);
				}
			}
		}
	}

	/**
	 * FIXME: 
	 * Such a method should be put into FileUtils (maybe it already exists there) 
	 * 
	 * @param reader
	 * @return
	 * @throws Exception
	 */
	private String convertFileToString(BufferedReader reader) throws Exception
	{
		StringBuffer snippet = new StringBuffer(1000);


		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			snippet.append(readData);
			buf = new char[1024];
		}
		reader.close();



		return new String(snippet);

	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
