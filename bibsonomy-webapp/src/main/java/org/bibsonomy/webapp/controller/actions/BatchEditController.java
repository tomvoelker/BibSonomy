package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
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
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.SystemTagErrorMessage;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.ListCommand;
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
 * Controller to batch edit (update tags and delete) resources 
 * 
 * @author dzo
 * @version $Id$
 */
public class BatchEditController implements MinimalisticController<BatchEditCommand>, ErrorAware{

	private static final int HASH_LENGTH = 32;
	private static final Log log = LogFactory.getLog(BatchEditController.class);
	
	private RequestLogic requestLogic;
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
		
		boolean postIsPublication = true;
		
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
		
		/*
		 * The following map contains the hashes of the posts, which are displayed
		 * in the list of batcheditcontent.tagx.
		 * It's meaning differs, depending on the flag isDeleteCheckedPosts(),
		 * which might get set by the previous calling controller. 
		 * Default is, that this page displays already stored posts, so the user can 
		 * edit(delete) them. 
		 * If the flag is set to false(non default), the posts displayed are only stored
		 * temporarily (at the very moment) in the the session, and the user can store 
		 * the posts by checking the checkboxes respectively.
		 * So the map with the name delete, contains posts which are to save, if the 
		 * priorly discussed flag is set to false.   
		 */
		
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
		
		/** determine, if we are going to store temporarily saved files **/
		boolean isDeleteAction = command.isDeleteCheckedPosts();
		if(isDeleteAction)
		{
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
					postIsPublication = this.determinePostRessource(post);
					
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
		} else {
			// create lists for delete and update action
			final List<Post<?>> postsToSave = new LinkedList<Post<?>>();
			final List<Post<?>> postsToUpdate = new LinkedList<Post<?>>();
			LinkedList<Post<?>> bibtex = (LinkedList<Post<?>>) this.requestLogic.getSessionAttribute(PostPublicationController.TEMPORARILY_IMPORTED_PUBLICATIONS);
			
			ListCommand<Post<?>> listCommand = new ListCommand<Post<?>>(command);
			listCommand.setList(bibtex);
			command.setPosts(listCommand);
			/*
			 * Put these posts into a hashmap, so we dont have to loop through the list 
			 *for every stored post!
			 */
			HashMap<String, Post<?>> bibtexHashMap = new HashMap<String, Post<?>>(); 
			for(Post<?> currentPost : bibtex)
			{
				bibtexHashMap.put(currentPost.getResource().getIntraHash(), currentPost);
			}
			/*
			 * loop through all hashes
			 */
			for (final String hash : newTagsMap.keySet()) {
				//get the appropriate post
				final Post<? extends Resource> post = bibtexHashMap.get(hash);
				postIsPublication = this.determinePostRessource(post);
				
				/*
				 * short check if hash is correct
				 */
				if (hash.length() != HASH_LENGTH) continue;
				
				/*
				 * delete post if checkbox is checked
				 */
				if (delete.containsKey(hash) && delete.get(hash)) {
					postsToSave.add(post);
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
					
					// update tags in post
					if (!present(post)) {
						log.warn("post with hash " + hash + " not found for user " + username + " while updating tags");
						continue;
					} else {
						post.setTags(newTags);
					}
				} catch (final RecognitionException ex) {
					log.warn("can't parse tags of resource " + hash + " for user " + username, ex);
				}
			}
			
			/*
			 * save posts
			 */
			if (present(postsToSave)) {
				log.debug("saving "  + postsToSave.size() + " posts for user " + username);
				try {
					this.logic.createPosts(postsToSave);
				} catch (DatabaseException ex) {
					Map<String, List<ErrorMessage>> errorMsgs = ex.getErrorMessages();
					List<Post<?>> updatePosts = new LinkedList<Post<?>>();
					for(String postHash : errorMsgs.keySet())
					{
						boolean toUpdate = true;
						for(ErrorMessage message : errorMsgs.get(postHash))
						{ 
							if(message instanceof SystemTagErrorMessage)
							{
								errors.rejectValue("posts.list["+postsToSave.indexOf(bibtexHashMap.get(postHash))+"].tags", 
													StringUtils.translateMessageKey(message.getLocalizedMessageKey(), 
													message.getParameters(), 
													command.getContext().getLocale()));
								toUpdate = false;
							}
							
							if(message instanceof DuplicatePostErrorMessage)
							{
								if(!command.isOverwrite()) {
									errors.rejectValue("posts.list["+postsToSave.indexOf(bibtexHashMap.get(postHash))+"].tags", 
														StringUtils.translateMessageKey(message.getLocalizedMessageKey(), 
														message.getParameters(), 
														command.getContext().getLocale()));
								} 
							}
						}
						if(toUpdate && command.isOverwrite())
						{
							updatePosts.add(bibtexHashMap.get(postHash));
							try {
								this.logic.updatePosts(postsToSave, PostUpdateOperation.UPDATE_ALL);
							} catch (DatabaseException ex1) {
								Map<String, List<ErrorMessage>> errorUpdateMsgs = ex.getErrorMessages();
								//TODO: no DatabaseExceptions get thrown yet, but just in case...
								for(ErrorMessage message : errorMsgs.get(postHash))
								{ 
									if(message instanceof SystemTagErrorMessage)
									{
										errors.rejectValue("command.posts.list["+postsToSave.indexOf(bibtexHashMap.get(postHash))+"].tags", "",
															StringUtils.translateMessageKey(message.getLocalizedMessageKey(), 
															message.getParameters(), 
															command.getContext().getLocale()));
						
									}
						
								}
							}
						}
					}
					
				}
			}
			
			log.debug("finished batch edit for user " + username);
	
			if(errors.hasErrors())
			{
				if(postIsPublication)
					return Views.BATCHEDITBIB;
				else
					return Views.BATCHEDITURL;
			}
	
			return Views.HOMEPAGE;
		}
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
	
	/**
	 * Sets a string attribute in the session.
	 * 
	 * @param key
	 * @param value
	 */
	protected void setSessionAttribute(final String key, final Object value) {
		requestLogic.setSessionAttribute(key, value);
	}
	
	/**
	 * Gets a string attribute from the session.
	 * 
	 * @param key
	 * @return
	 */
	protected Object getSessionAttribute(final String key) {
		return requestLogic.getSessionAttribute(key);
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
	
	private boolean determinePostRessource(Post<?> post)
	{
		Type resourceType = post.getResource().getClass();
		if(resourceType==null)
			throw new IllegalArgumentException("Untyped Post<?> recognized during BatchEditController.determinePostRessource");
		if(post.getResource().getClass().isAssignableFrom(BibTex.class))
			return true;
		else
			return false;
	}
}
