package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.webapp.command.actions.BatchEditCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;


/**
 * Controller to batch edit (update tags and delete) resources 
 * 
 * @author dzo
 * @version $Id$
 */
public class BatchEditController implements MinimalisticController<BatchEditCommand>, ErrorAware{

	private static final int HASH_LENGTH = 32;
	private static final Log log = LogFactory.getLog(BatchEditController.class);
	
	private LogicInterface logic;
	
	private Errors errors;
	
	
	@Override
	public BatchEditCommand instantiateCommand() {
		final BatchEditCommand command = new BatchEditCommand();
		command.setOldTags(new HashMap<String, String>());
		command.setNewTags(new HashMap<String, String>());
		command.setDelete(new HashMap<String, Boolean>());
		return command;
	}
	
	@Override
	public View workOn(final BatchEditCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		// check if user is logged in
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.LOGIN;
		}
		
		// check if ckey is valid
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		
		// get username
		final String username = context.getLoginUser().getName();

		/* 
		 * no errors begin with batch edit
		 */
		log.debug("batch edit for user " + username + " started");

		
		/*
		 * get the maps from command
		 */
		final Map<String, String> newTagsMap = command.getNewTags();
		final Map<String, String> oldTagsMap = command.getOldTags();
		final Map<String, Boolean> delete = command.getDelete();
		
		/*
		 * get addTag string from command and parse it to a 
		 * set of tags (which will be added to all posts)
		 */
		String addTagString = command.getTags();
		
		if (!present(addTagString)) {
			addTagString = "";
		}
		
		final Set<Tag> addTags = new TreeSet<Tag>();
		
		try {
			addTags.addAll(TagUtils.parse(addTagString));
		} catch (final RecognitionException ex) {
			log.warn("can't parse add tags for user " + username, ex);
		}
		
		// create lists for delete and update action
		final List<String> postsToDelete = new LinkedList<String>();
		final List<Post<?>> postsToUpdate = new LinkedList<Post<?>>();
		
		/*
		 * loop through all hashes
		 */
		for (final String hash : newTagsMap.keySet()) {
			/*
			 * short check if hash is correct
			 */
			if (hash.length() != HASH_LENGTH) continue;
			
			/*
			 * delete post if checkbox is checked
			 */
			if (delete.containsKey(hash) && delete.get(hash)) {
				postsToDelete.add(hash);
				continue; // update tags not required
			}
			
			/*
			 * update tags
			 */
			// get new and old tags
			final String oldTagsString = oldTagsMap.get(hash);
			final String newTagsString = newTagsMap.get(hash);
			
			try {
				// parse strings to sets of tags
				final Set<Tag> oldTags = TagUtils.parse(oldTagsString);
				final Set<Tag> newTags = TagUtils.parse(newTagsString);
				
				// add addTags to newTags
				newTags.addAll(addTags);
				
				if (oldTags.equals(newTags)) {
					// tags haven't changed, nothing to do
					continue;
				}
				
				// update tags in post
				final Post<? extends Resource> post = this.logic.getPostDetails(hash, username);
				
				if (!present(post)) {
					log.warn("post with hash " + hash + " not found for user " + username + " while updating tags");
				} else {
					post.setTags(newTags);
					postsToUpdate.add(post);
				}
				
			} catch (final RecognitionException ex) {
				log.warn("can't parse tags of resource " + hash + " for user " + username, ex);
			}
		}
		
		
		/*
		 * delete posts
		 */
		if (present(postsToDelete)) {
			log.debug("deleting "  + postsToDelete.size() + " posts for user " + username);
			this.logic.deletePosts(username, postsToDelete);
		}
		
		/*
		 * update tags of posts
		 */
		if (present(postsToUpdate)) {
			log.debug("updating " + postsToUpdate.size() + " posts for user " + username);
			this.logic.updatePosts(postsToUpdate, PostUpdateOperation.UPDATE_TAGS); // only update tags
		}
		
		log.debug("finished batch edit for user " + username);

		// get referer to redirect to it
		String referer = command.getReferer();
		
		// set default referer to user's page if empty or null
		if (referer == null || referer.trim().isEmpty()) {
			referer = "/user/" + this.encodeStringToUTF8(username);
		}

		return new ExtendedRedirectView(referer);
	}
	
	
	/**
	 * encodes a string to utf-8 format
	 * 
	 * TODO: extract to helper class, ...
	 * 
	 * @param toEncode
	 * @return the encoded utf-8 string
	 */
	private String encodeStringToUTF8(String toEncode) {
		String encoded = null;
		
		try {
			encoded = URLEncoder.encode(toEncode, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return toEncode;
		}
		
		return encoded;
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
}
