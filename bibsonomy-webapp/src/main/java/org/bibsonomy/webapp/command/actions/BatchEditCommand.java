package org.bibsonomy.webapp.command.actions;

import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.UserResourceViewCommand;


/**
 * @author dzo
 * @version $Id$
 */
public class BatchEditCommand extends UserResourceViewCommand {
	
	/**
	 * The flag (from postPublication) that determines, if already existing posts will get updated.
	 */
	private boolean isOverwrite;
	
	/**
	 * This List contains all posts, that were to save, but an error occurred 
	 * (needed for the error case)
	 */
	private ListCommand<Post<? extends Resource>> posts;


	/**
	 * @return the list of posts
	 */
	@Override
	public ListCommand<Post<BibTex>> getBibtex() {
		ListCommand<Post<BibTex>> bibtex = new ListCommand<Post<BibTex>>(this.posts);
		return bibtex;
	}
	
	/**
	 * @return the list of posts
	 */
	public ListCommand<Post<?>> getPosts() {
		return this.posts;
	}

	/**
	 * @param posts the list of posts
	 */
	public void setPosts(ListCommand<Post<?>> posts) {
		this.posts = posts;
	}
	
	/**
	 * this flag determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	private boolean deleteCheckedPosts;
	

	/**
	 * these tags will be added to all resources
	 */
	private String tags;
	
	/** 
	 * old tags of the resources; resource hashes as keys of the map and old tags as values
	 */
	private Map<String, String> oldTags;
	
	/** 
	 * newTags of the resources; resource hashes as keys of the map [hash = new tags], ...
	 */
	private Map<String, String> newTags;
	
	/**
	 * hashes of the resources which will be deleted (hash as key and "on" as value (checkbox))
	 */
	private Map<String, Boolean> delete;
	
	/**
	 * 
	 */
	private String referer;

	
	/**
	 * @return the flag that determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	public boolean getDeleteCheckedPosts() {
		return this.deleteCheckedPosts;
	}

	/**
	 * @return the flag that determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	public boolean isDeleteCheckedPosts() {
		return this.deleteCheckedPosts;
	}
	
	/**
	 * @param deleteCheckedPosts the flag that determines, weather the dialogue called was configured to 
	 * edit(delete) or edit(create) existing posts.
	 */
	public void setDeleteCheckedPosts(boolean deleteCheckedPosts) {
		this.deleteCheckedPosts = deleteCheckedPosts;
	}
	
	/**
	 * @return the tags
	 */
	public String getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * @return the oldTags
	 */
	public Map<String, String> getOldTags() {
		return this.oldTags;
	}

	/**
	 * @param oldTags the oldTags to set
	 */
	public void setOldTags(Map<String, String> oldTags) {
		this.oldTags = oldTags;
	}

	/**
	 * @return the newTags
	 */
	public Map<String, String> getNewTags() {
		return this.newTags;
	}

	/**
	 * @param newTags the newTags to set
	 */
	public void setNewTags(Map<String, String> newTags) {
		this.newTags = newTags;
	}

	/**
	 * @return the delete
	 */
	public Map<String, Boolean> getDelete() {
		return this.delete;
	}

	/**
	 * @param delete the delete to set
	 */
	public void setDelete(Map<String, Boolean> delete) {
		this.delete = delete;
	}

	/**
	 * @return the referer
	 */
	public String getReferer() {
		return this.referer;
	}

	/**
	 * @param referer the referer to set
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public boolean getIsOverwrite() {
		return this.isOverwrite;
	}
	
	public boolean isOverwrite() {
		return this.isOverwrite;
	}

	public void setOverwrite(boolean isOverwrite) {
		this.isOverwrite = isOverwrite;
	}


}
