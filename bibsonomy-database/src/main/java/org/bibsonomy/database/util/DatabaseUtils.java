package org.bibsonomy.database.util;

import static org.bibsonomy.util.ValidationUtils.present;

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
 * @author Jens Illig
 * @author Christian Schenk
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
	 * Returns the SqlMap which can be used to query the database.
	 */
	protected static SqlMapSession getSqlMap() {
		return client.openSession();
	}

	/**
	 * Gets all groups of the user and puts them in the param. If the two given
	 * users are friends the groupId for friends is also appended.
	 */
	public static void setGroups(final GeneralDatabaseManager db, final GenericParam param, final DBSession session) {
		final List<Integer> groupIds = db.getGroupIdsForUser(param.getUserName(), session);

		// each user is allowed to see public posts
		groupIds.add(GroupID.PUBLIC.getId());
		
		if (present(param.getUserName()) && present(param.getRequestedUserName())) {
			// If userName and requestedUserName are the same -> add private and friends
			// otherwise: if they're friends -> add friends
			if (param.getUserName().equals(param.getRequestedUserName())) {
				groupIds.add(GroupID.PRIVATE.getId());
				groupIds.add(GroupID.FRIENDS.getId());
			} else {
				final boolean friends = db.isFriendOf(param, session);
				if (friends) groupIds.add(GroupID.FRIENDS.getId());
			}
		}

		param.setGroups(groupIds);
	}

	/**
	 * This needs to be done for all get*ForGroup* queries.
	 */
	public static void prepareGetPostForGroup(final GeneralDatabaseManager db, final GenericParam param, final DBSession session) {
		DatabaseUtils.setGroups(db, param, session);
		// the group type needs to be set to friends because of the second union
		// in the SQL statement
		param.setGroupType(GroupID.FRIENDS);
	}

	/**
	 * This needs to be done for all get*ForUser* queries.
	 */
	public static void prepareGetPostForUser(final GeneralDatabaseManager db, final GenericParam param, final DBSession session) {
		// if the groupId is invalid we have to check for groups manually
		if (param.getGroupId() == GroupID.INVALID.getId()) {
			DatabaseUtils.setGroups(db, param, session);
		}
	}

	/**
	 * @return a {@link DBSessionFactory}
	 */
	public static DBSessionFactory getDBSessionFactory() {
		return new IbatisDBSessionFactory();
	}
}