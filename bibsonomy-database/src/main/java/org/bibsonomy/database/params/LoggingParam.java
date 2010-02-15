package org.bibsonomy.database.params;

/**
 * @author dzo
 * @version $Id$
 * @param <T> 
 */
public class LoggingParam<T> {
	private T oldId;
	private T newId;
	
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
}
