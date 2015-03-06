/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.StatisticsUnit;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.model.statistics.StatisticsValues;

/**
 * @author Dominik Benz
 * @author Stefan Stützer
 */
public class StatisticsDatabaseManager extends AbstractDatabaseManager {

	private static final StatisticsDatabaseManager singleton = new StatisticsDatabaseManager();

	/**
	 * @return StatisticsDatabaseManager
	 */
	public static StatisticsDatabaseManager getInstance() {
		return singleton;
	}
	
	
	private Chain<Statistics, StatisticsParam> postChain;
	private Chain<Statistics, StatisticsParam> tagChain;
	private Chain<Statistics, StatisticsParam> userChain;

	private final BibTexDatabaseManager bibtexDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;
	private final TagDatabaseManager tagDatabaseManager;
	private final AdminDatabaseManager adminDatabaseManager;
	private final Map<Class<? extends Resource>, PostDatabaseManager<? extends Resource, ? extends ResourceParam<? extends Resource>>> postDatabaseManager;

	private StatisticsDatabaseManager() {
		this.adminDatabaseManager = AdminDatabaseManager.getInstance();
		this.bibtexDBManager = BibTexDatabaseManager.getInstance();
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();
		this.tagDatabaseManager = TagDatabaseManager.getInstance();

		// TODO: refactor @see DBLogic
		this.postDatabaseManager = new HashMap<Class<? extends Resource>, PostDatabaseManager<? extends Resource, ? extends ResourceParam<? extends Resource>>>();
		this.postDatabaseManager.put(Bookmark.class, this.bookmarkDBManager);
		this.postDatabaseManager.put(BibTex.class, this.bibtexDBManager);
	}

	/**
	 * @param param
	 * @param session
	 * @return The number of posts matching the given params.
	 * 
	 */
	public Statistics getPostStatistics(final StatisticsParam param, final DBSession session) {
		final Statistics statisticData = postChain.perform(param, session);
		// to not get NPEs later
		if (present(statisticData)) {
			return statisticData;
		}
		return new Statistics();
	}
	
	/**
	 * @param constraints
	 * @param interval 
	 * @param status 
	 * @param classifier 
	 * @param unit 
	 * @param session 
	 * @return the statistics (currently only count) of all registered users matching
	 * 			the criteria
	 */
	public Statistics getUserStatistics(final Set<StatisticsConstraint> constraints, Classifier classifier, SpamStatus status, Integer interval, StatisticsUnit unit, final DBSession session) {
		final StatisticsParam param = new StatisticsParam();
		param.setClassifier(classifier);
		param.setSpamStatus(status);
		param.setInterval(interval);
		param.setConstraints(constraints);
		param.setUnit(unit);
		
		final Statistics statistics = this.userChain.perform(param, session);
		if (present(statistics)) {
			return statistics;
		}
		return new Statistics();
	}

	/**
	 * @param param
	 * @param session
	 * @return The number of tags matching the given params
	 */
	public int getTagStatistics(final StatisticsParam param, final DBSession session) {
		return tagChain.perform(param, session).getCount();
	}

	/**
	 * @param param
	 * @param session
	 * @return number of relations from a user
	 */
	public int getNumberOfRelationsForUser(final StatisticsParam param, final DBSession session) {
		final Integer count = this.queryForObject("getNumberOfRelationsForUser", param.getRequestedUserName(), Integer.class, session);
		return saveConvertToint(count);
	}

	/**
	 * @param resourceType
	 * @param requestedUserName
	 * @param userName 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param session
	 * @return a statistical number (int)
	 */
	public int getNumberOfResourcesForUser(final Class<? extends Resource> resourceType, final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsForUserCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
	}


	/**
	 * @param resourceType
	 * @param requestedUserName
	 * @param userName 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param session
	 * @return a statistical number (int)
	 */
	public int getNumberOfResourcesWithDiscussions(final Class<? extends Resource> resourceType, final String requestedUserName, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsWithDiscussionsCount(requestedUserName, userName, visibleGroupIDs, session);
	}


	/**
	 * @param resourceType
	 * @param groupId (of the requested group)
	 * @param userName 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param session
	 * @return a statistical number (int)
	 */
	public int getNumberOfResourcesWithDiscussionsForGroup(final Class<? extends Resource> resourceType, final int groupId, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsWithDiscussionsCountForGroup(groupId, userName, visibleGroupIDs, session);
	}

	/**
	 * @param resourceType
	 * @param requHash 
	 * @param simHash 
	 * @param session
	 * @return a statistical number (int)
	 */
	public int getNumberOfResourcesForHash(final Class<? extends Resource> resourceType, final String requHash, final HashID simHash, final DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsByHashCount(requHash, simHash, session);
	}

	/**
	 * Returns the number of resources of the given group
	 * 
	 * @param resourceType
	 * @param requestedUserName 
	 * @param userName 
	 * @param groupId
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of resources for given group
	 */
	public int getNumberOfResourcesForGroup(final Class<? extends Resource> resourceType, final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsForGroupCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
	}

	/**
	 * Returns the number of resources for a list of tags
	 * 
	 * @param resourceType
	 * @param tagIndex 
	 * @param groupId 
	 * @param session
	 * @return number of resources for a list of tags
	 */
	public int getNumberOfResourcesForTags(final Class<? extends Resource> resourceType, final List<TagIndex> tagIndex, final int groupId, final DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsByTagNamesCount(tagIndex, groupId, session);
	}

	/**
	 * Returns the number of resources for a given user and a list of tags
	 * 
	 * @param resourceType
	 * @param tagIndex
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of resources for a given user and a list of tags
	 */
	public int getNumberOfResourcesForUserAndTags(final Class<? extends Resource> resourceType, final List<TagIndex> tagIndex, final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsByTagNamesForUserCount(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
	}
	
	/**
	 * @param resourceType
	 * @param startDate 
	 * @param usersToExclude
	 * @param session 
	 * @return number of posts
	 */
	public int getNumberOfPosts(Class<? extends Resource> resourceType, Date startDate, List<String> usersToExclude, DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getPostsCount(startDate, usersToExclude, session);
	}
	
	/**
	 * @param resourceType
	 * @param startDate
	 * @param usersToExclude
	 * @param session
	 * @return number of posts in log table
	 */
	public int getNumberOfPostsInHistory(Class<? extends Resource> resourceType, Date startDate, List<String> usersToExclude, DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getHistoryPostsCount(startDate, usersToExclude, session);
	}
	
	/**
	 * @param resourceType
	 * @param startDate 
	 * @param usersToExclude 
	 * @param session
	 * @return number of unique items
	 */
	public int getNumberOfUniqueResources(Class<? extends Resource> resourceType, Date startDate, List<String> usersToExclude, DBSession session) {
		return this.getDatabaseManagerForResourceType(resourceType).getUniqueResourcesCount(startDate, usersToExclude, session);
	}
	
	/**
	 * @param session
	 * @return the number of posts in the clipboard
	 */
	public int getNumberOfClipboardPosts(DBSession session) {
		final Integer result = this.queryForObject("getClipboardCount", Integer.class, session);
		return result == null ? 0 : result.intValue();
	}

	/**
	 * Returns the number of resources for a given user that occur at least twice
	 * 
	 * @param resourceType
	 * @param requestedUserName 
	 * @param session
	 * @return number of resources  that occur at least twice
	 */
	public int getNumberOfDuplicates(final Class<? extends Resource> resourceType, final String requestedUserName, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getPostsDuplicateCount(requestedUserName, session);
		}

		throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
	}

	/**
	 * TODO: document me...
	 * 
	 * @param tagName
	 * @return tag global count
	 */
	public int getTagGlobalCount(final String tagName) {
		// FIXME: implement me...
		return 0;
	}
	
	/**
	 * @param session
	 * @return the number of distinct tags in the system
	 */
	public int getNumberOfTags(DBSession session) {
		return this.tagDatabaseManager.getNumberOfTags(session);
	}
	
	/**
	 * @param contentType 
	 * @param startDate 
	 * @param usersToExclude 
	 * @param session
	 * @return the number of tag assignments
	 */
	public int getNumberOfTas(int contentType, Date startDate, List<String> usersToExclude, DBSession session) {
		return this.tagDatabaseManager.getNumberOfTas(contentType, startDate, usersToExclude, session);
	}

	/**
	 * 
	 * @param resourceType
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of resources that are available for some groups
	 */
	public int getNumberOfResourcesForUserAndGroup(final Class<? extends Resource> resourceType, final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session){
		return this.getDatabaseManagerForResourceType(resourceType).getGroupPostsCount(requestedUserName, loginUserName, visibleGroupIDs, session);
	}

	/**
	 * @param resourceType
	 * @param requestedUserName
	 * @param tagIndex
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of resources that are available for some groups and tagged by a tag of the tagIndex
	 */
	public int getNumberOfResourcesForUserAndGroupByTag(final Class<? extends Resource> resourceType, final String requestedUserName, final List<TagIndex> tagIndex, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session){
		return this.getDatabaseManagerForResourceType(resourceType).getGroupPostsCountByTag(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
	}

	/**
	 * @param resourceType
	 * @param days
	 * @param session
	 * @return the number of days when a resource was popular
	 */
	public int getPopularDays(final Class<? extends Resource> resourceType, final int days, final DBSession session){
		return this.getDatabaseManagerForResourceType(resourceType).getPostPopularDays(days, session);
	}

	private PostDatabaseManager<? extends Resource, ? extends ResourceParam<? extends Resource>> getDatabaseManagerForResourceType(final Class<? extends Resource> resourceType) {
		if (this.postDatabaseManager.containsKey(resourceType)) {
			return this.postDatabaseManager.get(resourceType);
		}

		throw new UnsupportedResourceTypeException("Resource type " + resourceType.getSimpleName() + " not supported for this query.");
	}
	
	
	public StatisticsValues getUserDiscussionsStatistics(final StatisticsParam param, final DBSession session){
		return this.queryForObject("userRatingStatistic", param, StatisticsValues.class, session);
	}

	public StatisticsValues getUserDiscussionsStatisticsForGroup(final StatisticsParam param, final DBSession session){
		return this.queryForObject("userRatingStatisticForGroup", param, StatisticsValues.class, session);
	}
	
	/**
	 * @param spamStatus 
	 * @param session
	 * @return the number of registered users
	 */
	public int getNumberOfUsers(SpamStatus spamStatus, final DBSession session) {
		final StatisticsParam param = new StatisticsParam();
		param.setSpamStatus(spamStatus);
		final Integer result = this.queryForObject("getUserCount", param, Integer.class, session);
		return result == null ? 0 : result.intValue();
	}
	
	/**
	 * 
	 * @param statisticsUnit 
	 * @param interval 
	 * @param session
	 * @return the number of active users (posted at least one post)
	 */
	public int getNumberOfActiveUsers(Integer interval, StatisticsUnit statisticsUnit, final DBSession session) {
		final StatisticsParam param = new StatisticsParam();
		param.setUnit(statisticsUnit);
		param.setInterval(interval);
		final Integer result = this.queryForObject("getActiveUserCount", param, Integer.class, session);
		return result == null ? 0 : result.intValue();
	}

	/**
	 * @param spamStatus
	 * @param interval
	 * @param session
	 * @return the number of users classified by an admin matching the interval and spam status
	 */
	public int getNumberOfClassifiedUsersByAdmin(SpamStatus spamStatus, int interval, DBSession session) {
		return this.adminDatabaseManager.getNumberOfClassifedUsersByAdmin(spamStatus, interval, session);
	}
	
	/**
	 * @param spamStatus
	 * @param interval
	 * @param session
	 * @return the number of users classified by the classifier
	 */
	public int getNumberOfClassifiedUsersByClassifier(SpamStatus spamStatus, int interval, DBSession session) {
		return this.adminDatabaseManager.getNumberOfClassifedUsersByClassifier(spamStatus, interval, session);
	}

	/**
	 * @param postChain the postChain to set
	 */
	public void setPostChain(final Chain<Statistics, StatisticsParam> postChain) {
		this.postChain = postChain;
	}

	/**
	 * @param tagChain the tagChain to set
	 */
	public void setTagChain(final Chain<Statistics, StatisticsParam> tagChain) {
		this.tagChain = tagChain;
	}

	/**
	 * @param userChain the userChain to set
	 */
	public void setUserChain(Chain<Statistics, StatisticsParam> userChain) {
		this.userChain = userChain;
	}
}