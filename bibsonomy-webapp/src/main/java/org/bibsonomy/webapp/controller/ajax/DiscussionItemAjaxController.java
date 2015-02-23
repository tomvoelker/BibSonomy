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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.model.util.BibTexUtils.PREPRINT;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.GoldStandardPostLogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.services.Pingback;
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
 * @param <D> 
 */
public abstract class DiscussionItemAjaxController<D extends DiscussionItem> extends AjaxController implements ValidationAwareController<DiscussionItemAjaxCommand<D>>, ErrorAware {
	private static final Log log = LogFactory.getLog(DiscussionItemAjaxController.class);
	
	private Errors errors;
	private Pingback pingback;

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
			this.errors.reject("error.field.valid.ckey");
		}
		
		final String interHash = command.getHash();
		final String postUserName = command.getPostUserName();
		final String intraHash = command.getIntraHash();
		
		/*
		 * resource hash must be specified
		 */
		if (!present(interHash)) {
			this.errors.rejectValue("hash", "error.field.valid.hash");
			return this.getErrorView();
		}
		
		final User loginUser = command.getContext().getLoginUser();
		final String userName = loginUser.getName();
		
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
			return this.getErrorView();
		}
		
		final D discussionItem = command.getDiscussionItem();
		
		/*
		 * init groups from grouping command
		 */
		GroupingCommandUtils.initGroups(command, discussionItem.getGroups());
		
		
		boolean reloadPage = false;

		try {
			switch(this.requestLogic.getHttpMethod()) {
				case POST:
					reloadPage = this.createDiscussionItem(interHash, loginUser, postUserName, intraHash, discussionItem);
					break;
				case PUT:
					this.logic.updateDiscussionItem(userName, interHash, discussionItem);
					break;
				default:
					this.responseLogic.setHttpStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		} catch (final ValidationException ex) {
			log.warn("couldn't complete controller", ex);
			return this.getErrorView();
		}
		
		/*
		 * add hash as response
		 */
		final JSONObject result = new JSONObject();
		// TODO: send some error, if hash is null and show it to user
		result.put("hash", discussionItem.getHash());
		
		// preprint handling
		if (reloadPage) {
			result.put("reload", "true");
		}
		
		command.setResponseString(result.toString());
		return Views.AJAX_JSON;
	}
	
	@SuppressWarnings("null") // the originalPost could be null, but this is caught using present
	private boolean createDiscussionItem(final String interHash, final User loggedinUser, final String postUserName, final String intraHash, final DiscussionItem discussionItem) {
		boolean reloadPage = false;
		
		/*
		 * Before the discussionItem is created 
		 * we have to check whether the fitting Community Post exists
		 * and if necessary create it
		 * If possible, we create it from the post, that the loginuser had clicked on to start the discussion
		 */
		// for goldstandardPosts intraHash=interHash => query with interHash, NOT with intraHash
		Post<? extends Resource> goldStandardPost = this.logic.getPostDetails(interHash, GoldStandardPostLogicInterface.GOLD_STANDARD_USER_NAME);
		if (!present(goldStandardPost)) {
			/*
			 * No goldstandard post exists. The loginUser chose a regular (non Goldstandard) post to start a discussion.
			 * If the loginUser clicked on a star-rating icon (and did not change the url param) 
			 * then the postUserName contains the owner of the post to which the user wants to start a discussion.
			 * We first retrieve a suitable post (originalPost) to create a goldstandard from 
			 */
			reloadPage = true;
			log.debug("no gold standard found for intraHash " + interHash + ". Creating new gold standard");
			Post<? extends Resource> originalPost = null;
			
			// Try finding the post that the loginUser clicked on
			if (present(postUserName) && !GoldStandardPostLogicInterface.GOLD_STANDARD_USER_NAME.equals(postUserName) && present(intraHash)) {
				originalPost = this.logic.getPostDetails(intraHash, postUserName);
				if (!present(originalPost)) {
					log.warn("neither publications nor bookmarks found for intrahash '" + intraHash + "' when a postOwner was given: " + postUserName);
				}
			}
			
			// If no post could be found for postUserName, find any post, that is visible to the loginUser
			if (!present(originalPost)) {
				final List<Post<Bookmark>> bookmarkPosts = this.logic.getPosts(Bookmark.class, GroupingEntity.ALL, null, Collections.<String>emptyList(), interHash, null,SearchType.DEFAULT_SEARCH, null, null, null, null, 0, 1);
				if (present(bookmarkPosts)) {
					// Fixme: choose a public post if possible
					originalPost = bookmarkPosts.get(0);
				} else {
					// Fixme: choose a public post if possible
					final List<Post<BibTex>> publicationPosts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, Collections.<String>emptyList(), interHash, null,SearchType.DEFAULT_SEARCH, null, null, null, null, 0, 1);
					if (present(publicationPosts)) {
						originalPost = publicationPosts.get(0);
					}
				}
			}

			if (!present(originalPost)) {
				throw new IllegalStateException("A discussion item could not be created for hash "+interHash+" and username " + postUserName + " by user " + postUserName + " because no post was found that it could have been appended to.");
			}
			
			// we have found an original Post and now transform it into a goldstandard post
			final Post<Resource> newGoldStandardPost = new Post<Resource>();
			final Class<? extends Resource> resourceClass = originalPost.getResource().getClass();
			if (BibTex.class.isAssignableFrom(resourceClass)) {
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
				throw new UnsupportedResourceTypeException(resourceClass + " not supported.");
			}
			this.logic.createPosts(Collections.<Post<? extends Resource>>singletonList(newGoldStandardPost));
			
			goldStandardPost = newGoldStandardPost;
		} else {
			reloadPage = firstCommentInPreprint(goldStandardPost, postUserName); 
		}
		
		/*
		 * send a pingback/trackback for the public posted resource.
		 */
		if (present(this.pingback) && !loggedinUser.isSpammer() && GroupUtils.isPublicGroup(discussionItem.getGroups())) {
			// clear user for pingback and set the goldstandardpost
			goldStandardPost.setUser(null);
			this.pingback.sendPingback(goldStandardPost);
		}
		
		this.logic.createDiscussionItem(interHash, loggedinUser.getName(), discussionItem);
		
		return reloadPage;
	}

	/**
	 * Remove this method if you remove the preprint entry type
	 * @param goldStandard
	 * @param userName
	 * @return <code>true</code> if given post is a publication with entry type preprint <code>false</code> otherwise
	 */
	private static boolean firstCommentInPreprint(final Post<? extends Resource> goldStandard, final String userName) {
		final Resource res = goldStandard.getResource();
		if (res.getClass().equals(BibTex.class)) {
			if (((BibTex)res).getEntrytype().equals(PREPRINT)) {
				for (final DiscussionItem item : res.getDiscussionItems()) {
					if (item.getUser().getName().equals(userName)) {
						return false;
					}
				}
			}
		}
		return true;
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
	
	/**
	* A service that sends pingbacks / trackbacks to posted URLs.
	* 
	* @param pingback
	*/
	public void setPingback(final Pingback pingback) {
		this.pingback = pingback;
	}

}