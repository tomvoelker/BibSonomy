/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common.enums;

/**
 * An enum for permissions that are assigned on the group level.
 * If a groupLevelPermission is assigned to a group, then all members of that
 * group will have that permission.
 * 
 * @author sdo
 */
public enum GroupLevelPermission {
	/** Is allowed to mark community posts as inspected */
	COMMUNITY_POST_INSPECTION(0);

	/*
	 * TODO: further roles like
	 * * admin
	 * * spam flagger
	 * * discussion moderator
	 */

	private final int groupLevelPermissionId;

	/**
	 * Create a GroupLevelPermission with a given groupLevelPermissionId
	 */
	private GroupLevelPermission(final int groupLevelPermissionId) {
		this.groupLevelPermissionId = groupLevelPermissionId;
	}

	/**
	 * Returns the numerical representation of this object.
	 * 
	 * @return The numerical representation of the object.
	 */
	public int getGroupLevelPermissionId() {
		return this.groupLevelPermissionId;
	}

	/**
	 * Creates an instance of this class by its String representation.
	 * 
	 * @param groupLevelPermissionIdString -
	 *        a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static GroupLevelPermission getGroupLevelPermission(final String groupLevelPermissionIdString) {
		if (groupLevelPermissionIdString == null) {
			throw new IllegalArgumentException("the specified groupLevelPermission must be String representation of an Integer but was " + groupLevelPermissionIdString + ".");
		}
		return getGroupLevelPermission(Integer.parseInt(groupLevelPermissionIdString));
	}

	/**
	 * Creates an instance of this class by its Integer representation.
	 * 
	 * @param groupLevelPermissionId -
	 *        an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static GroupLevelPermission getGroupLevelPermission(final int groupLevelPermissionId) {
		for (final GroupLevelPermission glp : GroupLevelPermission.values()) {
			if (glp.groupLevelPermissionId == groupLevelPermissionId) {
				return glp;
			}
		}
		throw new IllegalArgumentException("unknown groupLevelPermissionId id " + groupLevelPermissionId);
	}

}
