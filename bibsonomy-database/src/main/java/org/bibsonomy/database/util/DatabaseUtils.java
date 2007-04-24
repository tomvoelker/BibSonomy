package org.bibsonomy.database.util;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.util.ExceptionUtils;
import org.bibsonomy.model.Resource;

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
	public static void setGroups(final GeneralDatabaseManager db, final GenericParam<? extends Resource> param) {
		// If userName and requestedUserName are the same - do nothing
		if (param.getUserName() != null && param.getRequestedUserName() != null) {
			if (param.getUserName().equals(param.getRequestedUserName())) return;
		}
		final Boolean friends = db.isFriendOf(param);
		final List<Integer> groups = db.getGroupsForUser(param);
		if (friends) groups.add(ConstantID.GROUP_FRIENDS.getId());
		param.setGroups(groups);
	}


	/*
	 * FIXME: vorläufig für set GroupForTag anfragen
	 */
	
	public static void setGroupsForTag(final GeneralDatabaseManager db, final UserParam param) {
		// If userName and requestedUserName are the same - do nothing
		if (param.getUserName() != null && param.getRequestedUserName() != null) {
			if (param.getUserName().equals(param.getRequestedUserName())) return;
		}
		final Boolean friends = db.isFriendOfTag(param);
		final List<Integer> groups = db.getGroupsForUserTag(param);
		if (friends) groups.add(ConstantID.GROUP_FRIENDS.getId());
		param.setGroups(groups);
	}
	
	/**
	 * This needs to be done for all get*ForGroup* queries.
	 */
	public static void prepareGetPostForGroup(final GeneralDatabaseManager db, final GenericParam<? extends Resource> param) {
		DatabaseUtils.setGroups(db, param);
		// the group type needs to be set to friends because of the second union
		// in the SQL statement
		param.setGroupType(ConstantID.GROUP_FRIENDS);
	}
	/*
	 * FIXME: vorläufig für Tag Anfragen 
	 */
	
	public static void prepareGetTagForGroup(final GeneralDatabaseManager db, final UserParam param) {
		DatabaseUtils.setGroupsForTag(db, param);
		// the group type needs to be set to friends because of the second union
		// in the SQL statement
		param.setGroupType(ConstantID.GROUP_FRIENDS);
	}
	
	
	/*
	 * FIXME:vorläufig für Tag Anfrage
	 */
	public static void prepareGetTagForUser(final GeneralDatabaseManager db, final UserParam param) {
		// if the groupId is invalid we have to check for groups manually
		if (param.getGroupId() == ConstantID.GROUP_INVALID.getId()) {
			DatabaseUtils.setGroupsForTag(db, param);
		}
	}
	
	
	/**
	 * This needs to be done for all get*ForUser* queries.
	 */
	public static void prepareGetPostForUser(final GeneralDatabaseManager db, final GenericParam<? extends Resource> param) {
		// if the groupId is invalid we have to check for groups manually
		if (param.getGroupId() == ConstantID.GROUP_INVALID.getId()) {
			DatabaseUtils.setGroups(db, param);
		}
	}
}