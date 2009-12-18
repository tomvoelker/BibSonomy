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
import org.bibsonomy.util.StringUtils;
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
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import bibtex.parser.ParseException;

/**
 * @author ema
 * @version $Id$
 */
public class PostPublicationController extends EditPostController<BibTex,PostPublicationCommand> implements MinimalisticController<PostPublicationCommand>, ErrorAware {
	private static final Integer MAXCOUNT_ERRORHANDLING = 1000;
	public static final String TEMPORARILY_IMPORTED_PUBLICATIONS = "TEMPORARILY_IMPORTED_PUBLICATIONS";
	
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

	//@Override
	@Override
	public View workOn(PostPublicationCommand command) {
		log.debug("workOn started");
		
		//within this map we store all errors while creating the uploaded posts
		//the errors will be concatenated at the end and rejected as an error for display.
		Map<String, List<ErrorMessage>> userError = null;
		
		/*
		 * default controller behaviour is to send the user to the first step of importing bookmarks (TASK_ENTER_PUBLICATIONS)
		 */
		if(!ValidationUtils.present(command.getTaskName()))
			command.setTaskName(PostPublicationCommand.TASK_ENTER_PUBLICATIONS);
		
		
		/******************************************************************************************************
		 * if this controller was called for the first step of importing bookmarks to myBibsonomy, we forward 
		 * to the view, where the DATA FOR THE IMPORT can be provided by the user.
		 ******************************************************************************************************/
		
		if(PostPublicationCommand.TASK_ENTER_PUBLICATIONS.equals(command.getTaskName()))
			return ShowEnterPublicationView(command);
		
		
		/******************************************************************************************************
		 * if this controller was called for the second step of importing bookmarks to myBibsonomy, we forward 
		 * the user to a view, where he can EDIT and CLEAN UP his imported publications. 
		 ******************************************************************************************************/
		
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
			File file = null;
			try {
				uploadedDocument = uploadFileHandler.writeUploadedFile();
				file = uploadedDocument.getFile();
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), command.getEncoding()));
			} catch (FileNotFoundException ex1) {
				errors.reject("error.upload.failed", "An error occurred during accessing your file.");
				
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);
				
			} catch (Exception ex1) {
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
			} catch (Exception ex) {
				errors.reject("error.upload.failed", "an error occurred during accessing your file.");
				
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				return ShowEnterPublicationView(command);
				
			}

			/*
			 * clear temporary file
			 */
			file.delete();
		} else {
			/*
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

		
		
		
		/*
		 * Parse the bibtex snippet	
		 */
		List<Post<BibTex>> bibtex = null;
		Set<Integer> parseErrorLines = null;
		try {
			PostBibTeXParser parser = new PostBibTeXParser();
			parser.setDelimiter(command.getDelimiter());
			parser.setWhitespace(command.getWhitespace());
			parser.setTryParseAll(true);
			bibtex = parser.parseBibTeXPosts(snippet);
			
			/**
			 * fetch PARSER ERRORS here
			 */
			parseErrorLines = this.getErroneousLineNumbers(parser.getCaughtExceptions());
			/**
			 * reject erroneous line numbers as global errors
			 */
			for(Integer lineNumber : parseErrorLines)
			{
				errors.reject("erroneous_line_numbers", lineNumber.toString());
			}
		} catch (ParseException ex) {
			errors.reject("error.upload.failed", "An error occurred during parsing process of your file.");
			
			/**
			 * BACK TO THE IMPORT/PUBLICATIONS VIEW
			 */
			return ShowEnterPublicationView(command);
			
		} catch (IOException ex) {
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
		
		/*
		 * Prepare the posts for the edit operations:
		 * add additional information from the form to the post (description, groups)... present in both upload tabs
		 */
		PostPublicationValidator validator = new PostPublicationValidator();
		User loginUser = command.getContext().getLoginUser();
		for(Post<BibTex> bib : bibtex)
		{
			//the post has to belong to the user in order to be able to edit it
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
		
		ListCommand<Post<BibTex>> postListCommand = new ListCommand<Post<BibTex>>(command);
		postListCommand.setList(bibtex);
		command.setBibtex(postListCommand);
		/**
		 * Check for INCOMPLETION ERRORS here
		 */
		validator.validate(command, errors);
		
		
		/**
		 * if we have errors, we dont store the publications (with only one little exception)
		 * therefore we do have to store them temporarily in the session
		 */
		if(errors.hasErrors())
		{
			command.setDeleteCheckedPosts(false); //posts will have to get saved, since an error occurred
			setSessionAttribute(TEMPORARILY_IMPORTED_PUBLICATIONS, bibtex);
		}
		
		
		/*
		 * if number of bibtexes contained is one, it can be edited in details, else we can use the 
		 * multi-post-edit view
		 */
		if(bibtex.size()==1)
		{
			command.setPost(bibtex.get(0));
			super.setErrors(getErrors()); 
			return super.workOn(command);
		} else {
			
			/*
			 * We have more than one bibtex, which means that this controller will forward to one calling the batcheditbib.jspx
			 */
			
			/**********************
			 * STORE THE BOOKMARKS
			 **********************/
			
			if(!command.isEditBeforeImport() && (!errors.hasErrors() || (errors.hasErrors() && bibtex.size()>MAXCOUNT_ERRORHANDLING)))
			{
				/**
				 * Concatenate all errors, that were found during savePublicationsForUser
				 */
				Map<String, List<ErrorMessage>> errorMsgs = savePublicationsForUser(postListCommand, command.isOverwrite(), command.isWriteAllCorrectOnes(), loginUser);
				for(int i=0; i<bibtex.size(); i++)
				{
					for(ErrorMessage msg : errorMsgs.get(bibtex.get(i)))
					{
						errors.rejectValue("bibtex.list["+i+"]", 
											"since we might have parameterized messages, we translate them within java and use the fallback",
											StringUtils.translateMessageKey(msg.getLocalizedMessageKey(), msg.getParameters(), command.getContext().getLocale())
											);
						
					}
				}
				command.setFormAction(ACTION_SAVE_BEFORE_EDIT);
			} else {
					command.setDeleteCheckedPosts(false); //posts will have to get saved, since an error occurred
					setSessionAttribute(TEMPORARILY_IMPORTED_PUBLICATIONS, bibtex);
			}

			/**
			 * If there are errors (incomplete/parse), we dont store them
			 */
			if(errors.hasErrors())
			{
				/**
				 * BACK TO THE IMPORT/PUBLICATIONS VIEW
				 */
				
				//TODO set a variable to determine, if #posts>treshold => list of imported and unimported
				//publications. since the correct ones are already saved, the action of the form (batchedit)
				//will be deleting the stored posts.
				command.setDeleteCheckedPosts(false); //posts will have to get saved, since an error occurred
				return ShowEnterPublicationView(command, true);
			}
			
			if(command.getEditBeforeImport())
				command.setDeleteCheckedPosts(false); //posts will have to get saved, because the user decided to
			
			return Views.BATCHEDITBIB;
		}
	}

	
	private View ShowEnterPublicationView(PostPublicationCommand command, boolean hasErrors)
	{
		if(hasErrors)
			command.setExtendedView(true);
		/**
		 * TODO: command.formAction setzen, auf den command, der die angekreuzten Publikationen speichert.
		 */
		return Views.POST_PUBLICATION;
	}
	
	
	private View ShowEnterPublicationView(PostPublicationCommand command)
	{
		return ShowEnterPublicationView(command, false);
	}
	
	private Map<String, List<ErrorMessage>> savePublicationsForUser(ListCommand<Post<BibTex>> postListCommand, boolean isOverwrite, boolean writeAllCorrectOnes, User user)
	{
		List<Post<?>> tmpList = new LinkedList<Post<?>>(postListCommand.getList());
		List<String> createdPostHash = new LinkedList<String>();
		Map<String, List<ErrorMessage>> errors = null;
		
		try {
			/**
			 * Try to save all posts in one transaction.
			 */
			createdPostHash = logic.createPosts(tmpList);
		} catch (DatabaseException e) {
			/**
			 * Something went wrong and an error occurred. The prior statement is "rolled backed"
			 */
			errors = e.getErrorMessages(); 
			Set<String> hashes = errors.keySet();
			/**
			 * Check for all posts if they have only DUPLICATE ERRORS 
			 */
			boolean isErroneousList = false;
			List<Post<?>> forUpdate = new LinkedList<Post<?>>();
			List<Post<?>> forCreate = new LinkedList<Post<?>>();
			List<Post<?>> forCorrection = new LinkedList<Post<?>>();
			for(Post<?> bib : tmpList)
			{
				List<ErrorMessage> errorMsges = errors.get(bib.getResource().getIntraHash());
				if(ValidationUtils.present(errorMsges))
				{
					boolean isErroneous = false;
					boolean hasDuplicate = false;
					ErrorMessage duplicateMessage = null;
					for(ErrorMessage msg : errorMsges)
					{
						if(msg instanceof DuplicatePostErrorMessage)
						{
							hasDuplicate = true;
							if(isOverwrite)
								duplicateMessage = msg;
						}
						else
						{
							isErroneous = true;
							isErroneousList = true;
						}
					}
					//if isOverwrite is true, duplicates are no errors
					if(ValidationUtils.present(duplicateMessage))
						errorMsges.remove(duplicateMessage);
					
					if(!isErroneous && hasDuplicate)
						forUpdate.add(bib);
					else if(isErroneous)
						forCorrection.add(bib);
				} else {
					forCreate.add(bib);
				}
			}
			
			/**
			 * If we got ONLY duplicate "errors", we save the non-duplicate ones and update the others,
			 * if isOverwrite is true. Same is true, if the number of publications is greater than the
			 * treshold.
			 */
			if(!isErroneousList || tmpList.size()>MAXCOUNT_ERRORHANDLING || writeAllCorrectOnes)
			{
				
				try {
					logic.createPosts(forCreate);
					if(isOverwrite)
						logic.updatePosts(forUpdate, PostUpdateOperation.UPDATE_ALL);
				} catch (DatabaseException ex) {
					//sollte gar nicht passieren, da forCreate-/forUpdate-Posts und keine Fehler 
					//besitzen
				}
				
			}
		}
		return errors;
	}
	

	
	private Set<Integer> getErroneousLineNumbers(ParseException[] exceptions)
	{
		Set<Integer> result = new HashSet<Integer>();
		final Pattern lineNumberPattern = Pattern.compile("([0-9]+).+");
		for(ParseException exceptIter : exceptions)
		{
			Matcher patMat = lineNumberPattern.matcher(exceptIter.getMessage());
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
	protected void setDuplicateErrorMessage(Post<BibTex> post, Errors errors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void workOnCommand(EditPostCommand<BibTex> command, User loginUser) {
		// TODO Auto-generated method stub
		
	}

}
