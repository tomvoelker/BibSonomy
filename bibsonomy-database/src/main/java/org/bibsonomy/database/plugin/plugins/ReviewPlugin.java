package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.ReviewParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Review;

/**
 * @author dzo
 * @version $Id$
 */
public class ReviewPlugin extends AbstractDatabasePlugin {
		
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onReviewUpdated(java.lang.String, org.bibsonomy.model.Review, org.bibsonomy.model.Review, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onReviewUpdated(final String interHash, final Review oldReview, final Review review, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final ReviewParam param = new ReviewParam();
				param.setInterHash(interHash);
				param.setReview(oldReview);
				update("updateReviewRatingsCacheDelete", param, session);
				param.setReview(review);
				insert("updateReviewRatingsCacheInsert", param, session);
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onReviewDeleted(java.lang.String, org.bibsonomy.model.Review, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onReviewDeleted(final String interHash, final Review oldReview, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final ReviewParam param = new ReviewParam();
				param.setInterHash(interHash);
				param.setReview(oldReview);
				update("updateReviewRatingsCacheDelete", param, session);
				/*
				 * delete all marked as helpful/not helpful
				 */
				delete("allHelpfulMarks", param, session);
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onReviewCreated(java.lang.String, org.bibsonomy.model.Review, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onReviewCreated(final String interHash, final Review review, final DBSession session) {
		return new Runnable() {
			@Override
			public void run() {
				final ReviewParam param = new ReviewParam();
				param.setInterHash(interHash);
				param.setReview(review);
				insert("updateReviewRatingsCacheInsert", param, session);
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onBibTexDelete(int, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onPublicationDelete(int contentId, DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				// TODO Delete review if publication was deleted by the user
				
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onBookmarkDelete(int, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onBookmarkDelete(int contentId, DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				// TODO Delete review if bookmark was deleted by the user
			}
		};
	}
	
}
