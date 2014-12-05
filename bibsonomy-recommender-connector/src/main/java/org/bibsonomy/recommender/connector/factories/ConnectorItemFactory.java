/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
