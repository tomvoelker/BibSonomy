package org.bibsonomy.recommender.tags;

import org.bibsonomy.model.RecommendedTagComparator;
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
	
	public String getInfo() {
		return "Default tag recommender.";
	}

}
