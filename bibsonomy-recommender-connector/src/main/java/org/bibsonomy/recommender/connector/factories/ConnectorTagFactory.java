package org.bibsonomy.recommender.connector.factories;

import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.connector.model.TagWrapper;

import recommender.core.interfaces.factories.RecommenderTagFactory;
import recommender.core.interfaces.model.RecommendationTag;

public class ConnectorTagFactory implements RecommenderTagFactory {

	@Override
	public RecommendationTag getEmptyTag() {

		return new TagWrapper(new Tag());
		
	}

}
