package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
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
	 * Checks whether two users, given by userName and requestedUserName, are
	 * friends.
	 * 
	 * @param param
	 *            Database-Properties used: userName, requestedUserName
	 * @return true if the users are friends, false otherwise
	 */
	public Boolean isFriendOf(final GenericParam param, final Transaction transaction) {
		if (param.getUserName() == null || param.getRequestedUserName() == null) return false;
		return this.queryForObject("isFriendOf", param, Boolean.class, transaction);
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
	public Boolean isSpammer(final GenericParam param, final Transaction transaction) {
		if (param.getRequestedUserName() == null) return false;
		return this.queryForObject("isSpammer", param, Boolean.class, transaction);
	}

	/**
	 * Gets all the groups of the given user.
	 * 
	 * @param param
	 *            Database-Properties used: userName
	 * @return A list of groupids
	 */
	public List<Integer> getGroupsForUser(final GenericParam param, final Transaction transaction) {
		return this.queryForList("getGroupsForUser", param, Integer.class, transaction);
	}

	/**
	 * Checks if group exists.
	 * 
	 * @param param
	 *            Database-Properties used: requestedGroupName
	 * @return groupid of group, ConstantID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupName(final GenericParam param, final Transaction transaction) {
		param.setUserName(null);
		return this.getGroupIdByGroupNameAndUserName(param, transaction);
	}

	/**
	 * Checks if a given user is in the given group.
	 * 
	 * @param param
	 *            Database-Properties used: requestedGroupName, userName
	 * @return groupid if user is in group, ConstantID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupNameAndUserName(final GenericParam param, final Transaction transaction) {
		if (param.getRequestedGroupName() == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "requestedGroupName is null");
		}
		final Integer rVal = this.queryForObject("getGroupIdByGroupNameAndUserName", param, Integer.class, transaction);
		if (rVal == null) return ConstantID.GROUP_INVALID.getId();
		return rVal;
	}

	/**
	 * Get a current ContentID for setting a bookmark update the current
	 * ContendID for bookmark and bibtex
	 */
	public Integer getNewContentId(final GenericParam param, final Transaction transaction) {
		return this.queryForObject("getNewContentId", param, Integer.class, transaction);
	}

	public void updateIds(final GenericParam param, final Transaction transaction) {
		this.insert("updateIds", param, transaction);
	}
}