package org.bibsonomy.recommender.item.service;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.filter.UserPrivacyFilter;
import org.bibsonomy.recommender.item.model.RecommendationUser;

/**
 * This interface extends the main access interface of the recommender library.
 * This is done to allow some bibsonomy specific data use and a greedy-loading approach.
 * 
 * @author lukas
 * @param <R> 
 *
 */
public interface ExtendedMainAccess<R extends Resource> extends RecommenderMainItemAccess<R> {

	/**
	 * This method retrieves a list of resources from the bibsonomy database by contentid.
	 * This is needed in case of caching the results fails and those have to be retrieved from the database.
	 * In this case the loading of a fully wrapped resource should take place.
	 * 
	 * @param ids a list of content ids for which to retrieve content
	 * 
	 * @return the wrapped posts belonging to the specified ids
	 */
	public List<Post<R>> getResourcesByIds(final List<Integer> ids);
	
	/**
	 * This method should provide access to a maximum of count items belonging to the requesting user.
	 * This merges his or her bibtex and bookmark resources to get a better overview of his preferences.
	 * 
	 * @param count the maximum count of items to return
	 * @param username the username for whom to retrieve his items
	 * 
	 * @return a maximum of count items owned by the requesting user
	 */
	public List<Post<? extends Resource>> getAllItemsOfQueryingUser(final int count, final String username);
	
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
	public List<String> getSimilarUsers(final int count, final RecommendationUser entity);
	
	/**
	 * This method tries to retrieve an item by it's given intrahash and username from the database.
	 * It returns the found item or null otherwise.
	 * 
	 * @param hash the intrahash of the resource
	 * @param userId the id of the item's owner
	 * @return the item or null otherwise
	 */
	public Post<? extends Resource> getItemByUserIdWithHash(final String hash, final String userId);
	
	/**
	 * Retrieves any item which title fits to the given title.
	 * 
	 * @param title the title of the item to retrieve
	 * @return the item or null if no item with this title was found
	 */
	public Post<? extends Resource> getItemByTitle(final String title);
	
}
