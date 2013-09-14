package org.bibsonomy.recommender.connector.filter;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl;
import org.bibsonomy.recommender.connector.model.UserWrapper;

import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.model.ItemRecommendationEntity;

/**
 * Filters user to send only insensitive data to external services.
 * 
 * @author lukas
 *
 */
public class UserPrivacyFilter implements PrivacyFilter<ItemRecommendationEntity>{

	private AbstractRecommenderMainItemAccessImpl dbAccess;
	
	@Override
	public ItemRecommendationEntity filterEntity(ItemRecommendationEntity entity) {
		
		if(entity instanceof UserWrapper) {
			
			final User unfiltered = ((UserWrapper) entity).getUser();
			final User filtered = new User();
			
			// map username to id
			filtered.setName(""+this.dbAccess.getUserIdByName(unfiltered.getName()));
			
			return new UserWrapper(filtered);
		}
		
		return null;
		
	}

	public void setDbAccess(AbstractRecommenderMainItemAccessImpl dbAccess) {
		this.dbAccess = dbAccess;
	}
	
}
