/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.comparators;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Comparator;

import org.bibsonomy.model.User;


/**
 * Compare BibSonomy users based on their user name
 * 
 * @author fei
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
