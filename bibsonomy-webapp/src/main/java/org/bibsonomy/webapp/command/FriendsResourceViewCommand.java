/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.User;

/**
 * @author Steffen Kress
 */
public class FriendsResourceViewCommand extends TagResourceViewCommand {
	private List<User> userFriends;
	private List<User> friendsOfUser;
	/** for queries for specific kinds of users (e.g., friends) */
	// TODO: use UserRelation as type
	private String userRelation;

	/**
	 * @param userFriends
	 */
	public void setUserFriends(final List<User> userFriends) {
		this.userFriends = userFriends;
	}

	/**
	 * @param friendsOfUser
	 */
	public void setFriendsOfUser(final List<User> friendsOfUser) {
		this.friendsOfUser = friendsOfUser;
		
	}

	/**
	 * @return friends of the user
	 */
	public List<User> getFriendsOfUser() {
		return friendsOfUser;
	}

	/**
	 * @return the users friends
	 */
	public List<User> getUserFriends() {
		return userFriends;
	}

	/**
	 * @return The relation the users to return have with the requested user.
	 */
	public String getUserRelation() {
		return this.userRelation;
	}

	/**
	 * @param userRelation
	 */
	public void setUserRelation(final String userRelation) {
		this.userRelation = userRelation;
	}

}
