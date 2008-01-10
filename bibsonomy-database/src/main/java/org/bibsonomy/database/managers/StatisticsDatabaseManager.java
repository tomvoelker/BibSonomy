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

	private StatisticsDatabaseManager() {
		generalDBManager = GeneralDatabaseManager.getInstance();
	}

	/**
	 * @return DocumentDatabaseManager
	 */
	public static StatisticsDatabaseManager getInstance() {
		return singleton;
	}
	
	/**
	 * @param userName
	 * @param session
	 * @return
	 */
	private int getNumBookmarksForUser(final StatisticsParam param, final DBSession session){
		return this.queryForObject("getNumBookmarksForUser", param, int.class, session);
	}
	
	/**
	 * @param userName
	 * @param session
	 * @return
	 */
	private int getNumPublicationsForUser(final StatisticsParam param, final DBSession session){
		return this.queryForObject("getNumPublicationsForUser", param, int.class, session);
	}
	
	/**
	 * @param userName
	 * @param resourceType
	 * @param session
	 * @return a statistical number (int)
	 */
	public int getNumberOfResourcesForUser(final String requestedUserName, final String loginUserName, Class<? extends Resource> resourceType, final DBSession session) {
		
		// check which groups this user is allowed to see
		List<Integer> groups = generalDBManager.getGroupIdsForUser(loginUserName, session);
		groups.add(GroupID.PUBLIC.getId());
		if (requestedUserName.equals(loginUserName)) {
			groups.add(GroupID.PRIVATE.getId());
			groups.add(GroupID.FRIENDS.getId());
		}
		
		StatisticsParam param = new StatisticsParam();
		param.setGroups(groups);
		param.setRequestedUserName(requestedUserName);
		
		if (resourceType == BibTex.class) {
			return this.getNumPublicationsForUser(param, session);
		}
		else if (resourceType == Bookmark.class) {
			return this.getNumBookmarksForUser(param, session);
		}
		else {
			throw new UnsupportedResourceTypeException("Resource type " + resourceType + " not supported for this query.");
		}
	}
}