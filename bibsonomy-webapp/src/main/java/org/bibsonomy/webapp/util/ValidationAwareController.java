/*
 * Created on 07.10.2007
 */
package org.bibsonomy.webapp.util;


/**
 * extended MinimalisticControllers interface which can be optionally implemented
 * to tell about whether validation is required or not
 * 
 * @param <T> type of the command object
 * @version $Id$
 * @author Jens Illig
 */
public interface ValidationAwareController<T> extends MinimalisticController<T> {
	/**
	 * @param command a command object initialized by the framework based on
	 *                the parameters of some request-event like a http-request
	 * @return decision whether validation for this request is required or not
	 */
	public boolean isValidationRequired(T command);
	
	/**
	 * @return the validator to use for validation
	 */
	public Validator<T> getValidator();
}
