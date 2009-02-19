package org.bibsonomy.database.managers;

import java.util.List;

import org.apache.log4j.Logger;
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

	private static final Logger log = Logger.getLogger(StatisticsDatabaseManager.class);
	
	private final BibTexDatabaseManager bibtexDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;
	private static final PostStatisticChain postchain = new PostStatisticChain();
	private static final TagStatisticChain tagChain = new TagStatisticChain();


	private StatisticsDatabaseManager() {
		this.bibtexDBManager = BibTexDatabaseManager.getInstance();
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();
	}

	/**
	 * @return StatisticsDatabaseManager
	 */
	public static StatisticsDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * @param param
	 * @param session
	 * @return
	 */
	public Integer getPostStatistics(final StatisticsParam param, final DBSession session) {
		// start the chain
		final List<Integer> count = postchain.getFirstElement().perform(param, session);		

		// FIXME: this is ugly, but using chain elements forces us to use lists as return types
		return count.get(0);
	}
	
	public Integer getTagStatistics(final StatisticsParam param, final DBSession session) {
		// start the chain
		final List<Integer> count = tagChain.getFirstElement().perform(param, session);		

		// FIXME: this is ugly, but using chain elements forces us to use lists as return types
		return count.get(0);
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
	 * @param groupId 
	 * @param session
	 * @return a statistical number (int)
	 */
	public Integer getNumberOfResourcesForUser(final Class<? extends Resource> resourceType, final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexForUserCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkForUserCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}
	

	/**
	 * @param resourceType
	 * @param requHash 
	 * @param simHash 
	 * @param session
	 * @return a statistical number (int)
	 */
	public Integer getNumberOfResourcesForHash(final Class<? extends Resource> resourceType, final String requHash, final HashID simHash, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexByHashCount(requHash, simHash, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkByHashCount(requHash, simHash, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
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
	public Integer getNumberOfResourcesForGroup(final Class<? extends Resource> resourceType, final String requestedUserName, final String userName, int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexForGroupCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkForGroupCount(requestedUserName, userName, groupId, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
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
	public Integer getNumberOfResourcesForTags(final Class<? extends Resource> resourceType, final List<TagIndex> tagIndex, final int groupId, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibtexByTagNamesCount(tagIndex, groupId, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkByTagNamesCount(tagIndex, groupId, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
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
	public Integer getNumberOfResourcesForUserAndTags(final Class<? extends Resource> resourceType, final List<TagIndex> tagIndex, final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexByTagNamesForUserCount(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkByTagNamesForUserCount(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}

	/**
	 * Returns the number of resources for a given user that occur at least twice
	 * 
	 * @param resourceType
	 * @param requestedUserName 
	 * @param session
	 * @return number of resources  that occur at least twice
	 */
	public Integer getNumberOfDuplicates(final Class<? extends Resource> resourceType, final String requestedUserName, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexDuplicateCount(requestedUserName, session);
		} 
		throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
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
	public Integer getNumberOfResourcesForUserAndGroup(final Class<? extends Resource> resourceType, final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session){
		if(resourceType == BibTex.class){
			return this.bibtexDBManager.getGroupBibtexCount(requestedUserName, loginUserName, visibleGroupIDs, session);
		}else if(resourceType == Bookmark.class){
			return this.bookmarkDBManager.getGroupBookmarkCount(requestedUserName, loginUserName, visibleGroupIDs, session);
		}else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
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
	public Integer getNumberOfResourcesForUserAndGroupByTag(final Class<? extends Resource> resourceType, final String requestedUserName, final List<TagIndex> tagIndex, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session){
		if(resourceType == BibTex.class){
			return this.bibtexDBManager.getGroupBibtexCountByTag(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		}else if(resourceType == Bookmark.class){
			return this.bookmarkDBManager.getGroupBookmarkCountByTag(requestedUserName, loginUserName, tagIndex, visibleGroupIDs, session);
		}else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}

	/**
	 * @param resourceType
	 * @param days
	 * @param session
	 * @return the number of days when a resource was popular
	 */
	public Integer getPopularDays(final Class<? extends Resource> resourceType, final int days, final DBSession session){
		if(resourceType == BibTex.class){
			return this.bibtexDBManager.getBibTexPopularDays(days, session);
		}else if(resourceType == Bookmark.class){
			return this.bookmarkDBManager.getBookmarkPopularDays(days, session);
		}else{
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}
}