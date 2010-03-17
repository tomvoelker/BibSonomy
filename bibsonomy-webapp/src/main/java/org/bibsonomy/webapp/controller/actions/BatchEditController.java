package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.SystemTagErrorMessage;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.actions.BatchEditCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;


/**
 * Controller to batch edit (update tags and delete) resources.
 * 
 * The controller handles two cases:
 * <ol>
 * <li>the given posts should be updated (and eventually some posts deleted - if the user flagged them)</li>
 * <li>the given posts should be stored (and eventually some posts ignored - if the user flagged them)</li>
 * </ol>
 * 
 * @author dzo
 * @version $Id$
 */
public class BatchEditController implements MinimalisticController<BatchEditCommand>, ErrorAware {

	private static final int HASH_LENGTH = 32;
	private static final Log log = LogFactory.getLog(BatchEditController.class);

	/**
	 * To redirect the user to the page she initially viewed before pressing
	 * the (batch)"edit" button, we need to strip the "bedit*" part of the URL
	 * using this pattern.  
	 */
	private static final Pattern BATCH_EDIT_URL_PATTERN = Pattern.compile("(bedit[a-z,A-Z]+/)");

	private RequestLogic requestLogic;
	private LogicInterface logic;

	private Errors errors;

	@Override
	public BatchEditCommand instantiateCommand() {
		final BatchEditCommand command = new BatchEditCommand();
		command.setOldTags(new HashMap<String, String>());
		command.setNewTags(new HashMap<String, String>());
		command.setDelete(new HashMap<String, Boolean>());

		command.getBibtex().setList(new LinkedList<Post<BibTex>>());
		command.getBookmark().setList(new LinkedList<Post<Bookmark>>());
		return command;
	}

	@Override
	public View workOn(final BatchEditCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * check if user is logged in
		 */
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.LOGIN;
		}

		/*
		 * check if ckey is valid
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}

		/*
		 * get user name
		 */
		final String loginUserName = context.getLoginUser().getName();

		log.debug("batch edit for user " + loginUserName + " started");




		/* *******************************************************
		 * FIRST: determine some flags which control the operation
		 * *******************************************************/
		/*
		 * the type of resource we're dealing with 
		 */
		final String resourceType = command.getResourcetype();
		final boolean postsArePublications = BibTex.class.getSimpleName().equalsIgnoreCase(resourceType);
		/*
		 * FIXME: rename/check setting of that flag in the command
		 */
		final boolean flagMeansDelete = command.getDeleteCheckedPosts();  
		/*
		 * When the user can flag posts to be deleted, this means those
		 * posts already exist. Thus, all other posts must be updated.
		 * 
		 * The other setting is, where the posts don't exist in the database
		 * (only in the session) and where they must be stored.
		 */
		final boolean updatePosts = flagMeansDelete;



		/* *******************************************************
		 * SECOND: get the data we're working on
		 * *******************************************************/
		/*
		 * posts that are flagged are either deleted or ignored 
		 */
		final Map<String, Boolean> postFlags = command.getDelete();
		/*
		 * put the posts from the session into a hash map (for faster access)
		 */
		final HashMap<String, Post<?>> postMap = getPostMap(updatePosts);
		/*
		 * the tags that should be added to all posts
		 */
		final Set<Tag> addTags = getAddTags(command.getTags());
		/*
		 * for each post we have its old tags and its new tags
		 */
		final Map<String, String> newTagsMap = command.getNewTags();
		final Map<String, String> oldTagsMap = command.getOldTags();




		/* *******************************************************
		 * THIRD: initialize temporary variables (lists)
		 * *******************************************************/
		/*
		 * create lists for the different types of actions 
		 */
		final List<String> postsToDelete = new LinkedList<String>();   // delete
		final List<Post<?>> postsToUpdate = new LinkedList<Post<?>>(); // update/store
		/*
		 * All posts will get the same date.
		 */
		final Date now = new Date();




		/* *******************************************************
		 * FOURTH: prepare the posts
		 * *******************************************************/
		/*
		 * loop through all hashes and check for each post, what to do
		 */
		for (final String intraHash : newTagsMap.keySet()) {
			/*
			 * short check if hash is correct
			 */
			if (intraHash.length() != HASH_LENGTH) continue;
			/*
			 * has this post been flagged by the user? 
			 */
			if (postFlags.containsKey(intraHash) && postFlags.get(intraHash)) {
				/*
				 * The post has been flagged by the user.
				 * Depending on the meaning of this flag, we add the 
				 * post to the list of posts to be deleted or just
				 * ignore it.
				 */
				if (flagMeansDelete) {
					/*
					 * flagged posts should be deleted, i.e., add them
					 * to the list of posts to be deleted and work on 
					 * the next post.
					 */
					postsToDelete.add(intraHash);
				}
				/*
				 * flagMeansDelete = true:  delete the post
				 * flagMeansDelete = false: ignore the post (neither save nor update it)
				 */
				continue;
			}
			/*
			 * We must store/update the post, thus we parse and check its tags
			 */
			try {
				final Set<Tag> oldTags = TagUtils.parse(oldTagsMap.get(intraHash));
				final Set<Tag> newTags = TagUtils.parse(newTagsMap.get(intraHash));
				/*
				 * we add all global tags to the set of new tags 
				 */
				newTags.addAll(addTags);
				/*
				 * if we want to update the posts, we only need to update posts
				 * where the tags have changed
				 */
				if (updatePosts && oldTags.equals(newTags)) {
					/*
					 * tags haven't changed, nothing to do
					 */
					continue;
				}
				/*
				 * For the create/update methods we need a post -> 
				 * create/get one.
				 */
				final Post<?> post;
				if (updatePosts) {
					/*
					 * we need only a "mock" posts containing the hash,
					 * username and the tags, since only the post's tags 
					 * are updated 
					 */
					post = new Post<Resource>();
					/*
					 * FIXME: create the appropriate resource (Bookmark or BibTex)
					 */
					post.getResource().setIntraHash(intraHash);
				} else {
					/*
					 * we get the complete post from the session, and store
					 * it in the database
					 */
					post = postMap.get(intraHash);
					// we should only add posts to that list that have errors (don't show ALL posts again)					
					//					/*
					//					 * needed when page is called with no imported posts
					//					 * FIXME: really?
					//					 */
					//					if (!present(post)) continue;
					//					/*
					//					 * FIXME: why do we need that?
					//					 */
					//					if (postsArePublications) {
					//						command.getBibtex().getList().add((Post<BibTex>) post);
					//					} else {
					//						command.getBookmark().getList().add((Post<Bookmark>)post);
					//					}
				}
				/*
				 * Finally, add the post to the list of posts that should 
				 * be stored or updated.
				 */
				if (!present(post)) {
					log.warn("post with hash " + intraHash + " not found for user " + loginUserName + " while updating tags");
				} else {
					/*
					 * set the date and the tags for this post 
					 * (everything else should already be set or not be changed)
					 */
					post.setDate(now);
					post.setTags(newTags);
					postsToUpdate.add(post);
				}

			} catch (final RecognitionException ex) {
				log.debug("can't parse tags of resource " + intraHash + " for user " + loginUserName, ex);
			}
		}




		/* *******************************************************
		 * FIFTH: update the database
		 * *******************************************************/
		/*
		 * delete posts
		 */
		if (present(postsToDelete)) {
			log.debug("deleting "  + postsToDelete.size() + " posts for user " + loginUserName);
			this.logic.deletePosts(loginUserName, postsToDelete);
		}
		/*
		 * update/store posts
		 */
		if (updatePosts) {
			/*
			 * FIXME: error handling missing (system tags errors!)
			 */
			log.debug("updating " + postsToUpdate.size() + " posts for user " + loginUserName);
			this.logic.updatePosts(postsToUpdate, PostUpdateOperation.UPDATE_TAGS); 
		} else {
			log.debug("storing "  + postsToUpdate.size() + " posts for user " + loginUserName);
			storePosts(postsToUpdate, command.isOverwrite(), resourceType, postMap);
		}

		log.debug("finished batch edit for user " + loginUserName);




		/* *******************************************************
		 * SIXTH: return to view
		 * *******************************************************/
		/*
		 * return to batch edit view on errors
		 */
		if (errors.hasErrors()) {
			if (postsArePublications) {
				/*
				 * FIXME: changed from Views.BATCHEDIT_TEMP_BIB to Views.BATCHEDITBIB 
				 * without setting the corresponding boolean command.editBeforeImport
				 * that would trigger the behaviour of Views.BATCHEDIT_TEMP_BIB.
				 * (problem: that attribute is not available in the command at hand)    
				 */
				return Views.BATCHEDITBIB;
			} 
			return Views.BATCHEDITURL;  
		}
		/*
		 * return to the page the user was initially coming from
		 * 
		 * FIXME: where is command.referer filled?
		 */
		return getFinalRedirect(command.getReferer(), loginUserName);
	}

	/**
	 * Tries to store the posts in the database, updates them if 
	 * necessary (duplicate) and allowed to to so (overwrite = true).
	 *
	 * FIXME: the error handling here is almost identical to that
	 * in {@link PostPublicationController#savePosts}
	 * 
	 * @param posts
	 * @param overwrite
	 * @param resourceType
	 * @param postMap
	 */
	private void storePosts(final List<Post<?>> posts, final boolean overwrite, final String resourceType, final HashMap<String, Post<?>> postMap) {
		final List<Post<?>> postsWithErrors = new LinkedList<Post<?>>();
		final List<Post<?>> postsForUpdate  = new LinkedList<Post<?>>();
		try {
			/*
			 * let's try to store the posts ...
			 */
			this.logic.createPosts(posts);
		} catch (final DatabaseException ex) {
			/*
			 * we expect, that something might happen ...
			 */
			final Map<String, List<ErrorMessage>> errorMessages = ex.getErrorMessages();
			/*
			 * check all error messages ...
			 */
			for (final String postHash: errorMessages.keySet()) {
				final Post<?> post = postMap.get(postHash);
				log.debug("checking errors for post " + postHash);
				/*
				 * get all error messages for this post
				 */
				final List<ErrorMessage> postErrorMessages = errorMessages.get(postHash);
				if (present(postErrorMessages)) {
					boolean hasErrors = false;
					boolean hasDuplicate = false;
					/*
					 * Error messages are connected with the erroneous posts
					 * via the post's position in the error list.
					 */
					final int postId = postsWithErrors.size() - 1;
					/*
					 * go over all error messages 
					 */
					for (final ErrorMessage errorMessage : postErrorMessages) { 
						log.debug("found error " + errorMessage);
						final String errorItem;
						if (errorMessage instanceof DuplicatePostErrorMessage) {
							hasDuplicate = true;
							if (overwrite) {
								/*
								 * if we shall overwrite posts, duplicates are no errors
								 */
								continue;
							} 
							errorItem = "resource";
						} else if (errorMessage instanceof SystemTagErrorMessage) {
							errorItem = "tags";
						} else {
							errorItem = "resource";
						}
						/*
						 * add error to list
						 * FIXME: postId is wrong!
						 */
						hasErrors = true;
						errors.rejectValue(resourceType+".list[" + postId + "]." + errorItem, errorMessage.getErrorCode(), errorMessage.getParameters(), errorMessage.getDefaultMessage());
					}
					if (hasErrors) {
						/*
						 * show the user the erroneous post
						 */
						postsWithErrors.add(post);
					} else if (hasDuplicate) {
						/*
						 * If the post has no errors, but is a duplicate, we add it to
						 * the list of posts which should be updated. 
						 */
						postsForUpdate.add(post);
					}
				}

			}
			if (overwrite) {
				try {
					this.logic.updatePosts(postsForUpdate, PostUpdateOperation.UPDATE_ALL);
				} catch (final DatabaseException ex1) {
					final Map<String, List<ErrorMessage>> allErrorMessages = ex1.getErrorMessages();
					/*
					 * iterating over all error messages ....
					 */
					for (final String postHash : errorMessages.keySet()) {
						/*
						 * Error messages are connected with the erroneous posts
						 * via the post's position in the error list.
						 */
						final int postId = postsWithErrors.size() - 1;
						boolean hasErrors = false;
						for (final ErrorMessage errorMessage: allErrorMessages.get(postHash)) { 
							if (errorMessage instanceof SystemTagErrorMessage) {
								/*
								 * add post to list of erroneous posts
								 */
								errors.rejectValue(resourceType+".list[" + postId + "].tags", errorMessage.getErrorCode(), errorMessage.getParameters(), errorMessage.getDefaultMessage());
								hasErrors = true;
							}
							
						}
						/*
						 * we add the post here and not in the above if-statement, because
						 * it could have several system tag error messages
						 * and then would be added several times to the list.
						 */
						if (hasErrors) {
							postsWithErrors.add(postMap.get(postHash));
						}
					}
				}
			}
		}
	}

	/**
	 * If updatePosts is false, we have to store the posts from 
	 * the session in the database. Therefore, this method gets 
	 * those posts from the session and puts them into a hashmap
	 * for faster access. 
	 * 
	 * @param updatePosts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Post<?>> getPostMap(final boolean updatePosts) {
		final HashMap<String, Post<?>> postMap = new HashMap<String, Post<?>>();
		final List<Post<?>> postsFromSession = (List<Post<?>>) this.requestLogic.getSessionAttribute(PostPublicationController.TEMPORARILY_IMPORTED_PUBLICATIONS);
		if (!updatePosts && ValidationUtils.present(postsFromSession)) {
			/*
			 * Put the posts into a hashmap, so we don't have to loop 
			 * through the list for every stored post.
			 */
			for (final Post<?> post : postsFromSession) {
				postMap.put(post.getResource().getIntraHash(), post);
			}
		}
		return postMap;
	} 


	/**
	 * Parses the tags that should be added to each post. 
	 * 
	 * @param addTagString
	 * @return
	 */
	private Set<Tag> getAddTags(final String addTagString) {
		try {
			/*
			 * ensure, that we don't try to parse a null string
			 */
			return TagUtils.parse(present(addTagString) ? addTagString : "");
		} catch (final RecognitionException ex) {
			log.warn("can't parse tags that should be added to all posts", ex);
		}
		return Collections.emptySet();
	}

	/**
	 * If the referer points to /bedit{bib,url}/abc, we redirect to /abc, otherwise
	 * to /user/loginUserName
	 * 
	 * @param referer
	 * @param loginUserName
	 * @return
	 */
	private View getFinalRedirect(final String referer, final String loginUserName) {
		String redirectUrl = referer;
		/*
		 * if we come from bedit{bib, burl}/{group, user}/{groupname, username},
		 * we remove this prefix to get back to the simple resource view in the group or user section
		 */
		final Matcher prefixMatcher = BATCH_EDIT_URL_PATTERN.matcher(referer);
		if (prefixMatcher.find())
			redirectUrl = prefixMatcher.replaceFirst("");
		/*
		 * if no URL is given, we redirect to the user's page
		 */
		if (!present(redirectUrl)) 
			redirectUrl = encodeStringToUTF8("/user" + loginUserName);
		return new ExtendedRedirectView(redirectUrl);
	}


	/**
	 * encodes a string to utf-8 format
	 * 
	 * TODO: extract to helper class, ...
	 * 
	 * @param toEncode
	 * @return the encoded utf-8 string
	 */
	private static String encodeStringToUTF8(String toEncode) {
		try {
			return URLEncoder.encode(toEncode, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return toEncode;
		}
	}


	@Override
	public Errors getErrors() {
		return this.errors;
	}


	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * sets the logic
	 * @param logic the logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return the request logic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	/**
	 * sets the requestLogic
	 * @param requestLogic the RequestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

}
