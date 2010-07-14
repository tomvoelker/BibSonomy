package org.bibsonomy.recommender.tags;

import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.meta.CompositeTagRecommender;
import org.bibsonomy.recommender.tags.simple.SimpleContentBasedTagRecommender;


/**
 * Default tag recommender.
 * 
 * @author rja
 * @version $Id$
 */
public class DefaultTagRecommender extends CompositeTagRecommender {

	/**
	 * Currently only the {@link SimpleContentBasedTagRecommender} is included.
	 */
	public DefaultTagRecommender() {
		super(new RecommendedTagComparator());
		addTagRecommender(new SimpleContentBasedTagRecommender());
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
