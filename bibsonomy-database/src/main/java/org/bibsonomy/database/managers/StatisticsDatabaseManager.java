package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.statistic.post.PostStatisticChain;
import org.bibsonomy.database.managers.chain.statistic.tag.TagStatisticChain;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * @author Dominik Benz
 * @author Stefan Stützer
 * @version $Id$
 */
public class StatisticsDatabaseManager extends AbstractDatabaseManager {

	private final static StatisticsDatabaseManager singleton = new StatisticsDatabaseManager();
	
	private static final PostStatisticChain postchain = new PostStatisticChain();
	private static final TagStatisticChain tagChain = new TagStatisticChain();
	
	/**
	 * @return StatisticsDatabaseManager
	 */
	public static StatisticsDatabaseManager getInstance() {
		return singleton;
	}

	private final BibTexDatabaseManager bibtexDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;

	private StatisticsDatabaseManager() {
		this.bibtexDBManager = BibTexDatabaseManager.getInstance();
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();
	}

	/**
	 * @param param
	 * @param session
	 * @return The number of posts matching the given params.
	 * 
	 */
	public int getPostStatistics(final StatisticsParam param, final DBSession session) {
		// FIXME: this is ugly, but using chain elements forces us to use lists as return types
		final Integer count = postchain.getFirstElement().perform(param, session).get(0);
		// to not get NPEs later
		return count == null ? 0 : count;
	}

	/**
	 * @param param
	 * @param session
	 * @return The number of tags matching the given params
	 */
	public int getTagStatistics(final StatisticsParam param, final DBSession session) {
		// FIXME: this is ugly, but using chain elements forces us to use lists as return types
		final Integer count = tagChain.getFirstElement().perform(param, session).get(0);
		// to not get NPEs later
		return count == null ? 0 : count;
	}

	/**
	 * @param param
	 * @param session
	 * @return number of relations from a user
	 */
	public Integer getNumberOfRelationsForUser(final StatisticsParam param, final DBSession session) {
		Integer result = null;

		result = this.queryForObject("getNumberOfRelationsForUser", param.getRequestedUserName(), Integer.class, session);

		return result;
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
		final Integer count;
		// TODO: remove resourceType checks!!! CrudableContent HashSet @see DBLogic
		if (resourceType == BibTex.class) {
			count = this.bibtexDBManager.getPostsForUserCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			count = this.bookmarkDBManager.getPostsForUserCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
	}

	/**
	 * @param resourceType
	 * @param requHash 
	 * @param simHash 
	 * @param session
	 * @return a statistical number (int)
	 */
	public int getNumberOfResourcesForHash(final Class<? extends Resource> resourceType, final String requHash, final HashID simHash, final DBSession session) {
		final Integer count;
		// TODO: remove resourceType checking
		if (resourceType == BibTex.class) {
			count = this.bibtexDBManager.getPostsByHashCount(requHash, simHash, session);
		} else if (resourceType == Bookmark.class) {
			count = this.bookmarkDBManager.getPostsByHashCount(requHash, simHash, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;		
	}
	
	/**
	 * @param resourceType
	 * @param requHash 
	 * @param simHash 
	 * @param userName
	 * @param session
	 * @return a statistical number (int)
	 */
	public int getNumberOfResourcesForHashAndUser(final Class<? extends Resource> resourceType, final String requHash, final HashID simHash, final String userName, final DBSession session) {
		final Integer count;
		// TODO resource checks
		if (resourceType == BibTex.class) {
			count = this.bibtexDBManager.getPostsByHashAndUserCount(requHash, simHash, userName, session);
		} else if (resourceType == Bookmark.class) {
			count = this.bookmarkDBManager.getPostsByHashAndUserCount(requHash, simHash, userName, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;		
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
	public int getNumberOfResourcesForGroup(final Class<? extends Resource> resourceType, final String requestedUserName, final String userName, int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		final Integer count;
		
		// TODO: refactor remove resourceType checking
		if (resourceType == BibTex.class) {
			count = this.bibtexDBManager.getPostsForGroupCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			count = this.bookmarkDBManager.getPostsForGroupCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
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
		// TODO: refactor
		final Integer count;
		if (resourceType == BibTex.class) {
			count = this.bibtexDBManager.getPostsByTagNamesCount(tagIndex, groupId, session);
		} else if (resourceType == Bookmark.class) {
			count = this.bookmarkDBManager.getPostsByTagNamesCount(tagIndex, groupId, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
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
		// TODO: refactor
		final Integer count;
		if (resourceType == BibTex.class) {
			count = this.bibtexDBManager.getPostsByTagNamesForUserCount(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			count = this.bookmarkDBManager.getPostsByTagNamesForUserCount(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
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
		final Integer count;
		if (resourceType == BibTex.class) {
			count = this.bibtexDBManager.getBibTexDuplicateCount(requestedUserName, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
	}

	/**
	 * TODO: document me...
	 * 
	 * @param tagName
	 * @return tag global count
	 */
	public Integer getTagGlobalCount(String tagName) {
		// FIXME: implement me...
		return null;
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
		// TODO refactor !!!
		final Integer count;
		if(resourceType == BibTex.class){
			count = this.bibtexDBManager.getGroupPostsCount(requestedUserName, loginUserName, visibleGroupIDs, session);
		}else if(resourceType == Bookmark.class){
			count = this.bookmarkDBManager.getGroupPostsCount(requestedUserName, loginUserName, visibleGroupIDs, session);
		}else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
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
		// TODO refactor
		final Integer count;
		if(resourceType == BibTex.class){
			count = this.bibtexDBManager.getGroupPostsCountByTag(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		}else if(resourceType == Bookmark.class){
			count = this.bookmarkDBManager.getGroupPostsCountByTag(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		}else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
	}

	/**
	 * @param resourceType
	 * @param days
	 * @param session
	 * @return the number of days when a resource was popular
	 */
	public int getPopularDays(final Class<? extends Resource> resourceType, final int days, final DBSession session){
		// TODO: refactor see other methods
		
		final Integer count;
		if(resourceType == BibTex.class){
			count = this.bibtexDBManager.getPostPopularDays(days, session);
		}else if(resourceType == Bookmark.class){
			count = this.bookmarkDBManager.getPostPopularDays(days, session);
		}else{
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
		// to not get NPEs 
		return count == null ? 0 : count;
	}
}