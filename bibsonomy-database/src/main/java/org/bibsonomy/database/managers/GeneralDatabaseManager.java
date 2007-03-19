package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.util.ExceptionUtils;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GenericParam;

/**
 * Used to retrieve all different kind of stuff from the database.
 * 
 * @author Christian Schenk
 */


public class GeneralDatabaseManager extends AbstractDatabaseManager {

	private final static GeneralDatabaseManager db = new GeneralDatabaseManager();
	
	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	private GeneralDatabaseManager() {
	}
	
	public static GeneralDatabaseManager getInstance(){
		return db;
	}

	
	
	
	
	/**
	 * Checks whether two users, given by userName and requestedUserName, are
	 * friends.
	 * 
	 * @param param
	 *            Database-Properties used: userName, requestedUserName
	 * @return true if the users are friends, false otherwise
	 */
	public Boolean isFriendOf(final GenericParam param) {
		if (param.getUserName() == null || param.getRequestedUserName() == null) return false;
		return (Boolean) this.queryForObject("isFriendOf", param);
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
	public Boolean isSpammer(final GenericParam param) {
		// TODO not tested
		if (param.getRequestedUserName() == null) return false;
		return (Boolean) this.queryForObject("isSpammer", param);
	}

	/**
	 * Gets all the groups of the given user.
	 * 
	 * @param param
	 *            Database-Properties used: userName
	 * @return A list of groupids
	 */
	public List<Integer> getGroupsForUser(final GenericParam param) {
		return this.intList("getGroupsForUser", param);
	}

	/**
	 * Checks if group exists.
	 * 
	 * @param param
	 *            Database-Properties used: requestedGroupName
	 * @return groupid of group, ConstantID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupName(final GenericParam param) {
		param.setUserName(null);
		return this.getGroupIdByGroupNameAndUserName(param);
	}

	/**
	 * Checks if a given user is in the given group.
	 * 
	 * @param param
	 *            Database-Properties used: requestedGroupName, userName
	 * @return groupid if user is in group, ConstantID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupNameAndUserName(final GenericParam param) {
		if (param.getRequestedGroupName() == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "requestedGroupName is null");
		}
		final Integer rVal = (Integer) this.queryForObject("getGroupIdByGroupNameAndUserName", param);
		if (rVal == null) return ConstantID.GROUP_INVALID.getId();
		return rVal;
	}
}