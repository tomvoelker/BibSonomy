package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.model.Review;

/**
 * @author dzo
 * @version $Id$
 */
public interface ReviewLogicInterface {
	
	/**
	 * 
	 * @param username
	 * @param interHash
	 * @param review
	 */
	public void createReview(String username, String interHash, Review review);
	
	/**
	 * 
	 * @param username
	 * @param interHash
	 * @param review
	 */
	public void updateReview(String username, String interHash, Review review);
	
	/**
	 * 
	 * @param username
	 * @param interHash
	 */
	public void deleteReview(String username, String interHash);
	
	/**
	 * 
	 * @param username
	 * @param reviewUsername
	 * @param interHash
	 * @param helpful
	 */
	public void markReview(String username, String reviewUsername, String interHash, boolean helpful);
	
	/**
	 * 
	 * @param interHash
	 * @return a list of reviews
	 */
	public List<Review> getReviews(String interHash);	
}
