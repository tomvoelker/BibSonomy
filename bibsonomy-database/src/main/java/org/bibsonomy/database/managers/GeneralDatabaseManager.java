package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;

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

	private GeneralDatabaseManager() {
	}

	/**
	 * @return the singleton instance
	 */
	public static GeneralDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Checks whether two users are friends. If one of the usernames is empty
	 * the result will be <code>false</code>. In case both usernames are
	 * equal <code>true</code> will be returned, i.e. every user is his own
	 * friend.
	 * 
	 * @param userName
	 *            the username whose friend should be checked
	 * @param friendUserName
	 *            the username of a possible friend
	 * @param session
	 *            a db session
	 * @return true if the users are friends, false otherwise
	 */
	public boolean isFriendOf(final String userName, final String friendUserName, final DBSession session) {
		if (present(userName) == false || present(friendUserName) == false) return false;
		if (userName.equals(friendUserName)) return true;
		final User user = new User(userName);
		user.addFriend(new User(friendUserName));
		return this.queryForObject("isFriendOf", user, Boolean.class, session);
	}

	/**
	 * Checks whether a user, given by userName, is a spammer. If userName is
	 * set to null the default behaviour is to return false, i.e. no spammer.
	 * 
	 * @param userName check the user with this name
	 * @param session a db session
	 * @return true if the user is a spammer, false otherwise
	 */
	public Boolean isSpammer(final String userName, final DBSession session) {
		if (present(userName) == false) return false;
		return this.queryForObject("isSpammer", userName, Boolean.class, session);
	}

	/**
	 * Gets all the groupIds of the given users groups.
	 * 
	 * @param userName userName to get the groupids for
	 * @param session a db session
	 * @return A list of groupids
	 */
	public List<Integer> getGroupIdsForUser(final String userName, final DBSession session) {
		if (present(userName) == false) return new ArrayList<Integer>();
		return this.queryForList("getGroupIdsForUser", userName, Integer.class, session);
	}

	/**
	 * Checks if group exists.
	 * 
	 * @param param Database-Properties used: requestedGroupName
	 * @param session a db session
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
	 * @param param Database-Properties used: requestedGroupName, userName
	 * @param session a db session
	 * @return groupid if user is in group, GroupID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupNameAndUserName(final GenericParam param, final DBSession session) {
		if (present(param.getRequestedGroupName()) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "requestedGroupName isn't set");
		}
		try {
			final GroupID specialGroup = GroupID.getSpecialGroup(param.getRequestedGroupName());
			if (specialGroup != null) {
				return specialGroup.getId();
			}
		}
		catch (IllegalArgumentException ex) {
			// do nothing - this simply means that the given group is not a special group
		}
		final Integer rVal = this.queryForObject("getGroupIdByGroupNameAndUserName", param, Integer.class, session);
		if (rVal == null) return GroupID.INVALID.getId();
		return rVal;
	}

	/**
	 * Gets the next database-ID for inserting an entity with the type specified
	 * by the idsType argument. Updates the ID generator.
	 * 
	 * @param idsType type of the id to be created
	 * @param session a db session
	 * @return the next database-ID
	 */
	public Integer getNewContentId(final ConstantID idsType, final DBSession session) {
		this.updateIds(idsType, session);
		return this.queryForObject("getNewContentId", idsType.getId(), Integer.class, session);
	}

	protected void updateIds(final ConstantID idsType, final DBSession session) {
		this.insert("updateIds", idsType.getId(), session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param idsType
	 * @param session
	 * @return current contentID
	 */
	public Integer getCurrentContentId(final ConstantID idsType, final DBSession session) {
		return this.queryForObject("getCurrentContentId", idsType.getId(), Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count new contentID from BibTeX
	 */
	public Integer countNewContentIdFromBibTex(final BibTexParam param, final DBSession session) {
		return this.queryForObject("countNewContentIdFromBibTex", param, Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count requested contentID from BibTeX
	 */
	public Integer countRequestedContentIdFromBibTex(final BibTexParam param, final DBSession session) {
		return this.queryForObject("countRequestedContentIdFromBibTex", param, Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count new contentID from Bookmark
	 */
	public Integer countNewContentIdFromBookmark(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("countNewContentIdFromBookmark", param, Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count requested contentID from Bookmark
	 */
	public Integer countRequestedContentIdFromBookmark(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("countRequestedContentIdFromBookmark", param, Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count tasIDs
	 */
	public Integer countTasIds(final TagParam param, final DBSession session) {
		return this.queryForObject("countTasIds", param, Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count logged tasIDs
	 */
	public Integer countLoggedTasIds(final TagParam param, final DBSession session) {
		return this.queryForObject("countLoggedTasIds", param, Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count tag relation
	 */
	public Integer countTagRelation(final TagRelationParam param, final DBSession session) {
		return this.queryForObject("countTagRelation", param, Integer.class, session);
	}

	/**
	 * TODO: document me...
	 * 
	 * @param param
	 * @param session
	 * @return count group
	 */
	public Integer countGroup(final GroupParam param, final DBSession session) {
		return this.queryForObject("countGroup", param, Integer.class, session);
	}
}