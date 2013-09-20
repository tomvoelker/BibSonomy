package org.bibsonomy.recommender.connector.factories;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.connector.model.RecommendationPost;
import recommender.core.interfaces.factories.RecommenderItemFactory;
import recommender.core.interfaces.model.RecommendationItem;

/**
 * Create an empty post and pass it forward.
 * 
 * @author lukas
 *
 */
public class ConnectorItemFactory implements RecommenderItemFactory{

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.factories.RecommenderItemFactory#getInstanceOfRecommendationItem()
	 */
	@Override
	public RecommendationItem getInstanceOfRecommendationItem() {
		final Resource r = new Resource() {
			
			/**
			 * for persistence
			 */
			private static final long serialVersionUID = -276353399156038877L;

			@Override
			public void recalculateHashes() {}
		};
		
		final Post<Resource> post = new Post<Resource>();
		post.setResource(r);
		
		return new RecommendationPost(post);
	}

}
