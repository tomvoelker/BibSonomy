package org.bibsonomy.database.params;

import org.bibsonomy.model.Review;

/**
 * @author dzo
 * @version $Id$
 */
public class ReviewParam {

	private String loggedinUsername;

	private String interHash;
	
	private String username;
	
	private boolean helpful;
	
	/**
	 * @return the helpful
	 */
	public boolean isHelpful() {
		return this.helpful;
	}

	/**
	 * @param helpful the helpful to set
	 */
	public void setHelpful(boolean helpful) {
		this.helpful = helpful;
	}

	private Review review;

	/**
	 * @return the interHash
	 */
	public String getInterHash() {
		return this.interHash;
	}

	/**
	 * @param interHash the interHash to set
	 */
	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the review
	 */
	public Review getReview() {
		return this.review;
	}

	/**
	 * @param review the review to set
	 */
	public void setReview(Review review) {
		this.review = review;
	}

	/**
	 * @return the loggedinUsername
	 */
	public String getLoggedinUsername() {
		return this.loggedinUsername;
	}

	/**
	 * @param loggedinUsername the loggedinUsername to set
	 */
	public void setLoggedinUsername(String loggedinUsername) {
		this.loggedinUsername = loggedinUsername;
	}
	
	
}
