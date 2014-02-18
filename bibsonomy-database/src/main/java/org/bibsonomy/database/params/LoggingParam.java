package org.bibsonomy.database.params;

import org.bibsonomy.model.User;

/**
 * @param <T> the class of the id
 * 
 * @author dzo
 * @version $Id$ 
 */
public class LoggingParam<T> {
	private T oldId;
	private T newId;
	
	/** Needed for History Update */
	private int requestedContentId;
	/** If a newcontentId is updated we need this as reference */
	private int newContentId;
	
	private User user;
	
	/**
	 * @return the oldId
	 */
	public T getOldId() {
		return this.oldId;
	}
	
	/**
	 * @param oldId the oldId to set
	 */
	public void setOldId(T oldId) {
		this.oldId = oldId;
	}

	/**
	 * @param newId the newId to set
	 */
	public void setNewId(T newId) {
		this.newId = newId;
	}

	/**
	 * @return the newId
	 */
	public T getNewId() {
		return newId;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the newContentId
	 */
	public int getNewContentId() {
		return newContentId;
	}

	/**
	 * @param newContentId the newContentId to set
	 */
	public void setNewContentId(int newContentId) {
		this.newContentId = newContentId;
	}

	/**
	 * @return the contentId
	 */
	public int getContentId() {
		return requestedContentId;
	}

	/**
	 * @param contentId the contentId to set
	 */
	public void setContentId(int contentId) {
		this.requestedContentId = contentId;
	}
}