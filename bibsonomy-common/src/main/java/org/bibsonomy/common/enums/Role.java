/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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

/**
 * @author Robert Jäschke
 * @version $Id$
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
	SYNC(4);

	private static final Role[] map = new Role[] {ADMIN, DEFAULT, NOBODY, DELETED, SYNC};
	private final int role;

	private Role(final int role) {
		this.role = role;
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
		return map[role];
	}
}