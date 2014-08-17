package org.bibsonomy.recommender.item.filter;

import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

import recommender.core.interfaces.filter.PrivacyFilter;

/**
 * Filters user to send only insensitive data to external services.
 * 
 * @author lukas
 *
 */
public class UserPrivacyFilter implements PrivacyFilter<RecommendationUser>{

	private ExtendedMainAccess<? extends Resource> dbAccess;
	
	/*
	 * This method maps usernames to their ids as they are set in the database.
	 * 
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.filter.PrivacyFilter#filterEntity(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public RecommendationUser filterEntity(RecommendationUser entity) {
		entity.setUserName(String.valueOf(this.dbAccess.getUserIdByName(entity.getUserName())));
		return entity;
	}

	/**
	 * @param dbAccess
	 */
	public void setDbAccess(ExtendedMainAccess<? extends Resource> dbAccess) {
		this.dbAccess = dbAccess;
	}
}
