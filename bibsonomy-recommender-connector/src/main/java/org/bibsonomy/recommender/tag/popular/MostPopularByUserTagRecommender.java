package org.bibsonomy.recommender.tag.popular;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.AbstractTagRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;

/**
 * Returns the most popular (i.e., most often used) tags of the user as 
 * recommendation for the entity.  
 * 
 * @author fei
 */
public class MostPopularByUserTagRecommender extends AbstractTagRecommender {
	
	private RecommenderMainTagAccess dbAccess;
	
	@Override
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> entity) {
		final String username = entity.getUser().getName();
		if (username != null) {
			
			/*
			 * we get the count to normalize the score
			 */
			final int count = this.dbAccess.getNumberOfTaggingsForUser(username);
			
			final List<Pair<String, Integer>> tagsWithCount = this.dbAccess.getMostPopularTagsForUser(username, this.numberOfTagsToRecommend);
			for (final Pair<String, Integer> tagWithCount : tagsWithCount) {
				final String tag = this.getCleanedTag(tagWithCount.getFirst());
				if (tag != null) {
					recommendedTags.add(new RecommendedTag(tag, ((1.0 * tagWithCount.getSecond().doubleValue()) / count), 0.5));
				}
			}
		}
	}

	@Override
	public String getInfo() {
		return "Most Popular Tags By User Recommender";
	}

	@Override
	protected void setFeedbackInternal(final Post<? extends Resource> entity, final RecommendedTag tag) {
		/*
		 * this recommender ignores feedback
		 */
	}
	
	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
}
