package org.bibsonomy.database.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.beans.TagRelationParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.ExceptionUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * Used to retrieve all different kind of stuff from the database.
 * 
 * @author Christian Schenk
 * @author Anton Wilhelm
 * @version $Id$
 */
public class GeneralDatabaseManager extends AbstractDatabaseManager {

	private static final Logger log = Logger.getLogger(GeneralDatabaseManager.class);
	private static final GeneralDatabaseManager singleton = new GeneralDatabaseManager();
	private static final ValidationUtils check = ValidationUtils.getInstance();
	
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
	public Boolean isFriendOf(final GenericParam param, final DBSession session) {
		if (check.present(param.getUserName()) == false) return false;
		if (check.present(param.getRequestedUserName()) == false) return false;
		if (param.getUserName().equals(param.getRequestedUserName())) return true;
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
	public Boolean isSpammer(final GenericParam param, final DBSession session) {
		if (check.present(param.getRequestedUserName()) == false) return false;
		return this.queryForObject("isSpammer", param, Boolean.class, session);
	}

	/**
	 * Gets all the groupIds of the given users groups.
	 * 
	 * @param param
	 *            Database-Properties used: userName
	 * @return A list of groupids
	 */
	public List<Integer> getGroupIdsForUser(final GenericParam param, final DBSession session) {
		return this.queryForList("getGroupIdsForUser", param, Integer.class, session);
	}

	/**
	 * Checks if group exists.
	 * 
	 * @param param
	 *            Database-Properties used: requestedGroupName
	 * @return groupid of group, GroupID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupName(final GenericParam param, final DBSession session) {
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
	public Integer getGroupIdByGroupNameAndUserName(final GenericParam param, final DBSession session) {
		if (check.present(param.getRequestedGroupName()) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "requestedGroupName isn't set");
		}
		final Integer rVal = this.queryForObject("getGroupIdByGroupNameAndUserName", param, Integer.class, session);
		if (rVal == null) return GroupID.INVALID.getId();
		return rVal;
	}

	/**
	 * Gets the next database-ID for inserting an entity with the type specified
	 * by the idsType argument. Updates the ID generator.
	 */
	public Integer getNewContentId(final ConstantID idsType, final DBSession session) {
		this.updateIds(idsType, session);
		return this.queryForObject("getNewContentId", idsType.getId(), Integer.class, session);
	}

	protected void updateIds(final ConstantID idsType, final DBSession session) {
		this.insert("updateIds", idsType.getId(), session);
	}
	
	public Integer getCurrentContentId(final ConstantID idsType, final DBSession session) {
		return this.queryForObject("getCurrentContentId", idsType.getId(), Integer.class, session);
	}
	
	public Integer countNewContentIdFromBibTex(final BibTexParam param, final DBSession session) {
		return this.queryForObject("countNewContentIdFromBibTex", param, Integer.class, session);
	}
	
	public Integer countRequestedContentIdFromBibTex(final BibTexParam param, final DBSession session) {
		return this.queryForObject("countRequestedContentIdFromBibTex", param, Integer.class, session);
	}

	public Integer countNewContentIdFromBookmark(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("countNewContentIdFromBookmark", param, Integer.class, session);
	}

	public Integer countRequestedContentIdFromBookmark(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("countRequestedContentIdFromBookmark", param, Integer.class, session);
	}
	
	public Integer countTasIds(final TagParam param, final DBSession session) {
		return this.queryForObject("countTasIds", param, Integer.class, session);
	}
	
	public Integer countLoggedTasIds(final TagParam param, final DBSession session) {
		return this.queryForObject("countLoggedTasIds", param, Integer.class, session);
	}
	
	public Integer countTagRelation(final TagRelationParam param, final DBSession session) {
		return this.queryForObject("countTagRelation", param, Integer.class, session);
	}
	
	public Integer countGroup(final GroupParam param, final DBSession session) {
		return this.queryForObject("countGroup", param, Integer.class, session);
	}
}