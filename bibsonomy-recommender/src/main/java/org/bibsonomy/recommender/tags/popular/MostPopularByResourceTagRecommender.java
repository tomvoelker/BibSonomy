package org.bibsonomy.recommender.tags.popular;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.AbstractTagRecommender;
import org.bibsonomy.recommender.tags.database.DBLogic;

/**
 * Returns the most popular (i.e., most often attached) tags of the resource as 
 * recommendation for the post.  
 * 
 * @author fei
 * @version $Id$
 */
public class MostPopularByResourceTagRecommender extends AbstractTagRecommender {
	private static final Log log = LogFactory.getLog(MostPopularByResourceTagRecommender.class);

	
	private DBLogic dbLogic;
	
	@Override
	protected void addRecommendedTagsInternal(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		
		/*
		 * we have to call recalculateHashes() first, otherwise the intraHash is not available
		 */
		resource.recalculateHashes();

		final String intraHash = resource.getIntraHash();
		if (present(intraHash)) {
			/*
			 * we get the count to normalize the score
			 */
			final int count = this.dbLogic.getNumberOfTasForResource(resource.getClass(), intraHash);
			log.debug("Resource has " + count + " TAS.");

			final List<Pair<String,Integer>> tagsWithCount = this.dbLogic.getMostPopularTagsForResource(resource.getClass(), intraHash, this.numberOfTagsToRecommend);
			if (present(tagsWithCount)) {
				for (final Pair<String,Integer> tagWithCount : tagsWithCount) {
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
