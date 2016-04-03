/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

import java.util.Date;

import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.util.data.Data;

/**
 * Represents a membership of a user in a specific group and the role she
 * represents there.
 * 
 * @author niebler
 */
public class GroupMembership {
	
	private User user;
	private GroupRole groupRole;
	private boolean userSharedDocuments;
	private Date joinDate;

	/**
	 * default constructor
	 */
	public GroupMembership() {
		// noop
	}
	
	/**
	 * 
	 * @param user
	 * @param groupRole
	 * @param userSharedDocuments
	 */
	public GroupMembership(User user, GroupRole groupRole, boolean userSharedDocuments) {
		this.user = user;
		this.groupRole = groupRole;
		this.userSharedDocuments = userSharedDocuments;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the groupRole
	 */
	public GroupRole getGroupRole() {
		return this.groupRole;
	}

	/**
	 * @param groupRole the groupRole to set
	 */
	public void setGroupRole(GroupRole groupRole) {
		this.groupRole = groupRole;
	}

	/**
	 * @return the userSharedDocuments
	 */
	public boolean isUserSharedDocuments() {
		return this.userSharedDocuments;
	}

	/**
	 * @param userSharedDocuments the userSharedDocuments to set
	 */
	public void setUserSharedDocuments(boolean userSharedDocuments) {
		this.userSharedDocuments = userSharedDocuments;
	}
	
	/**
	 * @return the joinDate
	 */
	public Date getJoinDate() {
		return this.joinDate;
	}

	/**
	 * @param joinDate the joinDate to set
	 */
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	
	/**
	 * toString. User GroupRole userSharedDocuments
	 * @return string
	 */
	@Override
	public String toString() {
		return this.user + " " + this.groupRole + " " + this.userSharedDocuments;
	}
}
