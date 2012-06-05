package org.bibsonomy.webapp.validation.util;

import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * @author dzo
 * @version $Id$
 */
public class ValidationTestUtils {

	public static Errors validate(final Validator<?> validator, final Object command) {
		final Errors errors = new BindException(command, "command");
		validator.validate(command, errors);
		return errors;
	}

}
