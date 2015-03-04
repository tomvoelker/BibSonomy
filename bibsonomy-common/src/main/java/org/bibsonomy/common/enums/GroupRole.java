/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common.enums;

import java.util.Collections;
import java.util.Set;

import org.bibsonomy.util.Sets;

/**
 * TODO: move pending group roles to a separate enum?
 * 
 * Enum for the different group roles.
 * 
 * @author clemensbaier
 */
public enum GroupRole {

	/** user */
	USER(2),
	
	/** moderator */
	MODERATOR(1, Sets.asSet(USER)),
	
	/** administrator */
	ADMINISTRATOR(0, Sets.asSet(MODERATOR, USER)),

	/** dummy */
	DUMMY(3),

	/** user invited by an admin or moderator */
	INVITED(4),

	/** request to join the group */
	REQUESTED(5);

	/** all pending group roles */
	public static final Set<GroupRole> PENDING_GROUP_ROLES = Sets.asSet(GroupRole.INVITED, GroupRole.REQUESTED);

	/** all non pending group roles */
	public static final Set<GroupRole> GROUP_ROLES = Sets.asSet(GroupRole.ADMINISTRATOR, GroupRole.MODERATOR, GroupRole.USER);

	private final int role;
	private final Set<GroupRole> impliedRoles;
	
	private GroupRole(final int role) {
		this(role, Collections.<GroupRole>emptySet());
	}

	private GroupRole(final int role, final Set<GroupRole> impliedRoles) {
		this.role = role;
		this.impliedRoles = impliedRoles;
	}

	/**
	 * Returns the numerical representation of this object.
	 * 
	 * @return The numerical representation of the object.
	 */
	public int getRole() {
		return this.role;
	}

	/**
	 * Creates an instance of this class by its String representation.
	 * 
	 * @param level
	 *        - a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static GroupRole getGroupRole(final String level) {
		if (level == null) {
			return USER;
		}
		return getGroupRole(Integer.parseInt(level));
	}

	/**
	 * Creates an instance of this class by its Integer representation.
	 * 
	 * @param level
	 *        - an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static GroupRole getGroupRole(final int level) {
		for (final GroupRole r : GroupRole.values()) {
			if (r.role == level) {
				return r;
			}
		}
		throw new IllegalArgumentException("unknown group role id " + level);
	}
	
	/**
	 * @param requiredRole
	 * @return <code>true</code> if the required role equals the actual role or
	 * the required role is implied by this role
	 */
	public boolean hasRole(final GroupRole requiredRole) {
		return this.equals(requiredRole) || this.impliedRoles.contains(requiredRole);
	}
}
