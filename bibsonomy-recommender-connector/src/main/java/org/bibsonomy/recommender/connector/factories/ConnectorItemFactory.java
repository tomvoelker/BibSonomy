package org.bibsonomy.recommender.connector.factories;

import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.connector.model.ResourceWrapper;

import recommender.core.interfaces.factories.RecommenderItemFactory;
import recommender.core.interfaces.model.RecommendationItem;

public class ConnectorItemFactory implements RecommenderItemFactory{

	@Override
	public RecommendationItem getInstanceOfRecommendationItem() {
		return new ResourceWrapper(new Resource() {
			
			/**
			 * for persistence
			 */
			private static final long serialVersionUID = -276353399156038877L;

			@Override
			public void recalculateHashes() {}
		});
	}

}
