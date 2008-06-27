package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.managers.chain.statistic.post.PostStatisticChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Stefan Stützer
 * @version $Id$
 */
public class StatisticsDatabaseManager extends AbstractDatabaseManager {

	private final static StatisticsDatabaseManager singleton = new StatisticsDatabaseManager();

	private final BibTexDatabaseManager bibtexDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;
	private static final PostStatisticChain postchain = new PostStatisticChain();


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
	
	public Integer getPostStatistics(final StatisticsParam param, final DBSession session) {
		// start the chain
		final List<Integer> count = postchain.getFirstElement().perform(param, session);		
		
		// FIXME: this is ugly, but using chain elements forces us to use lists as return types
		return count.get(0);
	}

	/**
	 * @param resourceType
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return a statistical number (int)
	 */
	public Integer getNumberOfResourcesForUser(final Class<? extends Resource> resourceType, final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexForUserCount(requestedUserName, loginUserName, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkForUserCount(requestedUserName, loginUserName, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}

	/**
	 * Returns the number of resources of the given group
	 * 
	 * @param resourceType
	 * @param groupId
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of resources for given group
	 */
	public Integer getNumberOfResourcesForGroup(final Class<? extends Resource> resourceType, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexForGroupCount(groupId, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkForGroupCount(groupId, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}

	/**
	 * Returns the number of resources for a list of tags
	 * 
	 * @param resourceType
	 * @param tags
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of resources for a list of tags
	 */
	public Integer getNumberOfResourcesForTags(final Class<? extends Resource> resourceType, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibtexByTagNamesCount(tagIndex, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkByTagNamesCount(tagIndex, visibleGroupIDs, session);
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
	 * @param param StatisticsParam
	 * @param session
	 * @return number of resources  that occur at least twice
	 */
	public Integer getNumberOfDuplicates(final Class<? extends Resource> resourceType, final String requestedUserName, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexDuplicateCount(requestedUserName, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
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
}