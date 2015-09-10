/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.DuplicatePostInSnippetErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.importer.PublicationImporter;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.PostPublicationCommandValidator;
import org.bibsonomy.webapp.validation.PublicationValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.ValidationUtils;

import bibtex.parser.ParseException;

/**
 * 
 * @author ema
 * @author rja
 */
public class PostPublicationController extends AbstractEditPublicationController<PostPublicationCommand> {
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

	/**
	 * Extracts the line number from the parser error messages.
	 */
	private static final Pattern lineNumberPattern = Pattern.compile("([0-9]+)");

	private PublicationImporter publicationImporter;

	@Override
	public PostPublicationCommand instantiateCommand() {
		/*
		 * initialize post & resource
		 */
		final PostPublicationCommand command = new PostPublicationCommand();
		command.setGroups(new ArrayList<String>());

		command.setPost(new Post<BibTex>());
		command.setAbstractGrouping(GroupUtils.buildPublicGroup().getName());
		command.getPost().setResource(new BibTex());
		command.setPostsErrorList(new LinkedHashMap<String, List<ErrorMessage>>());

		return command;
	}

	/**
	 * Handles posting of several posts, e.g., parsed from a BibTeX file.
	 * 
	 * TODO: We need to integrate a mechanism into the view to show warnings for
	 * posts we could import (currently, only errors are shown and then those 
	 * posts are also not imported).  
	 * 
	 * 
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#workOn(org.bibsonomy.webapp.command.actions.EditPostCommand)
	 */
	@Override
	public View workOn(final PostPublicationCommand command) {
		log.debug("workOn started");

		initializeDidYouKnowMessageCommand(command);

		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		final BibTex publication = command.getPost().getResource();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", LOGIN_NOTICE + publication.getClass().getSimpleName().toLowerCase());
		}

		/*
		 * If the user entered the post data manually, the EditPublicationController 
		 * will handle the remaining work.
		 * 
		 * To find out, if the data was entered manually, a good heuristic is to
		 * check if an entrytype is given, because that field can't be empty.
		 * We furthermore need to check the title, because the title cannot be empty 
		 * either and sometimes we like to preselect a certain entrytype.
		 */
		if (present(publication.getEntrytype()) && present(publication.getTitle())) {
			log.debug("user has manually entered post data -> forwarding to edit post controller");
			return super.workOn(command);
		}
		final String selection = command.getSelection();
		final boolean hasSelection = present(selection);
		final boolean hasFile = present(command.getFile());

		/*
		 * check for valid ckey
		 */
		if ((hasFile || hasSelection) && !context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		if (command.getPerson() != null) {
			if (present(command.getPerson().getPersonId())) {
				final Person person = this.logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getPersonId());
				command.setPerson(person);
			}
		}

		/*
		 * This handles the cases
		 * 1) the user just started the postPublication process
		 * 2) the user entered a snippet (might be empty)
		 * 3) the user selected a file to upload posts (might be empty)
		 * DOI/ISBN or manual input are handled in EditPostController
		 */
		/*
		 * This variable will hold the information contained in the bibtex/endnote-file or selection field
		 */
		String snippet = null;
		if (hasSelection) {
			/*
			 * The user has entered text into the snippet selection - we use that
			 */
			log.debug("user has filled selection");
			snippet = this.publicationImporter.handleSelection(selection);
		} else if (hasFile) {
			/*
			 * The user uploads a BibTeX or EndNote file
			 */
			log.debug("user uploads a file");
			// get the (never empty) content or add corresponding errors
			snippet = this.publicationImporter.handleFileUpload(command, this.errors);
		} else {
			/*
			 * nothing given ->
			 * user just opened the postPublication Dialogue OR
			 * user send empty snippet or "nonexisting" file
			 * FIXME: that second case should result in some error and hint for the user
			 */
			if (command.getPerson() != null) {
				final PersonName mainName = command.getPerson().getMainName();
				if (mainName != null) {
					List<PersonName> authorNames = new ArrayList<>();
					authorNames.add(mainName);
					publication.setAuthor(authorNames);
				}
			}
			
			return Views.POST_PUBLICATION;
		}

		// pdf file uploaded
		if (present(command.getFileName())) {
			return super.workOn(command);
		}

		/*
		 * Either a file or a snippet was given,
		 * it's content is now stored in snippet
		 * -> check if valid
		 */
		if (errors.hasErrors()) {
			log.debug("errors found, returning to view");
			if (log.isDebugEnabled()) {
				log.debug(errors);
			}
			return Views.POST_PUBLICATION;
		}

		/*
		 * Extract posts from snippet ...
		 */

		/*
		 * configure the parser
		 */
		final PostBibTeXParser parser = new PostBibTeXParser();
		parser.setDelimiter(command.getDelimiter());
		parser.setWhitespace(command.getWhitespace());
		parser.setTryParseAll(true);

		/*
		 * FIXME: why aren't commas, etc. removed?
		 */
		List<Post<BibTex>> posts = null;

		try {
			/*
			 * Parse the BibTeX snippet
			 */
			posts = parser.parseBibTeXPosts(snippet);
		} catch (final ParseException ex) {
			this.errors.reject("error.upload.failed.parse", ex.getMessage());
		} catch (final IOException ex) {
			this.errors.reject("error.upload.failed.parse", ex.getMessage());
		}
		PublicationValidator.handleParserWarnings(this.errors, parser, snippet, null);

		/*
		 * The errors we have collected until now should be fixed before we proceed.
		 * 
		 * (We did not collect errors due to individual broken BibTeX lines, yet!)
		 */
		if (this.errors.hasErrors()) {
			return Views.POST_PUBLICATION;
		}

		/*
		 * turn parse exceptions into error messages ...
		 */
		handleParseExceptions(parser.getCaughtExceptions());

		if (!this.errors.hasErrors() && !present(posts)) {
			/*
			 * no errors ... but also no posts ... Ooops!
			 * the parser was not able to produce posts but did not add errors nor throw exceptions
			 */
			this.errors.reject("error.upload.failed.parse", "Upload failed because of parser errors.");
			return Views.POST_PUBLICATION;
		}
		/* case:
		 * 	1) we are redirected to this page from a person page, and
		 * 	2) a new thesis wants to be added
		 * 
		 * only one thesis can be added each time (by snippet).
		 ***/
		if (command.getPerson() != null) {
			if ((posts != null) && (posts.size() > 1)) {
				this.errors.reject("error.add_new_thesis", "Only ONE new thesis is allowed to be added!");
				return Views.POST_PUBLICATION;
			}
		}
	
		/*
		 * If exactly one post has been extracted, and there were no parse exceptions, 
		 * the edit post controller can handle the remaining work.
		 */
		if ((posts != null) && (posts.size() == 1) && !this.errors.hasErrors()) {
			final Post<BibTex> post = posts.get(0);
			if (present(post)) {
				/*
				 * Delete the selection, otherwise the AbstractEditPublicationControllers 
				 * workOnCommand() method would try to scrape it.
				 */
				command.setSelection(null);
				command.setPost(post);
				/*
				 * When exactly one post is imported, its tags are not put into
				 * the tag field. Instead, we show them here as "tags of copied post".
				 */
				command.setCopytags(new LinkedList<Tag>(post.getTags()));
				return super.workOn(command);
			}
		}


		/*
		 * Complete the posts with missing information:
		 * 
		 * add additional information from the form to the
		 * post (description, groups)... present in both upload tabs
		 */
		final Set<String> unique_hashes = new TreeSet<String>();
		ErrorMessage errorMessage;
		if  (posts != null) {
			for (final Post<BibTex> post : posts) {
				post.setUser(context.getLoginUser());
				post.setDescription(command.getDescription());
				if (!present(post.getTags())) {
					post.setTags(Collections.singleton(TagUtils.getImportedTag()));
				}
				 /* set visibility of this post for the groups, the user specified
	
				 */
				GroupingCommandUtils.initGroups(command, post.getGroups());
				/*
				 * hashes have to be set, in order to call the validator
				 */
				post.getResource().recalculateHashes();
	
				/*
				 * user may import n bibtexes which m>1 of them are the same.
				 * 
				 * Since similar bibtexes have similar intrahashes, we find duplicate bibtexes
				 * by comparing intrahashes, and then add an error to not_unique bibtexes.
				 */
				if (!unique_hashes.contains(post.getResource().getIntraHash())) {
					unique_hashes.add(post.getResource().getIntraHash());
				} else {
					errorMessage = new DuplicatePostInSnippetErrorMessage("BibTex", post.getResource().getIntraHash());
					List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
					errorList.add(errorMessage);
					command.getPostsErrorList().put(post.getResource().getIntraHash(), errorList);
				}
			}
		}

		/*
		 * add list of posts to command for showing them to the user
		 * (such that he can edit them)
		 */
		final ListCommand<Post<BibTex>> postListCommand = new ListCommand<Post<BibTex>>(command);
		postListCommand.setList(posts);
		/*
		 * FIXME: rename the "bibtex" attribute of the command (hint: we try
		 * to avoid the name "bibtex" wherever possible)
		 * (hint: errors.pushNestedPath("bibtex"); in the PostPublicationCommandValidator 
		 * then has to be adapted, too. As does the code in the JSPs, of course.) 
		 */
		command.setBibtex(postListCommand);

		/*
		 * validate the posts
		 */
		ValidationUtils.invokeValidator(new PostPublicationCommandValidator(), command, this.errors);

		/*
		 * We try to store only posts that have no validation errors.
		 * The following function, add error(s) to the erroneous posts.
		 */
		final Map<Post<BibTex>, Integer> postsToStore = this.getPostsWithNoValidationErrors(posts, command.getPostsErrorList(),command.isOverwrite());

		if (log.isDebugEnabled()) {
			log.debug("will try to store " + postsToStore.size() + " of " + ((posts != null) ? Integer.toString(posts.size()) : "null") + " posts in database");
		}
		final List<Post<?>> validPosts = new LinkedList<Post<?>>(postsToStore.keySet());

		/*
		 * finally store the posts
		 */
		if (command.isEditBeforeImport()) {
			/*
			 * user wants to edit the posts before storing them
			 * -> put them into the session
			 */
			this.setSessionAttribute(TEMPORARILY_IMPORTED_PUBLICATIONS, validPosts);
			command.setUpdateExistingPost(false);

		} else {
			/*
			 * the publications are saved in the database
			 */
			this.storePosts(postsToStore, command.getOverwrite());
			command.setUpdateExistingPost(true);
		}

		/*
		 * If there are errors now or not - we return to the post
		 * publication view to let the user edit his/her posts. 
		 */
		return Views.POST_PUBLICATION;
	}

	/**
	 * Checks each post for validation errors and returns only those posts, 
	 * that don't have any errors. The posts are returned in a hashmap, where
	 * each post points to its position in the original list such that we can
	 * later add errors (from the database) at the correct position.
	 * 
	 * @param posts
	 * @return
	 */
	private Map<Post<BibTex>, Integer> getPostsWithNoValidationErrors(final List<Post<BibTex>> posts, final Map<String, List<ErrorMessage>> errorMessages, final boolean isOverwrite) {
		final Map<Post<BibTex>, Integer> storageList = new LinkedHashMap<Post<BibTex>, Integer>();
		/*
		 * iterate over all posts
		 */
		ErrorMessage errorMessage;
		for (int i = 0; i < posts.size(); i++) {
			boolean hasValidationErrors;//true, if the post has an error in errors. ...
			boolean isAlreadyInCollection = false;//true, if the post is already in the collection.
			boolean isAlreadyInSnippet = false;//true, if the post is already in the snippet.
			List<ErrorMessage> postErrorMessages = errorMessages.get(posts.get(i).getResource().getIntraHash());
			/*
			 * check, if this post has field errors
			 */
			hasValidationErrors = present(this.errors.getFieldErrors("bibtex.list[" + i + "]*"));

			/*
			 * check if this post is already stored in DB
			 * 
			 * We have already checked if this publication is in the snippet more than one time
			 * or not.
			 * (if yes, postErrorMessages.size() >= 1)
			 */
			if (present(postErrorMessages) && postErrorMessages.size() > 1) { 
				isAlreadyInSnippet = true;
			} else {
				if (present(postErrorMessages) && postErrorMessages.size()==1){
					isAlreadyInSnippet = true;
				}
				isAlreadyInCollection = this.isPostDuplicate(posts.get(i), isOverwrite);
				if (isAlreadyInCollection){
					errorMessage = new DuplicatePostErrorMessage("BibTex", posts.get(i).getResource().getIntraHash());
					if (!present(postErrorMessages)){
						postErrorMessages = new ArrayList<ErrorMessage>();
					}
					postErrorMessages.add(errorMessage);
					errorMessages.put(posts.get(i).getResource().getIntraHash(), postErrorMessages);
				}
			}
			if (!hasValidationErrors && !isAlreadyInCollection && !isAlreadyInSnippet) {
				log.debug("post no. " + i + " has no field errors");
				/*
				 * post has no field errors & is not duplicate--> try to store
				 * it in database
				 * 
				 * We also remember the original position of the post to
				 * add error messages later.
				 */
				storageList.put(posts.get(i), i);
			}
		}
		return storageList;
	}

	/**
	 * Extracts the parse exceptions and adds the line numbers with errors
	 * to the errors object.
	 * 
	 * @param parseExceptions
	 */
	private void handleParseExceptions(final ParseException[] parseExceptions) {
		final StringBuilder buf = new StringBuilder();
		boolean lineFound = false;
		for (final ParseException parseException : parseExceptions) {
			final Matcher m = lineNumberPattern.matcher(parseException.getMessage());
			if (m.find()) {
				/*
				 * if we have already found a broken line, append ", "
				 */
				if (lineFound) {
					buf.append(", ");
				}
				/*
				 * we have found a line number -> add it
				 */
				buf.append(m.group(1));
				lineFound = true;
			}
		}
		if (lineFound) {
			this.errors.reject("import.error.erroneous_line_numbers", new Object[] { buf }, "Your submitted publications contain errors at lines {0}.");
		}
	}

	/**
	 * Tries to save the posts in the database.
	 * 
	 * If posts already exist in the database and <code>overwrite</code> is
	 * <code>true</code>,
	 * those posts are overwritten (otherwise they produce an error).
	 * Posts that have errors will be rejected in any case.
	 * 
	 * FIXME: the error handling here is almost identical to that
	 * in {@link BatchEditController#storePosts}
	 * 
	 * @param postsToStore
	 * @param overwrite - posts which already exist are overwritten, if
	 *        <code>true</code>
	 */
	private void storePosts(final Map<Post<BibTex>, Integer> postsToStore, final boolean overwrite) {
		try {
			/*
			 * Try to save all posts in one transaction.
			 * (Hint: it's not a transaction in the database sense, but
			 * basically we try to
			 * save all posts and collect errors for posts we can't save.)
			 */
			this.logic.createPosts(new LinkedList<Post<?>>(postsToStore.keySet()));
		} catch (final DatabaseException e) {
			/*
			 * get error messages
			 */
			final Map<String, List<ErrorMessage>> errorMessages = e.getErrorMessages();
			log.debug("caught database exception, found " + errorMessages.size() + " errors");
			/*
			 * these posts will be updated
			 */
			final LinkedList<Post<?>> postsForUpdate = new LinkedList<Post<?>>();
			/*
			 * check for all posts what kind of errors they have
			 */
			for (final Entry<Post<BibTex>, Integer> entry : postsToStore.entrySet()) {
				/*
				 * get post and its position in the original list of posts
				 */
				final Post<BibTex> post = entry.getKey();
				final Integer i = entry.getValue();
				log.debug("found errors in post no. " + i);
				/*
				 * get all error messages for this post
				 */
				final List<ErrorMessage> postErrorMessages = errorMessages.get(post.getResource().getIntraHash());
				if (present(postErrorMessages)) {
					boolean hasErrors = false;
					boolean hasDuplicate = false;
					/*
					 * go over all error messages
					 */
					for (final ErrorMessage errorMessage : postErrorMessages) {
						log.debug("found error " + errorMessage);
						if (errorMessage instanceof DuplicatePostErrorMessage) {
							hasDuplicate = true;
							if (overwrite) {
								/*
								 * if we shall overwrite posts, duplicates are
								 * no errors
								 */
								continue;
							}
						}
						/*
						 * add error to error list
						 */
						hasErrors = true;
						this.errors.rejectValue("bibtex.list[" + i + "].resource", errorMessage.getErrorCode(), errorMessage.getParameters(), errorMessage.getDefaultMessage());
					}
					/*
					 * If the post has no errors, but is a duplicate, we add it
					 * to
					 * the list of posts which should be updated.
					 */
					if (!hasErrors && hasDuplicate) {
						postsForUpdate.add(post);
					}
				}
			}

			/*
			 * If we got ONLY duplicate "errors", we save the non-duplicate ones
			 * and update the others, if isOverwrite is true. Same is true, if
			 * the number of publications is greater than the threshold.
			 */
			try {
				if (overwrite) {
					log.debug("trying to update " + postsForUpdate.size() + " posts");
					this.logic.updatePosts(postsForUpdate, PostUpdateOperation.UPDATE_ALL);
				}
			} catch (final DatabaseException ex) {
				/*
				 * FIXME: The catch is only for logging. Do we need that much?
				 */
				final Map<String, List<ErrorMessage>> allErrorMessages = ex.getErrorMessages();
				log.debug("caught database exception, found " + allErrorMessages.size() + " errors");
				/*
				 * checking each post for errors
				 */
				for (final Post<?> post : postsForUpdate) {
					/*
					 * get intra hash and original position of post
					 */
					final String intraHash = post.getResource().getIntraHash();
					/*
					 * The i-th position in the list at hand is not the
					 * same as the i-th position in the original list! ->use
					 * mapping
					 */
					final int i = postsToStore.get(post);
					log.debug("checking post no. " + i + " with intra hash " + intraHash);
					final List<ErrorMessage> postErrorMessages = allErrorMessages.get(intraHash);
					if (present(postErrorMessages)) {
						log.debug("found " + postErrorMessages.size() + "error(s) on post no. " + i);
					}
				}
				log.debug("all field errors: " + this.errors.getFieldError("bibtex.*"));
			}
		}
	}

	@Override
	protected PostPublicationCommand instantiateEditPostCommand() {
		return new PostPublicationCommand();
	}

	/**
	 * @param publicationImporter the publicationImporter to set
	 */
	public void setPublicationImporter(final PublicationImporter publicationImporter) {
		this.publicationImporter = publicationImporter;
	}

	private boolean isPostDuplicate(final Post<BibTex> post, final boolean isOverwrite) {

		final String userName = post.getUser().getName();
		final String intraHash = post.getResource().getIntraHash();

		if (!isOverwrite && present(this.logic.getPostDetails(intraHash, userName))) {
			return true;
		}
		return false;
	}
}