package org.bibsonomy.recommender.tag.service;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

import recommender.core.model.Pair;

/**
 * This interface is absolutely mandatory for the tag recommender framework to work.
 * It allows the framework to get information out of your applicaion's database.
 * 
 * The functions have to be implemented really accurately to prevent the calculation
 * failures.
 */
public interface RecommenderMainTagAccess {
	

	/**
	 * This method should return the range most popular tags of a
	 * user, given by username
	 * 
	 * @param username
	 * @param range - the number of tags to get 
	 * 
	 * @return list of pairs [tagname,frequency]
	 */
	public List<Pair<String, Integer>> getMostPopularTagsForUser(final String username, final int range);

	/**
	 * This method should return the range most popular tags of a
	 * entity , unique identified by entityId
	 * 
	 * @param <T> The type of the resource.
	 * @param resourceType
	 * @param entityId
	 * @param range
	 * @return The most popular tags of the given resource.
	 */
	public List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(final Post<? extends Resource> entity, final String entityId, final int range);

	/**
	 * This method should return the count of different tags a user ever used
	 * 
	 * @param username
	 * @return number of tags used by given user 
	 */
	public Integer getNumberOfTagsForUser(String username);

	/**
	 * This method should return the count of taggings a user ever did.
	 * Each tag a user ever used counts (equal tags also count more than once).
	 * 
	 * @param username
	 * @return number of tag assignments of given user
	 */
	public Integer getNumberOfTaggingsForUser(String username);

	/**
	 * This method should return the count of tags which were used to tag the entity 
	 * which is unique identified by it's entityId
	 *
	 * @param entity - type of the resource 
	 * @param entityId - id of the entity
	 * 
	 * @return The number of tags attached to the resource.
	 */
	public Integer getNumberOfTagsForRecommendationEntity(final Post<? extends Resource> entity, final String entityId);

	/**
	 *
	 * This method should return the count of tag assignments for the specified entity
	 * which is unique identified by it's entityId
	 * 
	 * The count of tagging includes all tags which were ever assigned to this entity
	 * (even equal tag assignments count more than one time!)
	 *
	 * @param <T> 
	 * @param resourceType - type of the resource 
	 * @param entitiyId - id of the entity
	 * 
	 * @return The number of tag assignments of the resource.
	 */
	public Integer getNumberOfTagAssignmentsForRecommendationEntity(final Post<? extends Resource> entity, final String entitiyId);
	
	/**
	 * This method should return an unique id for a user,
	 * specified by it's userName
	 * 
	 * @param userName user's name
	 * @return user's id, null if user name doesn't exist
	 */
	public Integer getUserIDByName(String userName);

	/**
	 * This method should return an username for a user,
	 * who is unique identified by it's userID
	 * 
	 * @param userID user's id
	 * @return user's name, null if user id doesn't exist
	 */
	public String getUserNameByID(int userID);

	/**
	 * This method should return all tags, which were ever assigned to the entity,
	 * identified by it's entityId
	 * 
	 * (also duplicates should be returned! no distinct!)
	 * 
	 * @param entityId unique id of the entity
	 * @return list of all tags chosen by user for given entity
	 */
	public List<String> getTagNamesForRecommendationEntity(Integer entityId);
}
