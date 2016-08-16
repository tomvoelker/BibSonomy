package org.bibsonomy.webapp.util;

import org.bibsonomy.webapp.util.spring.controller.MinimalisticControllerSpringWrapper;
import org.springframework.validation.Errors;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 */
public interface WarningAware {
	/**
	 * @return the warnings that occured in binding this request
	 *         to the command
	 */
	public Errors getWarnings();
	/**
	 * @param warnings setter used by the framework (namely {@link MinimalisticControllerSpringWrapper}) to inject possible warnings
	 */
	public void setWarnings(Errors warnings);
}
