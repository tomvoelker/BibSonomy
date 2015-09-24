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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.actions.BatchEditCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller to batch edit (update tags and delete) resources.
 * 
 * The controller handles two cases if multiple posts are edited (on batch edit
 * site or post publication site):
 * <ol>
 * <li>the given posts should be updated (and eventually some posts deleted or
 * normalized - if the user flagged them)</li>
 * <li>the given posts should be stored (and eventually some posts ignored - if
 * the user flagged them)</li>
 * </ol>
 * 
 * Another distinction is between updating existing posts and editing posts
 * before actually storing them. The latter happens when a user selects
 * "edit before import" on a batch edit page.
 * 
 * The controller also updates tags for single posts with fast edit or on bibtex
 * details page.
 * 
 * @author pbu
 * @author dzo
 * @author ema
 * @author Nasim
 */
public class BatchEditController implements MinimalisticController<BatchEditCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(BatchEditController.class);

	private static final int HASH_LENGTH = 32;

	/*
	 * TODO: use enum for this
	 */
	private static final int IGNORE_ACTION = 0;
	private static final int ADD_TAGS_TO_ALL_POSTS_ACTION = 1;
	private static final int UPDATE_TAGS_OF_INDIVIDUAL_POSTS_ACTION = 2;
	private static final int NORMALIZE_ACTION = 3;
	private static final int DELETE_ACTION = 4;
	private static final int UPDATE_VIEWABLE_ACTION = 5;

	/**
	 * 
	 * @param resourceClass
	 * @return the old resource name
	 */
	@Deprecated
	// TODO: remove as soon as bibtex is renamed to publication in
	// SimpleResourceViewCommand
	public static String getOldResourceName(final Class<? extends Resource> resourceClass) {
		if (BibTex.class.equals(resourceClass)) {
			return "bibtex";
		}
		return ResourceFactory.getResourceName(resourceClass);
	}

	private RequestLogic requestLogic;
	private LogicInterface logic;

	private Errors errors;
	private URLGenerator urlGenerator;

	@Override
	public BatchEditCommand instantiateCommand() {
		final BatchEditCommand command = new BatchEditCommand();
		command.setOldTags(new HashMap<String, String>());
		command.setNewTags(new HashMap<String, String>());
		command.setPosts(new HashMap<String, Boolean>());

		command.getBibtex().setList(new LinkedList<Post<BibTex>>());
		command.getBookmark().setList(new LinkedList<Post<Bookmark>>());

		command.setGroups(new ArrayList<String>());
		command.setAbstractGrouping(GroupUtils.buildPublicGroup().getName());
		// command.setSelectNorm(true);
		command.setAction(new ArrayList<Integer>());
		return command;
	}

	/**
	 * This controller is called in two cases:
	 * 1. DirectEdit: When user clicks the gear button and selects 'edit own
	 * entries'
	 * 2. Import: When user imports several bibTexes through: Add post-> post
	 * publication->BibTeX/EndNote snippet
	 * 
	 * Differences:
	 * In direct edit, user each time can apply one edit option to the posts. So
	 * we have a list for each edit option in order to reduce database call load
	 * for each update.
	 * In indirect edit, choosing several edit options at the same time is also
	 * possible. So we have only one list (postsToCombiUpdate) of posts and a
	 * complete update will be performed on the post.
	 */
	@Override
	public View workOn(final BatchEditCommand command) {

		/*
		 * FIXME: rename the variables in this method. Most names are no longer
		 * suitable and refer to the older version where this controller only
		 * could handle tag updates. E.g. postsToNormalize, the tagMaps etc.
		 */
		final RequestWrapperContext context = command.getContext();

		/*
		 * We store the referer in the command, to send the user back to the
		 * page he's coming from at the end of the posting process.
		 */
		if (!ValidationUtils.present(command.getReferer())) {
			command.setReferer(this.requestLogic.getReferer());
		}

		/*
		 * check if user is logged in
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}

		/*
		 * check if ckey is valid
		 */
		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}

		/*
		 * get user name
		 */
		final String loginUserName = context.getLoginUser().getName();

		/*
		 * get edit action
		 * FIXME: Turn this into an enum
		 */
		final List<Integer> action = command.getAction();

		if (action.contains(IGNORE_ACTION)) {
			return this.getFinalRedirect(command.getReferer(), loginUserName);
		}

		/* *******************************************************
		 * FIRST: determine some flags which control the operation
		 * ******************************************************
		 */

		/*
		 * the type of resource we're dealing with
		 */
		final Set<Class<? extends Resource>> resourceTypes = command.getResourcetype();
		if (resourceTypes.size() != 1) {
			throw new IllegalArgumentException("Please provide exactly one resource type");
		}
		final boolean postsArePublications = resourceTypes.contains(BibTex.class);
		final Class<? extends Resource> resourceClass = resourceTypes.iterator().next();

		/*
		 * check if editing is direct
		 */
		final boolean directEdit = command.isDirectEdit();

		log.debug("batch edit for user " + loginUserName + " started");
		/**
		 * checks whether new post(s) should be stored (and updated) or we are
		 * editing
		 * existing post(s)
		 */
		final boolean updatePosts = command.isUpdateExistingPost();

		/* *******************************************************
		 * SECOND: get the data we're working on
		 * ******************************************************
		 */
		/*
		 * put the posts (if they are not new posts) from the session into a
		 * hash map (for faster access)
		 */
		final Map<String, Post<? extends Resource>> postMap = this.getPostMap(updatePosts);
		final Map<String, Boolean> markedPostsMap = command.getPosts();
		/*
		 * the tags that should be added to all posts
		 */
		final Set<Tag> addTags = this.getAddTags(command.getTags());
		/*
		 * for each post we have its old tags and its new tags
		 */
		Map<String, String> newTagsMap = new HashMap<String, String>();
		final Map<String, String> oldTagsMap = command.getOldTags();

		log.debug("#postFlags: " + markedPostsMap.size() + ", #postMap: " + postMap.size() + ", #addTags: " + addTags.size() + ", #newTags: " + newTagsMap.size() + ", #oldTags: " + oldTagsMap.size());

		/* *******************************************************
		 * THIRD: initialize temporary variables (lists)
		 * ******************************************************
		 */

		/*
		 * create lists for the different types of actions
		 */
		final List<String> postsToDelete = new LinkedList<String>();
		final List<Post<?>> postsToUpdateTags = new LinkedList<Post<?>>();
		final List<Post<?>> postsToNormalize = new LinkedList<Post<?>>();
		final List<Post<?>> postsToUpdateViewable = new LinkedList<Post<?>>();
		// several updates actions at the same time
		final List<Post<?>> postsToCombiUpdate = new LinkedList<Post<?>>();
		/*
		 * All updated posts will get the same date.
		 */
		final Date now = new Date();

		/* *******************************************************
		 * FOURTH: prepare the posts
		 * ******************************************************
		 */
		/*
		 * loop through all hashes and check for each post, what to do
		 */
		for (final Entry<String, Boolean> markedPost : markedPostsMap.entrySet()) {
			if (!markedPost.getValue()) {
				continue;
			}
			final String intraHash = markedPost.getKey();
			log.debug("working on post " + intraHash);
			/*
			 * short check if hash is correct
			 */
			if (intraHash.length() != HASH_LENGTH) {
				throw new IllegalArgumentException("Hashes must be of length " + HASH_LENGTH);
			}

			/*
			 * STEP 1: Check if post should be deleted or ignored.
			 */
			if (action.contains(DELETE_ACTION)) {
				postsToDelete.add(intraHash);
				continue;
			}

			/*
			 * STEP 2: Get the post (either from storage or from the temporary
			 * collection in the command
			 */
			Post<?> post;

			if (updatePosts) {
				// updating a post which is already stored
				post = this.logic.getPostDetails(intraHash, loginUserName);
				if (!ValidationUtils.present(post)) {
					log.warn("post with hash " + intraHash + " not found for user " + loginUserName + " while updating");
					continue;
				}
			} else {
				// the post is only temporarily stored
				post = postMap.get(intraHash);
			}

			/*
			 * FIXME: should we do that now? Shouldn't we update the date only
			 * if we actually do something?
			 */
			post.setDate(now);

			if (action.contains(NORMALIZE_ACTION)) {
				if (!BibTex.class.isAssignableFrom(resourceClass)) {
					throw new IllegalArgumentException("BibTex Key can only be normalized for publications");
				}
				final BibTex bibtex = (BibTex) post.getResource();

				if (ValidationUtils.present(bibtex)) {
					final String oldBibtexKey = bibtex.getBibtexKey();
					final String newBibtexKey = BibTexUtils.generateBibtexKey(bibtex);

					if (ValidationUtils.present(newBibtexKey)) {
						if (!newBibtexKey.equals(oldBibtexKey)) {
							((BibTex) post.getResource()).setBibtexKey(newBibtexKey);
						}
					}
				}
				if (directEdit) {
					postsToNormalize.add(post);
					/**
					 * we do not need to go further, because in direct
					 * mode,
					 * only one edit option is performed on each post.
					 **/
					continue;
				}
			}
			if (action.contains(ADD_TAGS_TO_ALL_POSTS_ACTION) || action.contains(UPDATE_TAGS_OF_INDIVIDUAL_POSTS_ACTION)) {
				/*
				 * We must store/update the post, thus we parse and
				 * check its tags
				 */
				try {
					TagUtils.parse(oldTagsMap.get(intraHash));
					final Set<Tag> newTags = new TreeSet<Tag>();
					// the following 'if' is for indirect mode, in which
					// both updates can be done at the same time.

					if (action.contains(ADD_TAGS_TO_ALL_POSTS_ACTION) && action.contains(UPDATE_TAGS_OF_INDIVIDUAL_POSTS_ACTION)) {
						newTags.addAll(TagUtils.parse(newTagsMap.get(intraHash)));
						newTags.addAll(getTagsCopy(addTags));
					} else if (action.contains(UPDATE_TAGS_OF_INDIVIDUAL_POSTS_ACTION)) {
						newTags.addAll(TagUtils.parse(newTagsMap.get(intraHash)));
					} else if (action.contains(ADD_TAGS_TO_ALL_POSTS_ACTION)) {
						newTags.addAll(TagUtils.parse(oldTagsMap.get(intraHash)));
						newTags.addAll(getTagsCopy(addTags));
					}
					post.setTags(newTags);
					if (directEdit) {
						postsToUpdateTags.add(post);
						continue;
					}
				} catch (final RecognitionException ex) {
					log.debug("can't parse tags of resource " + intraHash + " for user " + loginUserName, ex);
				}
			}
			if (action.contains(UPDATE_VIEWABLE_ACTION)) {
				/**
				 * set visibility of this post for the groups,
				 * the user specified
				 */
				GroupingCommandUtils.initGroups(command, post.getGroups());
				if (directEdit) {
					postsToUpdateViewable.add(post);
					continue;
				}
			}
			/**
			 * if we reach here, it means we have skipped all
			 * 'continues' and we
			 * are in indirect_edit mode
			 */
			postsToCombiUpdate.add(post);
		}

		/* *******************************************************
		 * FIFTH: update the database
		 * ******************************************************
		 */
		/*
		 * delete posts
		 */
		if (ValidationUtils.present(postsToDelete)) {
			log.debug("deleting " + postsToDelete.size() + " posts for user " + loginUserName);
			try {
				this.logic.deletePosts(loginUserName, postsToDelete);
			} catch (final IllegalStateException e) {
				// ignore - posts were already deleted
			}
		}

		/*
		 * after update/store contains all posts with errors, to show them to
		 * the user for correction
		 */
		final List<Post<? extends Resource>> postsWithErrors = new LinkedList<Post<? extends Resource>>();
		if (postsArePublications) {
			command.setBibtex(new ListCommand<Post<BibTex>>(command, (List) postsWithErrors));
		} else {
			command.setBookmark(new ListCommand<Post<Bookmark>>(command, (List) postsWithErrors));
		}

		/*
		 * update/store posts
		 */
		if (directEdit) {
			if (ValidationUtils.present(postsToUpdateTags)) {
				this.updatePosts(postsToUpdateTags, resourceClass, postMap, postsWithErrors, PostUpdateOperation.UPDATE_TAGS, loginUserName);
			}
			/*
			 * in the two following updates, postUpdateOperation is set to
			 * UPDATE_NORMALIZE and UPDATE_VIEWABLE in order to decrease
			 * database calling load. But actually for some database reasons, we
			 * will ignore the postUpdateOperation value in database manager
			 * and Update_all will be performed on the posts. Corresponding
			 * methods are available but commented in database manager.
			 * When the database issue is corrected, you should simply uncomment
			 * corresponding methods in database manager and
			 * the PostUpdateOperation value will be considered then.
			 */
			if (ValidationUtils.present(postsToNormalize)) {
				this.updatePosts(postsToNormalize, resourceClass, postMap, postsWithErrors, PostUpdateOperation.UPDATE_NORMALIZE, loginUserName);
			}
			if (ValidationUtils.present(postsToUpdateViewable)) {
				this.updatePosts(postsToUpdateViewable, resourceClass, postMap, postsWithErrors, PostUpdateOperation.UPDATE_VIEWABLE, loginUserName);
			}
		} else {// if import
			if (updatePosts) {
				this.updatePosts(postsToCombiUpdate, resourceClass, postMap, postsWithErrors, PostUpdateOperation.UPDATE_ALL, loginUserName);
			} else {
				/*
				 * FIXME:What happens to those posts that should have been
				 * stored but have not been selected for the post update
				 * operation? Currently they are ignored and not stored. This is
				 * a bit confusing.
				 */
				log.debug("storing " + postsToUpdateTags.size() + " posts for user " + loginUserName);
				this.storePosts(postsToCombiUpdate, resourceClass, postMap, postsWithErrors, command.isOverwrite(), loginUserName);
			}
		}
		log.debug("finished batch edit for user " + loginUserName);

		/* *******************************************************
		 * SIXTH: return to view
		 * ******************************************************
		 */
		/*
		 * handle AJAX requests
		 */
		if ("ajax".equals(command.getFormat())) {
			return Views.AJAX_EDITTAGS;
		}

		/*
		 * return to batch edit view on errors
		 */
		if (this.errors.hasErrors()) {
			if (postsArePublications) {
				return Views.BATCHEDITBIB;
			}
			return Views.BATCHEDITURL;
		}

		/*
		 * return to either the user page or current page(batchedit)
		 */
		return this.getFinalRedirect(command.getReferer(), loginUserName);

	}

	/**
	 * Returns a copy of the given tags (i.e., new instances!)
	 * 
	 * @param tags
	 * @return
	 */
	private static Set<Tag> getTagsCopy(final Set<Tag> tags) {
		final Set<Tag> tagsCopy = new TreeSet<Tag>();
		for (final Tag tag : tags) {
			tagsCopy.add(new Tag(tag));
		}
		return tagsCopy;
	}

	/**
	 * Tries to store the posts in the database, updates them if
	 * necessary (duplicate) and allowed to to so (overwrite = true).
	 * 
	 * FIXME: the error handling here is almost identical to that
	 * in {@link PostPublicationController#savePosts}
	 * 
	 * @param posts - the posts that should be stored
	 * @param resourceType - the type of resource the posts contain
	 * @param postMap - to access posts using their hash
	 * @param overwrite
	 * @param loginUserName TODO
	 */
	private void storePosts(final List<Post<? extends Resource>> posts, final Class<? extends Resource> resourceType, final Map<String, Post<?>> postMap, final List<Post<?>> postsWithErrors, final boolean overwrite, final String loginUserName) {
		final List<Post<?>> postsForUpdate = new LinkedList<Post<?>>();
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
			for (final String postHash : errorMessages.keySet()) {
				final Post<?> post = postMap.get(postHash);
				log.debug("checking errors for post " + postHash);
				/*
				 * get all error messages for this post
				 */
				final List<ErrorMessage> postErrorMessages = errorMessages.get(postHash);
				if (ValidationUtils.present(postErrorMessages)) {
					boolean hasErrors = false;
					boolean hasDuplicate = false;
					/*
					 * Error messages are connected with the erroneous posts
					 * via the post's position in the error list.
					 */
					final int postId = postsWithErrors.size();
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
						 * add post to list of erroneous posts
						 * (only if it has no errors already, to not add it
						 * twice)
						 */
						if (!hasErrors) {
							postsWithErrors.add(post);
						}
						hasErrors = true;
						this.errors.rejectValue(getOldResourceName(resourceType) + ".list[" + postId + "].resource", errorMessage.getErrorCode(), errorMessage.getParameters(), errorMessage.getDefaultMessage());
					}
					if (!hasErrors && hasDuplicate) {
						/*
						 * If the post has no errors, but is a duplicate, we add
						 * it to
						 * the list of posts which should be updated.
						 */
						postsForUpdate.add(post);
					}
				}

			}
			if (overwrite) {
				/*
				 * try to update the posts
				 */
				this.updatePosts(postsForUpdate, resourceType, postMap, postsWithErrors, PostUpdateOperation.UPDATE_ALL, loginUserName);
			}
		}
	}

	/**
	 * Tries to update the posts in the database.
	 * 
	 * @param posts - the posts that should be updated
	 * @param resourceType - the type of resource the posts contain
	 * @param postMap - to access posts using their hash
	 * @param postsWithErrors - the list of posts that already had errors. All
	 *        erroneous posts are added to that list
	 * @param operation - the type of operation that should be performed with
	 *        the posts in the database.
	 * @param loginUserName - to complete the post from the database, we need
	 *        the user's name
	 */
	private void updatePosts(final List<Post<? extends Resource>> posts, final Class<? extends Resource> resourceType, final Map<String, Post<?>> postMap, final List<Post<?>> postsWithErrors, final PostUpdateOperation operation, final String loginUserName) {
		try {
			this.logic.updatePosts(posts, operation);
		} catch (final DatabaseException ex) {
			final Map<String, List<ErrorMessage>> allErrorMessages = ex.getErrorMessages();
			/*
			 * iterating over all posts ....
			 */
			for (final Post<?> updatedPost : posts) {
				final String postHash = updatedPost.getResource().getIntraHash();
				/*
				 * get errors for this post
				 */
				final List<ErrorMessage> postErrorMessages = allErrorMessages.get(postHash);
				/*
				 * if there are no errors, continue
				 */
				if (!ValidationUtils.present(postErrorMessages)) {
					continue;
				}
				/*
				 * Error messages are connected with the erroneous posts
				 * via the post's position in the error list.
				 */
				final int postId = postsWithErrors.size();
				boolean hasErrors = false;
				for (final ErrorMessage errorMessage : postErrorMessages) {
					log.debug("found error " + errorMessage);
					/*
					 * add post to list of erroneous posts to show them the user
					 */
					if (!hasErrors) {
						/*
						 * we check for errors, to not add the post twice (if it
						 * has several errors)
						 * 
						 * NOTE: we need the complete post (not only hash or so)
						 * to
						 * show it on the batch edit page.
						 */
						Post<?> post = null;
						if (PostUpdateOperation.UPDATE_ALL.equals(operation)) {
							/*
							 * XXX: we use the type of operation as indicator
							 * where to get the posts from
							 * 
							 * Here, the complete post shall be updated, hence,
							 * we get it from
							 * the session (user is editing tags after importing
							 * posts).
							 */
							post = postMap.get(postHash);
						} else {
							/*
							 * only the tags shall be updated -> we got only the
							 * hash from
							 * the page and must get the post from the database
							 */
							try {
								post = this.logic.getPostDetails(postHash, loginUserName);
								/*
								 * we must add the tags from the post we tried
								 * to update -
								 * since those tags probably caused the error
								 */
								post.setTags(updatedPost.getTags());
							} catch (final ObjectNotFoundException ex1) {
								// ignore
							} catch (final ResourceMovedException ex1) {
								// ignore
							}
						}
						/*
						 * finally add the post
						 */
						postsWithErrors.add(post);
					}
					hasErrors = true;
					this.errors.rejectValue(getOldResourceName(resourceType) + ".list[" + postId + "].resource", errorMessage.getErrorCode(), errorMessage.getParameters(), errorMessage.getDefaultMessage());
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
	private Map<String, Post<? extends Resource>> getPostMap(final boolean updatePosts) {
		final Map<String, Post<? extends Resource>> postMap = new HashMap<String, Post<? extends Resource>>();
		final List<Post<? extends Resource>> postsFromSession = (List<Post<? extends Resource>>) this.requestLogic.getSessionAttribute(PostPublicationController.TEMPORARILY_IMPORTED_PUBLICATIONS);
		if (!updatePosts && ValidationUtils.present(postsFromSession)) {
			/*
			 * Put the posts into a map, so we don't have to loop
			 * through the list for every stored post.
			 */
			for (final Post<? extends Resource> post : postsFromSession) {
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
			return TagUtils.parse(ValidationUtils.present(addTagString) ? addTagString : "");
		} catch (final RecognitionException ex) {
			log.warn("can't parse tags that should be added to all posts", ex);
		}
		return Collections.emptySet();
	}

	/**
	 * If the referer points to /bedit{bib,url}/abc, we redirect to /abc,
	 * otherwise
	 * to /user/loginUserName
	 * 
	 * @param referer
	 * @param loginUserName
	 * @return
	 */

	private View getFinalRedirect(final String referer, final String loginUserName) {
		String redirectUrl = referer;
		/*
		 * if (ValidationUtils.present(referer)) {
		 * 
		 * final Matcher prefixMatcher =
		 * BATCH_EDIT_URL_PATTERN.matcher(referer);
		 * if (prefixMatcher.find()) {
		 * redirectUrl = prefixMatcher.replaceFirst("");
		 * redirectUrl = prefixMatcher.toString();
		 * }
		 * }
		 */
		/*
		 * if no URL is given, we redirect to the user's page
		 */
		if (!ValidationUtils.present(redirectUrl)) {
			redirectUrl = this.urlGenerator.getUserUrlByUserName(loginUserName);
		}
		return new ExtendedRedirectView(redirectUrl);
	}

	/*
	 * private View getFinalRedirect(final boolean isPub, final String
	 * loginUserName) {
	 * String redirectUrl = "referer";
	 * if (isPub) {
	 * redirectUrl = UrlUtils.safeURIEncode("beditbib/" + "user/" +
	 * loginUserName); // TODO: should be done by the URLGenerator
	 * }
	 * else{
	 * redirectUrl = UrlUtils.safeURIEncode("bediturl/" + "user/" +
	 * loginUserName); // TODO: should be done by the URLGenerator
	 * }
	 * return new ExtendedRedirectView(redirectUrl);
	 * }
	 */
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * sets the logic
	 * 
	 * @param logic the logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * sets the requestLogic
	 * 
	 * @param requestLogic the RequestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * 
	 * @param urlGenerator
	 */
	public void setUrlGenerator(final URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

}
