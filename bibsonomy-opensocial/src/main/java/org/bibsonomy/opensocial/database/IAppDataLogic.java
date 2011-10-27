package org.bibsonomy.opensocial.database;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.apache.shindig.protocol.DataCollection;

/**
 * database interface for managing application (i.e. gadget) specific 
 * persistent data entries
 * 
 * @author fei
 */
public interface IAppDataLogic {
	/**
	 * Retrives app data for the specified user list and group.
	 *
	 * @param userIds A set of UserIds.
	 * @param groupId The group
	 * @param appId   The app
	 * @param fields  The fields to filter the data by. Empty set implies all
	 * @return The data fetched
	 */
	public void deletePersonData(String userId, String groupId, String appId, Set<String> fields) throws SQLException;

	/**
	 * Deletes data for the specified user and group.
	 *
	 * @param userId  The user
	 * @param groupId The group
	 * @param appId   The app
	 * @param fields  The fields to delete. Empty set implies all
	 * @return an error if one occurs
	 */
	public DataCollection getPersonData(Set<String> userIds, String groupId, String appId, Set<String> fields) throws SQLException;

	/**
	 * Updates app data for the specified user and group with the new values.
	 *
	 * @param userId  The user
	 * @param groupId The group
	 * @param appId   The app
	 * @param fields  The fields to filter the data by. Empty set implies all
	 * @param values  The values to set
	 * @return an error if one occurs
	 */
	public void updatePersonData(String userId, String groupId, String appId, Set<String> fields, Map<String, String> values) throws SQLException;
}
