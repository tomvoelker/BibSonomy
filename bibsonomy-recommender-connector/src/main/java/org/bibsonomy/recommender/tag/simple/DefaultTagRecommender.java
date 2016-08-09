package org.bibsonomy.recommender.tag.simple;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.model.RecommendedTag;

import recommender.core.util.RecommendationResultComparator;
import recommender.impl.meta.CompositeRecommender;


/**
 * Default tag recommender.
 * 
 * @author rja
 */
public class DefaultTagRecommender extends CompositeRecommender<Post<? extends Resource>, RecommendedTag> {

	/**
	 * Currently only the {@link SimpleContentBasedTagRecommender} is included.
	 */
	public DefaultTagRecommender() {
		super(new RecommendationResultComparator<RecommendedTag>());
		addRecommender(new SimpleContentBasedTagRecommender());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.meta.CompositeTagRecommender#getInfo()
	 */
	@Override
	public String getInfo() {
		return "Default tag recommender.";
	}

}
