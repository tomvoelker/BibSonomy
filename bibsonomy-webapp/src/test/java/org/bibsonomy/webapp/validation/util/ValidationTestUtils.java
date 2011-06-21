package org.bibsonomy.webapp.validation.util;

import java.util.HashMap;

import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

/**
 * @author dzo
 * @version $Id$
 */
public class ValidationTestUtils {

	public static Errors validate(final Validator<?> validator, final Object command) {
		@SuppressWarnings("rawtypes")
		final Errors errors = new MapBindingResult(new HashMap(), "review");
		validator.validate(command, errors);
		return errors;
	}

}
