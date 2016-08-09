package org.bibsonomy.recommender.tag.testutil;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.model.RecommendedTag;

import recommender.impl.test.util.DummyRecommender;

/**
 * 
 * @author dzo
 */
public class DummyTagRecommender extends DummyRecommender<Post<? extends Resource>, RecommendedTag> {

	/* (non-Javadoc)
	 * @see recommender.impl.test.util.DummyRecommender#createRecommenationResult(java.lang.String, double, double)
	 */
	@Override
	protected RecommendedTag createRecommenationResult(String re, double score, double confidence) {
		return new RecommendedTag(re, score, confidence);
	}
}
