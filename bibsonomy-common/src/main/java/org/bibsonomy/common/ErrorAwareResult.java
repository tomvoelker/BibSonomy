package org.bibsonomy.common;

import org.bibsonomy.common.errors.ErrorMessage;

import java.util.List;

/**
 * a helper class to wrap errors while generating a result
 * @param <R>
 *
 * @author dzo
 */
public class ErrorAwareResult<R> {

	private R result;

	private List<ErrorMessage> errors;

	/**
	 * the default constructor
	 *
	 * @param result
	 * @param errors
	 */
	public ErrorAwareResult(R result, List<ErrorMessage> errors) {
		this.result = result;
		this.errors = errors;
	}

	/**
	 * @return the result
	 */
	public R getResult() {
		return result;
	}

	/**
	 * @return the errors
	 */
	public List<ErrorMessage> getErrors() {
		return errors;
	}
}
