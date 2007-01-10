package org.bibsonomy.ibatis.util;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.ibatis.db.impl.DatabaseManager;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.GenericParam;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Methods concerning the database.
 * 
 * @author Christian Schenk
 */
public class DatabaseUtils {

	/**
	 * Returns the SqlMapClient which can be used to query the database.
	 */
	public static SqlMapClient getSqlMapClient(final Logger log) {
		SqlMapClient rVal = null;
		try {
			final String resource = "SqlMapConfig.xml";
			final Reader reader = Resources.getResourceAsReader(resource);
			rVal = SqlMapClientBuilder.buildSqlMapClient(reader);
		} catch (final IOException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't initialize SqlMap");
		}
		return rVal;
	}

	/**
	 * Gets all groups of the user and puts them in the param. If the two given
	 * users are friends the groupId for friends is also appended.
	 */
	public static void setGroups(final DatabaseManager db, final GenericParam param) {
		// If userName and requestedUserName are the same - do nothing
		if (param.getUserName() != null && param.getRequestedUserName() != null) {
			if (param.getUserName().equals(param.getRequestedUserName())) return;
		}
		final Boolean friends = db.getGeneral().isFriendOf(param);
		final List<Integer> groups = db.getGeneral().getGroupsForUser(param);
		if (friends) groups.add(ConstantID.GROUP_FRIENDS.getId());
		param.setGroups(groups);
	}

	/**
	 * This needs to be done for all get*ForGroup* queries.
	 */
	public static void prepareGetPostForGroup(final DatabaseManager db, final GenericParam param) {
		DatabaseUtils.setGroups(db, param);
		// the group type needs to be set to friends because of the second union
		// in the SQL statement
		param.setGroupType(ConstantID.GROUP_FRIENDS);
	}

	/**
	 * This needs to be done for all get*ForUser* queries.
	 */
	public static void prepareGetPostForUser(final DatabaseManager db, final GenericParam param) {
		// if the groupId is invalid we have to check for groups manually
		if (param.getGroupId() == ConstantID.GROUP_INVALID.getId()) {
			DatabaseUtils.setGroups(db, param);
		}
	}
}