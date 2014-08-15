package org.bibsonomy.recommender.tag.simple;

import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.util.RecommendationResultComparator;
import recommender.impl.meta.CompositeRecommender;
import recommender.impl.model.RecommendedTag;


/**
 * Default tag recommender.
 * 
 * @author rja
 */
public class DefaultTagRecommender extends CompositeRecommender<TagRecommendationEntity, RecommendedTag> {

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
