/*
 * Created on 08.10.2007
 */
package org.bibsonomy.webapp.util;

import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.springframework.validation.Errors;

public interface ErrorAware {
	public Errors getErrors();
	public void setErrors(Errors errors);
}
