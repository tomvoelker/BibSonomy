package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.model.Review;

/**
 * @author dzo
 * @version $Id$
 */
public class ReviewCommand extends AjaxCommand {
	
	private Review review;
	private String hash;
	private String username;
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
	
}
