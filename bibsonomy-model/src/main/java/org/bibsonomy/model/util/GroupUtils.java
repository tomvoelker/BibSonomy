/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model.util;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupUtils {

	/**
	 * Public group
	 * 
	 * @return public group
	 */
	public static Group getPublicGroup() {
		return getGroup("public", "public group", GroupID.PUBLIC, Privlevel.PUBLIC);
	}

	/**
	 * Private group
	 * 
	 * @return private group
	 */
	public static Group getPrivateGroup() {
		return getGroup("private", "private group", GroupID.PRIVATE, Privlevel.HIDDEN);
	}

	/**
	 * Friends group
	 * 
	 * @return friends group
	 */
	public static Group getFriendsGroup() {
		return getGroup("friends", "group of all your BibSonomy friends", GroupID.FRIENDS, Privlevel.HIDDEN);
	}

	/**
	 * Invalid group
	 * 
	 * @return invalid group
	 */
	public static Group getInvalidGroup() {
		return getGroup("invalid", "invalid group", GroupID.INVALID, Privlevel.HIDDEN);
	}

	/**
	 * Helper method that returns a new {@link Group} object.
	 */
	private static Group getGroup(final String name, final String description, final GroupID groupId, final Privlevel privlevel) {
		final Group group = new Group();
		group.setName(name);
		group.setDescription(description);
		group.setGroupId(groupId.getId());
		group.setPrivlevel(privlevel);
		return group;
	}
}