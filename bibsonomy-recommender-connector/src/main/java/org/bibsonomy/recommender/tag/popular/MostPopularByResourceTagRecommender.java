package org.bibsonomy.recommender.tag.popular;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.AbstractTagRecommender;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;
import recommender.impl.model.RecommendedTag;

/**
 * Returns the most popular (i.e., most often attached) tags of the resource as 
 * recommendation for the entity.
 * 
 * @author fei
 */
public class MostPopularByResourceTagRecommender extends AbstractTagRecommender {
	private static final Log log = LogFactory.getLog(MostPopularByResourceTagRecommender.class);

	private RecommenderMainTagAccess dbAccess;
	
	@Override
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> entity) {
		final String entityId = null; // TODO entity.getRecommendationId();
		if (present(entityId)) {
			/*
			 * we get the count to normalize the score
			 */
			final int count = this.dbAccess.getNumberOfTagAssignmentsForRecommendationEntity(entity, entityId);
			log.debug("Resource has " + count + " TAS.");

			final List<Pair<String, Integer>> tagsWithCount = this.dbAccess.getMostPopularTagsForRecommendationEntity(entity, entityId, this.numberOfTagsToRecommend);
			if (tagsWithCount != null) {
				for (final Pair<String, Integer> tagWithCount : tagsWithCount) {
					final String tag = this.getCleanedTag(tagWithCount.getFirst());
					if (tag != null) {
						recommendedTags.add(new RecommendedTag(tag, ((1.0 * tagWithCount.getSecond()) / count), 0.5));
					}
				}
				log.debug("Returning the tags " + recommendedTags);
			} else {
				log.debug("Resource not found or no tags available.");
			}
		} else {
			log.debug("Could not get recommendations, because no intraHash was given.");
		}
	}
	
	@Override
	public String getInfo() {
		return "Most Popular Tags By Resource Recommender";
	}

	public void setDbAccess(RecommenderMainTagAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	
	@Override
	protected void setFeedbackInternal(final Post<? extends Resource> entity, final RecommendedTag tag) {
		/*
		 * this recommender ignores feedback
		 */
	}
}
