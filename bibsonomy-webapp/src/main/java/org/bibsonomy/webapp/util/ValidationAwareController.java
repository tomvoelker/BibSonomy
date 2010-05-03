package org.bibsonomy.webapp.util;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * extended MinimalisticControllers interface which can be optionally implemented
 * to tell about whether validation is required or not
 * 
 * @param <T> type of the command object
 * @version $Id$
 * @author Jens Illig
 */
public interface ValidationAwareController<T extends BaseCommand> extends MinimalisticController<T> {
	
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
