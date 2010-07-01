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
}
