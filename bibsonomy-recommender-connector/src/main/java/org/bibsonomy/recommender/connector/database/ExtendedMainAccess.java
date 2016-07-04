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
package org.bibsonomy.recommender.connector.database;

import java.util.List;

import org.bibsonomy.recommender.connector.filter.UserPrivacyFilter;

import recommender.core.interfaces.database.RecommenderMainItemAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

/**
 * This interface extends the main access interface of the recommender library.
 * This is done to allow some bibsonomy specific data use and a greedy-loading approach.
 * 
 * @author lukas
 *
 */
public interface ExtendedMainAccess extends RecommenderMainItemAccess {

	/**
	 * This method retrieves a list of resources from the bibsonomy database by contentid.
	 * This is needed in case of caching the results fails and those have to be retrieved from the database.
	 * In this case the loading of a fully wrapped resource should take place.
	 * 
	 * @param ids a list of content ids for which to retrieve content
	 * 
	 * @return the wrapped posts belonging to the specified ids
	 */
	public abstract List<RecommendationItem> getResourcesByIds(final List<Integer> ids);
	
	/**
	 * This method should provide access to a maximum of count items belonging to the requesting user.
	 * This merges his or her bibtex and bookmark resources to get a better overview of his preferences.
	 * 
	 * @param count the maximum count of items to return
	 * @param username the username for whom to retrieve his items
	 * 
	 * @return a maximum of count items owned by the requesting user
	 */
	public List<RecommendationItem> getAllItemsOfQueryingUser(final int count, final String username);
	
	/**
	 * This method allows the {@link UserPrivacyFilter} to substitute usernames
	 * by ids to forward those to external recommendation services.
	 * 
	 * @param username the username to substitute
	 * 
	 * @return the corresponding id
	 */
	public Long getUserIdByName(final String username);
	
	/**
	 * This method fetches similar users from database and returns them.
	 * 
	 * @param count the count of similar users to fetch
	 * @param entity the entity to get similar users for
	 * @return a list of similar users
	 */
	public List<String> getSimilarUsers(final int count, final ItemRecommendationEntity entity);
	
	/**
	 * This method tries to retrieve an item by it's given intrahash and username from the database.
	 * It returns the found item or null otherwise.
	 * 
	 * @param hash the intrahash of the resource
	 * @param userId the id of the item's owner
	 * @return the item or null otherwise
	 */
	public RecommendationItem getItemByUserIdWithHash(final String hash, final String userId);
	
	/**
	 * Retrieves any item which title fits to the given title.
	 * 
	 * @param title the title of the item to retrieve
	 * @return the item or null if no item with this title was found
	 */
	public RecommendationItem getItemByTitle(final String title);
	
}
