package org.bibsonomy.webapp.controller.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.SystemTagErrorMessage;
import org.bibsonomy.common.errors.UnspecifiedErrorMessage;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
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
import org.bibsonomy.webapp.validation.EditPublicationValidator;
import org.bibsonomy.webapp.validation.PostPublicationValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import bibtex.parser.ParseException;

/**
 * TODO: comment vs java doc
 * TODO: unused vars
 * TODO: bookmarks => publications
 * TODO: bibtex => publication
 * 
 * @author ema
 * @version $Id$
 */
public class PostPublicationController extends EditPostController<BibTex,PostPublicationCommand> implements MinimalisticController<PostPublicationCommand>, ErrorAware {
	/**
	 * the log...
	 */
	private static final Log log = LogFactory.getLog(PostPublicationController.class);
	
	/**
	 * if the user tries to import more than MAXCOUNT_ERRORHANDLING posts AND an error exists
	 * in one or more of the posts, the correct posts will be saved no matter what.
	 */
	private static final Integer MAXCOUNT_ERRORHANDLING = 1000;
	/**
	 * The session dictionary name for temporarily stored publications. 
	 * Will be used when PostPublicationCommand.editBeforeImport is true. 
	 */
	public static final String TEMPORARILY_IMPORTED_PUBLICATIONS = "TEMPORARILY_IMPORTED_PUBLICATIONS";
	
	private static final Group PUBLIC_GROUP = GroupUtils.getPublicGroup();

	/**
	 * File ending pattern for determining the type of imported file.
	 */
	private static final Pattern fileEnding = Pattern.compile("\\.([a-zA-Z]+)");
	
	/**
	 * will be filled when i have created the commands.
	 */
	public static final String ACTION_SAVE_BEFORE_EDIT ="";

	
	/**
	 * errors object...
	 */
	private Errors errors;

	private MessageSource messageSource;
	
	/**
	 * the factory used to get an instance of a FileUploadHandler.
	 */
	private FileUploadFactory uploadFactory;

	private boolean bibtexHasValidationErrors;
	
	private List<Post<?>> storageList;

	@Override
	public PostPublicationCommand instantiateCommand() {
		/**
		 * initialize post & resource
		 */
		final PostPublicationCommand command = new PostPublicationCommand();
		command.setGroups(new ArrayList<String>());

		command.setPost(new Post<BibTex>());
		command.setAbstractGrouping(PUBLIC_GROUP.getName());
		command.getPost().setResource(new BibTex());

		return command;
	}

	@Override
	public View workOn(final PostPublicationCommand command) {
		log.debug("workOn started");
		
		
		storageList = new LinkedList<Post<?>>();
		
		/*
		 * within this map we store all errors while creating the uploaded posts.
		 * the errors will be concatenated at the end and rejected as an error for display.
		 */
		final Map<String, List<ErrorMessage>> userError = null;
		
		/**
		 * default controller behaviour is to send the user to the first step of importing bookmarks (TASK_ENTER_PUBLICATIONS)
		 */
		if(!ValidationUtils.present(command.getTaskName()))
			command.setTaskName(PostPublicationCommand.TASK_ENTER_PUBLICATIONS);
		
		
		/* 
		 * if this controller was called for the first step of importing bookmarks to myBibsonomy, we forward 
		 * to the view, where the DATA FOR THE IMPORT can be provided by the user.
		 */
		
		if(PostPublicationCommand.TASK_ENTER_PUBLICATIONS.equals(command.getTaskName()))
			return ShowEnterPublicationView(command);
		
		
		/******************************************************************************************************
		 * if this controller was called for the second step of importing bookmarks to myBibsonomy, we forward 
		 * the user to a view, where he can EDIT and CLEAN UP his imported publications. 
		 ******************************************************************************************************/
		
		/**
		 * This variable will hold the information contained in the bibtex/endnote-file or selection field
		 */
		String snippet = null;

		/**
		 * Tab 2 (Upload Snippet)
		 */
		if (ValidationUtils.present(command.getSelection())) {
			snippet = command.getSelection();
		} else if(ValidationUtils.present(command.getFile())) {

			/**
			 * Tab 3 (Upload BibTex/Endnote)
			 * get temporary file from the command with the factory
			 */
			final CommonsMultipartFile uploadedFile = command.getFile();
			final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(uploadedFile.getFileItem()), 
					HandleFileUpload.bibtexEndnoteExt);

			Document uploadedDocument=null;
			BufferedReader reader=null;
			File file = null;
			try {
				uploadedDocument = uploadFileHandler.writeUploadedFile();
				file = uploadedDocument.getFile();
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), command.getEncoding()));
			} catch (final FileNotFoundException ex1) {
				errors.reject("error.upload.failed", "An error occurred during accessing your file.");
				
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);
			} catch (final Exception ex1) {
				errors.reject("error.upload.failed", "An error occurred during accessing your file.");
				
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);	
			}

			if(!ValidationUtils.present(reader))
			{
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);
			}

			/**
			 * extract the file ending
			 */
			String fileSuffix = null;
			final String uploadFileName = uploadedDocument.getFileName();

			final Matcher patMat = fileEnding.matcher(uploadFileName);
			if(patMat.find())
				fileSuffix = patMat.group(1).toLowerCase(); 

			/**
			 * in case the uploaded file is endnote, we convert it to bibtex				
			 */
			if(HandleFileUpload.bibtexEndnoteExt[1].equals(fileSuffix))
			{
				try {
					final EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
					reader = (BufferedReader) converter.EndnoteToBibtex(reader);
				} catch (final Exception ex) {
					errors.reject("error.upload.failed", "The submitted file does not contain valid endnotes.");
					
					/**
					 * BACK TO THE IMPORT/PUBLICATIONS VIEW
					 */
					return ShowEnterPublicationView(command);
				}
			}

			/*
			 * extract the file contents from the file
			 */
			try {
				snippet = convertFileToString(reader);
			} catch (final Exception ex) {
				errors.reject("error.upload.failed", "an error occurred during accessing your file.");
				
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);
			}

			/**
			 * clear temporary file
			 */
			file.delete();
		} else {
			/**
			 * This way of describing the error includes the bibtex snippet:
			 * 3 opportunities that are ok: bibtex snippet or file and endnote file. 
			 */
			errors.reject("error.upload.failed", "there was no valid bibtex or endnote entered.");
			
			/**
			 * BACK TO THE IMPORT/PUBLICATIONS VIEW
			 */
			return ShowEnterPublicationView(command);
		}

		if (!ValidationUtils.present(snippet)) 
		{
			errors.reject("error.upload.failed", "there was no valid bibtex or endnote entered.");
			
			/**
			 * BACK TO THE IMPORT/PUBLICATIONS VIEW
			 */
			return ShowEnterPublicationView(command);
		}
		
		/**
		 * Parse the bibtex snippet	
		 */
		List<Post<BibTex>> bibtex = null;
		Set<Integer> parseErrorLines = null;
		try {
			final PostBibTeXParser parser = new PostBibTeXParser();
			parser.setDelimiter(command.getDelimiter());
			parser.setWhitespace(command.getWhitespace());
			parser.setTryParseAll(true);
			bibtex = parser.parseBibTeXPosts(snippet);
			for(final Post<BibTex> aPostToCast : bibtex)
				storageList.add(aPostToCast);
			
			/**
			 * fetch PARSER ERRORS here
			 */
			parseErrorLines = this.getErroneousLineNumbers(parser.getCaughtExceptions());
			/**
			 * reject erroneous line numbers as global errors
			 */
			for(final Integer lineNumber : parseErrorLines)
			{
				errors.reject("erroneous_line_numbers", lineNumber.toString());
			}
		} catch (final ParseException ex) {
			errors.reject("error.upload.failed", "An error occurred during parsing process of your file.");
			
			/**
			 * BACK TO THE IMPORT/PUBLICATIONS VIEW
			 */
			return ShowEnterPublicationView(command);
		} catch (final IOException ex) {
			errors.reject("error.upload.failed", "An error occurred during parsing process of your file.");
			
			/**
			 * BACK TO THE IMPORT/PUBLICATIONS VIEW
			 */
			return ShowEnterPublicationView(command);
			
		}
		
		if(ValidationUtils.present(parseErrorLines))
			command.setErroneousLineNumbers(new LinkedList<Integer>(parseErrorLines));
		
		if(!ValidationUtils.present(bibtex))
		{
			errors.reject("error.upload.failed", "there was no bibtex or endnote entered.");
			
			/**
			 * BACK TO THE IMPORT/PUBLICATIONS VIEW
			 */
			return ShowEnterPublicationView(command);
		}
		
		/**
		 * Prepare the posts for the edit operations:
		 * add additional information from the form to the post (description, groups)... present in both upload tabs
		 */
		final PostPublicationValidator validator = new PostPublicationValidator();
		final User loginUser = command.getContext().getLoginUser();
		for(final Post<BibTex> bib : bibtex)
		{
			//user has to be set (was not after first clean up with daniel)
			bib.setUser(loginUser);
			//set visibility of this post for the groups, the user specified 
			initPostGroups(command, bib);
			//the description
			bib.setDescription(command.getDescription());
			//if not present, a valid date has to be set
			if(!ValidationUtils.present(bib.getDate()))
				setDate(bib, loginUser.getName());
			
			//hases have to be set, in order to call the validator
			bib.getResource().recalculateHashes();
		}
		
		final ListCommand<Post<BibTex>> postListCommand = new ListCommand<Post<BibTex>>(command);
		postListCommand.setList(bibtex);
		command.setBibtex(postListCommand);
		/**
		 * Check for INCOMPLETION ERRORS here - rejectIfEmpty checks
		 */
		final int numOfNoValidationErrors = errors.getErrorCount();
		validator.validate(command, errors);
		bibtexHasValidationErrors = numOfNoValidationErrors!=errors.getErrorCount();
		
		/*
		 * if we have errors, we dont store the publications (with only one little exception)
		 * therefore we do have to store them temporarily in the session
		 */
		if (errors.hasErrors())
		{
			command.setDeleteCheckedPosts(false); //posts will have to get saved, since an error occurred
			setSessionAttribute(TEMPORARILY_IMPORTED_PUBLICATIONS, bibtex);
		}
		
		/*
		 * if number of bibtexes contained is one, it can be edited in details, else we can use the 
		 * multi-post-edit view
		 */
		if (bibtex.size() == 1)
		{
			command.setPost(bibtex.get(0));
			super.setErrors(getErrors()); 
			return super.workOn(command);
		} else {
			final List<FieldError> errorList = errors.getFieldErrors("bibtex.*");
			int removedElements = 0;
			final List<Integer> removedIndexes = new LinkedList<Integer>();
			for(final FieldError anError : errorList)
			{
				final String fieldName = anError.getField();
				final Pattern indexPattern = Pattern.compile("([0-9]+)");
				final Matcher indexMatcher = indexPattern.matcher(fieldName);
				if(indexMatcher.find())
				{
					final int errorIndex = new Integer(indexMatcher.group(1));
					if(removedIndexes.contains(errorIndex)) continue;
					storageList.remove(errorIndex-removedElements);
					removedIndexes.add(errorIndex);
					removedElements++;
				}
			}
		
			/* *********************
			 * STORE THE BOOKMARKS
			 * *********************/
			if(!command.isEditBeforeImport())
			{
				savePublicationsForUser(postListCommand, command, loginUser);
				command.setFormAction(ACTION_SAVE_BEFORE_EDIT);
			} else {
				setSessionAttribute(TEMPORARILY_IMPORTED_PUBLICATIONS, bibtex);
			}

			/*
			 * If the user wants to store the posts permanently AND (his posts have no errors OR he ignores the errors OR the number of
			 * bibtexes is greater than the treshold, we will forward him to the appropriate site, where he can delete posts (they were saved)
			 */
			if(!command.isEditBeforeImport() && (!errors.hasErrors() || bibtex.size()>MAXCOUNT_ERRORHANDLING))
				command.setDeleteCheckedPosts(true); //posts will have to get saved, because the user decided to
			else
				command.setDeleteCheckedPosts(false);
			
			
			/* *************************
			 * RETURN THE CORRECT VIEW *
			 * *************************/
			if(errors.hasErrors())
			{
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 * Posts will get saved temporarily, since an error occurred (checked posts will be saved)
				 */
				return showEnterPublicationView(command, true);
			}
			
			/**
			 * if the user explicitly wants to store the posts AFTER editing we forward to the modified batcheditbib (batchedittempbib)
			 */
			if(command.isEditBeforeImport())
				return Views.BATCHEDIT_TEMP_BIB;
			
			return Views.BATCHEDITBIB;
		}
	}

	
	private View showEnterPublicationView(final PostPublicationCommand command, final boolean hasErrors)
	{
		command.setExtendedView(hasErrors);
		return Views.POST_PUBLICATION;
	}
	
	
	private View ShowEnterPublicationView(final PostPublicationCommand command)
	{
		return showEnterPublicationView(command, false);
	}
	
	private void savePublicationsForUser(final ListCommand<Post<BibTex>> postListCommand, final PostPublicationCommand command, final User user)
	{
		final boolean isOverwrite = command.getOverwrite();
		List<Post<?>> tmpList = new LinkedList<Post<?>>(postListCommand.getList());
		if(bibtexHasValidationErrors)
			tmpList = storageList;
		
		List<String> createdPostHash = new LinkedList<String>();
		Map<String, List<ErrorMessage>> errors = null;
		
		try {
			/**
			 * Try to save all posts in one transaction.
			 */
			createdPostHash = logic.createPosts(tmpList);
		} catch (final DatabaseException e) {
			
			/**
			 * Something went wrong and an error occurred. The prior statement is "rolled backed"
			 */
			errors = e.getErrorMessages(); 
			final Set<String> hashes = errors.keySet();
			/**
			 * Check for all posts if they have only DUPLICATE ERRORS 
			 */
			boolean isErroneousList = false;
			final List<Post<?>> forUpdate = new LinkedList<Post<?>>();
			final List<Post<?>> forCreate = new LinkedList<Post<?>>();
			//final List<Post<?>> forCorrection = new LinkedList<Post<?>>();
			for(final Post<?> bib : tmpList)
			{
				final List<ErrorMessage> errorMsges = errors.get(bib.getResource().getIntraHash());
				if(ValidationUtils.present(errorMsges))
				{
					boolean isErroneous = false;
					boolean hasDuplicate = false;
					ErrorMessage duplicateMessage = null;
					for(final ErrorMessage msg : errorMsges)
					{
						if(msg instanceof DuplicatePostErrorMessage)
						{
							hasDuplicate = true;
							if(isOverwrite)
								duplicateMessage = msg;
							else
							{
								Object[] params = null;
								if(msg.getParameters()!=null)
									params = msg.getParameters().toArray();
								this.errors.rejectValue("bibtex.list["+postListCommand.getList().indexOf(bib)+"].resource", 
										messageSource.getMessage(msg.getLocalizedMessageKey(), 
												params, 
												requestLogic.getLocale()),
										messageSource.getMessage(msg.getLocalizedMessageKey(), 
												params, 
												requestLogic.getLocale()));
							}
						}
						else if (msg instanceof SystemTagErrorMessage)
						{
							isErroneous = true;
							isErroneousList = true;
							Object[] params = null;
							if(msg.getParameters()!=null)
								params = msg.getParameters().toArray();
							this.errors.rejectValue("bibtex.list["+postListCommand.getList().indexOf(bib)+"].tags", 
									messageSource.getMessage(msg.getLocalizedMessageKey(), 
											params, 
											requestLogic.getLocale()),
									messageSource.getMessage(msg.getLocalizedMessageKey(), 
											params, 
											requestLogic.getLocale()));

						}
						else if(msg instanceof UnspecifiedErrorMessage)
						{
							Object[] params = null;
							if(msg.getParameters()!=null)
								params = msg.getParameters().toArray();
							this.errors.rejectValue("bibtex.list["+postListCommand.getList().indexOf(bib)+"].resource", 
									messageSource.getMessage(msg.getLocalizedMessageKey(), 
											params, 
											requestLogic.getLocale()),
									messageSource.getMessage(msg.getLocalizedMessageKey(), 
											params, 
											requestLogic.getLocale()));
						}
					}
					//if isOverwrite is true, duplicates are no errors
					if(ValidationUtils.present(duplicateMessage))
						errorMsges.remove(duplicateMessage);
					
					if(!isErroneous && hasDuplicate)
						forUpdate.add(bib);
				}
			}
			

			/**
			 * If we got ONLY duplicate "errors", we save the non-duplicate ones and update the others,
			 * if isOverwrite is true. Same is true, if the number of publications is greater than the
			 * treshold. 
			 */
			try {
				if(isOverwrite)
					logic.updatePosts(forUpdate, PostUpdateOperation.UPDATE_ALL);
			} catch (final DatabaseException ex) {
				for(final Post<?> bib : forUpdate)
				{
					final List<ErrorMessage> errorMsges = errors.get(bib.getResource().getIntraHash());
					if(ValidationUtils.present(errorMsges))
					{
						for(final ErrorMessage msg : errorMsges)
						{
							if(msg instanceof SystemTagErrorMessage)
							{
								Object[] params = null;
								if(msg.getParameters()!=null)
									params = msg.getParameters().toArray();
								this.errors.rejectValue("bibtex.list["+postListCommand.getList().indexOf(bib)+"].tags", 
										messageSource.getMessage(msg.getLocalizedMessageKey(), 
												params, 
												requestLogic.getLocale()),
										messageSource.getMessage(msg.getLocalizedMessageKey(), 
												params, 
												requestLogic.getLocale()));
	
							}
						}
					}
				}
			}
		}
	}

	

	
	private Set<Integer> getErroneousLineNumbers(final ParseException[] exceptions)
	{
		final Set<Integer> result = new HashSet<Integer>();
		final Pattern lineNumberPattern = Pattern.compile("([0-9]+).+");
		for(final ParseException exceptIter : exceptions)
		{
			final Matcher patMat = lineNumberPattern.matcher(exceptIter.getMessage());
			if(patMat.find())
				result.add(Integer.parseInt((patMat.group(1).toLowerCase())));
		}
		return result; 
	}
	
	
	/**
	 * FIXME: 
	 * Such a method should be put into FileUtils (maybe it already exists there) 
	 * 
	 * @param reader
	 * @return
	 * @throws Exception
	 */
	private String convertFileToString(final BufferedReader reader) throws Exception
	{
		final StringBuffer snippet = new StringBuffer(1000);


		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			final String readData = String.valueOf(buf, 0, numRead);
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
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	@Override
	protected View getPostView() {

		return Views.EDIT_PUBLICATION;
	}

	@Override
	protected EditPostValidator<BibTex> getValidator() {
		return new EditPublicationValidator(); 
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
	protected void setDuplicateErrorMessage(final Post<BibTex> post, final Errors errors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void workOnCommand(final EditPostCommand<BibTex> command, final User loginUser) {
		// TODO Auto-generated method stub
		
	}
	
	public FileUploadFactory getUploadFactory() {
		return this.uploadFactory;
	}

	public void setUploadFactory(final FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}

	public MessageSource getMessageSource() {
		return this.messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
