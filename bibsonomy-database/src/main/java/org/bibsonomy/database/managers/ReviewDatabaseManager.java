package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.ReviewParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.Review;

/**
 * @author dzo
 * @version $Id$
 */
public class ReviewDatabaseManager extends AbstractDatabaseManager {
	private static final ReviewDatabaseManager INSTANCE = new ReviewDatabaseManager();

	/**
	 * @return the @{link:ReviewDatabaseManager} instance
	 */
	public static ReviewDatabaseManager getInstance() {
		return INSTANCE;
	}
	
	private final DatabasePluginRegistry plugins;

	private ReviewDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
	}
	
	/**
	 * 
	 * @param interHash
	 * @param session
	 * @return all resources for the specific resource
	 */
	public List<Review> getReviewsForResource(final String interHash, final DBSession session) {
		return this.queryForList("getReviews", interHash, Review.class, session);
	}
	
	/**
	 * creates a new review 
	 * @param interHash
	 * @param review
	 * @param session
	 * @return <code>true</code> iff the review was created successfully
	 */
	public boolean createReviewForPost(final String interHash, final Review review, final DBSession session) {
		session.beginTransaction();
		try {
			final String username = review.getUser().getName();
			this.checkReview(review);
			
			final Review oldReview = this.getReviewForPostAndUser(interHash, username, session);
			if (oldReview != null) {
				return false; // TODO error message;
			}
			
			final ReviewParam param = this.createReviewParam(interHash, username);
			param.setReview(review);
			this.insert("insertReview", param, session);
			this.plugins.onReviewCreated(interHash, review, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return true;
	}

	private ReviewParam createReviewParam(final String interHash, final String username) {
		final ReviewParam param = new ReviewParam();
		param.setInterHash(interHash);
		param.setUsername(username);
		return param;
		
	}

	private ReviewParam createReviewParam(final String interHash, final String username, String loggedinUsername) {
		final ReviewParam param = createReviewParam(interHash, username);
		param.setLoggedinUsername(loggedinUsername);
		return param;
	}
	
	
	
	protected Review getReviewForPostAndUser(final String interHash, final String username, DBSession session) {
		final ReviewParam param = createReviewParam(interHash, username);
		return this.queryForObject("selectReviewForHashAndUser", param, Review.class, session);
	}
	
	/**
	 * updates a review
	 * @param interHash
	 * @param review
	 * @param session
	 * @return <code>true</code> iff the review was updated successfully
	 */
	public boolean updateReview(final String interHash, final Review review, final DBSession session) {
		session.beginTransaction();
		try {
			final String username = review.getUser().getName();
			this.checkReview(review);
			
			final Review oldReview = this.getReviewForPostAndUser(interHash, username, session);
			if (!present(oldReview)) {
				return false; // TODO error message;
			}
			
			final ReviewParam param = this.createReviewParam(interHash, username);
			param.setReview(review);
			this.update("updateReview", param, session);
			
			/*
			 * update cache
			 */
			this.plugins.onReviewUpdated(interHash, oldReview, review, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return true;
	}
	
	private void checkReview(final Review review) {
		// TODO: max text length?
		final double rating = review.getRating();
		
		if (Double.compare(rating, Review.MIN_REVIEW_RATING) < 0 || Double.compare(rating, Review.MAX_REVIEW_RATING) > 0) {
			throw new ValidationException("Review rating not in range"); // TODO error message?!
		}
		
		final double decimal = Math.abs(rating - Math.rint(rating));
		
		if (Double.compare(decimal, 0) != 0 && Double.compare(decimal - 0.5, 0) != 0) {
			throw new ValidationException("Only x.0 and x.5 ratings are supported");
		}
	}
	
	/**
	 * deletes a review
	 * @param interHash
	 * @param username
	 * @param session
	 * @return <code>true</code> iff review was deleted successfully
	 */
	public boolean deleteReview(final String interHash, final String username, final DBSession session) {
		session.beginTransaction();
		try {
			final Review oldReview = this.getReviewForPostAndUser(interHash, username, session);
			if (oldReview == null) {
				return false; // TODO error message;
			}
			
			final ReviewParam param = this.createReviewParam(interHash, username);
			this.delete("deleteReview", param, session);
			
			/*
			 * update cache
			 */
			this.plugins.onReviewDeleted(interHash, oldReview, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return true;
	}
	
	/**
	 * 
	 * @param loggedinUsername
	 * @param interHash
	 * @param username
	 * @param session
	 * @return <code>true</code> iff the user marked the review (interHash, username) already
	 */
	public boolean markedReview(final String loggedinUsername, final String interHash, final String username, final DBSession session) {
		final ReviewParam param = this.createReviewParam(interHash, username, loggedinUsername);
		final Boolean alreadyMarked = this.queryForObject("getReviewMark", param, Boolean.class, session);
		return alreadyMarked != null && alreadyMarked;
	}

	/**
	 * marks a review as helpful or not helpful
	 * @param loggedinUsername
	 * @param interHash
	 * @param username
	 * @param helpful
	 * @param session
	 * @return <code>true</code> iff the review was marked successfully
	 */
	public boolean markReview(final String loggedinUsername, final String interHash, final String username, final boolean helpful, DBSession session) {
		session.beginTransaction();
		try {
			final Review review = this.getReviewForPostAndUser(interHash, username, session);
			
			if (!present(review)) {
				throw new ValidationException("review not found");
			}
			final ReviewParam param = new ReviewParam();
			param.setInterHash(interHash);
			param.setUsername(username);
			param.setLoggedinUsername(loggedinUsername);
			
			if (this.markedReview(loggedinUsername, interHash, username, session)) {
				throw new ValidationException("you have allready marked this review");
			}
			
			param.setHelpful(helpful);
			this.insert("insertMark", param, session);
			this.update("update" + (helpful ? "" : "Not") + "Helpful", param, session);
			
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
		return true;
	}
}
