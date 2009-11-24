package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;

/**
 * Used to retrieve all different kind of stuff from the database.
 * 
 * @author Christian Schenk
 * @author Anton Wilhelm
 * @version $Id$
 */
public class GeneralDatabaseManager extends AbstractDatabaseManager {

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
	 * Checks whether <code>userA</code> is friend of <code>userB</code>, i.e., <strong>if 
	 * <code>userA</code> is in <code>userB</code>'s list of friends</strong>.
	 * <br/>
	 * <ul>
	 * <li>If one of the user names is empty the result will be <code>false</code>.</li> 
	 * <li>In case the user names are equal <code>true</code> will be returned, i.e. every user is his own
	 * friend.</li>
	 * </ul>
	 * 
	 * @param userA
	 *            the user who might be a friend of <code>userB</code>.
	 * @param userB
	 *            the user whose friendship to <code>userA</code> should be checked.
	 * @param session
	 *            a db session
	 *            
	 * @return <code>true</code> if <code>userA</code> is in <code>userB</code>'s list of friends, 
	 *         <code>false</code> otherwise
	 */
	public boolean isFriendOf(final String userA, final String userB, final DBSession session) {
		/*
		 * user names missing -> no friends
		 */
		if (present(userA) == false || present(userB) == false) return false;
		/*
		 * everybody is his/her own friend
		 */
		if (userA.equals(userB)) return true;
		/*
		 * we're looking at userB's friend list, hence, we create userB ...
		 */
		final User user = new User(userB);
		/*
		 * ... and then add userB to the list of his friends.
		 */
		user.addFriend(new User(userA));
		/*
		 * now we can query the DB, if userA is really userB's friend
		 */
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
	 * Gets the next database-ID for inserting an entity with the type specified
	 * by the idsType argument. Updates the ID generator.
	 * 
	 * @param idsType type of the id to be created
	 * @param session a db session
	 * @return the next database-ID
	 * TODO: rename this method (e.g. getNewId) and the corresponding queries since it handles any ConstantID 
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