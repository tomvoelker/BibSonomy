package org.bibsonomy.database.params;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;

/**
 * Parameters that are specific to users.
 * 
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
public class UserParam extends GenericParam {

	/**
	 * a user
	 */
	private User user;
	

	/**
	 * a user relation
	 */
	private UserRelation userRelation;

	
	/**
	 * Returns the first friend of this user.<br/>
	 * 
	 * XXX: iBatis should support this: "friends[0].name", which should return
	 * the name of the first friend - but this doesn't seem to work so we need
	 * this extra method.
	 * 
	 * 2011/06/01, fei: moved this code fragment from the user model - this
	 *                  is used only in the query 'isFriendOf'
	 * 
	 * @return friend
	 */
	public User getFriend() {
		if (!present(this.user) || this.user.getFriends().size() < 1) return null;
		return this.user.getFriends().get(0);
	}
	
	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * set user relation
	 * 
	 * @param userRelation
	 */
	public void setUserRelation(UserRelation userRelation) {
		this.userRelation = userRelation;
	}

	/**
	 * get user relation
	 * 
	 * @return the user relation
	 */
	public UserRelation getUserRelation() {
		return userRelation;
	}
}