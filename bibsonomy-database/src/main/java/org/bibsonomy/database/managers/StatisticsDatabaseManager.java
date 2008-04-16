package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class StatisticsDatabaseManager extends AbstractDatabaseManager {

	private final static StatisticsDatabaseManager singleton = new StatisticsDatabaseManager();

	private final BibTexDatabaseManager bibtexDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;

	private StatisticsDatabaseManager() {
		bibtexDBManager = BibTexDatabaseManager.getInstance();
		bookmarkDBManager = BookmarkDatabaseManager.getInstance();
	}

	/**
	 * @return DocumentDatabaseManager
	 */
	public static StatisticsDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * @param resourceType
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return a statistical number (int)
	 */
	public Integer getNumberOfResourcesForUser(Class<? extends Resource> resourceType, final String requestedUserName, final String loginUserName, List<Integer> visibleGroupIDs, final DBSession session) {
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
	public Integer getNumberOfResourcesForGroup(Class<? extends Resource> resourceType, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
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
	public Integer getNumberOfResourcesForTags(Class<? extends Resource> resourceType, final List<String> tags, List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibtexByTagNamesCount(tags, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkByTagNamesCount(tags, visibleGroupIDs, session);
		} else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}

	/**
	 * Returns the number of resources for a given user and a list of tags
	 * 
	 * @param resourceType
	 * @param tags
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of resources for a given user and a list of tags
	 */
	public Integer getNumberOfResourcesForUserAndTags(Class<? extends Resource> resourceType, final List<String> tags, final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexByTagNamesForUserCount(requestedUserName, loginUserName, tags, visibleGroupIDs, session);
		} else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkByTagNamesForUserCount(requestedUserName, loginUserName, tags, visibleGroupIDs, session);
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