package org.bibsonomy.recommender.connector.factories;

import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.connector.model.TagWrapper;

import recommender.core.interfaces.factories.RecommenderTagFactory;
import recommender.core.interfaces.model.RecommendationTag;

/**
 * Create an empty {@link Tag} and pass it forward.
 * 
 * @author lukas
 *
 */
public class ConnectorTagFactory implements RecommenderTagFactory {

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.factories.RecommenderTagFactory#getEmptyTag()
	 */
	@Override
	public RecommendationTag getEmptyTag() {

		return new TagWrapper(new Tag());
		
	}

}
