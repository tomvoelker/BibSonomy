package org.bibsonomy.database.managers.discussion;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.database.params.discussion.ReviewParam;
import org.bibsonomy.model.Review;
import org.bibsonomy.model.User;


/**
 * database manager for crud reviews
 * 
 * @author dzo
 * @version $Id$
 */
public class ReviewDatabaseManager extends DiscussionItemDatabaseManager<Review> {
	
	private static final ReviewDatabaseManager INSTANCE = new ReviewDatabaseManager();

	/**
	 * @return the @{link:ReviewDatabaseManager} instance
	 */
	public static ReviewDatabaseManager getInstance() {
		return INSTANCE;
	}

	private ReviewDatabaseManager() {
		// only call super
	}
	
	/** 
	 * check rating and only length of all properties
	 * 
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.discussion.DiscussionItemDatabaseManager#checkDiscussionItem(org.bibsonomy.model.DiscussionItem, DBSession)
	 */
	@Override
	protected void checkDiscussionItem(final Review review, final DBSession session) {
		final double rating = review.getRating();
		
		if (Double.compare(rating, Review.MIN_REVIEW_RATING) < 0 || Double.compare(rating, Review.MAX_REVIEW_RATING) > 0) {
			throw new ValidationException("Review rating not in range"); // TODO: error message?!
		}
		
		final double decimal = Math.abs(rating - Math.rint(rating));
		
		if (Double.compare(decimal, 0) != 0 && Double.compare(decimal - 0.5, 0) != 0) {
			throw new ValidationException("Only ?.0 and ?.5 ratings are supported"); // TODO: error message?
		}
		
		this.checkLength(review, session);
	}
	
	@Override
	protected void handleDiscussionItemDelete(final String interHash, final User user, final Review oldComment, final DBSession session) {
		final ReviewParam reviewParam = this.createReviewParam(interHash, user.getName());
		reviewParam.setDiscussionItem(oldComment);
		
		/*
		 * update rating cache
		 */
		if (!user.isSpammer()) {
			this.update("updateReviewRatingsCacheDelete", reviewParam, session);
		}
	}
	
	@Override
	protected boolean createDiscussionItem(final String interHash, final Review review, final DBSession session, int discussionId) {
		final String userName = review.getUser().getName();
		
		/*
		 * check if the user already reviewed the resource
		 */
		final Review oldReview = this.getReviewForPostAndUser(interHash, userName, session);
		if (present(oldReview)) {
			return false; // TODO error message;
		}

		/*
		 * check the review
		 */
		this.checkDiscussionItem(review, session);
		
		/*
		 * create the review
		 */
		final ReviewParam param = this.createReviewParam(interHash, userName);
		param.setDiscussionItem(review);
		this.insert("insertReview", param, session);

		/*
		 * update ratings cache only if the user isn't a spammer
		 */
		if (!review.getUser().isSpammer()) {
			insert("updateReviewRatingsCacheInsert", param, session);
		}
		
		return true;
	}
	
	@Override
	protected boolean updateDiscussionItem(final String interHash, final Review review, final Review oldComment, final DBSession session) {
		final String username = review.getUser().getName();		
		final ReviewParam param = this.createReviewParam(interHash, username);
		param.setDiscussionItem(review);
		
		/*
		 * update review
		 */
		this.update("updateReview", param, session);
		
		/*
		 * only update cache if user wasn't a spammer
		 */
		if (!review.getUser().isSpammer()) {
			this.insert("updateReviewRatingsCacheInsert", param, session);
			
			param.setDiscussionItem(oldComment);
			this.update("updateReviewRatingsCacheDelete", param, session);
		}
		
		return true;
	}
	
	protected Review getReviewForPostAndUser(final String interHash, final String username, final DBSession session) {
		final ReviewParam param = new ReviewParam();
		this.fillDiscussionItemParam(param, interHash, username);
		return this.queryForObject("getReviewForHashAndUser", param, Review.class, session);
	}
	
	private ReviewParam createReviewParam(final String interHash, final String username) {
		final ReviewParam param = new ReviewParam();
		this.fillDiscussionItemParam(param, interHash, username);
		return param;
	}

	@Override
	protected List<Review> getDiscussionItemsByHashForResource(final DiscussionItemParam<Review> param, final DBSession session) {
		return this.queryForList("getReviewsByHashForResource", param, Review.class, session);
	}
}
