package org.bibsonomy.webapp.controller.actions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
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
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.actions.PostMultiplePublicationCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import bibtex.parser.ParseException;

/**
 * @author ema
 * @version $Id$
 */
public class ImportPublicationsController extends SingleResourceListController implements MinimalisticController<PostMultiplePublicationCommand>, ErrorAware {
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

	//@Override
	@Override
	public PostMultiplePublicationCommand instantiateCommand() {
		PostMultiplePublicationCommand command = new PostMultiplePublicationCommand();
		return command;
	}

	public View workOn(PostMultiplePublicationCommand command) {
		log.debug("workOn started");
		
		/*
		 * This variable will hold the information contained in the bibtex/endnote-file or selection field
		 */
		String snippet = null;
		
		/*
		 * Tab 2 (Upload Snippet)
		 */
		if(command.getSelection()!=null) {
			snippet = command.getSelection();
		} else if(command.getFile()!=null) {
			
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
		
		/*
		 * Parse the bibtex snippet	
		 */
		List<Post<BibTex>> bibtex = null;
		try {
			bibtex = new PostBibTeXParser().parseBibTeXPosts(snippet); 
		} catch (ParseException ex) {
			errors.reject("error.upload.failed", "an error occurred during parsing process of your file.");
		} catch (IOException ex) {
			errors.reject("error.upload.failed", "an error occurred during parsing process of your file.");
			ex.printStackTrace();
		}
	
		if(bibtex==null)
		{
			//return to the old view showing the error.
		}
		
		/*
		 * if number of bibtexes contained is one, it can be edited in details, else we can use the 
		 * multi-post-edit view
		 */
		if(bibtex.size()==1){
			command.setPost(bibtex.get(0));
			editPublicationController.setErrors(getErrors()); 
			return editPublicationController.workOn(command);
		} else {
			/*
			 * We have more than one bibtex, which means that this controller will forward to one calling the batcheditbib.jspx
			 */
			User loginUser = command.getContext().getLoginUser();
		
			for(Post<BibTex> bib : bibtex)
				bib.setUser(loginUser);
			
			ListCommand<Post<BibTex>> postListCommand = new ListCommand<Post<BibTex>>(command);
			postListCommand.setList(bibtex);
			
			command.setBibtex(postListCommand);
			
			/*
			 * if the user wants to save all imported entries and edit them afterwards
			 */
			// stores all newly added bookmarks
			final Map<String, String> newBookmarkEntries = new HashMap<String, String>();

			// stores all the updated bookmarks
			final Map<String, String> updatedBookmarkEntries = new HashMap<String, String>();
			
			// stores all the non imported bookmarks
			final List<String> nonCreatedBookmarkEntries = new ArrayList<String>();
			if(!command.isEditBeforeImport())
			{
				savePublicationsForUser(postListCommand, command.isOverwrite(), loginUser, newBookmarkEntries, updatedBookmarkEntries, nonCreatedBookmarkEntries);
			} else { 
			/*
			 * if the user wants to edit the imported entries before saving
			 */
				command.setFormAction(ACTION_SAVE_BEFORE_EDIT);
			}
			return Views.BATCHEDITBIB;
		}
		
		
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
