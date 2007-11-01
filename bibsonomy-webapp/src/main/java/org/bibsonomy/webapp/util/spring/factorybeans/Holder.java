/*
 * Created on 08.10.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

/**
 * simply holds another object to have a constant reference to
 * a nonconstant field.
 * 
 * @param <T> type of the hold object
 * 
 * @author Jens Illig
 */
public class Holder<T> {
	private T obj;

	/**
	 * @return the hold object
	 */
	public T getObj() {
		return this.obj;
	}

	/**
	 * @param obj the object to hold
	 */
	public void setObj(T obj) {
		this.obj = obj;
	}
}
