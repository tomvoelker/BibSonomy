package org.bibsonomy.recommender.item.filter;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.model.ItemRecommendationEntity;

/**
 * Filters user to send only insensitive data to external services.
 * 
 * @author lukas
 *
 */
public class UserPrivacyFilter implements PrivacyFilter<ItemRecommendationEntity>{

	private ExtendedMainAccess dbAccess;
	
	/*
	 * This method maps usernames to their ids as they are set in the database.
	 * 
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.filter.PrivacyFilter#filterEntity(recommender.core.interfaces.model.RecommendationEntity)
	 */
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

	/**
	 * @param dbAccess
	 */
	public void setDbAccess(ExtendedMainAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
}
