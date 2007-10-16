/*
 * Created on 07.10.2007
 */
package org.bibsonomy.webapp.util;


public interface MinimalisticController<T> {
	public T instantiateCommand();
	public View workOn(T command);
}
