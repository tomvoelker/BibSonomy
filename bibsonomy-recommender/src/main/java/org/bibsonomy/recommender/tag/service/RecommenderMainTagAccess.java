/**
 * BibSonomy Recommendation - Tag and resource recommender.
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
	 * @param entity
	 * @param hash
	 * @param range
	 * @param resourceType
	 * @return The most popular tags of the given resource.
	 */
	public List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(final Post<? extends Resource> entity, String hash, final int range);
	
	/**
	 * @param username
	 * @param numberOfPreviousPosts
	 * @return the tags used by the user for previous posts
	 */
	public List<Pair<String, Integer>> getTagsOfPreviousPostsForUser(String username, int numberOfPreviousPosts);
	
	/**
	 * This method should return the count of different tags a user ever used
	 * 
	 * @param username
	 * @return number of tags used by given user 
	 */
	public Integer getNumberOfTagsForUser(String username);
	
	/**
	 * @param username
	 * @param numberOfPreviousPosts
	 * @return the number of previous tags
	 */
	public int getNumberOfTagsOfPreviousPostsForUser(String username, int numberOfPreviousPosts);

	/**
	 * This method should return the count of taggings a user ever did.
	 * Each tag a user ever used counts (equal tags also count more than once).
	 * 
	 * @param username
	 * @return number of tag assignments of given user
	 */
	public int getNumberOfTaggingsForUser(String username);
	
	/**
	 *
	 * This method should return the count of tag assignments for the specified entity
	 * which is unique identified by it's entityId
	 * 
	 * The count of tagging includes all tags which were ever assigned to this entity
	 * (even equal tag assignments count more than one time!)
	 * @param hash 
	 * @param entity - type of the resource 
	 *
	 * @return The number of tag assignments of the resource.
	 */
	public int getNumberOfTagAssignmentsForRecommendationEntity(final Post<? extends Resource> entity, String hash);
	
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
}
