/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model.comparators;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Comparator;

import org.bibsonomy.model.User;


/**
 * Compare BibSonomy users based on their user name
 * 
 * @author fei
 * @version $Id$
 */
public class UserComparator implements Comparator<User> {
	@Override
	public int compare(User u1, User u2) {
		if (!present(u1) || !present(u1.getName())) {
			if (!present(u2) || !present(u2.getName())) {
				return 0;
			}
			return -1;
		} else if (!present(u2) || !present(u2.getName())) {
			return 1;
		}
		
		return u1.getName().compareToIgnoreCase(u2.getName());
	}	
}
