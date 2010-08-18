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
 * Enum which summarizes which kinds of users we have in the system
 * (and which can be queried)
 * 
 * @author Folke Mitzlaff
 * @version $Id$
 */
public enum UsersType {
	/** the standard kind of user */
	DEFAULT,
	/** similar users */
	SIMILAR,
	/** friends, i.e. users I have added as friend */
	FRIENDS,
	/** users who have added myself as a friend */
	FRIENDOF,
	/** FRIENDS and FRIENDOF together */
	FRIENDSHIP
	;

	/**
	 * Returns the name for this kind of user, i.e.:
	 * 
	 * <pre>
	 *  DEFAULT    - default
	 *  SIMILAR    - similar
	 *  FRIENDS    - friends
	 *  FRIENDOF   - friendof
	 *  FRIENDSHIP - friendship
	 * </pre>
	 * 
	 * @return an all lowercase string for this kind of user
	 */
	public String getName() {
		return this.name().toLowerCase();
	}
}