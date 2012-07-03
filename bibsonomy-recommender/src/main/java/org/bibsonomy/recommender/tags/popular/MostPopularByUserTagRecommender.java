package org.bibsonomy.recommender.tags.popular;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.AbstractTagRecommender;
import org.bibsonomy.recommender.tags.database.DBLogic;

/**
 * Returns the most popular (i.e., most often used) tags of the user as 
 * recommendation for the post.  
 * 
 * @author fei
 * @version $Id$
 */
public class MostPopularByUserTagRecommender extends AbstractTagRecommender {
	
	private DBLogic dbLogic;
	
	@Override
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		final String username = post.getUser().getName();
		if (username != null) {
			
			/*
			 * we get the count to normalize the score
			 */
			final int count = this.dbLogic.getNumberOfTasForUser(username);
			
			final List<Pair<String, Integer>> tagsWithCount = this.dbLogic.getMostPopularTagsForUser(username, this.numberOfTagsToRecommend);
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

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(final DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

	@Override
	protected void setFeedbackInternal(final Post<? extends Resource> post) {
		/*
		 * this recommender ignores feedback
		 */
	}
}
