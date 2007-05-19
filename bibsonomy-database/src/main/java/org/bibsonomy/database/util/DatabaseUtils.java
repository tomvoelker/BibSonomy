package org.bibsonomy.database.util;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.util.ExceptionUtils;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * Methods concerning the database.
 * 
 * @author Christian Schenk
 * @author Jens Illig
 * @version $Id$
 */
public class DatabaseUtils {
	private static final Logger log = Logger.getLogger(DatabaseUtils.class);
	private static final SqlMapClient client;

	static {
		SqlMapClient clientTmp;
		try {
			final String resource = "SqlMapConfig.xml";
			final Reader reader = Resources.getResourceAsReader(resource);
			clientTmp = SqlMapClientBuilder.buildSqlMapClient(reader);
		} catch (final IOException ex) {
			clientTmp = null;
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't initialize SqlMapClient");
		}
		client = clientTmp;
	}

	/**
	 * Returns a database transaction.
	 */
	public static Transaction getDatabaseSession() {
		return new Transaction(getSqlMap());
	}

	/**
	 * Returns the SqlMap which can be used to query the database.
	 */
	private static SqlMapSession getSqlMap() {
		return client.openSession();
	}

	/**
	 * Gets all groups of the user and puts them in the param. If the two given
	 * users are friends the groupId for friends is also appended.
	 */
	public static void setGroups(final GeneralDatabaseManager db, final GenericParam param, final Transaction session) {
		// If userName and requestedUserName are the same - do nothing
		if (param.getUserName() != null && param.getRequestedUserName() != null) {
			if (param.getUserName().equals(param.getRequestedUserName())) return;
		}
		final Boolean friends = db.isFriendOf(param, session);
		final List<Integer> groupIds = db.getGroupIdsForUser(param, session);
		if (friends) {
			groupIds.add(GroupID.GROUP_FRIENDS.getId());
		}
		param.setGroups(groupIds);
	}


	/**
	 * This needs to be done for all get*ForGroup* queries.
	 */
	public static void prepareGetPostForGroup(final GeneralDatabaseManager db, final GenericParam param, final Transaction session) {
		DatabaseUtils.setGroups(db, param, session);
		// the group type needs to be set to friends because of the second union
		// in the SQL statement
		param.setGroupType(GroupID.GROUP_FRIENDS);
	}

	/**
	 * This needs to be done for all get*ForUser* queries.
	 */
	public static void prepareGetPostForUser(final GeneralDatabaseManager db, final GenericParam param, final Transaction session) {
		// if the groupId is invalid we have to check for groups manually
		if (param.getGroupId() == GroupID.GROUP_INVALID.getId()) {
			DatabaseUtils.setGroups(db, param, session);
		}
	}
}