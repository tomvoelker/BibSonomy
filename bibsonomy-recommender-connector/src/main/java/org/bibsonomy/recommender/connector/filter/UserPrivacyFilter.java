/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.recommender.connector.filter;

import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
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
