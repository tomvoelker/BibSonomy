package org.bibsonomy.webapp.util;

/**
 * @param <T> 
 * 
 * @author rja
 */
public interface Validator<T> extends org.springframework.validation.Validator {
	
	/**
	 * key for required attribute general message
	 */
	public static final String ERROR_FIELD_REQUIRED_KEY = "error.field.required";
}
