package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.ResourceType;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ExceptionUtils;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class StatisticsDatabaseManager extends AbstractDatabaseManager{
	
	private static final Logger log = Logger.getLogger(UserDatabaseManager.class);
	private final static StatisticsDatabaseManager singleton = new StatisticsDatabaseManager();
	private final GeneralDatabaseManager generalDBManager;
	private final BibTexDatabaseManager bibtexDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;

	private StatisticsDatabaseManager() {
		generalDBManager = GeneralDatabaseManager.getInstance();
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
	 * @param userName
	 * @param resourceType
	 * @param session
	 * @return a statistical number (int)
	 */
	public Integer getNumberOfResourcesForUser(Class<? extends Resource> resourceType, final String requestedUserName, final String loginUserName, final DBSession session) {				
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexForUserCount(requestedUserName, loginUserName, session);
		}
		else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkForUserCount(requestedUserName, loginUserName, session);
		}
		else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}
	
	/**
	 * Returns the number of resources of the given group
	 * 
	 * @param resourceType
	 * @param groupId
	 * @param loginUserName
	 * @param session
	 * @return
	 */
	public Integer getNumberOfResourcesForGroup(Class<? extends Resource> resourceType, final int groupId, final String loginUserName, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibTexForGroupCount(groupId, loginUserName, session);
		}
		else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkForGroupCount(groupId, loginUserName, session);
		}
		else {
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
	 * @return
	 */
	public Integer getNumberOfResourcesForTags(Class<? extends Resource> resourceType, final List<String> tags, List<Integer> visibleGroupIDs, final DBSession session) {
		if (resourceType == BibTex.class) {
			return this.bibtexDBManager.getBibtexByTagNamesCount(tags, visibleGroupIDs, session);
		}
		else if (resourceType == Bookmark.class) {
			return this.bookmarkDBManager.getBookmarkByTagNamesCount(tags, visibleGroupIDs, session);
		}
		else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}			
	}
	
	public Integer getTagGlobalCount(String tagName) {
		return null;
	}
}