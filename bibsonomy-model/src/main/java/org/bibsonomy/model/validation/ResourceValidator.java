package org.bibsonomy.model.validation;

import java.util.List;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.Resource;

/**
 * validates a {@link Resource}
 *
 * @author dzo
 * @param <R> 
 */
public interface ResourceValidator<R extends Resource> {
	
	/**
	 * validates a resource
	 * @param resource
	 * @return the validation errors
	 */
	public List<ErrorMessage> validateResource(final R resource);
}
