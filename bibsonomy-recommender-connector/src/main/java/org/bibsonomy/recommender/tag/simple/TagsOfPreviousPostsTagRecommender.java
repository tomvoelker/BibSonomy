package org.bibsonomy.recommender.tag.simple;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.AbstractTagRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;

/**
 * recommends the tags of the n previously posted items
 * 
 * @author dzo
 */
public class TagsOfPreviousPostsTagRecommender extends AbstractTagRecommender {
	private RecommenderMainTagAccess dbAccess;
	private int numberOfPreviousPosts = 1;

	/**
	 * init constructor
	 * @param dbAccess 
	 */
	public TagsOfPreviousPostsTagRecommender(final RecommenderMainTagAccess dbAccess) {
		super();
		this.dbAccess = dbAccess;
	}

	/**
	 * @param dbAccess 
	 * @param numberOfPreviousPosts
	 */
	public TagsOfPreviousPostsTagRecommender(final RecommenderMainTagAccess dbAccess, int numberOfPreviousPosts) {
		this(dbAccess);
		this.numberOfPreviousPosts = numberOfPreviousPosts;
	}
	
	/* (non-Javadoc)
	 * @see recommender.core.Recommender#getInfo()
	 */
	@Override
	public String getInfo() {
		return "Recommender that users the last " + this.numberOfPreviousPosts ;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.AbstractTagRecommender#addRecommendedTagsInternal(java.util.Collection, org.bibsonomy.model.Post)
	 */
	@Override
	protected void addRecommendedTagsInternal(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> entity) {
		final String username = entity.getUser().getName();
		final int count = this.dbAccess.getNumberOfTagsOfPreviousPostsForUser(username, this.numberOfPreviousPosts);
		
		final List<Pair<String, Integer>> tags = this.dbAccess.getTagsOfPreviousPostsForUser(username, this.numberOfPreviousPosts);
		for (final Pair<String, Integer> tagWithCount : tags) {
			final String tag = this.getCleanedTag(tagWithCount.getFirst());
			if (tag != null) {
				recommendedTags.add(new RecommendedTag(tag, ((1.0 * tagWithCount.getSecond().doubleValue()) / count), 0.5));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tag.AbstractTagRecommender#setFeedbackInternal(org.bibsonomy.model.Post, org.bibsonomy.recommender.tag.model.RecommendedTag)
	 */
	@Override
	protected void setFeedbackInternal(Post<? extends Resource> post, RecommendedTag tag) {
		// noop
	}

}
