package org.bibsonomy.recommender.tag.simple;

import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.simple.FixedRecommender;
import org.bibsonomy.recommender.tag.model.RecommendedTag;

import recommender.core.interfaces.model.RecommendationResult;
import recommender.core.util.RecommendationResultComparator;

/**
 * Always recommends the tags given in the constructor.
 * 
 * @author rja
 */
public class FixedTagsTagRecommender extends FixedRecommender<Post<? extends Resource>, RecommendedTag> {
	
	/**
	 * Adds the given tags to the fixed set of tags, ordered by their 
	 * occurrence in the arrays.
	 * 
	 * @param tags
	 */
	public FixedTagsTagRecommender(final String[] tags) {
		super(new TreeSet<RecommendedTag>(new RecommendationResultComparator<RecommendationResult>()));
		for (int i = 0; i < tags.length; i++) {
			this.results.add(new RecommendedTag(tags[i], 1.0 / (i + 1.0), 0.0));
		}
	}
}
