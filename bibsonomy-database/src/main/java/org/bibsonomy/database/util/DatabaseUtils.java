package org.bibsonomy.database.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
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
	private static final Log log = LogFactory.getLog(DatabaseUtils.class);
	
	private static final SqlMapClient client;

	static {
		// primary SqlMapClient
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
	 * Returns the default SqlMap which can be used to query the database.
	 */
	protected static SqlMapSession getSqlMap() {
		return client.openSession();
	}

	/**
	 * Checks if the logged-in user may see private / friends posts
	 * 
	 * PLEASE NOTE: as public stuff (tags, posts, ...) can be seen by everyone,
	 * the PUBLIC group is present by default in the param object (see constructor
 	 * of GenericParam
 	 * 
 	 * Furthermore, the groups the logged-in user is explitely member of (e.g. KDE)
 	 * are retrieved when getting access to the LogicInterface and are
 	 * set in the param object by the LogicInterfaceHelper.buildParam(... method
	 * 
	 * @param db
	 * @param param
	 * @param session
	 */
	public static void checkPrivateFriendsGroup(final GeneralDatabaseManager db, final GenericParam param, final DBSession session) {		 				
		if (present(param.getUserName()) && present(param.getRequestedUserName())) {
			final ArrayList<Integer> groupIds = new ArrayList<Integer>();
			// If userName and requestedUserName are the same -> add private and friends
			// otherwise: if they're friends -> only add friends
			if (param.getUserName().equals(param.getRequestedUserName())) {
				groupIds.add(GroupID.PRIVATE.getId());
				groupIds.add(GroupID.FRIENDS.getId());
			} else {
				if (db.isFriendOf(param.getUserName(), param.getRequestedUserName(), session)) {
					groupIds.add(GroupID.FRIENDS.getId());
				}
			}
			// add the groups
			param.addGroups(groupIds);			
		}
	}

	/**
	 * This needs to be done for all get*ForGroup* queries.
	 * @param db 
	 * @param param 
	 * @param session 
	 */
	public static void prepareGetPostForGroup(final GeneralDatabaseManager db, final GenericParam param, final DBSession session) {
		/*
		 * FIXME: Why is DatabaseUtils.checkPrivateFriendsGroup(db, param, session) called here?
		 * 
		 * It tests if a user name is given AND if the user is logged in. 
		 * At least the first test ALWAYS fails on /group/ pages, since we 
		 * have a group given, not a user name.  
		 * 
		 * 
		 */
		DatabaseUtils.checkPrivateFriendsGroup(db, param, session);

	}

	/**
	 * This needs to be done for all get*ForUser* queries.
	 * @param db 
	 * @param param 
	 * @param session 
	 */
	public static void prepareGetPostForUser(final GeneralDatabaseManager db, final GenericParam param, final DBSession session) {
		// if the groupId is invalid we have to check for groups manually
		if (param.getGroupId() == GroupID.INVALID.getId()) {
			DatabaseUtils.checkPrivateFriendsGroup(db, param, session);
		}
	}

	/**
	 * @return a {@link DBSessionFactory}
	 */
	public static DBSessionFactory getDBSessionFactory() {
		return new IbatisDBSessionFactory();
	}
}