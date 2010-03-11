package org.bibsonomy.webapp.controller.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import org.bibsonomy.scraper.exceptions.ConversionException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostPublicationCommandValidator;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
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
	 * will be filled when i have created the commands.
	 * 
	 * TODO: when? The variable is still empty! 
	 */
	public static final String ACTION_SAVE_BEFORE_EDIT = "";


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
	
	/**
	 * TODO: we could inject this object using Spring.
	 */
	private final EndnoteToBibtexConverter e2bConverter = new EndnoteToBibtexConverter();


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
		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		final BibTex publication = command.getPost().getResource();
		if (!context.isUserLoggedIn()) {
			/*
			 * We add two referer headers: the inner for this controller to 
			 * send the user back to the page he was initially coming from,
			 * the outer for the login page to send the user back to this 
			 * controller.
			 */
			return new ExtendedRedirectView("/login" + 
					"?notice=" + LOGIN_NOTICE + publication.getClass().getSimpleName().toLowerCase() + 
					"&referer=" + safeURIEncode(requestLogic.getCompleteRequestURL() + "&referer=" + safeURIEncode(requestLogic.getReferer()))); 
		}

		final User loginUser = context.getLoginUser();

		/* 
		 * If the user entered the post data manually, the EditPublicationController
		 * will handle the remaining work.
		 * 
		 * To find out, if the data was entered manually, a good heuristic is to 
		 * check if an entrytype is given, because that field can't be empty. 
		 */
		if (ValidationUtils.present(publication.getEntrytype())) {
			/* 
			 * get possibly existing post from database
			 */
			publication.recalculateHashes();
			command.setPost((Post<BibTex>) logic.getPostDetails(publication.getIntraHash(), loginUser.getName()));
			super.workOn(command);
		}

		/*
		 * This variable will hold the information contained in the bibtex/endnote-file or selection field
		 */
		String snippet = null;

		final String selection = command.getSelection();
		if (ValidationUtils.present(selection)) {
			/*
			 * The user has entered text into the selection - we use that 
			 */
			snippet = selection;
		} else if(ValidationUtils.present(command.getFile())) {
			/*
			 * The user uploads a BibTeX or Endnote file
			 */

			/*
			 * get temp file
			 */
			final CommonsMultipartFile uploadedFile = command.getFile();
			final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(uploadedFile.getFileItem()), HandleFileUpload.bibtexEndnoteExt);

			try {
				final Document uploadedDocument = uploadFileHandler.writeUploadedFile();
				final File file = uploadedDocument.getFile();
				final String fileName = uploadedDocument.getFileName();

				final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), command.getEncoding()));

				if (StringUtils.matchExtension(fileName, HandleFileUpload.bibtexEndnoteExt[1])) {
					/*
					 * In case the uploaded file is in EndNote format, we convert it to BibTeX.				
					 */
					snippet = e2bConverter.endnoteToBibtexString(reader);
				} else {
					/*
					 * or just use it as it is ...
					 */
					
				}

				/*
				 * clear temporary file
				 */
				file.delete();
			} catch (final ConversionException e) {
				errors.reject("error.conversion.failed", "An error occurred during converting your EndNote file to BibTeX.");
				/*
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);	
			} catch (final Exception ex1) {
				errors.reject("error.upload.failed", "An error occurred during accessing your file.");
				/*
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);	
			}



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
		final List<Post<?>> storageList = new LinkedList<Post<?>>();

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
		final PostPublicationCommandValidator validator = new PostPublicationCommandValidator();
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
		} 
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


	private View showEnterPublicationView(final PostPublicationCommand command, final boolean hasErrors) {
		command.setExtendedView(hasErrors);
		return Views.POST_PUBLICATION;
	}


	private View ShowEnterPublicationView(final PostPublicationCommand command) {
		return showEnterPublicationView(command, false);
	}

	private void savePublicationsForUser(final ListCommand<Post<BibTex>> postListCommand, final PostPublicationCommand command, final User user)
	{
		final boolean isOverwrite = command.getOverwrite();
		List<Post<?>> tmpList = new LinkedList<Post<?>>(postListCommand.getList());
		if(bibtexHasValidationErrors)
			tmpList = null; // TODO: was: storageList;

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
	protected PostValidator<BibTex> getValidator() {
		return new PostValidator<BibTex>(); 
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
