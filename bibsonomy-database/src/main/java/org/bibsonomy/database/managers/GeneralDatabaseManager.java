package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Used to retrieve all different kind of stuff from the database.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class GeneralDatabaseManager extends AbstractDatabaseManager {

	/** Singleton */
	private final static GeneralDatabaseManager singleton = new GeneralDatabaseManager();

	private GeneralDatabaseManager() {
	}

	public static GeneralDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Checks whether two users are friends.
	 * 
	 * @param param
	 *            Database-Properties used: userName, requestedUserName
	 * @return true if the users are friends, false otherwise
	 */
	public Boolean isFriendOf(final GenericParam param, final Transaction session) {
		if (param.getUserName() == null || param.getRequestedUserName() == null) {
			return false;
		}
		if (param.getUserName().equals(param.getRequestedUserName())) {
			return true;
		}
		return this.queryForObject("isFriendOf", param, Boolean.class, session);
	}

	/**
	 * Checks whether a user, given by requestedUserName, is a spammer. If
	 * requestedUserName is set to null the default behaviour is to return
	 * false, i.e. no spammer.
	 * 
	 * @param param
	 *            Database-Properties used: requestedUserName
	 * @return true if the user is a spammer, false otherwise
	 */
	public Boolean isSpammer(final GenericParam param, final Transaction session) {
		if (param.getRequestedUserName() == null) return false;
		return this.queryForObject("isSpammer", param, Boolean.class, session);
	}

	/**
	 * Gets all the groupIds of the given users groups.
	 * 
	 * @param param
	 *            Database-Properties used: userName
	 * @return A list of groupids
	 */
	public List<Integer> getGroupIdsForUser(final GenericParam param, final Transaction session) {
		return this.queryForList("getGroupIdsForUser", param, Integer.class, session);
	}

	/**
	 * Checks if group exists.
	 * 
	 * @param param
	 *            Database-Properties used: requestedGroupName
	 * @return groupid of group, GroupID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupName(final GenericParam param, final Transaction session) {
		final String oldUserName = param.getUserName();
		param.setUserName(null);
		try {
			return this.getGroupIdByGroupNameAndUserName(param, session);
		} finally {
			param.setUserName(oldUserName);
		}
	}

	/**
	 * Checks if a given user is in the given group.
	 * 
	 * @param param
	 *            Database-Properties used: requestedGroupName, userName
	 * @return groupid if user is in group, GroupID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupNameAndUserName(final GenericParam param, final Transaction session) {
		if (param.getRequestedGroupName() == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "requestedGroupName is null");
		}
		final Integer rVal = this.queryForObject("getGroupIdByGroupNameAndUserName", param, Integer.class, session);
		if (rVal == null) return GroupID.GROUP_INVALID.getId();
		return rVal;
	}

	/**
	 * Returns a new contentId
	 */
	public Integer getNewContentId(final ConstantID idsType, final Transaction session) {
		this.updateIds(idsType, session);
		return this.queryForObject("getNewContentId", idsType.getId(), Integer.class, session);
	}

	protected void updateIds(final ConstantID idsType, final Transaction session) {
		this.insert("updateIds", idsType.getId(), session);
	}
}