/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.util.Sets;

/**
 * @author Robert Jäschke
 */
public enum Role {
	/** Is allowed to use admin pages. */
	ADMIN(0),
	/** When a new user registers, he has this role. */
	DEFAULT(1),
	/** Not logged in. */
	NOBODY(2),
	/** deleted account */
	DELETED(3),
	/** Is allowed to modify/set the date of a post during synchronization. **/
	SYNC(4),
	/** allowed to add private posts via webservice only **/
	LIMITED(5),
	/** dummy user for a group **/
	GROUPUSER(6),
	/** a reporting users has access to all reporting pages of the system */
	REPORTING_USER(7);

	private final int role;

	Role(final int role) {
		this.role = role;
	}

	private static Map<Role, Set<Role>> IMPLIED_ROLES = new HashMap<>();

	static {
		IMPLIED_ROLES.put(ADMIN, Sets.asSet(DEFAULT, SYNC, REPORTING_USER));
		IMPLIED_ROLES.put(REPORTING_USER, Sets.asSet(DEFAULT));
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
	 * @param role -
	 *            a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static Role getRole(final String role) {
		if (role == null) return DEFAULT;
		return getRole(Integer.parseInt(role));
	}

	/**
	 * Creates an instance of this class by its Integer representation.
	 * 
	 * @param role -
	 *            an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static Role getRole(final int role) {
		for (Role r : Role.values()) {
			if (r.role == role) {
				return r;
			}
		}
		throw new IllegalArgumentException("unknown role id " + role);
	}

	/**
	 * get the implied roles of the role
	 * @param role
	 * @return
	 */
	public static Set<Role> getImpliedRoles(final Role role) {
		if (IMPLIED_ROLES.containsKey(role)) {
			return IMPLIED_ROLES.get(role);
		}

		return Collections.emptySet();
	}
}