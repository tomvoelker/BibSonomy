package org.bibsonomy.recommender.item.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;


/**
 * This interface is absolutely mandatory for the item recommender framework to work.
 * It allows the framework to get information out of your applicaion's database.
 * 
 * The functions have to be implemented really accurately to prevent the calculation
 * failures.
 * 
 * @author Lukas
 *
 */
public interface RecommenderMainItemAccess {

	/**
	 * This method should return the count most actual items from the database,
	 * sorted descending by it's creation date
	 * 
	 * @param count the count of items to return
	 * 
	 * @return a list of the most actual items, sorted descending by creation date
	 */
	public List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity);
	
	/**
	 * This method should provide access to a maximum of count items belonging to the specified user.
	 * 
	 * @param count the maximum count of items to return
	 * @param username the username for whom to retrieve his items
	 * 
	 * @return a maximum of count items owned by the user
	 */
	public List<RecommendationItem> getItemsForUser(final int count, final String username);
	
	/**
	 * This method should return a Collection of items which will be evaluated by content based
	 * filtering.
	 * 
	 * @param maxItemsToEvaluate the count of items to return
	 * @param entity the item recommendation entity to get recommendations for
	 * @return a collection of items to evaluate
	 */
	public Collection<RecommendationItem> getItemsForContentBasedFiltering(final int maxItemsToEvaluate, final ItemRecommendationEntity entity);
	
	/**
	 * This method should return a List of maxItemsToEvaluate items, with each item in it is tagged
	 * with at least one of the tags in tags.
	 * 
	 * @param maxItemsToEvaluate the maximum items to evaluate
	 * @param tags a set of tags, for which to retrieve the tagged items 
	 * @return a list of tagged items
	 */
	public List<RecommendationItem> getTaggedItems(final int maxItemsToEvaluate, final Set<String> tags);
	
}
