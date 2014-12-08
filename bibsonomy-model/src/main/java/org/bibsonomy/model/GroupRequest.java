package org.bibsonomy.model;

import java.util.Date;

/**
 * Stores the information of a group request.
 *
 * @author clemensbaier
 */
public class GroupRequest {

	/**
	 * TODO: use user object
	 * The username used for the request.
	 */
	private String userName;

	/**
	 * the additional info the requesting user has provided.
	 */
	private String reason;

	/**
	 * The {@link Date} when this request was submitted.
	 */
	private Date submissionDate;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the submissionDate
	 */
	public Date getSubmissionDate() {
		return this.submissionDate;
	}

	/**
	 * @param submissionDate the submissionDate to set
	 */
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
}
