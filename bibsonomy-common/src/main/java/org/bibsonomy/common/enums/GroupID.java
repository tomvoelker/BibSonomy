/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.common.enums;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Constant group ids.
 */
public enum GroupID {
	/** the public group */
	PUBLIC(0),
	/** the owning user's private group */
	PRIVATE(1),
	/** the owning user's friends group */
	FRIENDS(2),

	/**
	 * the kde group (normally groups are not hardoded as this, but this is an
	 * example used in some tescases)
	 */
	KDE(3),
	/** an invalid value */
	INVALID(-1),
	
	/**
	 * group for admins to be able to view spam posts
	 */
	ADMINSPAM(-2147483648);

	private final int id;

	private GroupID(final int id) {
		this.id = id;
	}

	/**
	 * @return the constant value behind the symbol
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param groupName
	 *            the groupname to look up
	 * @return GroupID representation of a special group which name correspond
	 *         to the argument
	 */
	public static GroupID getSpecialGroup(final String groupName) {
		if (present(groupName) == false) return null;
		final GroupID group = valueOf(groupName.toUpperCase());
		if (isSpecialGroupId(group.getId())) return group;
		return null;
	}

	/**
	 * categorizes groupIds between special and nonspecial groups. special
	 * groups are groups, that are not created by users.
	 * 
	 * @param groupId
	 *            the groupId to check
	 * @return true if the groupId argument is a special group
	 */
	public static boolean isSpecialGroupId(final int groupId) {
		return ((groupId < 3) && (groupId >= 0));
	}

	/**
	 * categorizes groupIds between special and nonspecial groups. special
	 * groups are groups, that are not created by users.
	 * 
	 * @param groupId
	 *            the groupId to check
	 * @return true if the groupId argument is a special group
	 */
	public static boolean isSpecialGroupId(final GroupID groupId) {
		return isSpecialGroupId(groupId.getId());
	}

	/**
	 * wrapper function to check if a given groupname represents a special group
	 * 
	 * @param groupName
	 * @return true if the given group is a special group, false otherwise
	 */
	public static boolean isSpecialGroup(final String groupName) {
		try {
			if (getSpecialGroup(groupName) != null) return true;
		} catch (IllegalArgumentException ignore) {
		}
		return false;
	}
}