package org.bibsonomy.recommender;

import org.bibsonomy.recommender.tags.SimpleContentBasedTagRecommender;


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
		addTagRecommender(new SimpleContentBasedTagRecommender());
	}
	
	public String getInfo() {
		return "Default tag recommender.";
	}

}
