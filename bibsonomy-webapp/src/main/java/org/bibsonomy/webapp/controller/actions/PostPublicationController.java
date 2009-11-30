package org.bibsonomy.webapp.controller.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.recommender.tags.database.RecommenderStatisticsManager;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.FileUploadFactory;
import org.bibsonomy.rest.utils.impl.HandleFileUpload;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.EditPostValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import bibtex.parser.ParseException;

/**
 * @author ema
 * @version $Id$
 */
public class PostPublicationController extends EditPostController<BibTex,PostPublicationCommand> implements MinimalisticController<PostPublicationCommand>, ErrorAware {
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
		
		/*
		 * default controller behaviour
		 */
		if(!ValidationUtils.present(command.getTaskName()))
			command.setTaskName(PostPublicationCommand.TASK_ENTER_PUBLICATIONS);

		return command;
	}

	//@Override
	@Override
	public View workOn(PostPublicationCommand command) {
		log.debug("workOn started");
		if(PostPublicationCommand.TASK_ENTER_PUBLICATIONS.equals(command.getTaskName()))
			return ShowEnterPublicationView(command);
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
			File file = uploadedDocument.getFile();
			try {
				uploadedDocument = uploadFileHandler.writeUploadedFile();
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), command.getEncoding()));
			} catch (FileNotFoundException ex1) {
				errors.reject("error.upload.failed", "an error occurred during accessing your file.");
			} catch (Exception ex1) {
				errors.reject("error.upload.failed", "an error occurred during accessing your file.");
			}

			if(ValidationUtils.present(reader))
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
			 * in case the uploaded file is endnote, we convert it to bibtex				
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

						/*
			 * clear temporary file
			 */
			file.delete();
		} else {
			errors.reject("error.upload.failed", "there was no bibtex or endnote entered.");
			//this way of describing the error includes the bibtex snippet 
			//3 opportunities that are ok: bibtex snippet or file and endnote file
		}

		if (!ValidationUtils.present(snippet)) 
		{
			errors.reject("error.upload.failed", "there was no bibtex or endnote entered.");
			//return to the old view showing the error.
		}

		/*
		 * Exchange whitespace
		 */
		int i=42;
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
		 * add additional information from the form to the post (description, groups)... present in both upload tabs
		 */
		User loginUser = command.getContext().getLoginUser();
		for(Post<BibTex> bib : bibtex)
		{
			initPost(command, bib, loginUser);
			bib.setDescription(command.getDescription());
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
				command.setFormAction(ACTION_SAVE_BEFORE_EDIT);
			} else { 
			/*
			 * if the user wants to edit the imported entries before saving
			 */
				
			}
			return Views.BATCHEDITBIB;
		}
	}

	
	private View ShowEnterPublicationView(PostPublicationCommand command)
	{
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
	protected void initPost(final EditPostCommand<BibTex> command, final Post<BibTex> post, final User loginUser) {
		/* 
		 * set the user of the post to the loginUser (the recommender might need
		 * the user name)
		 */
		post.setUser(loginUser);
		/*
		 * initialize groups
		 */
		this.initPostGroups(command, post);
		/*
		 * initialize relevantFor-tags FIXME: candidate for system tags
		 */
		this.initRelevantForTags(command, post);
		/*
		 * For each post process an unique identifier is generated. 
		 * This is used for mapping posts to recommendations.
		 */
		if (command.getPostID() == RecommenderStatisticsManager.getUnknownPID()) {
			command.setPostID(RecommenderStatisticsManager.getNewPID());
		}
	}
	
		/**
		 * Copy the groups from the command into the post (make proper groups from
		 * them)
		 * 
		 * @param command -
		 *            contains the groups as represented by the form fields.
		 * @param post -
		 *            the post whose groups should be populated from the command.
		 * @see #initCommandGroups(EditPostCommand, Post)
		 */
		private void initPostGroups(final EditPostCommand<BibTex> command, final Post<BibTex> post) {
			log.debug("initializing post's groups from command");
			/*
			 * we can avoid some checks here, because they're done in the validator
			 * ...
			 */
			final Set<Group> postGroups = post.getGroups();
			final String abstractGrouping = command.getAbstractGrouping();
			if ("other".equals(abstractGrouping)) {
				log.debug("found 'other' grouping");
				/*
				 * copy groups into post
				 */
				final List<String> groups = command.getGroups();
				log.debug("groups in command: " + groups);
				for (final String groupname : groups) {
					postGroups.add(new Group(groupname));
				}
				log.debug("groups in post: " + postGroups);
			} else {
				log.debug("public or private post");
				/*
				 * if the post is private or public --> remove all groups and add
				 * one (private or public)
				 */
				postGroups.clear();
				postGroups.add(new Group(abstractGrouping));
			}
		}
		
		
		/**
		 * FIXME: system tag handling should be done by system tags ... not by this
		 * controller.
		 */
		private static final String SYS_RELEVANT_FOR = SystemTags.RELEVANTFOR.getPrefix() + SystemTags.SYSTAG_DELIM;
		
		/**
		 * Adds the relevant groups from the command as system tags to the post. 
		 * 
		 * @param command
		 * @param post
		 */
		private void initRelevantForTags(final EditPostCommand<BibTex> command, final Post<BibTex> post) {
			final Set<Tag> tags = post.getTags();
			final List<Group> groups = command.getContext().getLoginUser().getGroups();
			final List<String> relevantGroups = command.getRelevantGroups();
			/*
			 * null check neccessary, because Spring sets the list to null, when no group 
			 * has been selected. :-(
			 */
			if (relevantGroups != null) {
				for (final String relevantGroup : relevantGroups) {
					/*
					 * ignore groups the user is not a member of
					 */
					if (groups.contains(new Group(relevantGroup))) {
						tags.add(new Tag(SYS_RELEVANT_FOR + relevantGroup));
					} else {
						log.info("ignored relevantFor group '" + relevantGroup + "' because user is not member of it");
					}
				}
			}
		}
	
	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	@Override
	protected View getPostView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EditPostValidator<BibTex> getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PostPublicationCommand instantiateEditPostCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BibTex instantiateResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setDuplicateErrorMessage(Post<BibTex> post, Errors errors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void workOnCommand(EditPostCommand<BibTex> command, User loginUser) {
		// TODO Auto-generated method stub
		
	}

}
