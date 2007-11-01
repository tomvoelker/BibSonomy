/*
 * Created on 08.10.2007
 */
package org.bibsonomy.webapp.util;

import org.bibsonomy.webapp.util.spring.controller.MinimalisticControllerSpringWrapper;
import org.springframework.validation.Errors;

/**
 * optional interface MinimalisticControllers can implement to receive info
 * about validation errors.
 * 
 * @author Jens Illig
 */
public interface ErrorAware {
	/**
	 * @return the validationerrors that occured in binding this request
	 *         to the command
	 */
	public Errors getErrors();
	/**
	 * @param errors setter used by the framework (namely {@link MinimalisticControllerSpringWrapper}) to inject possible validationerrors
	 */
	public void setErrors(Errors errors);
}
