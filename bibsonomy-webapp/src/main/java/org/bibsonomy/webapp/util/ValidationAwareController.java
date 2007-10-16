/*
 * Created on 07.10.2007
 */
package org.bibsonomy.webapp.util;


public interface ValidationAwareController<T> extends MinimalisticController<T> {
	public boolean isValidationRequired(T command);
}
