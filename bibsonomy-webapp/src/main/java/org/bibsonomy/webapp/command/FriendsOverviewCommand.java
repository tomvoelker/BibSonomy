/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * Command for friends and offriend info
 *
 * @author dzo
 */
public class FriendsOverviewCommand extends MultiResourceViewCommand {

	private List<User> friends;
	private List<User> ofFriends;
	private int entriesPerPage = -1;

	/**
	 * @return the friends
	 */
	public List<User> getFriends() {
		return this.friends;
	}

	/**
	 * @param friends the friends to set
	 */
	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	/**
	 * @return the ofFriends
	 */
	public List<User> getOfFriends() {
		return this.ofFriends;
	}

	/**
	 * @param ofFriends the ofFriends to set
	 */
	public void setOfFriends(List<User> ofFriends) {
		this.ofFriends = ofFriends;
	}
	
	/**
	 * @return entries per page
	 */
	public int getEntriesPerPage() {
		if (this.entriesPerPage == -1) {
			// fallback to user settings, if not set explicitly before via url parameter
			this.entriesPerPage = this.getContext().getLoginUser().getSettings().getListItemcount(); 
		}
		return this.entriesPerPage;
	}

	/**
	 * @param entriesPerPage the entriesPerPage to set
	 */
	public void setEntriesPerPage(int entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}
}
