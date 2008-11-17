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

import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class TagUtils {

	/**
	 * Get the maximum user count of all tags contained in a list
	 * 
	 * @param tags a list of tags
	 * @return the maximum user count
	 */
	public static int getMaxUserCount(List<Tag> tags) {
		int maxUserCount = 0;
		for (final Tag tag : tags) {
			if (tag.getUsercount() > maxUserCount) {
				maxUserCount = tag.getUsercount();
			}
		}
		return maxUserCount;
	}

	/**
	 * Get the maximum global count of all tags contained in a list
	 * 
	 * @param tags a list of tags
	 * @return the maximum global count
	 */
	public static int getMaxGlobalcountCount(List<Tag> tags) {
		int maxGlobalCount = 0;
		for (final Tag tag : tags) {
			if (tag.getGlobalcount() > maxGlobalCount) {
				maxGlobalCount = tag.getGlobalcount();
			}
		}
		return maxGlobalCount;
	}
}