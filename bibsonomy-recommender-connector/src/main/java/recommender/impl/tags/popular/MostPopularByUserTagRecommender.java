package recommender.impl.tags.popular;

import java.util.Collection;
import java.util.List;

import recommender.core.interfaces.database.RecommenderMainTagAccess;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.model.Pair;
import recommender.impl.model.RecommendedTag;
import recommender.impl.tags.AbstractTagRecommender;

/**
 * Returns the most popular (i.e., most often used) tags of the user as 
 * recommendation for the entity.  
 * 
 * @author fei
 */
public class MostPopularByUserTagRecommender extends AbstractTagRecommender {
	
	private RecommenderMainTagAccess dbAccess;
	
	@Override
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final TagRecommendationEntity entity) {
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
					recommendedTags.add(new RecommendedTag(tag, ((1.0 * tagWithCount.getSecond()) / count), 0.5));
				}
			}
		}
	}

	@Override
	public String getInfo() {
		return "Most Popular Tags By User Recommender";
	}

	@Override
	protected void setFeedbackInternal(final TagRecommendationEntity entity, final RecommendedTag tag) {
		/*
		 * this recommender ignores feedback
		 */
	}
	
	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
}
