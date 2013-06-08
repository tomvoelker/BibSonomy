package org.bibsonomy.recommender.connector.factories;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.UserWrapper;

import recommender.core.interfaces.factories.RecommenderUserFactory;
import recommender.core.interfaces.model.RecommendationUser;

public class ConnectorUserFactory implements RecommenderUserFactory {

	@Override
	public RecommendationUser getRecommendationuserInstance(String id) {
		return new UserWrapper(new User(id));
	}

}
