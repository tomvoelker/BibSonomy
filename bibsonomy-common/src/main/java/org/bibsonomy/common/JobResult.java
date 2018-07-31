package org.bibsonomy.common;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.errors.ErrorMessage;

import java.util.List;

/**
 * class to report a job result
 *
 * @author dzo
 */
public class JobResult {

	/**
	 * builds a jobResult with the provided errors
	 * @param errors
	 * @return
	 */
	public static JobResult buildFailure(final List<ErrorMessage> errors) {
		final JobResult jobResult = new JobResult();
		jobResult.setStatus(Status.FAIL);
		jobResult.setErrors(errors);
		return jobResult;
	}

	/**
	 * @return the JobResult with status ok
	 */
	public static JobResult buildSuccess() {
		final JobResult jobResult = new JobResult();
		jobResult.setStatus(Status.OK);
		return jobResult;
	}

	private Status status;

	private List<ErrorMessage> errors;

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the errors
	 */
	public List<ErrorMessage> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<ErrorMessage> errors) {
		this.errors = errors;
	}
}
