package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.GoldStandardPostLogicInterface;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.util.ObjectUtils;
import org.bibsonomy.webapp.command.ajax.DiscussionItemAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author dzo
 * @version $Id$
 * @param <D> 
 */
public abstract class DiscussionItemAjaxController<D extends DiscussionItem> extends AjaxController implements ValidationAwareController<DiscussionItemAjaxCommand<D>>, ErrorAware {
	private static final Log log = LogFactory.getLog(DiscussionItemAjaxController.class);
	
	private Errors errors;

	@Override
	public DiscussionItemAjaxCommand<D> instantiateCommand() {
		final DiscussionItemAjaxCommand<D> commentCommand = new DiscussionItemAjaxCommand<D>();
		commentCommand.setDiscussionItem(this.initDiscussionItem());
		return commentCommand;
	}
	
	protected abstract D initDiscussionItem();

	@Override
	public View workOn(final DiscussionItemAjaxCommand<D> command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}
		
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		
		final String interHash = command.getHash();
		final String postUserName = command.getPostUserName();
		final String intraHash = command.getIntraHash();
		System.out.println(postUserName);
		
		/*
		 * resource hash must be specified
		 */
		if (!present(interHash)) {
			errors.rejectValue("hash", "error.field.valid.hash");
			return returnErrorView();
		}
		
		final String userName = command.getContext().getLoginUser().getName();
		
		/*
		 * don't call the validator
		 */
		if (HttpMethod.DELETE.equals(this.requestLogic.getHttpMethod())) {
			this.logic.deleteDiscussionItem(userName, interHash, command.getDiscussionItem().getHash());
			return Views.AJAX_JSON;
		}
		
		/*
		 * validate the command (including discussionItem)
		 */
		ValidationUtils.invokeValidator(this.getValidator(), command, this.errors);
		
		/*
		 * if validation failed return to the ajax error view
		 */
		if (this.errors.hasErrors()) {
			return returnErrorView();
		}
		
		final D discussionItem = command.getDiscussionItem();
		
		/*
		 * init groups from grouping command
		 */
		GroupingCommandUtils.initGroups(command, discussionItem.getGroups());
		

		try {
			switch(this.requestLogic.getHttpMethod()) {
				case POST:
					this.createDiscussionItem(interHash, userName, postUserName, intraHash, discussionItem);
					break;
				case PUT:
					this.logic.updateDiscussionItem(userName, interHash, discussionItem);
					break;
				default:
					this.responseLogic.setHttpStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		} catch (final ValidationException ex) {
			log.warn("couldn't complete controller", ex);
			return returnErrorView();
		}
		
		/*
		 * add hash as response
		 */
		final JSONObject result = new JSONObject();
		// TODO: send some error, if hash is null and show it to user
		result.put("hash", discussionItem.getHash());
		command.setResponseString(result.toString());		
		return Views.AJAX_JSON;
	}
	

	
	@SuppressWarnings("null") // the originalPost could be null, but this is caught using present
	private void createDiscussionItem(final String interHash, final String userName, final String postUserName, final String intraHash, DiscussionItem discussionItem) {
		/*
		 * Before the discussionItem is created 
		 * we have to check whether the fitting Community Post exists
		 * and if necessary create it
		 * If possible, we create it from the post, that the loginuser had clicked on to start the discussion
		 */
		// for goldstandardPosts intraHash=interHash => query with interHash, NOT with intraHash
		final Post<? extends Resource> goldStandardPost = this.logic.getPostDetails(interHash, GoldStandardPostLogicInterface.GOLD_STANDARD_USER_NAME);
		if (!present(goldStandardPost)) {
			/*
			 * No goldstandard post exists. The loginUser chose a regular (non Goldstandard) post to start a discussion.
			 * If the loginUser clicked on a star-rating icon (and did not change the url param) 
			 * then the postUserName contains the owner of the post to which the user wants to start a discussion.
			 * We first retrieve a suitable post (originalPost) to create a goldstandard from 
			 */
			log.debug("no gold standard found for intraHash " + interHash + ". Creating new gold standard");
			Post<? extends Resource> originalPost = null;
			
			// Try finding the post that the loginUser clicked on
			if (present(postUserName) && !GoldStandardPostLogicInterface.GOLD_STANDARD_USER_NAME.equals(postUserName) && present(intraHash)) {
				originalPost = this.logic.getPostDetails(intraHash, postUserName);
				if (!present(originalPost)) {
					log.warn("neither publications nor bookmarks found for intrahash '" + intraHash + "' when a postOwner was given: "+postUserName);
				}
			}
			
			// If no post could be found for postUserName, find any post, that is visible to the loginUser
			if (!present(originalPost)) {
				final List<Post<Bookmark>> bookmarkPosts = this.logic.getPosts(Bookmark.class, GroupingEntity.ALL, null, Collections.<String>emptyList(), interHash, null, null, null, null, null, 0, 1);
				if (present(bookmarkPosts)) {
					// Fixme: choose a public post if possible
					originalPost = bookmarkPosts.get(0);
				} else {
					// Fixme: choose a public post if possible
					final List<Post<BibTex>> publicationPosts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, Collections.<String>emptyList(), interHash, null, null, null, null, null, 0, 1);
					if (present(publicationPosts)) {
						originalPost = publicationPosts.get(0);
					}
				}
			}

			if (!present(originalPost)) {
				throw new IllegalStateException("A discussion item could not be created for hash "+interHash+" and username "+postUserName+" by user "+userName+" because no post was found that it could have been appended to.");
			}

			// we have found an original Post and now transform it into a goldstandard post
			final Post<Resource> newGoldStandardPost = new Post<Resource>();
			if (BibTex.class.isAssignableFrom(originalPost.getResource().getClass())) {
				final GoldStandardPublication goldStandardPublication = new GoldStandardPublication();
				ObjectUtils.copyPropertyValues(originalPost.getResource(), goldStandardPublication);
				/*
				 * clear some private stuff
				 */
				goldStandardPublication.setPrivnote("");
				newGoldStandardPost.setResource(goldStandardPublication);
			} else if (Bookmark.class.isAssignableFrom(originalPost.getResource().getClass())) {
				final GoldStandardBookmark goldStandardBookmark = new GoldStandardBookmark();
				ObjectUtils.copyPropertyValues(originalPost.getResource(), goldStandardBookmark);

				newGoldStandardPost.setResource(goldStandardBookmark);
			} else {
				throw new IllegalStateException("A discussion item could not be created for hash "+interHash+" and username "+postUserName+" by user "+userName+" because no post was found that it could have been appended to.");
			}
			this.logic.createPosts(Collections.<Post<? extends Resource>>singletonList(newGoldStandardPost));
		}
		this.logic.createDiscussionItem(interHash, userName, discussionItem);
	}

	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	@Override
	public boolean isValidationRequired(final DiscussionItemAjaxCommand<D> command) {
		return false;
	}
}
