package org.bibsonomy.webapp.command.actions;

import java.util.Map;

import org.bibsonomy.webapp.command.UserResourceViewCommand;


/**
 * @author dzo
 * @version $Id$
 */
public class BatchEditCommand extends UserResourceViewCommand {
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
	private Map<String, String> delete;
	
	/**
	 * 
	 */
	private String referer;

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
	public Map<String, String> getDelete() {
		return this.delete;
	}

	/**
	 * @param delete the delete to set
	 */
	public void setDelete(Map<String, String> delete) {
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

}
